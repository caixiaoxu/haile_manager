package com.yunshang.haile_manager_android.ui.activity.common

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioGroup
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.SearchSelectRadioViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams.SearchSelectTypeParam
import com.yunshang.haile_manager_android.data.arguments.SearchSelectParam
import com.yunshang.haile_manager_android.data.rule.SearchSelectRadioEntity
import com.yunshang.haile_manager_android.databinding.ActivitySearchSelectRadioBinding
import com.yunshang.haile_manager_android.databinding.ItemDepartmentMultiSelectBinding
import com.yunshang.haile_manager_android.databinding.ItemDepartmentSelectBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity

class SearchSelectRadioActivity :
    BaseBusinessActivity<ActivitySearchSelectRadioBinding, SearchSelectRadioViewModel>(
        SearchSelectRadioViewModel::class.java,
        BR.vm
    ) {

    override fun layoutId(): Int = R.layout.activity_search_select_radio

    override fun backBtn(): View = mBinding.barDepartmentSelectTitle.getBackBtn()

    /**
     * 设置标题右侧按钮
     */
    private fun initRightBtn() {
        mBinding.barDepartmentSelectTitle.getRightBtn().run {
            setText(R.string.finish)
            setTextColor(
                ContextCompat.getColor(
                    this@SearchSelectRadioActivity,
                    R.color.colorPrimary
                )
            )
            setOnClickListener {
                mViewModel.selectList.value?.let { list ->
                    val selected = list.filter { select -> select.getCheck()?.value ?: false }

                    if (SearchSelectTypeParam.SearchSelectTypeTakeChargeShop == mViewModel.searchSelectType.value
                        && -1 != mViewModel.staffId
                    ) {
                        mViewModel.updateStaffShop(
                            this@SearchSelectRadioActivity,
                            selected
                        )
                    } else {
                        setResult(
                            when (mViewModel.searchSelectType.value) {
                                SearchSelectTypeParam.SearchSelectTypeShop, SearchSelectTypeParam.SearchSelectTypeTakeChargeShop, SearchSelectTypeParam.SearchSelectTypeRechargeShop -> SearchSelectTypeParam.ShopResultCode
                                SearchSelectTypeParam.SearchSelectTypeDeviceModel -> SearchSelectTypeParam.DeviceModelResultCode
                                else -> RESULT_OK
                            },
                            Intent().apply {
                                putExtra(
                                    SearchSelectTypeParam.ResultData,
                                    GsonUtils.any2Json(
                                        selected.map {
                                            SearchSelectParam(
                                                it.getSelectId(),
                                                it.getSelectName()
                                            )
                                        }
                                    )
                                )
                            })
                        finish()
                    }
                }
            }
        }
    }

    override fun initIntent() {
        super.initIntent()

        // 类别
        mViewModel.searchSelectType.value =
            intent.getIntExtra(SearchSelectTypeParam.SearchSelectType, -1)

        mViewModel.categoryId =
            intent.getIntExtra(SearchSelectTypeParam.CategoryId, -1)

        mViewModel.staffId =
            intent.getIntExtra(SearchSelectTypeParam.StaffId, -1)
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE
        initRightBtn()

        mBinding.tvDepartmentSelectListAll.setOnClickListener {
            (mViewModel.isAllSelect.value?.let { !it }
                ?: false).also { isAll ->
                if (mBinding.svDepartmentSelectList.getChildAt(0) is LinearLayout) {
                    (mBinding.svDepartmentSelectList.getChildAt(0) as LinearLayout).children.forEach { cb ->
                        (cb as AppCompatCheckBox).isChecked = isAll
                    }
                }
                mViewModel.isAllSelect.value = isAll
            }
        }
    }

    override fun initEvent() {
        super.initEvent()

        mBinding.etDepartmentSelectSearch.onTextChange = {
            mViewModel.requestSearch()
        }

        mViewModel.selectList.observe(this) {
            mViewModel.checkSelectAll()
            initSelectList(it)
        }
        mViewModel.jump.observe(this) {
            finish()
        }
    }

    /**
     * 初始化选择列表
     */
    private fun initSelectList(list: MutableList<out SearchSelectRadioEntity>?) {
        val inflate = LayoutInflater.from(this)
        if (SearchSelectTypeParam.SearchSelectTypeTakeChargeShop == mViewModel.searchSelectType.value) {
            if (mBinding.svDepartmentSelectList.getChildAt(0) !is LinearLayout) {
                mBinding.svDepartmentSelectList.removeAllViews()
            } else {
                (mBinding.svDepartmentSelectList.getChildAt(0) as LinearLayout).removeAllViews()
            }

            if (mBinding.svDepartmentSelectList.childCount == 0) {
                mBinding.svDepartmentSelectList.addView(
                    LinearLayout(this).apply {
                        orientation = LinearLayout.VERTICAL
                    },
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }

            list?.let {
                it.forEachIndexed { index, item ->
                    DataBindingUtil.inflate<ItemDepartmentMultiSelectBinding>(
                        inflate,
                        R.layout.item_department_multi_select,
                        null,
                        false
                    ).let { binding ->
                        binding.item = item
                        if (0 < index) {
                            binding.root.setBackgroundResource(R.drawable.shape_top_stroke_dividing_efefef_mlr16)
                        } else {
                            binding.root.setBackgroundColor(Color.WHITE)
                        }

                        (binding.root as AppCompatCheckBox).setOnCheckedChangeListener { _, isCheck ->
                            item.getCheck()?.value = isCheck
                            mViewModel.checkSelectAll()
                        }
                        (mBinding.svDepartmentSelectList.getChildAt(0) as LinearLayout).addView(
                            binding.root,
                            ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                            )
                        )
                    }
                }
            }
        } else {
            if (mBinding.svDepartmentSelectList.getChildAt(0) !is RadioGroup) {
                mBinding.svDepartmentSelectList.removeAllViews()
            } else {
                (mBinding.svDepartmentSelectList.getChildAt(0) as RadioGroup).removeAllViews()
            }

            if (mBinding.svDepartmentSelectList.childCount == 0) {
                mBinding.svDepartmentSelectList.addView(
                    RadioGroup(this),
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }

            list?.let {
                it.forEachIndexed { index, item ->
                    DataBindingUtil.inflate<ItemDepartmentSelectBinding>(
                        inflate,
                        R.layout.item_department_select,
                        null,
                        false
                    ).let { binding ->
                        binding.item = item
                        if (0 < index) {
                            binding.root.setBackgroundResource(R.drawable.shape_top_stroke_dividing_efefef_mlr16)
                        } else {
                            binding.root.setBackgroundColor(Color.WHITE)
                        }
                        (mBinding.svDepartmentSelectList.getChildAt(0) as RadioGroup).addView(
                            binding.root,
                            ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                            )
                        )
                    }
                }
            }
        }
    }

    override fun initData() {
        mViewModel.requestSearch()
    }
}
package com.shangyun.haile_manager_android.ui.activity.common

import android.content.Intent
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.databinding.DataBindingUtil
import com.lsy.framelib.utils.DimensionUtils
import com.lsy.framelib.utils.gson.GsonUtils
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.vm.SearchSelectRadioViewModel
import com.shangyun.haile_manager_android.data.arguments.SearchSelectParam
import com.shangyun.haile_manager_android.data.rule.SearchSelectRadioEntity
import com.shangyun.haile_manager_android.databinding.ActivitySearchSelectRadioBinding
import com.shangyun.haile_manager_android.databinding.ItemDepartmentSelectBinding
import com.shangyun.haile_manager_android.ui.activity.BaseBusinessActivity

class SearchSelectRadioActivity :
    BaseBusinessActivity<ActivitySearchSelectRadioBinding, SearchSelectRadioViewModel>() {

    companion object {
        const val SearchSelectType = "searchSelectType"
        const val CategoryId = "categoryId"
        const val ShopResultCode = 0x90001
        const val DeviceModelResultCode = 0x90002
        const val ResultData = "resultData"
    }

    private val mSearchSelectRadioViewModel by lazy {
        getActivityViewModelProvider(this)[SearchSelectRadioViewModel::class.java]
    }

    override fun layoutId(): Int = R.layout.activity_search_select_radio

    override fun getVM(): SearchSelectRadioViewModel = mSearchSelectRadioViewModel

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
                mSearchSelectRadioViewModel.selectList.value?.let { list ->
                    val selected = list.find { select -> select.getCheck()?.value ?: false }
                    selected?.let { s ->
                        setResult(
                            when (mSearchSelectRadioViewModel.searchSelectType.value) {
                                SearchSelectRadioViewModel.SearchSelectTypeShop -> ShopResultCode
                                SearchSelectRadioViewModel.SearchSelectTypeDeviceModel -> DeviceModelResultCode
                                else -> RESULT_OK
                            },
                            Intent().apply {
                                putExtra(
                                    ResultData, GsonUtils.any2Json(
                                        SearchSelectParam(
                                            s.getSelectId(),
                                            s.getSelectName()
                                        )
                                    )
                                )
                            })
                    }
                    finish()
                }
            }
        }
    }

    override fun initIntent() {
        super.initIntent()

        // 类别
        mSearchSelectRadioViewModel.searchSelectType.value =
            intent.getIntExtra(SearchSelectType, -1)

        mSearchSelectRadioViewModel.categoryId =
            intent.getIntExtra(CategoryId, -1)
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE

        initRightBtn()
    }

    override fun initEvent() {
        super.initEvent()

        mBinding.etDepartmentSelectSearch.onTextChange = {
            mSearchSelectRadioViewModel.requestSearch()
        }

        mSearchSelectRadioViewModel.selectList.observe(this) {
            initSelectList(it)
        }
    }

    /**
     * 初始化选择列表
     */
    private fun initSelectList(list: MutableList<out SearchSelectRadioEntity>?) {
        mBinding.rgDepartmentSelectList.removeAllViews()
        list?.let {
            it.forEach { item ->
                val itemView =
                    LayoutInflater.from(this).inflate(R.layout.item_department_select, null, false)
                DataBindingUtil.bind<ItemDepartmentSelectBinding>(itemView)?.let { binding ->
                    binding.item = item
                    mBinding.rgDepartmentSelectList.addView(
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

    override fun initData() {
        mBinding.vm = mSearchSelectRadioViewModel

        mSearchSelectRadioViewModel.requestSearch()
    }
}
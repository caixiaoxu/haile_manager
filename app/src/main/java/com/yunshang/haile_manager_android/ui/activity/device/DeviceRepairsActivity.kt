package com.yunshang.haile_manager_android.ui.activity.device

import android.content.Intent
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.ui.weight.SingleTapTextView
import com.lsy.framelib.utils.DimensionUtils
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.DeviceRepairsViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.arguments.SearchSelectParam
import com.yunshang.haile_manager_android.data.common.SearchType
import com.yunshang.haile_manager_android.data.entities.DeviceRepairsEntity
import com.yunshang.haile_manager_android.databinding.ActivityDeviceRepairsBinding
import com.yunshang.haile_manager_android.databinding.ItemDeviceRepairsBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.activity.common.SearchActivity
import com.yunshang.haile_manager_android.ui.activity.common.SearchSelectRadioActivity
import com.yunshang.haile_manager_android.ui.activity.common.ShopPositionSelectorActivity
import com.yunshang.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter
import com.yunshang.haile_manager_android.ui.view.dialog.CommonBottomSheetDialog
import com.yunshang.haile_manager_android.ui.view.refresh.CommonRefreshRecyclerView
import com.yunshang.haile_manager_android.utils.BitmapUtils

class DeviceRepairsActivity :
    BaseBusinessActivity<ActivityDeviceRepairsBinding, DeviceRepairsViewModel>(
        DeviceRepairsViewModel::class.java, BR.vm
    ) {

    private val mAdapter by lazy {
        CommonRecyclerAdapter<ItemDeviceRepairsBinding, DeviceRepairsEntity>(
            R.layout.item_device_repairs,
            BR.item
        ) { mItemBinding, _, item ->
            mViewModel.isBatch.observe(this) {
                mItemBinding?.isBatch = it
            }
            mItemBinding?.cbDeviceRepairsSelect?.setOnCheckedChangeListener { _, isChecked ->
                item.selected = isChecked
                refreshSelectBatchNum()
            }

            mItemBinding?.root?.setOnClickListener {
                if (true == mViewModel.isBatch.value) {
                    return@setOnClickListener
                }
                item.deviceId?.let { deviceId ->
                    startActivity(
                        Intent(
                            this@DeviceRepairsActivity,
                            DeviceRepairsReplyListActivity::class.java
                        ).apply {
                            putExtras(IntentParams.CommonParams.pack(deviceId))
                        })
                }
            }
        }
    }

    // 搜索选择界面
    private val startSearchSelect =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            it.data?.let { intent ->
                when (it.resultCode) {
                    IntentParams.SearchSelectTypeParam.ShopResultCode -> {
                        intent.getStringExtra(IntentParams.SearchSelectTypeParam.ResultData)
                            ?.let { json ->
                                GsonUtils.json2List(json, SearchSelectParam::class.java)
                                    ?.let { selected ->
                                        mViewModel.selectShopList.value = selected
                                        mViewModel.selectPositionList.value = null
                                        mViewModel.selectDeviceCategoryList.value = null
                                        mBinding.rvDeviceRepairsList.requestRefresh()
                                    }
                            }
                    }
                    IntentParams.ShopPositionSelectorParams.ShopPositionSelectorResultCode -> {
                        mViewModel.selectPositionList.value =
                            IntentParams.ShopPositionSelectorParams.parseSelectList(intent)
                        mBinding.rvDeviceRepairsList.requestRefresh()
                    }
                }
            }
        }

    override fun layoutId(): Int = R.layout.activity_device_repairs

    override fun backBtn(): View = mBinding.barDeviceRepairsTitle.getBackBtn()

    /**
     * 设置标题右侧按钮
     */
    private fun initRightBtn() {
        mBinding.barDeviceRepairsTitle.getRightArea().removeAllViews()
        mBinding.barDeviceRepairsTitle.getRightArea().run {
            val padding = DimensionUtils.dip2px(this@DeviceRepairsActivity, 8f)
            addView(AppCompatImageButton(this@DeviceRepairsActivity).apply {
                setImageDrawable(
                    BitmapUtils.tintDrawable(
                        ContextCompat.getDrawable(
                            this@DeviceRepairsActivity,
                            R.mipmap.icon_search
                        ),
                        ContextCompat.getColor(this@DeviceRepairsActivity, R.color.common_txt_color)
                    )
                )
                setBackgroundColor(Color.TRANSPARENT)
                setOnClickListener {
                    startActivity(
                        Intent(
                            this@DeviceRepairsActivity,
                            SearchActivity::class.java
                        ).apply {
                            putExtra(SearchType.SearchType, SearchType.DeviceRepairs)
                        })
                }
            }, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

            addView(SingleTapTextView(this@DeviceRepairsActivity).apply {
                mViewModel.isBatch.observe(this@DeviceRepairsActivity) {
                    setText(if (it) R.string.cancel else R.string.batch_reply)
                }
                textSize = 14f
                setTextColor(
                    ContextCompat.getColor(
                        this@DeviceRepairsActivity,
                        R.color.color_black_85
                    )
                )
                val ph = DimensionUtils.dip2px(this@DeviceRepairsActivity, 12f)
                val pV = DimensionUtils.dip2px(this@DeviceRepairsActivity, 4f)
                setPadding(ph, pV, ph, pV)
                setOnClickListener {
                    mViewModel.isBatch.value = !(mViewModel.isBatch.value ?: false).apply {
                        if (!this) {
                            resetSelectBatchNum()
                        }
                    }
                }
                layoutParams =
                    LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).also { lp ->
                        lp.marginStart = padding
                        lp.marginEnd = padding
                    }
            })
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE
        initRightBtn()

        // 全部门店
        mBinding.tvDeviceRepairsShop.setOnClickListener {
            startSearchSelect.launch(
                Intent(
                    this@DeviceRepairsActivity,
                    SearchSelectRadioActivity::class.java
                ).apply {
                    putExtras(
                        IntentParams.SearchSelectTypeParam.pack(
                            IntentParams.SearchSelectTypeParam.SearchSelectTypeShop,
                            mustSelect = false,
                            moreSelect = true,
                            selectArr = mViewModel.selectShopList.value?.map { item -> item.id }
                                ?.toIntArray() ?: intArrayOf()
                        )
                    )
                }
            )
        }

        // 点位
        mBinding.tvDeviceRepairsPosition.setOnClickListener {
            if (mViewModel.selectShopList.value.isNullOrEmpty()) {
                SToast.showToast(this@DeviceRepairsActivity, "请先选择门店")
                return@setOnClickListener
            }
            startSearchSelect.launch(
                Intent(
                    this@DeviceRepairsActivity,
                    ShopPositionSelectorActivity::class.java
                ).apply {
                    putExtras(
                        IntentParams.ShopPositionSelectorParams.pack(
                            mustSelect = false,
                            selectList = mViewModel.selectPositionList.value,
                            shopIdList = mViewModel.selectShopList.value?.map { item -> item.id }
                                ?.toIntArray()
                        )
                    )
                }
            )
        }

        // 设备类型
        mBinding.tvDeviceRepairsDevice.setOnClickListener {
            if (mViewModel.selectShopList.value.isNullOrEmpty()) {
                SToast.showToast(this@DeviceRepairsActivity, "请先选择门店")
                return@setOnClickListener
            }
//            mViewModel.categoryList.value?.let { list ->
//                MultiSelectBottomSheetDialog.Builder<CategoryEntity>("设备类型", list).apply {
//                    onValueSureListener =
//                        object : MultiSelectBottomSheetDialog.OnValueSureListener<CategoryEntity> {
//                            override fun onValue(
//                                selectData: List<CategoryEntity>,
//                                allSelectData: List<CategoryEntity>
//                            ) {
//                            }
//                        }
//                }.build().show(supportFragmentManager)
//            }
        }

        // 状态
        mBinding.tvDeviceRepairsStatus.setOnClickListener {
            val list = listOf(
                SearchSelectParam(0, "全部状态"),
                SearchSelectParam(10, "已回复"),
                SearchSelectParam(20, "未回复"),
            )

            CommonBottomSheetDialog.Builder(
                "", list
            ).apply {
                onValueSureListener =
                    object : CommonBottomSheetDialog.OnValueSureListener<SearchSelectParam> {
                        override fun onValue(data: SearchSelectParam?) {
                            mViewModel.selectStatus.value = data
                            mBinding.rvDeviceRepairsList.requestRefresh()
                        }
                    }
            }.build().show(supportFragmentManager)
        }

        mBinding.rvDeviceRepairsList.layoutManager = LinearLayoutManager(this)
        mBinding.rvDeviceRepairsList.adapter = mAdapter
        mBinding.rvDeviceRepairsList.requestData =
            object : CommonRefreshRecyclerView.OnRequestDataListener<DeviceRepairsEntity>() {
                override fun requestData(
                    isRefresh: Boolean,
                    page: Int,
                    pageSize: Int,
                    callBack: (responseList: ResponseList<out DeviceRepairsEntity>?) -> Unit
                ) {
                    mViewModel.requestDeviceRepairsList(page, pageSize, callBack)
                }
            }

        mBinding.cbDeviceRepairsAll.setOnCheckClickListener {
            if (!mBinding.cbDeviceRepairsAll.isChecked) {
                selectAll()
            } else {
                resetSelectBatchNum()
            }

            true
        }

        mBinding.btnDeviceRepairsReply.setOnClickListener {

        }
    }

    private fun selectAll() {
        mAdapter.list.forEach {
            it.selected = 10 == it.replyStatus
        }
        mViewModel.refreshSelectBatchNum(mAdapter.list)
    }

    private fun resetSelectBatchNum() {
        mAdapter.list.forEach {
            it.selected = false
        }
        mViewModel.refreshSelectBatchNum(mAdapter.list)
    }

    private fun refreshSelectBatchNum() {
        mViewModel.refreshSelectBatchNum(mAdapter.list)
    }

    override fun initData() {
        mViewModel.requestData()
        mBinding.rvDeviceRepairsList.requestRefresh()
    }
}
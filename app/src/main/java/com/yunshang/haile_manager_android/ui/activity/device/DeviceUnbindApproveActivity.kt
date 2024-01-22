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
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.ui.weight.SingleTapTextView
import com.lsy.framelib.utils.DimensionUtils
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.StringUtils
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.business.vm.DeviceUnbindApproveViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.arguments.SearchSelectParam
import com.yunshang.haile_manager_android.data.common.SearchType
import com.yunshang.haile_manager_android.data.entities.CategoryEntity
import com.yunshang.haile_manager_android.data.entities.DeviceUnbindApproveEntity
import com.yunshang.haile_manager_android.databinding.ActivityDeviceUnbindApproveBinding
import com.yunshang.haile_manager_android.databinding.ItemDeviceRepairsBinding
import com.yunshang.haile_manager_android.databinding.ItemDeviceUnbindApproveBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.activity.common.SearchActivity
import com.yunshang.haile_manager_android.ui.activity.common.SearchSelectRadioActivity
import com.yunshang.haile_manager_android.ui.activity.common.ShopPositionSelectorActivity
import com.yunshang.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter
import com.yunshang.haile_manager_android.ui.view.dialog.CommonBottomSheetDialog
import com.yunshang.haile_manager_android.ui.view.dialog.CommonDialog
import com.yunshang.haile_manager_android.ui.view.dialog.MultiSelectBottomSheetDialog
import com.yunshang.haile_manager_android.ui.view.refresh.CommonRefreshRecyclerView
import com.yunshang.haile_manager_android.utils.BitmapUtils
import com.yunshang.haile_manager_android.utils.UserPermissionUtils

/**
 * Title :
 * Author: Lsy
 * Date: 2024/1/16 13:40
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class DeviceUnbindApproveActivity :
    BaseBusinessActivity<ActivityDeviceUnbindApproveBinding, DeviceUnbindApproveViewModel>(
        DeviceUnbindApproveViewModel::class.java, BR.vm
    ) {

    private val mAdapter by lazy {
        CommonRecyclerAdapter<ItemDeviceUnbindApproveBinding, DeviceUnbindApproveEntity>(
            R.layout.item_device_unbind_approve,
            BR.item
        ) { mItemBinding, _, item ->
            mViewModel.isBatch.observe(this) {
                mItemBinding?.isBatch = it
            }
            mItemBinding?.cbDeviceRepairsSelect?.setOnCheckedChangeListener { _, isChecked ->
                item.selected = isChecked
                refreshSelectBatchNum()
            }

            mItemBinding?.btnDeviceRepairsDeviceDetail?.setOnClickListener {
                startActivity(
                    Intent(
                        this@DeviceUnbindApproveActivity,
                        DeviceDetailActivity::class.java
                    ).apply {
                        putExtra(DeviceDetailActivity.GoodsId, item.goodsId)
                    }
                )
            }

            mItemBinding?.root?.setOnClickListener {
                if (true == mViewModel.isBatch.value) {
                    item.selected = !item.selected
                    refreshSelectBatchNum()
                    return@setOnClickListener
                }
                startActivity(
                    Intent(
                        this@DeviceUnbindApproveActivity,
                        DeviceUnbindApproveDetailsActivity::class.java
                    ).apply {
                        item.id?.let {
                            putExtras(IntentParams.CommonParams.pack(it))
                        }
                    })
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
                                        mBinding.rvDeviceUnbindApproveList.requestRefresh()
                                    }
                            }
                    }

                    IntentParams.ShopPositionSelectorParams.ShopPositionSelectorResultCode -> {
                        mViewModel.selectPositionList.value =
                            IntentParams.ShopPositionSelectorParams.parseSelectList(intent)
                        mBinding.rvDeviceUnbindApproveList.requestRefresh()
                    }
                }
            }
        }

    override fun layoutId(): Int = R.layout.activity_device_unbind_approve

    override fun backBtn(): View = mBinding.barDeviceUnbindApproveTitle.getBackBtn()

    override fun initEvent() {
        super.initEvent()

        mViewModel.isBatch.observe(this) {
            if (!it) resetSelectBatchNum()
        }
        LiveDataBus.with(BusEvents.DEVICE_UNBIND_APPROVE_STATUS)?.observe(this) {
            mBinding.rvDeviceUnbindApproveList.requestRefresh()
        }
    }


    /**
     * 设置标题右侧按钮
     */
    private fun initRightBtn() {
        mBinding.barDeviceUnbindApproveTitle.getRightArea().removeAllViews()
        mBinding.barDeviceUnbindApproveTitle.getRightArea().run {
            val padding = DimensionUtils.dip2px(this@DeviceUnbindApproveActivity, 8f)
            addView(AppCompatImageButton(this@DeviceUnbindApproveActivity).apply {
                setImageDrawable(
                    BitmapUtils.tintDrawable(
                        ContextCompat.getDrawable(
                            this@DeviceUnbindApproveActivity,
                            R.mipmap.icon_search
                        ),
                        ContextCompat.getColor(
                            this@DeviceUnbindApproveActivity,
                            R.color.common_txt_color
                        )
                    )
                )
                setBackgroundColor(Color.TRANSPARENT)
                setOnClickListener {
                    startActivity(
                        Intent(
                            this@DeviceUnbindApproveActivity,
                            SearchActivity::class.java
                        ).apply {
                            putExtra(SearchType.SearchType, SearchType.DeviceUnbind)
                        })
                }
            }, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

            addView(SingleTapTextView(this@DeviceUnbindApproveActivity).apply {
                mViewModel.isBatch.observe(this@DeviceUnbindApproveActivity) {
                    setText(if (it) R.string.cancel else R.string.batch_dispose)
                }
                textSize = 14f
                setTextColor(
                    ContextCompat.getColor(
                        this@DeviceUnbindApproveActivity,
                        R.color.color_black_85
                    )
                )
                val ph = DimensionUtils.dip2px(this@DeviceUnbindApproveActivity, 12f)
                setPadding(ph, 0, ph, 0)
                setOnClickListener {
                    mViewModel.isBatch.value = !(mViewModel.isBatch.value ?: false)
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
        mBinding.tvDeviceUnbindApproveShop.setOnClickListener {
            startSearchSelect.launch(
                Intent(
                    this@DeviceUnbindApproveActivity,
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
        mBinding.tvDeviceUnbindApprovePosition.setOnClickListener {
            if (mViewModel.selectShopList.value.isNullOrEmpty()) {
                SToast.showToast(this@DeviceUnbindApproveActivity, "请先选择门店")
                return@setOnClickListener
            }
            startSearchSelect.launch(
                Intent(
                    this@DeviceUnbindApproveActivity,
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
        mBinding.tvDeviceUnbindApproveDevice.setOnClickListener {
            mViewModel.categoryList.value?.let { list ->
                MultiSelectBottomSheetDialog.Builder("设备类型（可多选）", list).apply {
                    isCanSelectEmpty = true
                    onValueSureListener =
                        object :
                            MultiSelectBottomSheetDialog.OnValueSureListener<CategoryEntity> {
                            override fun onValue(
                                selectData: List<CategoryEntity>,
                                allSelectData: List<CategoryEntity>
                            ) {
                                mViewModel.selectDeviceCategoryList.value = selectData
                                mBinding.rvDeviceUnbindApproveList.requestRefresh()
                            }
                        }
                }.build().show(supportFragmentManager)
            }
        }

        // 状态
        mBinding.tvDeviceUnbindApproveStatus.setOnClickListener {
            val list = listOf(
                SearchSelectParam(0, "全部状态"),
                SearchSelectParam(1, "未处理"),
                SearchSelectParam(2, "批准"),
                SearchSelectParam(3, "驳回"),
            )

            CommonBottomSheetDialog.Builder(
                "", list
            ).apply {
                selectData = mViewModel.selectStatus.value
                onValueSureListener =
                    object : CommonBottomSheetDialog.OnValueSureListener<SearchSelectParam> {
                        override fun onValue(data: SearchSelectParam?) {
                            mViewModel.selectStatus.value = data
                            mBinding.rvDeviceUnbindApproveList.requestRefresh()
                        }
                    }
            }.build().show(supportFragmentManager)
        }

        mBinding.rvDeviceUnbindApproveList.layoutManager = LinearLayoutManager(this)
        mBinding.rvDeviceUnbindApproveList.adapter = mAdapter
        mBinding.rvDeviceUnbindApproveList.requestData =
            object : CommonRefreshRecyclerView.OnRequestDataListener<DeviceUnbindApproveEntity>() {
                override fun requestData(
                    isRefresh: Boolean,
                    page: Int,
                    pageSize: Int,
                    callBack: (responseList: ResponseList<out DeviceUnbindApproveEntity>?) -> Unit
                ) {
                    if (UserPermissionUtils.hasRepairsListPermission()) {
                        mViewModel.requestDeviceUnbindApproveList(page, pageSize, callBack)
                    }
                }
            }

        mBinding.cbDeviceUnbindApproveAll.setOnCheckClickListener {
            if (!mBinding.cbDeviceUnbindApproveAll.isChecked) {
                selectAll()
            } else {
                resetSelectBatchNum()
            }

            true
        }

        mBinding.btnDeviceUnbindApproveDispose.setOnClickListener {
            val ids =
                mAdapter.list.filter { item -> item.selected }.mapNotNull { item -> item.id }

            CommonDialog.Builder("请选择批量处理的结果")
                .apply {
                    title = StringUtils.getString(R.string.tip)
                    showClose = true
                    isCancelable = true
                    setNegativeButton(StringUtils.getString(R.string.reject1)) {
                        mViewModel.disposeDeviceUnbind(this@DeviceUnbindApproveActivity, ids, 2)
                    }
                    setPositiveButton(StringUtils.getString(R.string.approve)) {
                        mViewModel.disposeDeviceUnbind(this@DeviceUnbindApproveActivity, ids, 1)
                    }
                }.build().show(supportFragmentManager)
        }
    }

    private fun selectAll() {
        mAdapter.list.forEach {
            it.selected = 1 == it.status
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
        mBinding.rvDeviceUnbindApproveList.requestRefresh()
    }
}
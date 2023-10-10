package com.yunshang.haile_manager_android.ui.activity.shop

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.TypefaceSpan
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.utils.DimensionUtils
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.business.vm.ShopManagerViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.common.SearchType
import com.yunshang.haile_manager_android.data.entities.ShopEntity
import com.yunshang.haile_manager_android.data.entities.ShopPositionEntity
import com.yunshang.haile_manager_android.databinding.ActivityShopManagerBinding
import com.yunshang.haile_manager_android.databinding.ItemShopListBinding
import com.yunshang.haile_manager_android.databinding.ItemShopManagerPositionBinding
import com.yunshang.haile_manager_android.databinding.PopupShopOperateManagerBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.activity.common.SearchActivity
import com.yunshang.haile_manager_android.ui.activity.common.SearchSelectRadioActivity
import com.yunshang.haile_manager_android.ui.activity.device.DeviceManagerActivity
import com.yunshang.haile_manager_android.ui.activity.personal.IncomeCalendarActivity
import com.yunshang.haile_manager_android.ui.view.TranslucencePopupWindow
import com.yunshang.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter
import com.yunshang.haile_manager_android.ui.view.dialog.DeviceCategoryDialog
import com.yunshang.haile_manager_android.ui.view.refresh.CommonRefreshRecyclerView
import com.yunshang.haile_manager_android.ui.view.refresh.CustomDividerItemDecoration
import com.yunshang.haile_manager_android.utils.NumberUtils
import com.yunshang.haile_manager_android.utils.UserPermissionUtils

class ShopManagerActivity :
    BaseBusinessActivity<ActivityShopManagerBinding, ShopManagerViewModel>(
        ShopManagerViewModel::class.java,
        BR.vm
    ) {

    override fun layoutId(): Int = R.layout.activity_shop_manager

    override fun backBtn(): View = mBinding.shopTitleBar.getBackBtn()

    /**
     * 设置标题右侧按钮
     */
    private fun initRightBtn() {
        mBinding.shopTitleBar.getRightBtn(true).run {
            setText(R.string.operate_manager)
            setCompoundDrawablesRelativeWithIntrinsicBounds(
                R.mipmap.icon_add, 0, 0, 0
            )
            compoundDrawablePadding = DimensionUtils.dip2px(this@ShopManagerActivity, 4f)
            setOnClickListener {
                showDeviceOperateView()
            }
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE

        // 搜索界面
        mBinding.viewShopManagerSearchBg.setOnClickListener {
            startActivity(Intent(this@ShopManagerActivity, SearchActivity::class.java).apply {
                putExtra(SearchType.SearchType, SearchType.Shop)
            })
        }

        // 刷新
        mBinding.tvShopManagerListRefresh.setOnClickListener {
            mBinding.rvShopList.requestRefresh()
        }

        mBinding.rvShopList.layoutManager = LinearLayoutManager(this)
        mBinding.rvShopList.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            ).apply {
                ResourcesCompat.getDrawable(resources, R.drawable.divide_size8, null)?.let {
                    setDrawable(it)
                }
            })
        mBinding.rvShopList.adapter = buildShopAdapter()
        mBinding.rvShopList.requestData =
            object : CommonRefreshRecyclerView.OnRequestDataListener<ShopEntity>() {
                override fun requestData(
                    isRefresh: Boolean,
                    page: Int,
                    pageSize: Int,
                    callBack: (responseList: ResponseList<out ShopEntity>?) -> Unit
                ) {
                    if (true == mSharedViewModel.hasShopListPermission.value) {
                        mViewModel.requestShopList(page, pageSize, callBack)
                    }
                }
            }
    }

    /**
     * 构建门店Adapter
     */
    private fun buildShopAdapter() = CommonRecyclerAdapter<ItemShopListBinding, ShopEntity>(
        R.layout.item_shop_list,
        BR.item
    ) { mItemBinding, _, item ->
        mItemBinding?.share = mSharedViewModel
        var title =
            StringUtils.getString(R.string.total_income)
        var value =
            StringUtils.getString(R.string.unit_money) + NumberUtils.keepTwoDecimals(item.income)
        var start = title.length + 1
        var end = title.length + 1 + value.length
        // 格式化总收益样式
        mItemBinding?.tvItemShopTotalIncome?.text =
            com.yunshang.haile_manager_android.utils.StringUtils.formatMultiStyleStr(
                "$title：$value",
                arrayOf(
                    ForegroundColorSpan(
                        ResourcesCompat.getColor(
                            resources,
                            R.color.colorPrimary,
                            null
                        )
                    ),
                    AbsoluteSizeSpan(DimensionUtils.sp2px(18f, this@ShopManagerActivity)),
                    //                        StyleSpan(Typeface.BOLD),
                    TypefaceSpan("money"),
                ), start, end
            )
        mItemBinding?.tvItemShopTotalIncome?.setOnClickListener {
            if (!UserPermissionUtils.hasProfitCalendarPermission()) {
                SToast.showToast(this@ShopManagerActivity, "无收益日历的功能权限")
                return@setOnClickListener
            }
            //  跳转到店铺收益
            startActivity(
                Intent(
                    this@ShopManagerActivity,
                    IncomeCalendarActivity::class.java
                ).apply {
                    putExtra(IncomeCalendarActivity.ProfitType, 1)
                    putExtra(IncomeCalendarActivity.ProfitSearchId, item.id)
                })
        }

        // 点位数
        title = StringUtils.getString(R.string.pt)
        value = item.positionCount.toString()
        start = title.length + 1
        end = title.length + 1 + value.length
        // 格式化样式
        mItemBinding?.tvItemShopPositionNum?.text =
            com.yunshang.haile_manager_android.utils.StringUtils.formatMultiStyleStr(
                "$title：$value",
                arrayOf(
                    AbsoluteSizeSpan(DimensionUtils.sp2px(16f, this@ShopManagerActivity)),
                    StyleSpan(Typeface.BOLD),
                ), start, end
            )
        mItemBinding?.tvItemShopPositionNum?.setOnClickListener {
            if (item.fold) {
                if (item.positionList.isEmpty()) {
                    mViewModel.requestPositionList(it.context, item) {
                        (mItemBinding.rvShopPositionList.adapter as CommonRecyclerAdapter<*, ShopPositionEntity>).refreshList(
                            item.positionList,
                            true
                        )
                    }
                } else {
                    item.fold = false
                }
            } else {
                item.fold = true
            }
        }
        // 设备数
        title = StringUtils.getString(R.string.device_num)
        value = item.deviceNum.toString()
        start = title.length + 1
        end = title.length + 1 + value.length
        // 格式化设备数样式
        mItemBinding?.tvItemShopDeviceNum?.text =
            com.yunshang.haile_manager_android.utils.StringUtils.formatMultiStyleStr(
                "$title：$value",
                arrayOf(
                    AbsoluteSizeSpan(DimensionUtils.sp2px(16f, this@ShopManagerActivity)),
                    StyleSpan(Typeface.BOLD),
                ), start, end
            )

        // 点位列表
        mItemBinding?.rvShopPositionList?.layoutManager = LinearLayoutManager(this)
        if ((mItemBinding?.rvShopPositionList?.itemDecorationCount ?: 0) == 0) {
            ContextCompat.getDrawable(this, R.drawable.divide_size8)?.let {
                mItemBinding?.rvShopPositionList?.addItemDecoration(
                    CustomDividerItemDecoration(
                        this,
                        CustomDividerItemDecoration.VERTICAL
                    ).apply {
                        setDrawable(it)
                    }
                )
            }
        }
        mItemBinding?.rvShopPositionList?.adapter =
            CommonRecyclerAdapter<ItemShopManagerPositionBinding, ShopPositionEntity>(
                R.layout.item_shop_manager_position, BR.item
            ) { mInternalItemBinding, _, posititon ->
                // 设备数
                title = StringUtils.getString(R.string.device_num)
                value = posititon.deviceNum.toString()
                start = title.length + 1
                end = title.length + 1 + value.length
                // 格式化设备数样式
                mInternalItemBinding?.tvShopManagerPositionDeviceNum?.text =
                    com.yunshang.haile_manager_android.utils.StringUtils.formatMultiStyleStr(
                        "$title：$value",
                        arrayOf(
                            AbsoluteSizeSpan(DimensionUtils.sp2px(16f, this@ShopManagerActivity)),
                            StyleSpan(Typeface.BOLD),
                        ), start, end
                    )

                mInternalItemBinding?.tvShopManagerPositionDeviceNum?.setOnClickListener {
                    if (null != item.id && null != posititon.id && 0 < posititon.deviceNum) {
                        //id 不为空，并且设备数大于0
                        mViewModel.requestPositionDeviceNum(item.id, posititon.id) { num ->
                            if (!num.isNullOrEmpty()) {
                                // 点位设备列表
                                DeviceCategoryDialog.Builder(false, num).apply {
                                    onDeviceCodeSelectListener = { type ->
                                        startActivity(Intent(
                                            this@ShopManagerActivity,
                                            DeviceManagerActivity::class.java
                                        ).apply {
                                            putExtras(
                                                IntentParams.DeviceManagerParams.pack(
                                                    categoryBigType = type
                                                )
                                            )
                                        })
                                    }
                                }.build().show(supportFragmentManager)
                            }
                        }
                    }
                }
                mInternalItemBinding?.root?.setOnClickListener {
                    // 跳转到点位详情
                    startActivity(Intent(
                        this@ShopManagerActivity,
                        ShopPositionDetailActivity::class.java
                    ).apply {
                        putExtras(IntentParams.CommonParams.pack(posititon.id))
                    })
                }
            }

        // 如果点位数据不为空，刷新
        if (item.positionList.isNotEmpty()) {
            (mItemBinding?.rvShopPositionList?.adapter as? CommonRecyclerAdapter<*, ShopPositionEntity>)?.refreshList(
                item.positionList,
                true
            )
        }
        // 加载更多
        mItemBinding?.tvShopPositionMore?.setOnClickListener {
            mViewModel.requestPositionList(it.context, item) {
                (mItemBinding.rvShopPositionList.adapter as CommonRecyclerAdapter<*, ShopPositionEntity>).refreshList(
                    item.positionList,
                    true
                )
            }
        }

        // 进入详情
        mItemBinding?.root?.setOnClickListener {
            if (true == mSharedViewModel.hasShopInfoPermission.value && null != item.id) {
                startActivity(Intent(
                    this@ShopManagerActivity,
                    ShopDetailActivity::class.java
                ).apply {
                    putExtras(IntentParams.ShopParams.pack(item.id))
                })
            }
        }
    }

    /**
     * 显示店铺管理界面
     */
    private fun AppCompatTextView.showDeviceOperateView() {
        val mPopupBinding =
            PopupShopOperateManagerBinding.inflate(LayoutInflater.from(this@ShopManagerActivity))
        val popupWindow = TranslucencePopupWindow(
            mPopupBinding.root,
            window,
            DimensionUtils.dip2px(this@ShopManagerActivity, 110f)
        )

        mPopupBinding.tvShopOperateAddShop.setOnClickListener {
            popupWindow.dismiss()
            startActivity(
                Intent(
                    this@ShopManagerActivity,
                    ShopCreateAndUpdateActivity::class.java
                )
            )
        }
        mPopupBinding.tvShopOperateAddPt.setOnClickListener {
            popupWindow.dismiss()
            // 跳转新增点位
            startActivity(
                Intent(
                    this@ShopManagerActivity,
                    ShopPositionCreateActivity::class.java
                )
            )
        }
        mPopupBinding.tvShopOperatePaySetting.setOnClickListener {
            popupWindow.dismiss()
            startActivity(Intent(
                this@ShopManagerActivity,
                SearchSelectRadioActivity::class.java
            ).apply {
                putExtras(
                    IntentParams.SearchSelectTypeParam.pack(
                        IntentParams.SearchSelectTypeParam.SearchSelectTypePaySettingsShop,
                        mustSelect = true,
                        moreSelect = true
                    )
                )
            })
        }
        popupWindow.showAsDropDown(
            this,
            -DimensionUtils.dip2px(this@ShopManagerActivity, 16f),
            0
        )
    }

    override fun initEvent() {
        super.initEvent()

        mSharedViewModel.hasShopListPermission.observe(this) {
            mBinding.rvShopList.requestRefresh()
        }
        mSharedViewModel.hasShopInfoPermission.observe(this) {}
        mSharedViewModel.hasShopProfitPermission.observe(this) {}
        mSharedViewModel.hasShopAddPermission.observe(this) {
            if (it)
                initRightBtn()
        }

        // 修改成功后
        LiveDataBus.with(BusEvents.SHOP_LIST_STATUS)?.observe(this) {
            mBinding.rvShopList.requestRefresh()
        }
    }

    override fun initData() {
    }
}
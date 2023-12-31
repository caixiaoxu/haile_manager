package com.yunshang.haile_manager_android.ui.activity.coupon

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.ui.weight.SingleTapTextView
import com.lsy.framelib.utils.DimensionUtils
import com.lsy.framelib.utils.StringUtils
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.business.vm.CouponManageViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.arguments.SearchSelectParam
import com.yunshang.haile_manager_android.data.common.SearchType
import com.yunshang.haile_manager_android.data.entities.CategoryEntity
import com.yunshang.haile_manager_android.data.entities.CouponEntity
import com.yunshang.haile_manager_android.databinding.ActivityCouponManageBinding
import com.yunshang.haile_manager_android.databinding.ItemCouponListBinding
import com.yunshang.haile_manager_android.databinding.PopupCouponOperateManagerBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.activity.common.SearchActivity
import com.yunshang.haile_manager_android.ui.activity.common.SearchSelectRadioActivity
import com.yunshang.haile_manager_android.ui.view.IndicatorPagerTitleView
import com.yunshang.haile_manager_android.ui.view.TranslucencePopupWindow
import com.yunshang.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter
import com.yunshang.haile_manager_android.ui.view.dialog.CommonBottomSheetDialog
import com.yunshang.haile_manager_android.ui.view.dialog.CommonDialog
import com.yunshang.haile_manager_android.ui.view.dialog.MultiSelectBottomSheetDialog
import com.yunshang.haile_manager_android.ui.view.refresh.CommonRefreshRecyclerView
import com.yunshang.haile_manager_android.utils.BitmapUtils
import com.yunshang.haile_manager_android.utils.UserPermissionUtils
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator

class CouponManageActivity :
    BaseBusinessActivity<ActivityCouponManageBinding, CouponManageViewModel>(
        CouponManageViewModel::class.java, BR.vm
    ) {

    // 搜索选择界面
    private val startSearchSelect =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            it.data?.let { intent ->
                intent.getStringExtra(IntentParams.SearchSelectTypeParam.ResultData)?.let { json ->
                    GsonUtils.json2List(json, SearchSelectParam::class.java)?.let { selected ->
                        when (it.resultCode) {
                            IntentParams.SearchSelectTypeParam.ShopResultCode -> {
                                if (selected.isNotEmpty()) {
                                    val shopId = selected.first().id
                                    mViewModel.selectShop.value = if (0 == shopId) null else shopId
                                    mBinding.tvCouponDepartment.text = selected.first().name

                                    mBinding.rvCouponList.requestRefresh()
                                }
                            }
                        }
                    }
                }
            }
        }

    private val mAdapter by lazy {
        CommonRecyclerAdapter<ItemCouponListBinding, CouponEntity>(
            R.layout.item_coupon_list,
            BR.item
        ) { mItemBinding, _, item ->
            mViewModel.isBatch.observe(this) {
                mItemBinding?.isBatch = it
            }
            mItemBinding?.cbDeviceRepairsSelect?.setOnCheckedChangeListener { _, isChecked ->
                if (1 == item.state) {
                    item.selected = isChecked
                    refreshSelectBatchNum()
                }
            }
            mItemBinding?.root?.setOnClickListener {
                if (true == mViewModel.isBatch.value) {
                    if (1 == item.state) {
                        item.selected = !item.selected
                        refreshSelectBatchNum()
                    }
                    return@setOnClickListener
                }
                startActivity(
                    Intent(
                        this@CouponManageActivity,
                        CouponDetailActivity::class.java
                    ).apply {
                        putExtras(IntentParams.CommonParams.pack(item.id))
                    })
            }
        }
    }

    override fun layoutId(): Int = R.layout.activity_coupon_manage

    override fun backBtn(): View = mBinding.barCouponManageTitle.getBackBtn()

    override fun onBackListener() {
        if (mViewModel.isBatch.value == true) {
            mViewModel.isBatch.value = false
            resetSelectBatchNum()
        } else
            super.onBackListener()
    }

    override fun initIntent() {
        super.initIntent()

        mViewModel.keyword = IntentParams.SearchParams.parseKeyWord(intent)
    }

    /**
     * 设置标题右侧按钮
     */
    private fun initRightBtn() {
        mBinding.barCouponManageTitle.getRightArea().removeAllViews()
        mBinding.barCouponManageTitle.getRightArea().run {
            val padding = DimensionUtils.dip2px(this@CouponManageActivity, 8f)
            addView(AppCompatImageButton(this@CouponManageActivity).apply {
                setImageDrawable(
                    BitmapUtils.tintDrawable(
                        ContextCompat.getDrawable(
                            this@CouponManageActivity,
                            R.mipmap.icon_search
                        ),
                        ContextCompat.getColor(this@CouponManageActivity, R.color.common_txt_color)
                    )
                )
                setBackgroundColor(Color.TRANSPARENT)
                setOnClickListener {
                    startActivity(
                        Intent(
                            this@CouponManageActivity,
                            SearchActivity::class.java
                        ).apply {
                            putExtra(SearchType.SearchType, SearchType.Coupon)
                        })
                }
            }, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

            if (UserPermissionUtils.hasSendCouponPermission()) {
                addView(SingleTapTextView(this@CouponManageActivity).apply {
                    setText(R.string.operate)
                    textSize = 14f
                    setTextColor(Color.WHITE)
                    val ph = DimensionUtils.dip2px(this@CouponManageActivity, 12f)
                    val pV = DimensionUtils.dip2px(this@CouponManageActivity, 4f)
                    setPadding(ph, pV, ph, pV)
                    setBackgroundResource(R.drawable.shape_sf0a258_r22)
                    setOnClickListener {
                        showDeviceOperateView()
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
    }

    /**
     * 显示设备管理界面
     */
    private fun AppCompatTextView.showDeviceOperateView() {
        val mPopupBinding =
            PopupCouponOperateManagerBinding.inflate(LayoutInflater.from(this@CouponManageActivity))
        val popupWindow = TranslucencePopupWindow(
            mPopupBinding.root,
            window,
            DimensionUtils.dip2px(this@CouponManageActivity, 110f)
        )

        mPopupBinding.tvCouponOperateIssue.setOnClickListener {
            popupWindow.dismiss()
            startActivity(
                Intent(
                    this@CouponManageActivity,
                    IssueCouponsActivity::class.java
                )
            )
        }
        mPopupBinding.tvCouponOperateBatchInvalid.setOnClickListener {
            popupWindow.dismiss()
            mViewModel.isBatch.value = true
        }
        popupWindow.showAsDropDown(
            this,
            -DimensionUtils.dip2px(this@CouponManageActivity, 16f),
            0
        )
    }

    override fun initEvent() {
        super.initEvent()

        // 状态监听
        mViewModel.curCouponStatus.observe(this) {
            mBinding.rvCouponList.requestRefresh()
        }

        LiveDataBus.with(BusEvents.COUPON_LIST_STATUS)?.observe(this) {
            mBinding.rvCouponList.requestRefresh()
            mViewModel.isBatch.value = false
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE
        initRightBtn()

        // 设备状态
        mBinding.indicatorCouponStatus.navigator = CommonNavigator(this).apply {

            adapter = object : CommonNavigatorAdapter() {
                override fun getCount(): Int = mViewModel.couponStatus.size

                override fun getTitleView(context: Context?, index: Int): IPagerTitleView {
                    return IndicatorPagerTitleView(context).apply {
                        normalColor = ContextCompat.getColor(
                            this@CouponManageActivity,
                            R.color.color_black_65
                        )
                        selectedColor = ContextCompat.getColor(
                            this@CouponManageActivity,
                            R.color.color_black_85
                        )
                        normalFontSize = 14f
                        selectFontSize = 14f
                        mViewModel.couponStatus[index].run {
                            num.observe(this@CouponManageActivity) { n ->
                                text = title + if (0 < n) " $n" else " 0"
                            }
                            setOnClickListener {
                                mViewModel.curCouponStatus.value = value
                                onPageSelected(index)
                                notifyDataSetChanged()
                            }
                        }
                    }
                }

                override fun getIndicator(context: Context?): IPagerIndicator {
                    return LinePagerIndicator(context).apply {
                        mode = LinePagerIndicator.MODE_EXACTLY
                        lineWidth = DimensionUtils.dip2px(this@CouponManageActivity, 20f).toFloat()
                        lineHeight = DimensionUtils.dip2px(this@CouponManageActivity, 3f).toFloat()
                        roundRadius =
                            DimensionUtils.dip2px(this@CouponManageActivity, 1.5f).toFloat()
                        setColors(
                            ContextCompat.getColor(
                                this@CouponManageActivity,
                                R.color.color_black_85
                            )
                        )
                    }
                }
            }
        }

        // 券列表
        mBinding.tvCouponType.setOnClickListener {
            CommonBottomSheetDialog.Builder(
                getString(R.string.coupon_type),
                mViewModel.couponTypeList
            ).apply {
                mustSelect = true
                onValueSureListener =
                    object :
                        CommonBottomSheetDialog.OnValueSureListener<SearchSelectParam> {
                        override fun onValue(data: SearchSelectParam?) {
                            mBinding.tvCouponType.text = data?.name
                            mViewModel.selectCouponType.value =
                                if (-1 == data?.id) null else data?.id

                            mBinding.rvCouponList.requestRefresh()
                        }
                    }
            }.build().show(supportFragmentManager)
        }

        // 店铺
        mBinding.tvCouponDepartment.setOnClickListener {
            startSearchSelect.launch(
                Intent(
                    this@CouponManageActivity,
                    SearchSelectRadioActivity::class.java
                ).apply {
                    putExtras(
                        IntentParams.SearchSelectTypeParam.pack(
                            IntentParams.SearchSelectTypeParam.SearchSelectTypeCouponShop,
                            mustSelect = true,
                            hasAll = true,
                            selectArr = intArrayOf(mViewModel.selectShop.value ?: 0)
                        )
                    )
                }
            )
        }

        // 设备
        mBinding.tvCouponCategory.setOnClickListener {
            MultiSelectBottomSheetDialog.Builder(
                getString(R.string.coupon_device_dialog_title),
                mViewModel.categoryList.value ?: listOf()
            ).apply {
                supportSingle = true
                onValueSureListener =
                    object :
                        MultiSelectBottomSheetDialog.OnValueSureListener<CategoryEntity> {
                        override fun onValue(
                            selectData: List<CategoryEntity>,
                            allSelectData: List<CategoryEntity>
                        ) {
                            selectData.firstOrNull()?.let { first ->
                                mViewModel.selectCategory.value =
                                    if (0 == first.id) null else first.id
                                mBinding.tvCouponCategory.text = first.name

                                mBinding.rvCouponList.requestRefresh()
                            }
                        }
                    }
            }.build().show(supportFragmentManager)
        }

        // 刷新
        mBinding.tvCouponListRefresh.setOnClickListener {
            mBinding.rvCouponList.requestRefresh()
        }

        // 券列表
        mBinding.rvCouponList.layoutManager = LinearLayoutManager(this)
        mBinding.rvCouponList.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            ).apply {
                ResourcesCompat.getDrawable(resources, R.drawable.divide_size8, null)?.let {
                    setDrawable(it)
                }
            })
        mBinding.rvCouponList.adapter = mAdapter
        mBinding.rvCouponList.requestData =
            object : CommonRefreshRecyclerView.OnRequestDataListener<CouponEntity>() {
                override fun requestData(
                    isRefresh: Boolean,
                    page: Int,
                    pageSize: Int,
                    callBack: (responseList: ResponseList<out CouponEntity>?) -> Unit
                ) {
                    mViewModel.requestCouponList(page, pageSize, callBack)
                }
            }

        mBinding.cbCouponManageAll.setOnCheckClickListener {
            if (!mBinding.cbCouponManageAll.isChecked) {
                selectAll()
            } else {
                resetSelectBatchNum()
            }
            true
        }
        mBinding.btnCouponManagerCancellation.setOnClickListener {
            CommonDialog.Builder("是否作废").apply {
                negativeTxt = StringUtils.getString(R.string.cancel)
                setPositiveButton(StringUtils.getString(R.string.cancellation)) {
                    mViewModel.abandonCoupon(this@CouponManageActivity, mAdapter.list)
                }
            }.build().show(supportFragmentManager)
        }
    }

    private fun selectAll() {
        mAdapter.list.forEach {
            it.selected = 1 == it.state
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
    }
}
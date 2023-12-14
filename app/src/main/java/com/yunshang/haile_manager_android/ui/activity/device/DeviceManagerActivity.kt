package com.yunshang.haile_manager_android.ui.activity.device

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.TypefaceSpan
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
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.StringUtils
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.business.vm.DeviceManagerViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.arguments.SearchSelectParam
import com.yunshang.haile_manager_android.data.common.DeviceCategory
import com.yunshang.haile_manager_android.data.common.SearchType
import com.yunshang.haile_manager_android.data.entities.CategoryEntity
import com.yunshang.haile_manager_android.data.entities.DeviceEntity
import com.yunshang.haile_manager_android.data.rule.DeviceIndicatorEntity
import com.yunshang.haile_manager_android.databinding.ActivityDeviceManagerBinding
import com.yunshang.haile_manager_android.databinding.ItemDeviceListBinding
import com.yunshang.haile_manager_android.databinding.ItemDeviceManagerErrorStatusBinding
import com.yunshang.haile_manager_android.databinding.PopupDeviceOperateManagerBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.activity.common.SearchActivity
import com.yunshang.haile_manager_android.ui.activity.common.SearchSelectRadioActivity
import com.yunshang.haile_manager_android.ui.activity.common.ShopPositionSelectorActivity
import com.yunshang.haile_manager_android.ui.activity.personal.IncomeCalendarActivity
import com.yunshang.haile_manager_android.ui.view.IndicatorPagerTitleView
import com.yunshang.haile_manager_android.ui.view.TranslucencePopupWindow
import com.yunshang.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter
import com.yunshang.haile_manager_android.ui.view.adapter.ViewBindingAdapter.visibility
import com.yunshang.haile_manager_android.ui.view.dialog.CommonBottomSheetDialog
import com.yunshang.haile_manager_android.ui.view.dialog.CommonDialog
import com.yunshang.haile_manager_android.ui.view.refresh.CommonRefreshRecyclerView
import com.yunshang.haile_manager_android.utils.BitmapUtils
import com.yunshang.haile_manager_android.utils.NumberUtils
import com.yunshang.haile_manager_android.utils.UserPermissionUtils
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator


class DeviceManagerActivity :
    BaseBusinessActivity<ActivityDeviceManagerBinding, DeviceManagerViewModel>(
        DeviceManagerViewModel::class.java,
        BR.vm
    ) {

    // 搜索选择界面
    private val startSearchSelect =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            it.data?.let { intent ->
                when (it.resultCode) {
                    IntentParams.ShopPositionSelectorParams.ShopPositionSelectorResultCode -> {
                        mViewModel.selectDepartment.value =
                            IntentParams.ShopPositionSelectorParams.parseSelectList(intent)
                    }
                    IntentParams.SearchSelectTypeParam.DeviceModelResultCode -> {
                        intent.getStringExtra(IntentParams.SearchSelectTypeParam.ResultData)
                            ?.let { json ->
                                GsonUtils.json2List(json, SearchSelectParam::class.java)
                                    ?.let { selected ->
                                        if (selected.isNotEmpty()) {
                                            mViewModel.selectDeviceModel.value = selected[0]
                                        } else {
                                            mViewModel.selectDeviceModel.value = null
                                        }
                                    }
                            }
                    }
                }
            }
        }

    // 跳转转移界面
    private val selectTransferNext =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                IntentParams.ShopPositionSelectorParams.ShopPositionSelectorResultCode -> {
                    result.data?.let { intent ->
                        val positionId = IntentParams.ShopPositionSelectorParams.parseSelectList(
                            intent
                        )?.firstOrNull()?.positionList?.firstOrNull()?.id

                        val selectItems = mAdapter.list.filter { item -> item.selected }
                        // 判断是否有关联设备
                        if (true) {
                            transferDevices(positionId, selectItems)
                        } else {
                            CommonDialog.Builder("该设备存在关联设备，转移操作，会同步转移关联的设备。若不需要则请先解除关联").apply {
                                title = StringUtils.getString(R.string.tip)
                                negativeTxt = StringUtils.getString(R.string.cancel)
                                setPositiveButton(StringUtils.getString(R.string.sure)) {
                                    transferDevices(positionId, selectItems)
                                }
                            }.build().show(supportFragmentManager)
                        }
                    }
                }
            }
        }

    private fun transferDevices(
        positionId: Int?,
        selectItems: List<DeviceEntity>
    ) {
        mViewModel.transferDevice(positionId, selectItems.map { item -> item.id }) {
            mViewModel.isBatch.value = false
            mBinding.rvDeviceManagerList.requestRefresh()
        }
    }

    private val mAdapter by lazy {
        CommonRecyclerAdapter<ItemDeviceListBinding, DeviceEntity>(
            R.layout.item_device_list,
            BR.item
        ) { mItemBinding, _, item ->
            mViewModel.isBatch.observe(this) {
                mItemBinding?.isBatch = it
            }

            val title =
                StringUtils.getString(R.string.total_income)
            val value =
                StringUtils.getString(R.string.unit_money) + NumberUtils.keepTwoDecimals(item.income)
            val start = title.length + 1
            val end = title.length + 1 + value.length
            // 格式化总收益样式
            mItemBinding?.tvItemDeviceTotalIncome?.text =
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
                        AbsoluteSizeSpan(DimensionUtils.sp2px(18f, this@DeviceManagerActivity)),
                        TypefaceSpan("money")
                    ), start, end
                )
            mItemBinding?.tvItemDeviceTotalIncome?.setOnClickListener {
                if (!UserPermissionUtils.hasProfitCalendarPermission()) {
                    SToast.showToast(this@DeviceManagerActivity, "无收益日历的功能权限")
                    return@setOnClickListener
                }
                // 跳转到设备收益
                startActivity(
                    Intent(
                        this@DeviceManagerActivity,
                        IncomeCalendarActivity::class.java
                    ).apply {
                        putExtra(IncomeCalendarActivity.ProfitType, 2)
                        putExtra(IncomeCalendarActivity.ProfitSearchId, item.id)
                        putExtra(IncomeCalendarActivity.DeviceName, item.name)
                    })
            }

            mItemBinding?.cbDeviceRepairsSelect?.setOnCheckedChangeListener { _, isChecked ->
                item.selected = isChecked
                refreshSelectBatchNum()
            }
            // 进入详情
            mItemBinding?.root?.setOnClickListener {
                if (true == mViewModel.isBatch.value) {
                    item.selected = !item.selected
                    refreshSelectBatchNum()
                    return@setOnClickListener
                }

                if (UserPermissionUtils.hasDeviceInfoPermission()) {
                    // 设备详情
                    startActivity(
                        Intent(
                            this@DeviceManagerActivity,
                            DeviceDetailActivity::class.java
                        ).apply {
                            putExtra(DeviceDetailActivity.GoodsId, item.id)
                        }
                    )
                }
            }
        }
    }

    override fun layoutId(): Int = R.layout.activity_device_manager

    override fun backBtn(): View = mBinding.barDeviceTitle.getBackBtn()

    override fun onBackListener() {
        if (true == mViewModel.isBatch.value)
            mViewModel.isBatch.value = false
        else
            super.onBackListener()
    }

    override fun initIntent() {
        super.initIntent()
        mViewModel.searchKey.value = IntentParams.SearchParams.parseKeyWord(intent)
        IntentParams.DeviceManagerParams.parseShop(intent)?.let {
            mViewModel.selectDepartment.value = mutableListOf(it)
        }
        mViewModel.bigCategoryType = IntentParams.DeviceManagerParams.parseCategoryBigType(intent)
    }

    /**
     * 设置标题右侧按钮
     */
    private fun initRightBtn() {
        if (mViewModel.searchKey.value.isNullOrEmpty()) {
            mBinding.barDeviceTitle.getRightArea().run {
                val padding = DimensionUtils.dip2px(this@DeviceManagerActivity, 8f)
                addView(AppCompatImageButton(this@DeviceManagerActivity).apply {
                    setImageDrawable(
                        BitmapUtils.tintDrawable(
                            ContextCompat.getDrawable(
                                this@DeviceManagerActivity,
                                R.mipmap.icon_search
                            ),
                            ContextCompat.getColor(
                                this@DeviceManagerActivity,
                                R.color.common_txt_color
                            )
                        )
                    )
                    setBackgroundColor(Color.TRANSPARENT)
                    setOnClickListener {
                        startActivity(
                            Intent(
                                this@DeviceManagerActivity,
                                SearchActivity::class.java
                            ).apply {
                                putExtra(SearchType.SearchType, SearchType.Device)
                            })
                    }
                }, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

                addView(SingleTapTextView(this@DeviceManagerActivity).apply {
                    setText(R.string.operate)
                    textSize = 14f
                    setTextColor(Color.WHITE)
                    val ph = DimensionUtils.dip2px(this@DeviceManagerActivity, 12f)
                    val pV = DimensionUtils.dip2px(this@DeviceManagerActivity, 4f)
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
            PopupDeviceOperateManagerBinding.inflate(LayoutInflater.from(this@DeviceManagerActivity))
        val popupWindow = TranslucencePopupWindow(
            mPopupBinding.root,
            window,
            DimensionUtils.dip2px(this@DeviceManagerActivity, 110f)
        )

        mPopupBinding.tvDeviceOperateAdd.setOnClickListener {
            popupWindow.dismiss()
            startActivity(
                Intent(
                    this@DeviceManagerActivity,
                    DeviceCreateV2Activity::class.java
                )
            )
        }
        mPopupBinding.tvDeviceOperateUpdate.setOnClickListener {
            popupWindow.dismiss()
            startActivity(
                Intent(
                    this@DeviceManagerActivity,
                    DeviceBatchUpdateActivity::class.java
                )
            )
        }
        mPopupBinding.tvDeviceOperateStart.setOnClickListener {
            popupWindow.dismiss()
            startActivity(
                Intent(
                    this@DeviceManagerActivity,
                    DeviceBatchStartActivity::class.java
                )
            )
        }
        mPopupBinding.tvDeviceOperateTransfer.setOnClickListener {
            popupWindow.dismiss()
            mViewModel.isBatch.value = true
        }
        popupWindow.showAsDropDown(
            this,
            -DimensionUtils.dip2px(this@DeviceManagerActivity, 16f),
            0
        )
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE

        // 所属门店
        mBinding.tvDeviceCategoryDepartment.setOnClickListener {
            startSearchSelect.launch(
                Intent(
                    this@DeviceManagerActivity,
                    ShopPositionSelectorActivity::class.java
                ).apply {
                    putExtras(
                        IntentParams.ShopPositionSelectorParams.pack(
                            mustSelect = false,
                            selectList = mViewModel.selectDepartment.value
                        )
                    )
                }
            )
        }

        // 设备类型
        mBinding.tvDeviceCategoryCategory.setOnClickListener {
            mViewModel.categoryList.value?.let {
                showDeviceCategoryDialog(it)
            }
        }

        // 设备模型
        mBinding.tvDeviceCategoryModel.setOnClickListener {
            startSearchSelect.launch(
                Intent(
                    this@DeviceManagerActivity,
                    SearchSelectRadioActivity::class.java
                ).apply {
                    putExtras(
                        IntentParams.SearchSelectTypeParam.pack(
                            IntentParams.SearchSelectTypeParam.SearchSelectTypeDeviceModel,
                            mViewModel.selectDeviceCategory.value?.firstOrNull()?.id ?: -1,
                            mustSelect = false
                        )
                    )
                }
            )
        }

        // 网络状态
        mBinding.tvDeviceCategoryNetworkStatus.setOnClickListener {
            val deviceCategoryDialog =
                CommonBottomSheetDialog.Builder(
                    getString(R.string.network_status), arrayListOf(
                        SearchSelectParam(1, getString(R.string.online)),
                        SearchSelectParam(2, getString(R.string.offline)),
                        SearchSelectParam(4, getString(R.string.break_down)),
                    )
                ).apply {
                    mustSelect = false
                    onValueSureListener =
                        object :
                            CommonBottomSheetDialog.OnValueSureListener<SearchSelectParam> {
                            override fun onValue(data: SearchSelectParam?) {
                                mViewModel.selectNetworkStatus.value = data
                            }
                        }
                }
                    .build()
            deviceCategoryDialog.show(supportFragmentManager)
        }

        mBinding.tvDeviceCategoryDeviceStatus.setOnClickListener {
            CommonBottomSheetDialog.Builder(
                getString(R.string.device_status), arrayListOf(
                    SearchSelectParam(1, getString(R.string.enable)),
                    SearchSelectParam(2, getString(R.string.disEnable)),
                )
            ).apply {
                mustSelect = false
                selectData = mViewModel.selectDeviceStatus.value
                onValueSureListener =
                    object :
                        CommonBottomSheetDialog.OnValueSureListener<SearchSelectParam> {
                        override fun onValue(data: SearchSelectParam?) {
                            mViewModel.selectDeviceStatus.value = data
                        }
                    }
            }.build().show(supportFragmentManager)
        }

        // 设备状态
        mBinding.indicatorDeviceStatus.navigator = CommonNavigator(this).apply {

            adapter = object : CommonNavigatorAdapter() {
                override fun getCount(): Int = mViewModel.deviceStatus.size

                override fun getTitleView(context: Context?, index: Int): IPagerTitleView {
                    return IndicatorPagerTitleView(context).apply {
                        normalColor = ContextCompat.getColor(
                            this@DeviceManagerActivity,
                            R.color.color_black_65
                        )
                        selectedColor = ContextCompat.getColor(
                            this@DeviceManagerActivity,
                            R.color.color_black_85
                        )
                        normalFontSize = 14f
                        selectFontSize = 14f
                        mViewModel.deviceStatus[index].run {
                            num.observe(this@DeviceManagerActivity) { n ->
                                text = title + if (0 < n) " $n" else " 0"
                            }
                            setOnClickListener {
                                mViewModel.curWorkStatus.value = value
                                onPageSelected(index)
                                notifyDataSetChanged()
                            }
                        }
                    }
                }

                override fun getIndicator(context: Context?): IPagerIndicator {
                    return LinePagerIndicator(context).apply {
                        mode = LinePagerIndicator.MODE_EXACTLY
                        lineWidth =
                            DimensionUtils.dip2px(this@DeviceManagerActivity, 20f).toFloat()
                        lineHeight =
                            DimensionUtils.dip2px(this@DeviceManagerActivity, 3f).toFloat()
                        roundRadius =
                            DimensionUtils.dip2px(this@DeviceManagerActivity, 1.5f).toFloat()
                        setColors(
                            ContextCompat.getColor(
                                this@DeviceManagerActivity,
                                R.color.color_black_85
                            )
                        )
                    }
                }
            }
        }

        // 刷新
        mBinding.tvDeviceManagerListRefresh.setOnClickListener {
            mBinding.rvDeviceManagerList.requestRefresh()
        }

        mBinding.rvDeviceManagerList.layoutManager = LinearLayoutManager(this)
        mBinding.rvDeviceManagerList.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            ).apply {
                ResourcesCompat.getDrawable(resources, R.drawable.divide_size8, null)?.let {
                    setDrawable(it)
                }
            })

        mBinding.rvDeviceManagerList.listStatusImgResId = R.mipmap.icon_list_device_empty
        mBinding.rvDeviceManagerList.listStatusTxtResId = R.string.empty_device
        mBinding.rvDeviceManagerList.adapter = mAdapter
        mBinding.rvDeviceManagerList.requestData =
            object : CommonRefreshRecyclerView.OnRequestDataListener<DeviceEntity>() {
                override fun requestData(
                    isRefresh: Boolean,
                    page: Int,
                    pageSize: Int,
                    callBack: (responseList: ResponseList<out DeviceEntity>?) -> Unit
                ) {
                    if (UserPermissionUtils.hasDeviceListPermission()) {
                        mViewModel.requestDeviceList(page, pageSize, callBack)
                    }
                }
            }

        // 全选
        mBinding.cbDeviceManagerTransferAll.setOnCheckClickListener {
            if (!mBinding.cbDeviceManagerTransferAll.isChecked) {
                selectAll()
            } else {
                resetSelectBatchNum()
            }

            true
        }
        mBinding.btnDeviceManagerTransfer.setOnClickListener {
            // 设备转移
            selectTransferNext.launch(
                Intent(
                    this@DeviceManagerActivity,
                    ShopPositionSelectorActivity::class.java
                ).apply {
                    putExtras(
                        IntentParams.ShopPositionSelectorParams.pack(
                            canMultiSelect = false,
                            canSelectAll = false,
                            mustSelect = false,
                            title = "选择营业点"
                        )
                    )
                }
            )
        }
    }

    /**
     * 构建异常状态界面
     */
    private fun buildErrorStatus() {
        val list = mViewModel.errorStatus.filter { item ->
            // 出水故障、免费设备,饮水特有的
            val drinking = if (mViewModel.selectDeviceCategory.value?.any() { category ->
                    DeviceCategory.isDrinking(category.code)
                } != true) {
                item.value != 3 && item.value != 4
            } else true

            // 电磁阀异常,淋浴特有的
            val shower = if (mViewModel.selectDeviceCategory.value?.any { category ->
                    DeviceCategory.isShower(category.code)
                } != true) {
                item.value != 5
            } else {
                true
            }
            drinking && shower
        }

        mBinding.svDeviceErrorStatusList.visibility(list.isNotEmpty())
        mBinding.llDeviceErrorStatusList.buildChild<ItemDeviceManagerErrorStatusBinding, DeviceIndicatorEntity<Int>>(
            list
        ) { _, childBinding, data ->
            data.num.observe(this@DeviceManagerActivity) {
                childBinding.tvDeviceManagerErrorStatus.text =
                    data.title + if (it > 0) " $it" else " 0"
            }

            mViewModel.selectErrorStatus.observe(this) {
                childBinding.tvDeviceManagerErrorStatus.setBackgroundResource(if (data.value == it) R.drawable.shape_device_manager_error_status_selected_bg else R.drawable.shape_device_manager_error_status_bg)
            }

            childBinding.tvDeviceManagerErrorStatus.setOnClickListener {
                if (mViewModel.selectErrorStatus.value == data.value)
                    mViewModel.selectErrorStatus.value = null
                else
                    mViewModel.selectErrorStatus.value = data.value
            }
        }
    }

    override fun initEvent() {
        super.initEvent()

        mSharedViewModel.hasDeviceAddPermission.observe(this) {
            if (it)
                initRightBtn()
        }

        mViewModel.isBatch.observe(this) {
            if (!it) resetSelectBatchNum()
        }

        // 选择店铺
        mViewModel.selectDepartment.observe(this) {
            mBinding.tvDeviceCategoryDepartment.text = when (val count: Int = (it?.size ?: 0)) {
                0 -> ""
                1 -> it?.firstOrNull()?.name ?: ""
                else -> "已选中${count}个门店"
            }
            mBinding.rvDeviceManagerList.requestRefresh()
        }

        // 选择设备类型
        mViewModel.selectDeviceCategory.observe(this) {
            buildErrorStatus()

            mBinding.tvDeviceCategoryCategory.text =
                if (1 == (it?.size ?: 0)) it?.firstOrNull()?.name ?: "" else ""

            // 切换设备类型后，重置异常状态
            if ((it?.any() { category -> DeviceCategory.isDrinking(category.code) } == false
                        && (3 == mViewModel.selectErrorStatus.value || 4 == mViewModel.selectErrorStatus.value))
                || it?.any() { category -> DeviceCategory.isShower(category.code) } == false
                && 5 == mViewModel.selectErrorStatus.value) {
                mViewModel.selectErrorStatus.value = null
            }

            mBinding.rvDeviceManagerList.requestRefresh()
        }

        // 选择设备模型
        mViewModel.selectDeviceModel.observe(this) {
            mBinding.tvDeviceCategoryModel.text = it?.name ?: ""
            mBinding.rvDeviceManagerList.requestRefresh()
        }

        // 选择设备模型
        mViewModel.selectNetworkStatus.observe(this) {
            mBinding.tvDeviceCategoryNetworkStatus.text = it?.name ?: ""
            mBinding.rvDeviceManagerList.requestRefresh()
        }

        // 选择设备状态
        mViewModel.selectDeviceStatus.observe(this) {
            mBinding.tvDeviceCategoryDeviceStatus.text = it?.name ?: ""
            mBinding.rvDeviceManagerList.requestRefresh()
        }

        // 切换工作状态
        mViewModel.curWorkStatus.observe(this) {
            if (-1 == mViewModel.bigCategoryType || null != mViewModel.selectDeviceCategory.value) {
                mBinding.rvDeviceManagerList.requestRefresh()
            }
        }

        //切换异常状态
        mViewModel.selectErrorStatus.observe(this) {
            mBinding.rvDeviceManagerList.requestRefresh()
        }

        // 监听刷新
        LiveDataBus.with(BusEvents.DEVICE_LIST_STATUS)?.observe(this) {
            mBinding.rvDeviceManagerList.requestRefresh()
        }

        // 监听删除
        LiveDataBus.with(BusEvents.DEVICE_LIST_ITEM_DELETE_STATUS, Int::class.java)
            ?.observe(this) {
                mViewModel.requestData(4)
                mAdapter.deleteItem { item -> item.id == it }
            }
    }

    /**
     * 显示设备类型弹窗
     */
    private fun showDeviceCategoryDialog(categoryEntities: List<CategoryEntity>) {
        val deviceCategoryDialog =
            CommonBottomSheetDialog.Builder(
                getString(R.string.device_category),
                categoryEntities
            )
                .apply {
                    mustSelect = false
                    onValueSureListener =
                        object :
                            CommonBottomSheetDialog.OnValueSureListener<CategoryEntity> {
                            override fun onValue(data: CategoryEntity?) {
                                mViewModel.selectDeviceCategory.value = data?.let { listOf(data) }
                                mViewModel.selectDeviceModel.value = null
                            }
                        }
                }
                .build()
        deviceCategoryDialog.show(supportFragmentManager)
    }


    private fun selectAll() {
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
        mViewModel.requestData(1)
    }
}
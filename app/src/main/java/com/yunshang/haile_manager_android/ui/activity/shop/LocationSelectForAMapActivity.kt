package com.yunshang.haile_manager_android.ui.activity.shop

import android.Manifest
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode
import com.amap.api.maps2d.AMap
import com.amap.api.maps2d.CameraUpdateFactory
import com.amap.api.maps2d.LocationSource
import com.amap.api.maps2d.model.*
import com.lsy.framelib.utils.DimensionUtils
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.LocationSelectViewModel
import com.yunshang.haile_manager_android.business.vm.SearchSelectViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.arguments.PoiResultData
import com.yunshang.haile_manager_android.databinding.ActivityLocationSelectBinding
import com.yunshang.haile_manager_android.databinding.ItemLocationSelectBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.activity.common.SearchSelectActivity
import com.yunshang.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter
import com.yunshang.haile_manager_android.utils.StringUtils
import timber.log.Timber


class LocationSelectForAMapActivity :
    BaseBusinessActivity<ActivityLocationSelectBinding, LocationSelectViewModel>(
        LocationSelectViewModel::class.java,
        BR.vm
    ), LocationSource {

    private val permissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.READ_PHONE_STATE,
    )

    // 蓝点
    private var marker: Marker? = null

    // 范围
    private var range: Circle? = null

    // 当前定位经纬度
    private var curLatLng: LatLng? = null

    // 定位
    private val locationClient: AMapLocationClient by lazy {
        AMapLocationClient(this).apply {
            //设置定位监听
            setLocationListener {
                if (it.errorCode == 0) {
                    curLatLng = LatLng(it.latitude, it.longitude)
                    // 移动视图
                    map.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(curLatLng, 14f)
                    )
                    // 蓝点
                    if (null == marker) {
                        marker = map.addMarker(
                            MarkerOptions().position(curLatLng).anchor(0.5f, 0.5f)
                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_amap_anchor))
                        )
                    } else {
                        marker?.position = curLatLng
                    }
                    // 精度圈
                    if (null == range) {
                        range = map.addCircle(
                            CircleOptions().center(curLatLng).radius(1000.0)
                                .fillColor(Color.parseColor("#88F0A258")).strokeColor(
                                    ResourcesCompat.getColor(
                                        resources,
                                        R.color.colorPrimary,
                                        null
                                    )
                                ).strokeWidth(
                                    DimensionUtils.dip2px(
                                        this@LocationSelectForAMapActivity,
                                        0.5f
                                    ).toFloat()
                                )
                        )
                    } else {
                        range?.center = curLatLng
                    }
                    // 搜索周边
                    mViewModel.searchNearbyOfAMap(
                        this@LocationSelectForAMapActivity,
                        it.latitude,
                        it.longitude
                    )
                } else {
                    Timber.d("定位失败，${it.errorCode}-${it.errorInfo}")
                }
            }
            //设置定位参数
            setLocationOption(AMapLocationClientOption().apply {
                //设置为高精度定位模式
                locationMode = AMapLocationMode.Hight_Accuracy
                isOnceLocation = true
            })
        }
    }

    // 权限
    private val requestMultiplePermission =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result: Map<String, Boolean> ->

            var isAllow = true
            for (permission in permissions) {
                if (true != result[permission]) {
                    isAllow = false
                    break
                }
            }
            if (isAllow) {
                initMap()
                locationClient.startLocation()
            }
        }

    // 搜索界面
    private val startSearchSelect =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                setResult(ShopCreateAndUpdateActivity.LocationResultCode, it.data)
                finish()
            }
        }

    private lateinit var map: AMap

    private val mAdapter: CommonRecyclerAdapter<ItemLocationSelectBinding, PoiResultData> by lazy {
        CommonRecyclerAdapter(
            R.layout.item_location_select,
            BR.item
        ) { mBinding, pos, item ->
            mBinding?.ivItemLocationSelectPoiIcon?.setImageResource(
                if (0 == pos) R.mipmap.icon_location_cur else R.mipmap.icon_location_poi
            )
            mBinding?.tvItemLocationSelectPoiDistance?.text = StringUtils.friendJuli(item.distance)

            mBinding?.root?.setOnClickListener {
                setResult(ShopCreateAndUpdateActivity.LocationResultCode, Intent().apply {
                    putExtra(
                        ShopCreateAndUpdateActivity.LocationResultData,
                        GsonUtils.any2Json(item)
                    )
                })
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AMapLocationClient.updatePrivacyShow(this, true, true)
        AMapLocationClient.updatePrivacyAgree(this, true)

        super.onCreate(savedInstanceState)
        mBinding.mapLocationSelect.onCreate(savedInstanceState)

        requestMultiplePermission.launch(permissions)
    }

    override fun onDestroy() {
        mBinding.mapLocationSelect.onDestroy()
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        mBinding.mapLocationSelect.onResume()
    }

    override fun onPause() {
        super.onPause()
        mBinding.mapLocationSelect.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mBinding.mapLocationSelect.onSaveInstanceState(outState)
    }

    override fun layoutId(): Int = R.layout.activity_location_select

    override fun backBtn(): View = mBinding.locationSelectTitleBar.getBackBtn()

    override fun initIntent() {
        super.initIntent()

        IntentParams.LocationSelectParams.parseCity(intent)?.let {
            mViewModel.cityCode = it
        }
        IntentParams.LocationSelectParams.parseShopTypeName(intent)?.let {
            mViewModel.shopTypeName = it
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE

        mBinding.tvSearchContent.setOnClickListener {
            startSearchSelect.launch(
                Intent(
                    this@LocationSelectForAMapActivity,
                    SearchSelectActivity::class.java
                ).apply {
                    putExtra(SearchSelectViewModel.SEARCH_TYPE, SearchSelectViewModel.LOCATION)
                    putExtra(SearchSelectViewModel.CityCode, mViewModel.cityCode)
                })
        }

        mBinding.rvLocationSelectList.layoutManager =
            LinearLayoutManager(this@LocationSelectForAMapActivity)
        mBinding.rvLocationSelectList.adapter = mAdapter
    }

    /**
     * 初始化地图
     */
    private fun initMap() {
        map = mBinding.mapLocationSelect.map
        // 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        map.isMyLocationEnabled = true
        // 布局
        map.uiSettings.apply {
            isZoomControlsEnabled = false //缩放按钮
            isMyLocationButtonEnabled = true//定位按钮
            isCompassEnabled = false //指南针
            isScaleControlsEnabled = false//比例尺
        }
        // 定位按钮事件
        map.setLocationSource(this@LocationSelectForAMapActivity)
        //镜头
        map.moveCamera(CameraUpdateFactory.zoomTo(17f))

        val myLocationStyle = MyLocationStyle()
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE)
        map.setMyLocationStyle(myLocationStyle)
    }

    override fun initEvent() {
        super.initEvent()

        mViewModel.poiItemList.observe(this) {
            mAdapter.refreshList(it, true)
        }
    }

    override fun initData() {
    }

    override fun activate(listener: LocationSource.OnLocationChangedListener?) {
        locationClient.startLocation()
    }

    override fun deactivate() {
        locationClient.stopLocation()
        locationClient.onDestroy()
    }
}
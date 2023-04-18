package com.shangyun.haile_manager_android.ui.activity.shop

import android.Manifest
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.res.ResourcesCompat
import com.amap.api.maps2d.AMap
import com.amap.api.maps2d.MapView
import com.amap.api.maps2d.MapsInitializer
import com.amap.api.maps2d.model.BitmapDescriptor
import com.amap.api.maps2d.model.BitmapDescriptorFactory
import com.amap.api.maps2d.model.MyLocationStyle
import com.lsy.framelib.utils.DimensionUtils
import com.lsy.framelib.utils.gson.GsonUtils
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.vm.LocationSelectViewModel
import com.shangyun.haile_manager_android.business.vm.SearchSelectViewModel
import com.shangyun.haile_manager_android.data.entities.SchoolSelectEntity
import com.shangyun.haile_manager_android.databinding.ActivityLocationSelectBinding
import com.shangyun.haile_manager_android.ui.activity.BaseBusinessActivity

class LocationSelectActivity :
    BaseBusinessActivity<ActivityLocationSelectBinding, LocationSelectViewModel>() {
    private val mLocationSelectViewModel by lazy {
        getActivityViewModelProvider(this)[LocationSelectViewModel::class.java]
    }

    private val permissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
    )

    // 权限
    private val requestMultiplePermission by lazy {
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
            }
        }
    }

    // 搜索界面
    private val startSearchSelect =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                setResult(ShopCreateActivity.LocationResultCode, it.data)
                finish()
            }
        }

    private lateinit var map: AMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding.mapLocationSelect.onCreate(savedInstanceState)
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

    override fun getVM(): LocationSelectViewModel = mLocationSelectViewModel

    override fun backBtn(): View = mBinding.locationSelectTitleBar.getBackBtn()


    override fun initView() {
        window.statusBarColor = Color.WHITE

        mBinding.tvSearchContent.setOnClickListener {
            startSearchSelect.launch(
                Intent(
                    this@LocationSelectActivity,
                    SearchSelectActivity::class.java
                ).apply {
                    putExtra(SearchSelectViewModel.SEARCH_TYPE, SearchSelectViewModel.LOCATION)
                })
        }

        requestMultiplePermission.launch(permissions)
    }

    private fun initMap() {
        map = mBinding.mapLocationSelect.map.apply {
            setMyLocationStyle(MyLocationStyle().apply {
                setMyLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE)
                myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.amap_anchor))
                showMyLocation(true)
                strokeColor(ResourcesCompat.getColor(resources, R.color.colorPrimary, null))
                strokeWidth(DimensionUtils.dip2px(this@LocationSelectActivity, 0.5f).toFloat())
                radiusFillColor(Color.parseColor("#88F0A258"))
            })
            // 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
            isMyLocationEnabled = true
            uiSettings.apply {
                isZoomControlsEnabled = false //缩放按钮
                isMyLocationButtonEnabled = true//定位按钮
                isCompassEnabled = false //指南针
                isScaleControlsEnabled = false//比例尺
            }
            //定位监听
            setOnMyLocationChangeListener {
                it.altitude
            }
        }
    }

    override fun initData() {
        mBinding.vm = mLocationSelectViewModel
    }
}
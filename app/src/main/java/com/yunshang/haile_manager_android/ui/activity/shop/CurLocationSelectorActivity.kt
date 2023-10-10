package com.yunshang.haile_manager_android.ui.activity.shop

import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.lsy.framelib.utils.SystemPermissionHelper
import com.lsy.framelib.utils.gson.GsonUtils
import com.tencent.map.geolocation.TencentLocation
import com.tencent.map.geolocation.TencentLocationListener
import com.tencent.map.geolocation.TencentLocationManager
import com.tencent.tencentmap.mapsdk.maps.CameraUpdateFactory
import com.tencent.tencentmap.mapsdk.maps.TencentMap
import com.tencent.tencentmap.mapsdk.maps.TencentMap.OnCameraChangeListener
import com.tencent.tencentmap.mapsdk.maps.TencentMapInitializer
import com.tencent.tencentmap.mapsdk.maps.model.CameraPosition
import com.tencent.tencentmap.mapsdk.maps.model.LatLng
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.CurLocationSelectorViewModel
import com.yunshang.haile_manager_android.business.vm.SearchSelectViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.arguments.PoiResultData
import com.yunshang.haile_manager_android.databinding.ActivityCurLocationSelectorBinding
import com.yunshang.haile_manager_android.databinding.ItemLocationSelectBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.activity.common.SearchSelectActivity
import com.yunshang.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter
import com.yunshang.haile_manager_android.utils.StringUtils
import timber.log.Timber

class CurLocationSelectorActivity :
    BaseBusinessActivity<ActivityCurLocationSelectorBinding, CurLocationSelectorViewModel>(
        CurLocationSelectorViewModel::class.java, BR.vm
    ), TencentLocationListener {

    private val permissions = SystemPermissionHelper.locationPermissions()
        .plus(SystemPermissionHelper.readWritePermissions()).plus(
            SystemPermissionHelper.phoneStatePermissions()
        )

    private val mLocationManager by lazy { TencentLocationManager.getInstance(this) }

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
            }
        }

    // 搜索界面
    private val startSearchSelect =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                setResult(IntentParams.LocationParams.LocationResultCode, it.data)
                finish()
            }
        }

    private lateinit var map: TencentMap

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
                setResult(IntentParams.LocationParams.LocationResultCode, Intent().apply {
                    putExtras(IntentParams.LocationParams.pack(item))
                })
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        TencentLocationManager.setUserAgreePrivacy(true)
        TencentMapInitializer.setAgreePrivacy(true)

        super.onCreate(savedInstanceState)
        requestMultiplePermission.launch(permissions)
    }

    override fun onDestroy() {
        mBinding.tmapLocationSelect.onDestroy()
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        mBinding.tmapLocationSelect.onResume()
    }

    override fun onPause() {
        super.onPause()
        mBinding.tmapLocationSelect.onPause()
    }

    override fun layoutId(): Int = R.layout.activity_cur_location_selector

    override fun backBtn(): View = mBinding.locationSelectTitleBar.getBackBtn()


    override fun initView() {
        window.statusBarColor = Color.WHITE

        mBinding.tvSearchContent.setOnClickListener {
//            startSearchSelect.launch(
//                Intent(
//                    this@CurLocationSelectorActivity,
//                    SearchSelectActivity::class.java
//                ).apply {
//                    putExtra(SearchSelectViewModel.SEARCH_TYPE, SearchSelectViewModel.LOCATION)
//                    putExtra(SearchSelectViewModel.CityCode, mViewModel.cityCode)
//                })
        }

        mBinding.rvLocationSelectList.layoutManager =
            LinearLayoutManager(this@CurLocationSelectorActivity)
        mBinding.rvLocationSelectList.adapter = mAdapter
    }

    /**
     * 初始化地图
     */
    private fun initMap() {
        map = mBinding.tmapLocationSelect.map

        map.addOnMapLoadedCallback {}
        map.mapType = TencentMap.MAP_TYPE_NORMAL
        map.moveCamera(CameraUpdateFactory.zoomTo(15f))
        // ui控件
        map.uiSettings.apply {
            setLogoScale(0.7f)
            isScaleViewEnabled = false
            isCompassEnabled = false
        }
        map.setOnCameraChangeListener(object : OnCameraChangeListener {
            override fun onCameraChange(cameraPosition: CameraPosition?) {
            }

            override fun onCameraChangeFinished(cameraPosition: CameraPosition?) {
                Timber.e("${cameraPosition?.target?.latitude},${cameraPosition?.target?.longitude}")
                cameraPosition?.target?.let {
                    mViewModel.parseLatLngAndNearBy(this@CurLocationSelectorActivity, LatLng(it.latitude,it.longitude))
                }
            }
        })
    }

    override fun initEvent() {
        super.initEvent()

        mViewModel.poiItemList.observe(this) {
            mAdapter.refreshList(it, true)
        }
    }

    override fun initData() {
        //开启定位
        when (mLocationManager.requestSingleFreshLocation(null, this, Looper.myLooper())) {
            1 -> Timber.e("定位失败，设备缺少使用腾讯定位服务需要的基本条件")
            2 -> Timber.e("定位失败，manifest 中配置的 key 不正确")
            3 -> Timber.e("定位失败，自动加载libtencentloc.so失败")
            4 -> Timber.e("定位失败，隐私未权限")
            else -> {}
        }
    }

    override fun onLocationChanged(tencentLocation: TencentLocation, i: Int, s: String?) {
        if (i == TencentLocation.ERROR_OK) {
            mViewModel.startParse = true
            map.moveCamera(
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition(
                        LatLng(tencentLocation.latitude,tencentLocation.longitude),
                        15f,
                        0f,
                        0f
                    )
                )
            )
        }
    }

    override fun onStatusUpdate(p0: String?, p1: Int, p2: String?) {
    }
}
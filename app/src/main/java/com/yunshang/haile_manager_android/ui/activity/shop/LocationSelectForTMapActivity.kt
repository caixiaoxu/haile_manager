package com.yunshang.haile_manager_android.ui.activity.shop

import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.lsy.framelib.utils.SystemPermissionHelper
import com.tencent.map.geolocation.TencentLocation
import com.tencent.map.geolocation.TencentLocationListener
import com.tencent.map.geolocation.TencentLocationManager
import com.tencent.tencentmap.mapsdk.maps.CameraUpdateFactory
import com.tencent.tencentmap.mapsdk.maps.LocationSource
import com.tencent.tencentmap.mapsdk.maps.TencentMap
import com.tencent.tencentmap.mapsdk.maps.TencentMapInitializer
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


class LocationSelectForTMapActivity :
    BaseBusinessActivity<ActivityLocationSelectBinding, LocationSelectViewModel>(
        LocationSelectViewModel::class.java,
        BR.vm
    ), LocationSource, TencentLocationListener {

    private val permissions = SystemPermissionHelper.locationPermissions()
        .plus(SystemPermissionHelper.readWritePermissions()).plus(
            SystemPermissionHelper.phoneStatePermissions()
        )

    private val mHandler = Handler(Looper.getMainLooper()) {
        (it.obj as? Location)?.let { location ->
            mViewModel.searchNearbyOfTMap(
                this@LocationSelectForTMapActivity,
                location.latitude,
                location.longitude
            )
        }
        false
    }

    private var mLocationChangedListener: LocationSource.OnLocationChangedListener? = null

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
                    IntentParams.LocationParams.pack(item)
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
                    this@LocationSelectForTMapActivity,
                    SearchSelectActivity::class.java
                ).apply {
                    putExtra(SearchSelectViewModel.SEARCH_TYPE, SearchSelectViewModel.LOCATION)
                    putExtra(SearchSelectViewModel.CityCode, mViewModel.cityCode)
                })
        }

        mBinding.rvLocationSelectList.layoutManager =
            LinearLayoutManager(this@LocationSelectForTMapActivity)
        mBinding.rvLocationSelectList.adapter = mAdapter
    }

    /**
     * 初始化地图
     */
    private fun initMap() {
        map = mBinding.tmapLocationSelect.map
        // 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        map.isMyLocationEnabled = true

        map.addOnMapLoadedCallback {}
        map.mapType = TencentMap.MAP_TYPE_NORMAL
        map.moveCamera(CameraUpdateFactory.zoomTo(17f))
        // ui控件
        map.uiSettings.apply {
            setLogoScale(0.7f)
            isScaleViewEnabled = false
            isCompassEnabled = false
        }

        map.setOnMyLocationChangeListener() {
            mHandler.removeMessages(0)
            mHandler.sendMessageDelayed(Message.obtain().apply {
                what = 0
                obj = it
            }, 500)
        }
        map.setLocationSource(this)
        map.setMyLocationClickListener { latLng -> true }
    }

    override fun initEvent() {
        super.initEvent()

        mViewModel.poiItemList.observe(this) {
            mAdapter.refreshList(it, true)
        }
    }

    override fun initData() {
    }

    override fun activate(onLocationChangedListener: LocationSource.OnLocationChangedListener?) {
        //这里我们将地图返回的位置监听保存为当前 Activity 的成员变量
        mLocationChangedListener = onLocationChangedListener
        //开启定位
        val err: Int = mLocationManager.requestSingleFreshLocation(
            null, this, Looper.myLooper()
        )
        when (err) {
            1 -> Timber.e("定位失败，设备缺少使用腾讯定位服务需要的基本条件")
            2 -> Timber.e("定位失败，manifest 中配置的 key 不正确")
            3 -> Timber.e("定位失败，自动加载libtencentloc.so失败")
            4 -> Timber.e("定位失败，隐私未权限")
            else -> {}
        }
    }

    override fun deactivate() {
        //当不需要展示定位点时，需要停止定位并释放相关资源
        if (null != mLocationManager) {
            mLocationManager.removeUpdates(this)
        }
        mLocationChangedListener = null
    }

    override fun onLocationChanged(tencentLocation: TencentLocation, i: Int, s: String?) {
        if (i == TencentLocation.ERROR_OK && mLocationChangedListener != null) {
            mViewModel.curLocation = PoiResultData(
                tencentLocation.name,
                tencentLocation.address,
                tencentLocation.latitude,
                tencentLocation.longitude, 0.0
            )
            val location = Location(tencentLocation.provider)
            //设置经纬度
            location.latitude = tencentLocation.latitude
            location.longitude = tencentLocation.longitude
            //设置精度，这个值会被设置为定位点上表示精度的圆形半径
            location.accuracy = tencentLocation.accuracy
            //设置定位标的旋转角度，注意 tencentLocation.getBearing() 只有在 gps 时才有可能获取
            location.bearing = tencentLocation.bearing
            //将位置信息返回给地图
            mLocationChangedListener!!.onLocationChanged(location)
        }
    }

    override fun onStatusUpdate(p0: String?, p1: Int, p2: String?) {
    }
}
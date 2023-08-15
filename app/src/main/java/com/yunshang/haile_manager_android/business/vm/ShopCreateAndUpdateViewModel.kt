package com.yunshang.haile_manager_android.business.vm

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.apiService.ShopService
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.data.arguments.BusinessHourEntity
import com.yunshang.haile_manager_android.data.arguments.BusinessHourParams
import com.yunshang.haile_manager_android.data.arguments.ShopCreateParam
import com.yunshang.haile_manager_android.data.entities.SchoolSelectEntity
import com.yunshang.haile_manager_android.data.entities.ShopBusinessTypeEntity
import com.yunshang.haile_manager_android.data.entities.ShopDetailEntity
import com.yunshang.haile_manager_android.data.entities.ShopTypeEntity
import com.yunshang.haile_manager_android.data.model.ApiRepository
import com.yunshang.haile_manager_android.utils.StringUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Title :
 * Author: Lsy
 * Date: 2023/4/10 11:19
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class ShopCreateAndUpdateViewModel : BaseViewModel() {
    private val mRepo = ApiRepository.apiClient(ShopService::class.java)

    // 店铺数据
    val createAndUpdateEntity: MutableLiveData<ShopCreateParam> =
        MutableLiveData(ShopCreateParam())

    // 门店类型列表
    val shopTypeList: MutableLiveData<List<ShopTypeEntity>> = MutableLiveData()

    // 门店业务列表
    val shopBusinessTypeList: MutableLiveData<List<ShopBusinessTypeEntity>> = MutableLiveData()

    // 门店类型
    val shopTypeValue: MutableLiveData<ShopTypeEntity> = MutableLiveData()

    // 学校名称
    val schoolNameValue: MutableLiveData<String> = MutableLiveData()

    // 小区名
    var cityName = ""

    // 所在区域
    val areaValue: MutableLiveData<String> = MutableLiveData()

    // 小区
    val mansionValue: MutableLiveData<String> = MutableLiveData()

    // 详细地址
    val addressValue: MutableLiveData<String> = MutableLiveData()

    // 营业时间
    val workTimeValue: MutableLiveData<String> = MutableLiveData()

    // 业务类型
    val businessTypeValue: MutableLiveData<String> = MutableLiveData()

    /**
     * 初始化数据
     */
    fun requestData() {
        launch({
            shopTypeList.postValue(ApiRepository.dealApiResult(mRepo.shopTypeList()))
            shopBusinessTypeList.postValue(ApiRepository.dealApiResult(mRepo.shopBusinessType()))
        })
    }

    /**
     * 切换店铺类型
     */
    fun changeShopType(data: ShopTypeEntity?) {
        if (null != data && -1 != data.type && data.type != shopTypeValue.value?.type) {
            shopTypeValue.postValue(data)
            createAndUpdateEntity.value?.let {
                it.shopType = data.type
            }
            changeSchool(null)
        }
    }

    /**
     * 切换学校
     */
    fun changeSchool(school: SchoolSelectEntity?) {
        // 学校id
        createAndUpdateEntity.value?.schoolId = school?.id ?: -1
        // 学校名
        createAndUpdateEntity.value?.schoolName = school?.name ?: ""
        schoolNameValue.value = school?.name ?: ""
        createAndUpdateEntity.value?.lat = school?.lat
        createAndUpdateEntity.value?.lng = school?.lng
        // 省市区
        changeArea(
            school?.provinceId ?: -1,
            school?.provinceName,
            school?.cityId ?: -1,
            school?.cityName,
            school?.districtId ?: -1,
            school?.districtName
        )
        changeAddress(school?.address)
    }

    /**
     * 切换地区，省市区
     */
    fun changeArea(
        provinceId: Int,
        provinceName: String?,
        cityId: Int,
        cityName: String?,
        districtId: Int,
        districtName: String?
    ) {
        createAndUpdateEntity.value?.provinceId = provinceId
        createAndUpdateEntity.value?.cityId = cityId
        createAndUpdateEntity.value?.districtId = districtId
        this.cityName = cityName ?: ""
        areaValue.value = StringUtils.formatArea(
            provinceName,
            cityName,
            districtName
        )
    }

    /**
     * 切换小区
     */
    fun changeMansion(title: String, latitude: Double, longitude: Double, address: String) {
        createAndUpdateEntity.value?.area = title
        createAndUpdateEntity.value?.lat = latitude
        createAndUpdateEntity.value?.lng = longitude
        mansionValue.value = title
        changeAddress(address)
    }

    /**
     * 切换详细地址
     */
    private fun changeAddress(address: String?) {
        // 详细地址
        createAndUpdateEntity.value?.address = address ?: ""
        addressValue.value = address ?: ""
    }

    /**
     * 切换营业时间
     */
    fun changeWorkTime(
        timeList: MutableList<BusinessHourEntity>? = null,
        workTime: String? = null
    ) {
        timeList?.let {
            createAndUpdateEntity.value?.workTimeStr = GsonUtils.any2Json(timeList.map { item ->
                BusinessHourParams(
                    item._weekDays.map { day -> day.id },
                    item._workTime
                )
            })
            workTimeValue.value = timeList.joinToString("\n") { item ->
                item.hourWeekVal + item.workTime
            }
            createAndUpdateEntity.value?.workTime = workTime ?: run {
                val arr = Array(7) { "" }
                for (index in 0..6) {
                    arr[index] =
                        timeList.filter { item -> null != item.weekDays.find { day -> day.id == (index + 1) } }
                            .joinToString(",") { item ->
                                item.workTime
                            }
                }
                GsonUtils.any2Json(arr)
            }
        }
    }

    /**
     * 切换业务类型
     */
    fun changeBusinessType(datas: List<ShopBusinessTypeEntity>) {
        val sb = StringBuilder()
        val sbId = StringBuilder()
        datas.forEach { type ->
            sb.append(type.getTitle()).append("、")
            sbId.append(type.type).append(",")
        }
        if (sb.isNotEmpty()) {
            sb.deleteCharAt(sb.length - 1)
        }
        if (sbId.isNotEmpty()) {
            sbId.deleteCharAt(sbId.length - 1)
        }
        businessTypeValue.postValue(sb.toString())
        createAndUpdateEntity.value?.shopBusiness = sbId.toString()
    }

    fun submit(view: View) {
        createAndUpdateEntity.value?.let { params ->
            if (params.name.isEmpty()) {
                SToast.showToast(msg = "请输入门店名称")
                return
            }

            if (-1 == params.shopType) {
                SToast.showToast(msg = "请选择门店类型")
                return
            }

            if (-1 == params.provinceId || -1 == params.cityId || -1 == params.districtId) {
                SToast.showToast(msg = "请选择所在区域")
                return
            }

            if (params.address.isEmpty()) {
                SToast.showToast(msg = "请输入详细位置")
                return
            }

            if (1 == params.shopType) {
                if (-1 == params.schoolId || params.schoolName.isEmpty() || null == params.lat || null == params.lng) {
                    SToast.showToast(msg = "请选择学校")
                    return
                }

                val temp = params.area
                params.area = params.address
                params.address = temp
            } else {
                if (params.area.isEmpty() || null == params.lat || null == params.lng) {
                    SToast.showToast(msg = "请选择小区/大厦")
                    return
                }
            }

            if (params.workTime.isEmpty()) {
                SToast.showToast(msg = "请选择营业时间")
                return
            }

            if (params.serviceTelephone.isEmpty()) {
                SToast.showToast(msg = "请输入客服电话")
                return
            }
            if (params.shopBusiness.isEmpty()) {
                SToast.showToast(msg = "请选择门店业务类型")
                return
            }

            launch({
                ApiRepository.dealApiResult(
                    if (null == params.id) {
                        mRepo.createShop(
                            ApiRepository.createRequestBody(GsonUtils.any2Json(params))
                        )
                    } else {
                        mRepo.updateShop(
                            ApiRepository.createRequestBody(GsonUtils.any2Json(params))
                        )
                    }
                )
                withContext(Dispatchers.Main) {
                    SToast.showToast(
                        view.context,
                        if (null == params.id) R.string.add_success else R.string.update_success
                    )
                }
                jump.postValue(0)
                LiveDataBus.post(BusEvents.SHOP_LIST_STATUS, true)
                LiveDataBus.post(BusEvents.SHOP_DETAILS_STATUS, true)
            })
        } ?: SToast.showToast(msg = "数据丢失了，请重新输入")
    }

    /**
     * 把店铺详情的数据赋值到编辑参数中
     */
    fun updateShopDetail(json: String?) {
        val shopDetailEntity = GsonUtils.json2Class(json, ShopDetailEntity::class.java)
        shopDetailEntity?.let {
            createAndUpdateEntity.value =
                    // id、店铺名、详细地址
                ShopCreateParam(
                    shopDetailEntity.id,
                    name = shopDetailEntity.name,
                    address = if (1 == shopDetailEntity.shopType) shopDetailEntity.area else shopDetailEntity.address,
                    serviceTelephone = shopDetailEntity.serviceTelephone,
                    _paymentSettings = shopDetailEntity.paymentSettings
                )

            // 店铺类型
            changeShopType(ShopTypeEntity(shopDetailEntity.shopType, shopDetailEntity.shopTypeName))

            if (1 == shopDetailEntity.shopType) {
                changeSchool(
                    SchoolSelectEntity(
                        shopDetailEntity.schoolId,
                        shopDetailEntity.schoolName,
                        shopDetailEntity.shopType,
                        shopDetailEntity.lng,
                        shopDetailEntity.lat,
                        shopDetailEntity.provinceName,
                        shopDetailEntity.cityName,
                        shopDetailEntity.districtName,
                        shopDetailEntity.provinceId,
                        shopDetailEntity.cityId,
                        shopDetailEntity.districtId,
                        shopDetailEntity.area,
                    )
                )
                createAndUpdateEntity.value?.area = shopDetailEntity.address
            } else {
                //省市区
                changeArea(
                    shopDetailEntity.provinceId,
                    shopDetailEntity.provinceName,
                    shopDetailEntity.cityId,
                    shopDetailEntity.cityName,
                    shopDetailEntity.districtId,
                    shopDetailEntity.districtName,
                )
                //小区、经纬度、详细地址
                changeMansion(
                    shopDetailEntity.area,
                    shopDetailEntity.lat,
                    shopDetailEntity.lng,
                    shopDetailEntity.address
                )
            }
            // 营业时间
            changeWorkTime(shopDetailEntity.workTimeArr(), shopDetailEntity.workTime)
            // 业务类型
            changeBusinessType(shopDetailEntity.businessName)
        }
    }
}
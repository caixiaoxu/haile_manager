package com.shangyun.haile_manager_android.business.vm

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.amap.api.services.core.PoiItem
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.gson.GsonUtils
import com.shangyun.haile_manager_android.business.apiService.ShopService
import com.shangyun.haile_manager_android.data.entities.SchoolSelectEntity
import com.shangyun.haile_manager_android.data.entities.ShopBusinessTypeEntity
import com.shangyun.haile_manager_android.data.entities.ShopCreateEntity
import com.shangyun.haile_manager_android.data.entities.ShopTypeEntity
import com.shangyun.haile_manager_android.data.model.ApiRepository
import com.shangyun.haile_manager_android.utils.StringUtils
import timber.log.Timber

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
class ShopCreateViewModel : BaseViewModel() {
    private val mRepo = ApiRepository.apiClient(ShopService::class.java)

    // 店铺数据
    val shopDetails: MutableLiveData<ShopCreateEntity> = MutableLiveData(ShopCreateEntity())

    // 门店类型列表
    val shopTypeList: MutableLiveData<List<ShopTypeEntity>> = MutableLiveData()

    // 门店业务列表
    val shopBusinessTypeList: MutableLiveData<List<ShopBusinessTypeEntity>> = MutableLiveData()

    // 门店类型
    val shopTypeValue: MutableLiveData<ShopTypeEntity> = MutableLiveData()

    // 学校名称
    val schoolNameValue: MutableLiveData<String> = MutableLiveData()

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
        launch(
            {
                shopTypeList.postValue(ApiRepository.dealApiResult(mRepo.shopTypeList()))
                shopBusinessTypeList.postValue(ApiRepository.dealApiResult(mRepo.shopBusinessType()))
            },
            {
                it.message?.let { it1 -> SToast.showToast(msg = it1) }
                Timber.d("请求失败或异常$it")
            },
            {
                Timber.d("请求结束")
            })
    }

    /**
     * 切换店铺类型
     */
    fun changeShopType(data: ShopTypeEntity) {
        if (data.type != shopTypeValue.value?.type) {
            shopTypeValue.postValue(data)
            shopDetails.value?.let {
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
        shopDetails.value?.schoolId = school?.id ?: -1
        // 学校名
        shopDetails.value?.schoolName = school?.name ?: ""
        schoolNameValue.value = school?.name ?: ""
        shopDetails.value?.lat = school?.lat
        shopDetails.value?.lng = school?.lng
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
        shopDetails.value?.provinceId = provinceId
        shopDetails.value?.cityId = cityId
        shopDetails.value?.districtId = districtId
        areaValue.value = StringUtils.formatArea(
            provinceName,
            cityName,
            districtName
        )
    }

    /**
     * 切换小区
     */
    fun changeMansion(poiItem: PoiItem) {
        shopDetails.value?.area = poiItem.title
        shopDetails.value?.lat = poiItem.latLonPoint.latitude
        shopDetails.value?.lng = poiItem.latLonPoint.longitude
        mansionValue.value = poiItem.title
        changeAddress(poiItem.provinceName + poiItem.cityName + poiItem.adName + poiItem.snippet)
    }

    /**
     * 切换详细地址
     */
    private fun changeAddress(address: String?) {
        // 详细地址
        shopDetails.value?.address = address ?: ""
        addressValue.value = address ?: ""
    }

    /**
     * 切换营业时间
     */
    fun changeWorkTime(time: String) {
        shopDetails.value?.workTime = time
        workTimeValue.value = time
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
        shopDetails.value?.shopBusiness = sbId.toString()
    }

    fun submit(view: View) {
        shopDetails.value?.let { params ->
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
                    mRepo.createShop(
                        ApiRepository.createRequestBody(GsonUtils.any2Json(params))
                    )
                )
                jump.postValue(0)
            }, {
                it.message?.let { it1 -> SToast.showToast(msg = it1) }
                Timber.d("请求失败或异常$it")
            }, {
                Timber.d("请求结束")
            })
        } ?: SToast.showToast(msg = "数据丢失了，请重新输入")
    }
}
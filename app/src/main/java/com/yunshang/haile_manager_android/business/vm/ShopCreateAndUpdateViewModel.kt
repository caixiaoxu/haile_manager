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
import com.yunshang.haile_manager_android.data.arguments.OperationSettings
import com.yunshang.haile_manager_android.data.arguments.ShopCreateParam
import com.yunshang.haile_manager_android.data.entities.SchoolSelectEntity
import com.yunshang.haile_manager_android.data.entities.ShopDetailEntity
import com.yunshang.haile_manager_android.data.entities.ShopTypeEntity
import com.yunshang.haile_manager_android.data.model.ApiRepository
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

    // 运营设置
    val volumeVisibleState: MutableLiveData<Int> by lazy {
        MutableLiveData()
    }

    /**
     * 初始化数据
     */
    fun requestData() {
        launch({
            shopTypeList.postValue(ApiRepository.dealApiResult(mRepo.shopTypeList()))
        })
    }

    fun submit(view: View) {
        createAndUpdateEntity.value?.let { params ->
            if (params.name.length < 2) {
                SToast.showToast(msg = "请输入2-20位的门店名称")
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
//
//            if (params.workTime.isEmpty()) {
//                SToast.showToast(msg = "请选择营业时间")
//                return
//            }
//
//            if (params.serviceTelephone.isEmpty()) {
//                SToast.showToast(msg = "请输入客服电话")
//                return
//            }
//            if (params.shopBusiness.isEmpty()) {
//                SToast.showToast(msg = "请选择门店业务类型")
//                return
//            }

            // 运营设置
            params.operationSettings = OperationSettings(volumeVisibleState.value ?: 0)

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
                    _paymentSettings = shopDetailEntity.paymentSettings,
                    operationSettings = shopDetailEntity.operationSettings
                )

            // 店铺类型
            createAndUpdateEntity.value?.changeShopType(ShopTypeEntity(shopDetailEntity.shopType, shopDetailEntity.shopTypeName))

            if (1 == shopDetailEntity.shopType) {
                createAndUpdateEntity.value?.changeSchool(
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
                createAndUpdateEntity.value?.mansionValue = shopDetailEntity.address
            } else {
                //省市区
                createAndUpdateEntity.value?.changeArea(
                    shopDetailEntity.provinceId,
                    shopDetailEntity.provinceName,
                    shopDetailEntity.cityId,
                    shopDetailEntity.cityName,
                    shopDetailEntity.districtId,
                    shopDetailEntity.districtName,
                )
                //小区、经纬度、详细地址
                createAndUpdateEntity.value?.changeMansion(
                    shopDetailEntity.area,
                    shopDetailEntity.lat,
                    shopDetailEntity.lng,
                    shopDetailEntity.address
                )
            }
            // 营业时间
//            changeWorkTime(shopDetailEntity.workTimeArr(), shopDetailEntity.workTime)
            // 业务类型
//            changeBusinessType(shopDetailEntity.businessName)
            // 运营设置
            volumeVisibleState.postValue(shopDetailEntity.operationSettings.volumeVisibleState)
        }
    }
}
package com.shangyun.haile_manager_android.business.vm

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.shangyun.haile_manager_android.business.apiService.ShopService
import com.shangyun.haile_manager_android.data.entities.SchoolSelectEntity
import com.shangyun.haile_manager_android.data.entities.ShopDetailsEntity
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

    val shopDetails: MutableLiveData<ShopDetailsEntity> = MutableLiveData(ShopDetailsEntity())

    // 门店类型列表
    val shopTypeList: MutableLiveData<List<ShopTypeEntity>> = MutableLiveData()

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

    // 业务类型
    val businessTypeValue: MutableLiveData<String> = MutableLiveData()

    // 营业时间
    val workTimeValue: MutableLiveData<String> = MutableLiveData()

    /**
     * 初始化数据
     */
    fun requestData() {
        launch(
            {
                val types = ApiRepository.dealApiResult(mRepo.shopTypeList())
                shopTypeList.postValue(types)
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
     * 切换学校
     */
    fun changeSchool(school: SchoolSelectEntity?) {
        // 学校id
        shopDetails.value?.schoolId = school?.id ?: -1
        // 学校名
        shopDetails.value?.schoolName = school?.name ?: ""
        schoolNameValue.value = school?.name ?: ""
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
    fun changeMansion(mansion: String, address: String?) {
        shopDetails.value?.area = mansion
        mansionValue.value = mansion
        changeAddress(address)
    }

    /**
     * 切换地区
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

    fun submit(view: View) {
    }


}
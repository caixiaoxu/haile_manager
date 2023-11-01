package com.yunshang.haile_manager_android.data.arguments

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.data.entities.SchoolSelectEntity
import com.yunshang.haile_manager_android.data.entities.ShopTypeEntity
import com.yunshang.haile_manager_android.utils.StringUtils

/**
 * Title :
 * Author: Lsy
 * Date: 2023/4/14 15:48
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class ShopCreateParam(
    var id: Int? = null,
    var shopType: Int? = null,
    var name: String = "",
    var provinceId: Int = -1,
    var cityId: Int = -1,
    var districtId: Int = -1,
    var area: String = "",
    var address: String = "",
    var lng: Double? = null,
    var lat: Double? = null,
    var schoolId: Int? = null,
    var schoolName: String = "",
    @Transient
    var workTime: String = "",
    @Transient
    var workTimeStr: String = "",
    @Transient
    var serviceTelephone: String = "",
    @Transient
    var shopBusiness: String = "",
) : BaseObservable() {

    @get:Bindable
    var shopTypeVal: Int?
        get() = shopType
        set(value) {
            if (shopType != value) {
                shopType = value
                notifyPropertyChanged(BR.shopTypeVal)
                changeSchool(null)
            }
        }

    @Transient
    @get:Bindable
    var shopTypeName: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.shopTypeName)
        }

    /**
     * 切换店铺类型
     */
    fun changeShopType(data: ShopTypeEntity?) {
        if (null != data && -1 != data.type) {
            shopTypeVal = data.type
            shopTypeName = data.name
            changeSchool(null)
            changeMansion("",null,null,"")
        }
    }

    @get:Bindable
    var schoolNameVal: String
        get() = schoolName
        set(value) {
            schoolName = value
            notifyPropertyChanged(BR.schoolNameVal)
        }

    /**
     * 切换学校
     */
    fun changeSchool(school: SchoolSelectEntity?) {
        // 学校id
        schoolId = school?.id ?: -1
        // 学校名
        schoolNameVal = school?.name ?: ""
        lat = school?.lat
        lng = school?.lng
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
        //如果学校不为空，清空area数据
        school?.let {
            mansionValue = ""
        }
    }

    @Transient
    var cityName: String = ""

    @Transient
    @get:Bindable
    var areaValue: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.areaValue)
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
        this.provinceId = provinceId
        this.cityId = cityId
        this.districtId = districtId
        this.cityName = cityName ?: ""
        areaValue = StringUtils.formatArea(
            provinceName,
            cityName,
            districtName
        )
    }

    @get:Bindable
    var mansionValue: String
        get() = area
        set(value) {
            area = value
            notifyPropertyChanged(BR.mansionValue)
        }

    /**
     * 切换小区
     */
    fun changeMansion(title: String?, latitude: Double?, longitude: Double?, address: String?) {
        mansionValue = title ?:""
        lat = latitude
        lng = longitude
        changeAddress(address)
    }

    @get:Bindable
    var addressValue: String
        get() = address
        set(value) {
            address = value
            notifyPropertyChanged(BR.addressValue)
        }

    /**
     * 切换详细地址
     */
    private fun changeAddress(address: String?) {
        // 详细地址
        addressValue = address ?: ""
    }
}

data class OperationSettings(
    val volumeVisibleState: Int
)
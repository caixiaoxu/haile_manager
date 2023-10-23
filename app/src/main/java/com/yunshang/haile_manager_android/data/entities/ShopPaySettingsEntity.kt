package com.yunshang.haile_manager_android.data.entities

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R

/**
 * Title :
 * Author: Lsy
 * Date: 2023/8/2 17:45
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class ShopPaySettingsEntity(
    var tokenCoinAllowRefund: Int,
    val goodsSettingList: List<GoodsSetting>?,
    var shopIdList: List<Int>? = null,
    var shopId: Int? = null
) {
    fun setTokenCoinAllowRefund(isCheck: Boolean) {
        tokenCoinAllowRefund = if (isCheck) 1 else 0
    }

    fun showSettingList(): Boolean =
        !(goodsSettingList.isNullOrEmpty()
                || goodsSettingList.all { item -> !item.checkTokenCoinForceUse && !item.checkNoPassForceUse && !item.checkNoPassTipUse })
}

data class GoodsSetting(
    var goodsCategoryId: Int,
    var goodsCategoryName: String,
    var tokenCoinForceUse: Int = 0,
    var noPassForceUse: Int = 0,
    var noPassTipUse: Int = 0
) : BaseObservable() {

    @get:Bindable
    var checkTokenCoinForceUse: Boolean
        get() = 1 == tokenCoinForceUse
        set(value) {
            if (value) {
                tokenCoinForceUse = 1
                noPassForceUse = 0
                noPassTipUse = 0
                notifyPropertyChanged(BR.checkTokenCoinForceUse)
                notifyPropertyChanged(BR.checkNoPassForceUse)
                notifyPropertyChanged(BR.checkNoPassTipUse)
            } else {
                tokenCoinForceUse = 0
                notifyPropertyChanged(BR.checkTokenCoinForceUse)
            }
        }

    @get:Bindable
    var checkNoPassForceUse: Boolean
        get() = 1 == noPassForceUse
        set(value) {
            if (value) {
                noPassForceUse = 1
                tokenCoinForceUse = 0
                noPassTipUse = 0
                notifyPropertyChanged(BR.checkTokenCoinForceUse)
                notifyPropertyChanged(BR.checkNoPassForceUse)
                notifyPropertyChanged(BR.checkNoPassTipUse)
            } else {
                noPassForceUse = 0
                notifyPropertyChanged(BR.checkNoPassForceUse)
            }
        }

    @get:Bindable
    var checkNoPassTipUse: Boolean
        get() = 1 == noPassTipUse
        set(value) {
            if (value) {
                noPassForceUse = 0
                tokenCoinForceUse = 0
                noPassTipUse = 1
                notifyPropertyChanged(BR.checkTokenCoinForceUse)
                notifyPropertyChanged(BR.checkNoPassForceUse)
                notifyPropertyChanged(BR.checkNoPassTipUse)
            } else {
                noPassTipUse = 0
                notifyPropertyChanged(BR.checkNoPassTipUse)
            }
        }

    fun getOpenSettings(): String = (if (checkTokenCoinForceUse) {
        StringUtils.getString(R.string.compel_use_starfish)
    } else if (checkNoPassForceUse) {
        StringUtils.getString(R.string.compel_no_pwd_pay)
    } else if (checkNoPassTipUse) {
        StringUtils.getString(R.string.no_pwd_pay)
    } else "").replace("\n", "")
}
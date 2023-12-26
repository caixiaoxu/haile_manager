package com.yunshang.haile_manager_android.data.rule

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.yunshang.haile_manager_android.BR

/**
 * Title :
 * Author: Lsy
 * Date: 2023/4/17 16:16
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
abstract class ICommonNewBottomItemEntity : BaseObservable() {
    @Transient
    @get:Bindable
    var commonItemSelect: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.commonItemSelect)
        }
}
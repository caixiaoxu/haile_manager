package com.yunshang.haile_manager_android.data.rule

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import com.yunshang.haile_manager_android.BR

/**
 * Title :
 * Author: Lsy
 * Date: 2023/4/18 10:15
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
abstract class SearchSelectRadioEntity : BaseObservable() {

    abstract fun getSelectId(): Int

    /**
     * 获取名称
     */
    abstract fun getSelectName(): String

    /**
     * 选中参数
     */
    @get:Bindable
    var getCheck: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.getCheck)
        }
}
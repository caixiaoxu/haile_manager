package com.yunshang.haile_manager_android.data.rule

import androidx.lifecycle.MutableLiveData

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
interface SearchSelectRadioEntity {

    fun getSelectId():Int

    /**
     * 获取名称
     */
    fun getSelectName():String

    /**
     * 选中参数
     */
    fun getCheck(): MutableLiveData<Boolean>?
}
package com.yunshang.haile_manager_android.data.rule

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
interface ISearchSelectEntity {

    fun getSearchId(): Int

    /**
     * 获取标题
     */
    fun getTitle(): String

    /**
     * 获取内容
     */
    fun getContent(): Array<String> = arrayOf()

    /**
     * 获取右边侧
     */
    fun getValue(): String? = null
}
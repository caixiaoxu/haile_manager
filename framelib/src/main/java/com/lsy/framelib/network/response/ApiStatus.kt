package com.lsy.framelib.network.response

/**
 * Title : 接口请求状态
 * Author: Lsy
 * Date: 2023/3/20 14:53
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
enum class ApiStatus {
    STATE_LOADING,//加载中
    STATE_SUCCESS,//成功
    STATE_COMPLETED,//完成
    STATE_EMPTY,//数据为null
    STATE_FAILED,//失败
}
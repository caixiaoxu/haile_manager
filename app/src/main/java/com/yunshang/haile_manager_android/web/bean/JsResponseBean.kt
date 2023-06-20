package com.yunshang.haile_manager_android.web.bean

/**
 * Title :
 * Author: Lsy
 * Date: 2023/5/24 14:50
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
</desc></version></time></author> */
class JsResponseBean<T> {
    val code: Int
    var msg: String? = null
    var data: T? = null
        private set

    constructor(code: Int) {
        this.code = code
        when (code) {
            -2 -> msg = "不是当前登陆的用户"
            -1 -> msg = "参数不合法/无效"
            1 -> msg = "账号未登录"
            2 -> msg = "账号异常，请联系运营商"
            3 -> msg = "接口无权限"
            4 -> msg = "发生未知错误"
        }
    }

    constructor(code: Int, msg: String?) {
        this.code = code
        this.msg = msg
    }

    constructor(code: Int, msg: String?, data: T) {
        this.code = code
        this.msg = msg
        this.data = data
    }

    constructor(data: T) {
        this.data = data
        code = 0
    }

    fun setData(data: T) {
        this.data = data
    }
}
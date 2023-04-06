package com.shangyun.haile_manager_android.business

import com.lsy.framelib.utils.gson.GsonUtils
import okhttp3.MediaType
import okhttp3.RequestBody

/**
 * Title : 请求体封装
 * Author: Lsy
 * Date: 2023/4/6 09:48
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
object RequestBodyUtil {

    /**
     * 生成请求body
     */
    fun createRequestBody(params: Map<String, Any>): RequestBody =
        RequestBody.create(MediaType.parse("application/json"), GsonUtils.any2Json(params))

}
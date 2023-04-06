package com.lsy.framelib.utils.gson

import android.text.TextUtils
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

/**
 * Title : 处理Long类型json
 * Author: Lsy
 * Date: 2023/3/17 15:16
 * Version:
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class LongDeserializer : JsonDeserializer<Long> {

    /**
     * 反序列化，处理null或空字符串
     */
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Long = if (json == null || TextUtils.isEmpty(json.asString)) 0 else json.asLong
}
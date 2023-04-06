package com.lsy.framelib.utils.gson

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter

/**
 * Title : 空字符串处理器
 * Author: Lsy
 * Date: 2023/3/17 15:13
 * Version:
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class NullStringAdapter : TypeAdapter<String>() {

    /**
     * 反序列化，处理空
     */
    override fun read(reader: JsonReader): String {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull()
            return ""
        }

        return reader.nextString()
    }

    /**
     * 序列化，处理空
     */
    override fun write(writer: JsonWriter, value: String?) {
        if (value == null) {
            writer.nullValue()
            return
        }

        writer.value(value)
    }
}
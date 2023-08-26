package com.yunshang.haile_manager_android.data.entities

import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonParser
import com.yunshang.haile_manager_android.data.rule.SearchSelectRadioEntity

/**
 * Title :
 * Author: Lsy
 * Date: 2023/4/24 15:26
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class SpuEntity(
    val brandName: String,
    val spu: MutableList<Spu>,
    val popular: MutableList<Spu>,
)

data class Spu(
    val id: Int,
    val code: String,
    val categoryId: Int,
    val categoryId2: Int,
    val categoryId3: Int,
    val categoryName: String,
    val brandId: Int,
    val brandName: String,
    val type: Int,
    val name: String,
    val feature: String,
    val mainPic: String,
    val mainVideo: String,
    val tags: String,
    val soldState: Int,
    val soldStateOp: Int,
    val inventoryType: Int,
    val chargeUnit: Int,
    val price: Int,
    val extAttr: String,
    val introduction: String,
    val taxCodeId: Int,
    val manufacturer: String,
    val vendor: String,
    val createTime: String,
    val creatorId: Int,
    val creatorName: String,
    val lastEditor: Int,
    val payType: String,
    val specs: List<SpuSpec>,
    val skus: List<SkuEntity>,
    val tagList: List<SpuTag>,
    val files: List<SpuFile>,
    val communicationType: Int,
    val extAttrDto: SpuExtAttrDto,
) : SearchSelectRadioEntity {
    @Transient
    var shortFeature: String? = null

    @Transient
    var isSelect: MutableLiveData<Boolean>? = null
        get() {
            if (null == field) {
                field = MutableLiveData(false)
            }
            return field
        }

    override fun getSelectId(): Int = id

    override fun getSelectName(): String = name

    override fun getCheck(): MutableLiveData<Boolean>? = isSelect

    fun getIgnorePayCodeFlag(): Boolean = try {
        JsonParser.parseString(extAttr).asJsonObject["ignorePayCodeFlag"].asBoolean
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

data class SpuSpec(
    val code: String,
    val createTime: String,
    val creatorId: Int,
    val creatorName: Int,
    val description: String,
    val id: Int,
    val name: String,
    val sort: Int,
    val status: Int,
    val updateTime: String,
    val values: List<SpecValue>
)

data class SpuTag(
    val color: String,
    val createTime: String,
    val creatorId: Int,
    val creatorName: Int,
    val deleteFlag: Int,
    val id: Int,
    val lastEditor: Int,
    val name: String,
    val remark: String,
    val sort: Int,
    val spuCount: String,
    val type: Int,
    val updateTime: String
)

data class SpuFile(
    val id: Int,
    val name: String,
    val sort: Int,
    val spuId: Int,
    val type: Int,
    val url: String
)

data class SpuExtAttrDto(
    val communicationType: Int,
    val ignorePayCodeFlag: Boolean,
    val specValueIdList: List<Int>,
    val functionType: Int,
    val priceType: List<Int>,
    val priceCalculateMode: List<Int>
)

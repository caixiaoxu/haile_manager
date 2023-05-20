package com.shangyun.haile_manager_android.data.entities

import com.shangyun.haile_manager_android.data.rule.ICommonBottomItemEntity
import com.shangyun.haile_manager_android.data.rule.IMultiSelectBottomItemEntity

/**
 * Title :
 * Author: Lsy
 * Date: 2023/4/24 14:47
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class CategoryEntity(
    val id: Int,
    val parentId: Int,
    val parent: CategoryEntity,
    val code: String,
    val platform: Int,
    val type: Int,
    val name: String,
    val icon: String,
    val picUrl: String,
    val description: String,
    val creatorId: Int,
    val creatorName: String,
    val lastEditor: Int,
    val children: List<CategoryChildren>,
    val brands: List<CategoryBrand>,
    val specs: List<CategorySpec>
) : ICommonBottomItemEntity, IMultiSelectBottomItemEntity {
    override var isCheck: Boolean = false

    override fun getTitle(): String = name

    override fun hashCode(): Int {
        return super.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other is CategoryEntity && other.id == id) return true
        return super.equals(other)
    }
}

data class CategoryChildren(
    val brands: List<Any>,
    val children: List<Any>,
    val code: String,
    val creatorId: Int,
    val creatorName: String,
    val description: String,
    val icon: String,
    val id: Int,
    val lastEditor: Int,
    val name: String,
    val parent: String,
    val parentId: Int,
    val picUrl: String,
    val platform: Int,
    val specs: List<Any>,
    val type: Int
)

data class CategoryBrand(
    val code: String,
    val creatorId: Int,
    val creatorName: String,
    val files: List<CategoryFile>,
    val hasFile: Boolean,
    val id: Int,
    val image: String,
    val lastEditor: Int,
    val letter: String,
    val level: Int,
    val name: String
)

data class CategoryFile(
    val brandId: Int,
    val id: Int,
    val name: String,
    val sort: Int,
    val url: String
)

data class CategorySpec(
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
    val values: List<CategoryValue>
)

data class CategoryValue(
    val code: String,
    val createTime: String,
    val creatorId: Int,
    val creatorName: String,
    val description: String,
    val ext: CategoryExt,
    val id: Int,
    val image: String,
    val lastEditor: Int,
    val name: String,
    val specId: Int,
    val specName: String,
    val updateTime: String
)

data class CategoryExt(
    val minutes: Int,
    val price: String
)
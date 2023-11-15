package com.yunshang.haile_manager_android.business.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.lsy.framelib.ui.base.BaseViewModel
import com.yunshang.haile_manager_android.business.apiService.ShopService
import com.yunshang.haile_manager_android.data.entities.ShopAndPositionSelectEntity
import com.yunshang.haile_manager_android.data.model.ApiRepository

/**
 * Title :
 * Author: Lsy
 * Date: 2023/10/11 10:40
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class ShopPositionSelectorViewModel : BaseViewModel() {
    private val mShopRepo = ApiRepository.apiClient(ShopService::class.java)

    // 是否显示点位
    var showPosition: Boolean = true

    // 是否需要可多选
    var canMultiSelect: Boolean = true

    // 是否可全选
    var canSelectAll: Boolean = true

    // 只显示对应的门店信息
    var shopIdList: IntArray? = null

    // 搜索内容
    val searchContent: MutableLiveData<String> = MutableLiveData()

    // 是否全选
    val isAll: MutableLiveData<Boolean> = MutableLiveData(false)

    // 之前选择的列表数据
    var oldShopPositionList: MutableList<ShopAndPositionSelectEntity>? = null

    // 原始列表数据
    val originShopPositionList: MutableList<ShopAndPositionSelectEntity> = mutableListOf()

    val shopPositionList: MutableLiveData<MutableList<ShopAndPositionSelectEntity>?> by lazy { MutableLiveData() }

    // 是否显示列表
    val showList: LiveData<Boolean> = shopPositionList.map {
        !it.isNullOrEmpty()
    }

    /**
     * 检测是否全选或全不选
     */
    fun checkIsAll() {
        shopPositionList.value?.let {
            if (it.all { item -> 2 == item.selectType }) {
                isAll.value = true
            } else if (it.any { item -> 2 != item.selectType }) {
                isAll.value = false
            }
        }
    }

    /**
     * 获取店铺点位列表
     */
    fun requestShopPositionList() {
        launch({
            val result = ApiRepository.dealApiResult(
                mShopRepo.requestShopSelectListV2(
                    ApiRepository.createRequestBody(hashMapOf())
                )
            )
            originShopPositionList.clear()
            val list = shopIdList?.let { shopIds ->
                result?.filter { item -> (null != item.id && item.id in shopIds) }?.toMutableList()
            } ?: result
            list?.let {
                dealOldSelectList(list)
                originShopPositionList.addAll(list)
            }
            shopPositionList.postValue(list)
        })
    }

    /**
     * 处理之前选择的数据
     */
    private fun dealOldSelectList(result: MutableList<ShopAndPositionSelectEntity>) {
        if (!oldShopPositionList.isNullOrEmpty()) {
            // 遍历门店
            result.forEach { select ->
                // 相同门店
                oldShopPositionList?.find { item -> item.id == select.id }?.let { sameShop ->
                    // 遍历点位
                    select.positionList?.forEach { position ->
                        // 如果包含，改为选中
                        position.selectVal =
                            sameShop.positionList?.find { itemPosition -> itemPosition.id == position.id }
                                ?.let { true } ?: false
                    }
                    // 判断是否点位全选
                    select.checkIsAll()
                }
            }
            // 判断是否门店全选
            checkIsAll()
        }
    }

    /**
     * 搜索
     */
    fun searchShopPositionList(content: String) {
        if (content.isNullOrEmpty()) {
            shopPositionList.postValue(originShopPositionList.onEach {
                it.checkIsAll()
            })
        } else {
            launch({
                val list = mutableListOf<ShopAndPositionSelectEntity>()
                originShopPositionList.forEach {
                    it.name?.let { name ->
                        if (name.contains(content)) {
                            // 如果门店名包含
                            list.add(it)
                        } else {
                            // 遍历，点位名包含
                            it.positionList?.let { position ->
                                position.filter { item -> item.name?.contains(content) ?: false }
                                    .let { selects ->
                                        if (selects.isNotEmpty()) {
                                            list.add(
                                                it.copy(positionList = selects.toMutableList())
                                                    .also { select ->
                                                        select.fold = it.fold
                                                        select.checkIsAll()
                                                    })
                                        }
                                    }
                            }
                        }
                    }
                }
                shopPositionList.postValue(list)
            })
        }
    }
}
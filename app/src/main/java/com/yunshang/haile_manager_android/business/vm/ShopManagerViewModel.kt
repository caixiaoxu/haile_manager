package com.yunshang.haile_manager_android.business.vm

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.apiService.ShopService
import com.yunshang.haile_manager_android.data.entities.ShopEntity
import com.yunshang.haile_manager_android.data.model.ApiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * Title :
 * Author: Lsy
 * Date: 2023/4/10 11:19
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class ShopManagerViewModel : BaseViewModel() {
    private val mRepo = ApiRepository.apiClient(ShopService::class.java)

    val mShopCountStr: MutableLiveData<String> = MutableLiveData()

    /**
     * 刷新店铺列表
     */
    fun requestShopList(
        page: Int,
        pageSize: Int,
        result: (listWrapper: ResponseList<ShopEntity>?) -> Unit
    ) {
        launch({
            if (1 == page) {
                ApiRepository.dealApiResult(mRepo.requestShopAndPositionNum())?.let {
                    mShopCountStr.postValue(
                        StringUtils.getString(
                            R.string.shop_num_hint,
                            it.shopCount,
                            it.positionCount
                        ),
                    )
                }
            }

            val listWrapper = ApiRepository.dealApiResult(
                mRepo.requestShopList(
                    ApiRepository.createRequestBody(
                        hashMapOf(
                            "page" to page,
                            "pageSize" to pageSize,
                        )
                    )
                )
            )
            withContext(Dispatchers.Main) {
                result.invoke(listWrapper)
            }
        }, {
            Timber.d("请求失败或异常$it")
            withContext(Dispatchers.Main) {
                it.message?.let { it1 -> SToast.showToast(msg = it1) }
                result.invoke(null)
            }
        }, null, 1 == page)
    }

    /**
     * 请求点位列表
     */
    fun requestPositionList(
        context: Context,
        shop: ShopEntity,
        callback: (hasMore: Boolean) -> Unit
    ) {
        launch({
            ApiRepository.dealApiResult(
                mRepo.requestPositionList(
                    hashMapOf(
                        "page" to shop.page,
                        "pageSize" to 20,
                        "shopId" to shop.id,
                    )
                )
            )?.let { result ->
                if (!result.items.isNullOrEmpty()) {
                    shop.positionList = shop.positionList.apply { addAll(result.items!!) }
                }
                val hasMore = shop.positionList.size < result.total
                if (hasMore) {
                    shop.page++
                }
                withContext(Dispatchers.Main) {
                    callback(hasMore)
                }
            } ?: withContext(Dispatchers.Main) {
                callback(false)
            }
        }, {
            withContext(Dispatchers.Main) {
                SToast.showToast(context, it?.message ?: "")
                callback(false)
            }
        })
    }
}
package com.yunshang.haile_manager_android.business.vm

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.apiService.DeviceService
import com.yunshang.haile_manager_android.business.apiService.OrderService
import com.yunshang.haile_manager_android.business.apiService.ShopService
import com.yunshang.haile_manager_android.data.common.SearchType
import com.yunshang.haile_manager_android.data.model.ApiRepository
import com.yunshang.haile_manager_android.data.rule.ISearchSelectEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
class SearchViewModel : BaseViewModel() {
    private val mDeviceRepo = ApiRepository.apiClient(DeviceService::class.java)
    private val mShopRepo = ApiRepository.apiClient(ShopService::class.java)
    private val mOrderRepo = ApiRepository.apiClient(OrderService::class.java)

    // 搜索类型
    @SearchType.ISearchType
    var searchType: Int = 0

    // 搜索提示语
    val searchHints = arrayOf(
        StringUtils.getString(R.string.device_search_hint),
        StringUtils.getString(R.string.search_shop_hint),
        StringUtils.getString(R.string.search_order_hint),
        StringUtils.getString(R.string.search_order_hint),
        StringUtils.getString(R.string.order_search_hint),
        StringUtils.getString(R.string.recharge_account_search_hint),
        StringUtils.getString(R.string.sub_account_search_hint),
    )

    // 搜索内容
    val searchKey: MutableLiveData<String> = MutableLiveData("")

    /**
     * 清空
     */
    fun clear(view: View) {
        searchKey.value = ""
    }

    /**
     * 搜索列表
     */
    fun searchList(
        page: Int,
        pageSize: Int,
        result1: ((responseList: MutableList<out ISearchSelectEntity>?) -> Unit)? = null,
        result2: ((responseList: ResponseList<out ISearchSelectEntity>?) -> Unit)? = null
    ) {
        launch({
            when (searchType) {
                SearchType.Device -> searchDeviceList(page, pageSize, result2)
                SearchType.Shop -> searchShopList(page, pageSize, result1)
                SearchType.Order, SearchType.AppointOrder -> searchOrderList(
                    page,
                    pageSize,
                    searchType == SearchType.AppointOrder,
                    result2
                )
            }
        }, null, null, 1 == page)
    }

    /**
     * 搜索设备列表
     */
    private suspend fun searchDeviceList(
        page: Int,
        pageSize: Int,
        result: ((responseList: ResponseList<out ISearchSelectEntity>?) -> Unit)?
    ) {
        val listWrapper = ApiRepository.dealApiResult(
            mDeviceRepo.deviceList(
                hashMapOf(
                    "page" to page,
                    "pageSize" to pageSize,
                    "workStatus" to "",
                    "keywords" to (searchKey.value?.trim() ?: "")
                )
            )
        )
        listWrapper?.let {
            withContext(Dispatchers.Main) {
                result?.invoke(it)
            }
        }
    }

    /**
     * 搜索店铺列表
     */
    private suspend fun searchShopList(
        page: Int,
        pageSize: Int,
        result: ((responseList: MutableList<out ISearchSelectEntity>?) -> Unit)?
    ) {
        val listWrapper = ApiRepository.dealApiResult(
            mShopRepo.shopSearchList(
                ApiRepository.createRequestBody(
                    hashMapOf(
                        "page" to page,
                        "pageSize" to pageSize,
                        "name" to (searchKey.value?.trim() ?: "")
                    )
                )
            )
        )
        listWrapper?.let {
            withContext(Dispatchers.Main) {
                result?.invoke(it)
            }
        }
    }

    /**
     * 搜索订单列表
     */
    private suspend fun searchOrderList(
        page: Int,
        pageSize: Int,
        isAppoint: Boolean,
        result: ((responseList: ResponseList<out ISearchSelectEntity>?) -> Unit)?
    ) {

        val params: HashMap<String, Any> = hashMapOf(
            "page" to page,
            "pageSize" to pageSize,
            "searchType" to 2,
            "searchStr" to (searchKey.value?.trim() ?: ""),
        )

        if (isAppoint) {
            params["orderType"] = 300
        }

        val listWrapper = ApiRepository.dealApiResult(
            mOrderRepo.requestOrderList(params)
        )
        listWrapper?.let {
            withContext(Dispatchers.Main) {
                result?.invoke(it)
            }
        }
    }
}
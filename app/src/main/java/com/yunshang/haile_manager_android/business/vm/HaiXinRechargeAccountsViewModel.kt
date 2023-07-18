package com.yunshang.haile_manager_android.business.vm

import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.apiService.HaiXinService
import com.yunshang.haile_manager_android.business.apiService.ShopService
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.arguments.SearchSelectParam
import com.yunshang.haile_manager_android.data.entities.HaixinRechargeAccountEntity
import com.yunshang.haile_manager_android.data.entities.OrderListEntity
import com.yunshang.haile_manager_android.data.model.ApiRepository
import com.yunshang.haile_manager_android.utils.DateTimeUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * Title :
 * Author: Lsy
 * Date: 2023/6/14 19:42
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class HaiXinRechargeAccountsViewModel : BaseViewModel() {
    val mHaiXinRepo = ApiRepository.apiClient(HaiXinService::class.java)
    private val mShopRepo = ApiRepository.apiClient(ShopService::class.java)

    // 用户数量
    val mAccountsCountStr: MutableLiveData<String> = MutableLiveData()

    val searchKeyword: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    // 选择的店铺
    val selectDepartment: MutableLiveData<SearchSelectParam> by lazy {
        MutableLiveData()
    }

    fun requestData() {
        launch({
            ApiRepository.dealApiResult(
                mShopRepo.shopSelectList(
                    ApiRepository.createRequestBody(
                        hashMapOf()
                    )
                )
            )?.let {
                it.firstOrNull()?.let { shop ->
                    selectDepartment.postValue(SearchSelectParam(shop.id, shop.name))
                }
            }
        })
    }

    fun requestRechargeList(
        page: Int,
        pageSize: Int,
        result: (listWrapper: ResponseList<HaixinRechargeAccountEntity>?) -> Unit

    ) {
        launch({
            val params: HashMap<String, Any> = hashMapOf(
                "page" to page,
                "pageSize" to pageSize,
            )
            // 店铺
            selectDepartment.value?.let {
                params["shopId"] = it.id
            }

            // 关键字
            searchKeyword.value?.let {
                params["phone"] = it
            }

            ApiRepository.dealApiResult(
                mHaiXinRepo.requestRechargeAccountsList(ApiRepository.createRequestBody(params))
            )?.let {
                mAccountsCountStr.postValue(
                    StringUtils.getString(R.string.recharge_account_num_hint, it.total),
                )
                withContext(Dispatchers.Main) {
                    result.invoke(it)
                }
            }
        }, {
            Timber.d("请求失败或异常$it")
            withContext(Dispatchers.Main) {
                it.message?.let { it1 -> SToast.showToast(msg = it1) }
                result.invoke(null)
            }
        }, null, 1 == page)
    }
}
package com.yunshang.haile_manager_android.business.vm

import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.ui.base.BaseViewModel
import com.yunshang.haile_manager_android.business.apiService.HaiXinService
import com.yunshang.haile_manager_android.data.entities.HaixinRechargeAccountDetailEntity
import com.yunshang.haile_manager_android.data.entities.HaixinRechargeAccountEntity
import com.yunshang.haile_manager_android.data.model.ApiRepository
import com.yunshang.haile_manager_android.utils.DateTimeUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

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
class HaiXinRechargeAccountDetailListViewModel : BaseViewModel() {
    val mHaiXinRepo = ApiRepository.apiClient(HaiXinService::class.java)

    var userId: Int = -1
    var shopId: Int = -1
    var shopName: String = ""

    var searchDate: Date = Date()

    val totalHaixinOfMap: MutableMap<String, HaixinRechargeAccountEntity> = mutableMapOf()

    fun requestHaixinRechargeList(
        page: Int,
        pageSize: Int,
        callBack: (responseList: ResponseList<out HaixinRechargeAccountDetailEntity>?) -> Unit
    ) {
        if (-1 == shopId || -1 == userId) return

        launch({

            if (1 == page) {
                ApiRepository.dealApiResult(
                    mHaiXinRepo.requestRechargeAccountDetailTotalOfMonth(
                        ApiRepository.createRequestBody(
                            hashMapOf(
                                "shopId" to shopId,
                                "userId" to userId,
                                "startTime" to DateTimeUtils.formatDateTimeStartParam(
                                    DateTimeUtils.getMonthFirst(
                                        searchDate
                                    )
                                ),
                                "endTime" to DateTimeUtils.formatDateTimeEndParam(
                                    DateTimeUtils.getMonthLast(
                                        searchDate
                                    )
                                ),
                            )
                        )
                    )
                )?.let {
                    totalHaixinOfMap.put(DateTimeUtils.formatDateTime(searchDate, "yyyy年MM月"), it)
                }
            }

            ApiRepository.dealApiResult(
                mHaiXinRepo.requestRechargeAccountDetailList(
                    ApiRepository.createRequestBody(
                        hashMapOf(
                            "page" to page,
                            "pageSize" to pageSize,
                            "memberId" to userId,
                            "shopId" to shopId,
                            "startTime" to DateTimeUtils.formatDateTimeStartParam(
                                DateTimeUtils.getMonthFirst(
                                    searchDate
                                )
                            ),
                            "endTime" to DateTimeUtils.formatDateTimeEndParam(
                                DateTimeUtils.getMonthLast(
                                    searchDate
                                )
                            ),
                        )
                    )
                )
            )?.let {
                withContext(Dispatchers.Main) {
                    callBack.invoke(it)
                }
            }
        })
    }
}
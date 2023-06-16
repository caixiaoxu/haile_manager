package com.yunshang.haile_manager_android.business.vm

import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.ui.base.BaseViewModel
import com.yunshang.haile_manager_android.business.apiService.HaiXinService
import com.yunshang.haile_manager_android.data.entities.HaiXinTotalEntity
import com.yunshang.haile_manager_android.data.entities.HaixinRechargeEntity
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
class HaiXinRechargeListViewModel : BaseViewModel() {
    val mHaiXinRepo = ApiRepository.apiClient(HaiXinService::class.java)

    var searchDate: Date = Date()

    val totalHaixinOfMap: MutableMap<String, HaiXinTotalEntity> = mutableMapOf()

    fun requestHaixinRechargeList(
        page: Int,
        pageSize: Int,
        callBack: (responseList: ResponseList<out HaixinRechargeEntity>?) -> Unit
    ) {
        launch({

            if (1 == page) {
                ApiRepository.dealApiResult(
                    mHaiXinRepo.requestHaiXinTotal(
                        ApiRepository.createRequestBody(
                            hashMapOf(
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
                mHaiXinRepo.requestHaiXinRechargeList(
                    ApiRepository.createRequestBody(
                        hashMapOf(
                            "page" to page,
                            "pageSize" to pageSize,
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
package com.yunshang.haile_manager_android.business.vm

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.apiService.MessageService
import com.yunshang.haile_manager_android.business.vm.base.BaseComposeViewModel
import com.yunshang.haile_manager_android.data.entities.MessageEntity
import com.yunshang.haile_manager_android.data.model.ApiRepository
import com.yunshang.haile_manager_android.ui.activity.base.PageState
import kotlinx.coroutines.async

/**
 * Title :
 * Author: Lsy
 * Date: 2024/1/26 15:40
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class HomeNewViewModel : BaseComposeViewModel() {
    private val mMessageRepo = ApiRepository.apiClient(MessageService::class.java)

    // 功能管理
    val funcList = mutableStateListOf<FunArea>().apply {
        addAll(
            listOf(
                FunArea(
                    R.string.home_func_title,
                    listOf(
                        FunArea.FunItem(
                            R.string.device_manager,
                            R.mipmap.icon_device_manager,
                        ),
                        FunArea.FunItem(
                            R.string.pt,
                            R.mipmap.icon_shop_manager,
                        ).apply {
                            msgNum = 100
                        },
                        FunArea.FunItem(
                            R.string.order_manager,
                            R.mipmap.icon_order_manager,
                        ),
                        FunArea.FunItem(
                            R.string.staff_manager,
                            R.mipmap.icon_staff_manager,
                        ).apply {
                            msgNum = 1
                        },
                        FunArea.FunItem(
                            R.string.work_order_manager,
                            R.mipmap.icon_work_order_manager,
                        ),
                        FunArea.FunItem(
                            R.string.notice_manager,
                            R.mipmap.icon_notice_manager,
                        ).apply {
                            msgNum = 64
                        },
                        FunArea.FunItem(
                            R.string.device_repairs,
                            R.mipmap.icon_device_repairs,
                        ),
                        FunArea.FunItem(
                            R.string.device_unbind_approve,
                            R.mipmap.icon_device_unbind_approve,
                        ).apply {
                            msgNum = 10
                        },
                        FunArea.FunItem(
                            R.string.spares_purchase,
                            R.mipmap.icon_spares_purchase,
                        ),
                    )
                ),
                FunArea(
                    R.string.home_marketing_title,
                    listOf(
                        FunArea.FunItem(
                            R.string.discount_manager,
                            R.mipmap.icon_discounts_manager,
                        ),
                        FunArea.FunItem(
                            R.string.haixin_manager,
                            R.mipmap.icon_haixin_manager,
                        ),
                        FunArea.FunItem(
                            R.string.issue_coupons,
                            R.mipmap.icon_issue_coupons,
                        ),
                        FunArea.FunItem(
                            R.string.coupon_manage,
                            R.mipmap.icon_coupon_manage,
                        ),
                    )
                ),
                FunArea(
                    R.string.home_capital_title,
                    listOf(
                        FunArea.FunItem(
                            R.string.sub_account_manager,
                            R.mipmap.icon_sub_account_manager,
                        ),
                        FunArea.FunItem(
                            R.string.invoice_manager,
                            R.mipmap.icon_invoice_manager,
                        ),
                    )
                ),
            )
        )
    }

    /**
     * 请求数据
     * @param type 0-自动(如果没有数据就刷新，有数据就不刷新)； 1-强制刷新；
     */
    fun requestData(type: Int = 0) {
        pageState = PageState.LoadData
    }

    fun requestIdleData() {
        launch({
            val unRead = async { requestUnReadCount() }
            val msgList = async { requestMessageList() }
            msgList.await()
            unRead.await()
        })
    }

    // 最新消息数量
    var unReadMsgNum by mutableStateOf(0)

    /**
     * 未读消息数量
     */
    private suspend fun requestUnReadCount() {
        // 未读消息数量
        ApiRepository.dealApiResult(
            mMessageRepo.messageTypeCount(
                ApiRepository.createRequestBody(
                    hashMapOf(
                        "appType" to 1,
                        "readStatus" to 0
                    )
                )
            )
        )?.let { list ->
            Log.i("TAG", "requestIdleData: 33333333")
            // 累加
            var count = 0
            list.forEach {
                count += it.count
            }
            unReadMsgNum = count
        }
    }

    // 最新消息
    val lastMsgList = mutableStateListOf<MessageEntity>()

    /**
     * 消息列表
     */
    private suspend fun requestMessageList() {
        ApiRepository.dealApiResult(
            mMessageRepo.messageList(
                ApiRepository.createRequestBody(
                    hashMapOf(
                        "pageNum" to 1,
                        "pageSize" to 2,
                        "appType" to 1
                    )
                )
            )
        )?.items?.let {
            Log.i("TAG", "requestIdleData: 444444444")
            lastMsgList.clear()
            lastMsgList.addAll(it)
        } ?: run {
            lastMsgList.clear()
        }
    }

    /**
     * 功能操作区域
     */
    data class FunArea(
        val name: Int,
        val funItemList: List<FunItem>? = null
    ) {
        data class FunItem(
            val name: Int,
            val icon: Int,
        ) {
            var msgNum by mutableStateOf(0)
        }
    }
}
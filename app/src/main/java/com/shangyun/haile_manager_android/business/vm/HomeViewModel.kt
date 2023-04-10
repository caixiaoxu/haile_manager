package com.shangyun.haile_manager_android.business.vm

import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.apiService.CapitalService
import com.shangyun.haile_manager_android.business.apiService.MessageService
import com.shangyun.haile_manager_android.data.entities.MessageEntity
import com.shangyun.haile_manager_android.data.model.ApiRepository
import com.shangyun.haile_manager_android.ui.activity.*
import com.shangyun.haile_manager_android.utils.UserPermissionUtils
import timber.log.Timber

/**
 * Title :
 * Author: Lsy
 * Date: 2023/4/7 13:58
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class HomeViewModel : BaseViewModel() {
    private val mCapitalRepo = ApiRepository.apiClient(CapitalService::class.java)
    private val mMessageRepo = ApiRepository.apiClient(MessageService::class.java)

    // 总收入
    val inComeVal: MutableLiveData<String> = MutableLiveData()

    // 最新消息
    val lastMsgList: MutableLiveData<List<MessageEntity>?> = MutableLiveData()

    // 最新消息数量
    val unReadMsgNum: MutableLiveData<Int> = MutableLiveData(0)

    val funcList: MutableLiveData<List<FunItem>> = MutableLiveData(
        arrayListOf(
            FunItem(
                "设备管理",
                R.mipmap.icon_device_manager,
                DeviceManagerActivity::class.java,
                UserPermissionUtils.hasDevicePermission()
            ),
            FunItem(
                "门店管理",
                R.mipmap.icon_shop_manager,
                ShopManagerActivity::class.java,
                UserPermissionUtils.hasShopPermission()
            ),
            FunItem(
                "订单管理",
                R.mipmap.icon_order_manager,
                OrderManagerActivity::class.java,
                UserPermissionUtils.hasOrderPermission()
            ),
            FunItem(
                "人员管理",
                R.mipmap.icon_staff_manager,
                StaffManagerActivity::class.java,
                UserPermissionUtils.hasPersonPermission()
            ),
        )
    )

    val marketingList: MutableLiveData<List<FunItem>> = MutableLiveData(
        arrayListOf(
            FunItem(
                "折扣优惠",
                R.mipmap.icon_discounts_manager,
                DiscountsManagerActivity::class.java,
                UserPermissionUtils.hasMarketingPermission()
            ),
        )
    )

    val capitalList: MutableLiveData<List<FunItem>> = MutableLiveData(
        arrayListOf(
            FunItem(
                "分账管理",
                R.mipmap.icon_sub_account_manager,
                SubAccountManagerActivity::class.java,
                UserPermissionUtils.hasDistributionPermission()
            ),
        )
    )

    /**
     * 请求首页数据
     */
    fun requestHomeData() {
        launch(
            {
                // 总收益
                requestIncomeToday()
                // 未读消息数
                requestUnReadCount()
                // 消息列表
                requestMessageList()
            },
            {
                it.message?.let { it1 -> SToast.showToast(msg = it1) }
                Timber.d("请求失败或异常$it")
            },
            {
                Timber.d("请求结束")
            })
    }

    /**
     * 今日总收益
     */
    private suspend fun requestIncomeToday() {
        inComeVal.postValue(ApiRepository.dealApiResult(mCapitalRepo.totalIncomeToady()))
    }

    /**
     * 未读消息数量
     */
    private suspend fun requestUnReadCount() {
        // 未读消息数量
        val unReadList = ApiRepository.dealApiResult(
            mMessageRepo.messageTypeCount(
                ApiRepository.createRequestBody(
                    hashMapOf(
                        "appType" to 1,
                        "readStatus" to 0
                    )
                )
            )
        )
        // 累加
        var count = 0
        unReadList?.forEach {
            count += it.count
        }
        unReadMsgNum.postValue(count)
    }

    /**
     * 消息列表
     */
    private suspend fun requestMessageList() {
        val messageList = ApiRepository.dealApiResult(
            mMessageRepo.messageList(
                ApiRepository.createRequestBody(
                    hashMapOf(
                        "pageNum" to 1,
                        "pageSize" to 2,
                        "appType" to 1
                    )
                )
            )
        )
        lastMsgList.postValue(messageList?.items)
        // 测试数据
//        mHomeViewModel.lastMsgList.value = mutableListOf(
//            MessageEntity(0, 0, 0, 0, "", 0, "", "设备告警1", "您有一台设备无法启动了", "", 0, "2小时前", ""),
//            MessageEntity(0, 0, 0, 0, "", 0, "", "设备告警2", "您有一台设备无法启动了", "", 0, "2小时前", ""),
//            MessageEntity(0, 0, 0, 0, "", 0, "", "设备告警3", "您有一台设备无法启动了", "", 0, "2小时前", ""),
//            MessageEntity(0, 0, 0, 0, "", 0, "", "设备告警4", "您有一台设备无法启动了", "", 1, "2小时前", ""),
//            MessageEntity(0, 0, 0, 0, "", 0, "", "设备告警5", "您有一台设备无法启动了", "", 1, "2小时前", ""),
//            MessageEntity(0, 0, 0, 0, "", 0, "", "设备告警6", "您有一台设备无法启动了", "", 0, "2小时前", ""),
//        )
    }

    data class FunItem(
        val name: String,
        val icon: Int,
        val clz: Class<*>,
        var isShow: Boolean = true
    )
}
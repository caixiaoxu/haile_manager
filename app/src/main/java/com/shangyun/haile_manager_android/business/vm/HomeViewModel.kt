package com.shangyun.haile_manager_android.business.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.StringUtils
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.apiService.CapitalService
import com.shangyun.haile_manager_android.business.apiService.MessageService
import com.shangyun.haile_manager_android.data.entities.HomeIncomeEntity
import com.shangyun.haile_manager_android.data.entities.MessageEntity
import com.shangyun.haile_manager_android.data.model.ApiRepository
import com.shangyun.haile_manager_android.ui.activity.device.DeviceManagerActivity
import com.shangyun.haile_manager_android.ui.activity.discounts.DiscountsManagerActivity
import com.shangyun.haile_manager_android.ui.activity.order.OrderManagerActivity
import com.shangyun.haile_manager_android.ui.activity.shop.ShopManagerActivity
import com.shangyun.haile_manager_android.ui.activity.staff.StaffManagerActivity
import com.shangyun.haile_manager_android.ui.activity.subAccount.SubAccountManagerActivity
import com.shangyun.haile_manager_android.utils.DateTimeUtils
import com.shangyun.haile_manager_android.utils.UserPermissionUtils
import timber.log.Timber
import java.util.*

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

    //选择的时间
    val selectedDate: MutableLiveData<Date> = MutableLiveData(Date())
    val selectedDateVal: LiveData<String> = selectedDate.map {
        DateTimeUtils.formatDateTime("yyyy年MM月", it)
    }

    // 总收入
    val inComeVal: MutableLiveData<String> = MutableLiveData()

    // 最新消息数量
    val unReadMsgNum: MutableLiveData<Int> = MutableLiveData(0)

    // 最新消息
    val lastMsgList: MutableLiveData<List<MessageEntity>?> = MutableLiveData()

    // 收益趋势
    val homeIncomeList: MutableLiveData<List<HomeIncomeEntity>?> = MutableLiveData()

    // 功能管理
    val funcList: MutableLiveData<List<FunItem>> = MutableLiveData(
        arrayListOf(
            FunItem(
                StringUtils.getString(R.string.device_manager),
                R.mipmap.icon_device_manager,
                DeviceManagerActivity::class.java,
                UserPermissionUtils.hasDevicePermission()
            ),
            FunItem(
                StringUtils.getString(R.string.shop_manager),
                R.mipmap.icon_shop_manager,
                ShopManagerActivity::class.java,
                UserPermissionUtils.hasShopPermission()
            ),
            FunItem(
                StringUtils.getString(R.string.order_manager),
                R.mipmap.icon_order_manager,
                OrderManagerActivity::class.java,
                UserPermissionUtils.hasOrderPermission()
            ),
            FunItem(
                StringUtils.getString(R.string.staff_manager),
                R.mipmap.icon_staff_manager,
                StaffManagerActivity::class.java,
                UserPermissionUtils.hasPersonPermission()
            ),
        )
    )

    // 营销中心
    val marketingList: MutableLiveData<List<FunItem>> = MutableLiveData(
        arrayListOf(
            FunItem(
                StringUtils.getString(R.string.discount_manager),
                R.mipmap.icon_discounts_manager,
                DiscountsManagerActivity::class.java,
                UserPermissionUtils.hasMarketingPermission()
            ),
        )
    )

    // 资金管理
    val capitalList: MutableLiveData<List<FunItem>> = MutableLiveData(
        arrayListOf(
            FunItem(
                StringUtils.getString(R.string.sub_account_manager),
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
     * 首页收益趋势
     */
    fun requestHomeIncome() {
        launch(
            {
                val incomeList = ApiRepository.dealApiResult(
                    mCapitalRepo.homeInCome(
                        ApiRepository.createRequestBody(
                            hashMapOf(
                                "startTime" to DateTimeUtils.formatDateTime(
                                    DateTimeUtils.getMonthFirst(selectedDate.value)
                                ),
                                "endTime" to DateTimeUtils.formatDateTime(
                                    DateTimeUtils.getMonthLast(selectedDate.value)
                                )
                            ),
                        )
                    )
                )
                incomeList?.let { list ->
                    homeIncomeList.postValue(list)
                }
            },
            {
                it.message?.let { it1 -> SToast.showToast(msg = it1) }
                Timber.d("请求失败或异常$it")
            },
            {
                Timber.d("请求结束")
            }, false
        )

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
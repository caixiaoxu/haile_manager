package com.yunshang.haile_manager_android.business.vm

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.ui.base.BaseViewModel
import com.yunshang.haile_manager_android.business.apiService.StaffService
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.data.arguments.StaffPermission
import com.yunshang.haile_manager_android.data.entities.StaffDetailEntity
import com.yunshang.haile_manager_android.data.model.ApiRepository

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
class StaffDetailViewModel : BaseViewModel() {
    private val mStaffRepo = ApiRepository.apiClient(StaffService::class.java)

    var staffId: Int = -1

    val staffDetail: MutableLiveData<StaffDetailEntity> by lazy {
        MutableLiveData()
    }

    fun requestStaffDetailData() {
        if (-1 == staffId) {
            return
        }

        launch({
            ApiRepository.dealApiResult(mStaffRepo.requestStaffDetail(staffId))?.let {
                staffDetail.postValue(it)
            }
        })
    }

    /**
     * 返回权限列表id
     */
    fun getPermissionIds(): List<StaffPermission> =
        staffDetail.value?.menuList?.flatMap { menu ->
            arrayListOf<StaffPermission>().apply {
                add(StaffPermission(menu.id))
                addAll(menu.childList.map { child -> StaffPermission(child.id) })
            }
        } ?: listOf()

    /**
     * 返回营业数据的门店信息
     */
    fun getProfitShopList() = staffDetail.value?.menuList?.find { item ->
        item.perms == "league:profit"
    }?.dataPermissionDto?.dataPermissionShopDtoList

    /**
     * 返回营业数据的分账可查看信息
     */
    fun getProfitTypesList() = staffDetail.value?.menuList?.find { item ->
        item.perms == "league:profit"
    }?.dataPermissionDto?.fundsDistributionType?.toIntArray()

    fun deleteStaff(view: View) {
        if (-1 == staffId) {
            return
        }

        launch({
            ApiRepository.dealApiResult(
                mStaffRepo.deleteStaff(
                    ApiRepository.createRequestBody(
                        hashMapOf(
                            "id" to staffId
                        )
                    )
                )
            )
            LiveDataBus.post(BusEvents.STAFF_LIST_ITEM_DELETE_STATUS, staffId)
            jump.postValue(0)
        })
    }
}
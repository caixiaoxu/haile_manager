package com.shangyun.haile_manager_android.business.vm

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.ui.base.BaseViewModel
import com.shangyun.haile_manager_android.business.apiService.StaffService
import com.shangyun.haile_manager_android.business.event.BusEvents
import com.shangyun.haile_manager_android.data.entities.StaffDetailEntity
import com.shangyun.haile_manager_android.data.model.ApiRepository

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
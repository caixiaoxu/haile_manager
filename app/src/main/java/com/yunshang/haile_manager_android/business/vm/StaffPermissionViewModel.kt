package com.yunshang.haile_manager_android.business.vm

import android.content.Context
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.apiService.LoginUserService
import com.yunshang.haile_manager_android.business.apiService.StaffService
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.data.arguments.DataPermissionRequest
import com.yunshang.haile_manager_android.data.arguments.StaffPermission
import com.yunshang.haile_manager_android.data.arguments.StaffPermissionParams
import com.yunshang.haile_manager_android.data.entities.DataPermissionShopDto
import com.yunshang.haile_manager_android.data.model.ApiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
class StaffPermissionViewModel : BaseViewModel() {
    private val mStaffRepo = ApiRepository.apiClient(StaffService::class.java)
    private val mUserRepo = ApiRepository.apiClient(LoginUserService::class.java)

    private val specialPerms = listOf(
        "league:profit",
        "league:profit:home",
        "league:data:list",
        "league:profit:calendar",
        "league:profit:detail"
    )

    val permissionList: MutableLiveData<MutableList<StaffPermissionParams>> by lazy {
        MutableLiveData()
    }

    // 可查看数据的门店
    var shopList: List<DataPermissionShopDto>? = null
    // 可查看分账的数据
    var fundsDistributionTypes: List<Int>? = null

    var staffId: Int = -1

    var selectList: MutableList<StaffPermission> = mutableListOf()

    val isAll: MutableLiveData<Boolean> = MutableLiveData(false)

    /**
     * 处理数据，合并成一个集合
     */
    fun dealPermissionList() {
        launch({
            ApiRepository.dealApiResult(
                mUserRepo.permissionMenu()
            )?.let { list ->
                list.forEach { parent ->
                    parent.childList?.forEach { child ->
                        child.isOldCheck = if (selectList.isEmpty()) false
                        else null != selectList.find { item -> item.menuId == child.menuId }
                        child.isCheck = child.isOldCheck
                    }
                }
                // 合并
                permissionList.postValue(
                    list.map { StaffPermissionParams(it, it.childList) }
                        .filter { !it.child.isNullOrEmpty() }.toMutableList()
                )
            }
        })
    }

    /**
     * 获取选中的权限ids
     */
    fun getSelectPermissionIds(): ArrayList<StaffPermission> {
        val ids = ArrayList<StaffPermission>()
        permissionList.value?.let { list ->
            list.forEach {
                val idList =
                    it.child?.filter { child -> child.isCheck }
                        ?.map { child ->
                            StaffPermission(
                                child.menuId,
                                if (specialPerms.contains(child.perms)) DataPermissionRequest(
                                    fundsDistributionTypes, shopList?.map { item -> item.id },
                                ) else null
                            )
                        }
                if (!idList.isNullOrEmpty()) {
                    ids.add(
                        StaffPermission(
                            it.parent.menuId,
                            if (specialPerms.contains(it.parent.perms)) DataPermissionRequest(
                                fundsDistributionTypes, shopList?.map { item -> item.id },
                            ) else null
                        )
                    )
                    ids.addAll(idList)
                }
            }
        }
        return ids
    }

    fun selectAll() {
        permissionList.value?.let {
            it.forEach { staffParams ->
                staffParams.child?.forEach { entity ->
                    entity.isOldCheck = !isAll.value!!
                    entity.isCheck = entity.isOldCheck
                }
                staffParams.notifyPropertyChanged(BR.selectNum)
            }
        }
        isAll.value = !isAll.value!!
    }

    /**
     * 检测是否全选
     */
    fun checkSelectAll() {
        viewModelScope.launch(Dispatchers.IO) {
            permissionList.value?.let {
                isAll.postValue(it.all { staffParams ->
                    staffParams.child?.all { child -> child.isCheck } ?: true
                })
            }
        }
    }

    /**
     * 修改员工权限
     */
    fun updateStaffPermission(context: Context) {
        if (-1 == staffId) {
            return
        }

        launch({
            ApiRepository.dealApiResult(
                mStaffRepo.updateStaffPermission(
                    ApiRepository.createRequestBody(
                        hashMapOf(
                            "userId" to staffId,
                            "menuInfoList" to getSelectPermissionIds(),
                        )
                    )
                )
            )
            withContext(Dispatchers.Main) {
                SToast.showToast(context, R.string.update_success)
            }
            LiveDataBus.post(BusEvents.STAFF_DETAILS_STATUS, true)
            jump.postValue(0)
        })
    }
}
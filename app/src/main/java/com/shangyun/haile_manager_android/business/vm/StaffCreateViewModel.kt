package com.shangyun.haile_manager_android.business.vm

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.StringUtils
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.apiService.StaffService
import com.shangyun.haile_manager_android.business.event.BusEvents
import com.shangyun.haile_manager_android.data.arguments.SearchSelectParam
import com.shangyun.haile_manager_android.data.arguments.StaffParam
import com.shangyun.haile_manager_android.data.entities.StaffRoleEntity
import com.shangyun.haile_manager_android.data.model.ApiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

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
class StaffCreateViewModel : BaseViewModel() {
    private val mStaffRepo = ApiRepository.apiClient(StaffService::class.java)

    val name: MutableLiveData<String> by lazy {
        MutableLiveData()
    }
    val phone: MutableLiveData<String> by lazy {
        MutableLiveData()
    }
    val role: MutableLiveData<StaffRoleEntity> by lazy {
        MutableLiveData()
    }
    val needShopAndPermission: LiveData<Boolean> = role.map {
        !(null == it || StaffParam.isSpecialRole(it.role))
    }

    val takeChargeShop: MutableLiveData<List<SearchSelectParam>> by lazy {
        MutableLiveData()
    }
    val takeChargeShopStr: LiveData<String> = takeChargeShop.map {
        StringUtils.getString(if (it.isNullOrEmpty()) R.string.no_configure else R.string.configured)
    }

    val permission: MutableLiveData<ArrayList<Int>> by lazy {
        MutableLiveData()
    }
    val permissionStr: LiveData<String> = permission.map {
        StringUtils.getString(if (it.isNullOrEmpty()) R.string.no_configure else R.string.configured)
    }

    val roleList: MutableLiveData<List<StaffRoleEntity>> by lazy {
        MutableLiveData()
    }


    // 是否可提交
    val canSubmit: MediatorLiveData<Boolean> = MediatorLiveData(false).apply {
        addSource(name) {
            value = checkSubmit()
        }
        addSource(phone) {
            value = checkSubmit()
        }
        addSource(role) {
            value = checkSubmit()
        }
        addSource(takeChargeShop) {
            value = checkSubmit()
        }
        addSource(permission) {
            value = checkSubmit()
        }
    }

    /**
     * 检测是否可提交
     */
    private fun checkSubmit(): Boolean =
        !name.value.isNullOrEmpty() && !phone.value.isNullOrEmpty() && null != role.value
                && (StaffParam.isSpecialRole(role.value!!.role) || (null != takeChargeShop.value && !roleList.value.isNullOrEmpty()))

    fun requestRoleList() {
        launch({
            ApiRepository.dealApiResult(mStaffRepo.requestRoleList())?.let { list ->
                roleList.postValue(list.map { StaffRoleEntity(it) })
            }
        })
    }

    fun submit(view: View) {
        val params = hashMapOf<String, Any>(
            "name" to name.value!!,
            "account" to phone.value!!,
            "tagName" to role.value!!.role,
        )

        takeChargeShop.value?.let { list ->
            params["shopIdList"] = list.map { shop -> shop.id }
        }

        permission.value?.let {
            params["menuIdList"] = it
        }

        launch({
            ApiRepository.dealApiResult(
                mStaffRepo.createStaff(
                    ApiRepository.createRequestBody(params)
                )
            )
            withContext(Dispatchers.Main) {
                SToast.showToast(view.context, R.string.add_success)
            }
            LiveDataBus.post(BusEvents.STAFF_LIST_STATUS, true)
            jump.postValue(0)
        })
    }
}
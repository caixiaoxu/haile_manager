package com.shangyun.haile_manager_android.business.vm

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.StringUtils
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.apiService.StaffService
import com.shangyun.haile_manager_android.data.arguments.SearchSelectParam
import com.shangyun.haile_manager_android.data.entities.StaffRoleEntity
import com.shangyun.haile_manager_android.data.model.ApiRepository
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
    val takeChargeShop: MutableLiveData<SearchSelectParam> by lazy {
        MutableLiveData()
    }
    val takeChargeShopStr: LiveData<String> = takeChargeShop.map {
        StringUtils.getString(if (null == it) R.string.no_configure else R.string.configured)
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

    fun requestRoleList() {
        launch({
            ApiRepository.dealApiResult(mStaffRepo.requestRoleList())?.let { list ->
                roleList.postValue(list.map { StaffRoleEntity(it) })
            }
        }, {
            it.message?.let { it1 -> SToast.showToast(msg = it1) }
            Timber.d("请求失败或异常$it")
        }, {
            Timber.d("请求结束")
        })
    }

    fun submit(view: View) {

    }
}
package com.shangyun.haile_manager_android.business.vm

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lsy.framelib.ui.base.BaseViewModel
import com.shangyun.haile_manager_android.BR
import com.shangyun.haile_manager_android.data.arguments.StaffPermissionParams
import com.shangyun.haile_manager_android.data.entities.UserPermissionEntity
import com.shangyun.haile_manager_android.data.model.SPRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
    val permissionList: MutableLiveData<MutableList<StaffPermissionParams>> by lazy {
        MutableLiveData()
    }

    var selectList: List<Int> = arrayListOf()

    val isAll: MutableLiveData<Boolean> = MutableLiveData(false)

    /**
     * 处理数据，合并成一个集合
     */
    fun dealPermissionList() {
        viewModelScope.launch(Dispatchers.IO) {
            SPRepository.userPermissions?.let { list ->
                // 取出父权限和子权限
                val parent = ArrayList<UserPermissionEntity>()
                val child = HashMap<Int, MutableList<UserPermissionEntity>>()
                list.forEach {
                    if (0 == it.parentId) {
                        parent.add(it)
                    } else {
                        it.isCheck =
                            if (selectList.isEmpty()) false else selectList.contains(it.menuId)
                        if (child[it.parentId].isNullOrEmpty()) {
                            child[it.parentId] = mutableListOf()
                        }
                        child[it.parentId]!!.add(it)
                    }
                }
                // 合并
                permissionList.postValue(
                    parent.map { StaffPermissionParams(it, child[it.menuId]) }
                        .filter { !it.child.isNullOrEmpty() }.toMutableList()
                )
            }
        }
    }

    /**
     * 获取选中的权限ids
     */
    fun getSelectPermissionIds(): ArrayList<Int> {
        val ids = ArrayList<Int>()
        permissionList.value?.let { list ->
            list.forEach {
                val idList =
                    it.child?.filter { child -> child.isCheck }?.map { child -> child.menuId }
                if (!idList.isNullOrEmpty()) {
                    ids.add(it.parent.menuId)
                    ids.addAll(idList)
                }
            }
        }
        return ids
    }

    fun selectAll(view: View) {
        permissionList.value?.let {
            it.forEach { staffParams ->
                staffParams.child?.forEach { entity ->
                    entity.isCheck = !isAll.value!!
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
}
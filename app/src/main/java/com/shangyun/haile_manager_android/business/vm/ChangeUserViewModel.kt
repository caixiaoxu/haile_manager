package com.shangyun.haile_manager_android.business.vm

import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.shangyun.haile_manager_android.data.entities.CODE
import com.shangyun.haile_manager_android.data.entities.ChangeUserEntity
import com.shangyun.haile_manager_android.data.entities.ONE
import com.shangyun.haile_manager_android.data.entities.PASSWORD
import com.shangyun.haile_manager_android.data.model.SPRepository
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
class ChangeUserViewModel : BaseViewModel() {

    // 列表数据
    val userList: MutableLiveData<MutableList<ChangeUserEntity>> =
        MutableLiveData(SPRepository.changeUser!!)

    fun changeUser(data: ChangeUserEntity, mSharedViewModel: SharedViewModel) {
        launch({
            when (data.loginType) {
                PASSWORD -> mSharedViewModel.loginForPassword(
                    data.userInfo.userInfo.phone,
                    data.password ?: ""
                )
                CODE, ONE -> mSharedViewModel.login(
                    hashMapOf(
                        "token" to data.loginInfo.token,
                        "userId" to data.loginInfo.userId,
                    ), data.loginType
                )
            }
            jump.postValue(1)
        }, {
            it.message?.let { it1 -> SToast.showToast(msg = it1) }
            Timber.d("请求失败或异常$it")
            jump.postValue(2)
        }, {
        })
    }
}
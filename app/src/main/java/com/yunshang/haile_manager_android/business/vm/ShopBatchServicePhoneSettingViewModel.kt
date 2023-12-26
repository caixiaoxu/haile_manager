package com.yunshang.haile_manager_android.business.vm

import android.view.View
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.apiService.ShopService
import com.yunshang.haile_manager_android.data.entities.ShopAndPositionSelectEntity
import com.yunshang.haile_manager_android.data.model.ApiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Title :
 * Author: Lsy
 * Date: 2023/11/1 14:05
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class ShopBatchServicePhoneSettingViewModel : BaseViewModel() {
    private val mShopRepo = ApiRepository.apiClient(ShopService::class.java)

    // 选择的店铺点位
    val selectPositions: MutableLiveData<MutableList<ShopAndPositionSelectEntity>?> by lazy {
        MutableLiveData()
    }

    val selectPositionNames: LiveData<String> = selectPositions.map {
        when (val size = it?.flatMap { item -> item.positionList ?: listOf() }?.size ?: 0) {
            0 -> ""
            1 -> it?.firstOrNull()?.positionList?.firstOrNull()?.name ?: ""
            else -> "已选择${size}个营业点"
        }
    }

    val servicePhoneList: MutableLiveData<MutableList<ServicePhoneEntity>> =
        MutableLiveData(mutableListOf(ServicePhoneEntity(true)))

    fun save(v: View) {
        if (selectPositions.value.isNullOrEmpty()) return
        if (servicePhoneList.value?.any { it.phone.isNotEmpty() } != true) {
            SToast.showToast(v.context, "请至少输入一个客服电话")
            return
        }
        launch({
            ApiRepository.dealApiResult(
                mShopRepo.saveBatchServicePhoneSetting(
                    ApiRepository.createRequestBody(
                        hashMapOf(
                            "positionIds" to selectPositions.value?.flatMap {
                                it.positionList ?: listOf()
                            }?.mapNotNull { it.id },
                            "serviceTelephone" to servicePhoneList.value?.mapNotNull { it.phone.ifEmpty { null } }
                                ?.joinToString(",")
                        )
                    )
                )
            )
            withContext(Dispatchers.Main) {
                SToast.showToast(v.context, R.string.submit_success)
            }
            jump.postValue(0)
        })
    }

    data class ServicePhoneEntity(
        val isFirst: Boolean,
        var phone: String = ""
    ) : BaseObservable() {

        @get:Bindable
        var phoneVal: String
            get() = phone
            set(value) {
                phone = value
                notifyPropertyChanged(BR.phoneVal)
            }

        fun rightImg(): Int =
            if (isFirst) R.mipmap.icon_service_phone_add else R.mipmap.icon_service_phone_delete
    }
}
package com.yunshang.haile_manager_android.business.vm

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.apiService.ShopService
import com.yunshang.haile_manager_android.data.arguments.SearchSelectParam
import com.yunshang.haile_manager_android.data.entities.OperationCompensationSetting
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
class ShopBatchCompensationSettingViewModel : BaseViewModel() {
    private val mShopRepo = ApiRepository.apiClient(ShopService::class.java)

    // 选择的门店
    val selectShops: MutableLiveData<MutableList<SearchSelectParam>> by lazy {
        MutableLiveData()
    }

    val selectShopNames: LiveData<String> = selectShops.map {
        when (val size = it.size) {
            0 -> ""
            1 -> it.firstOrNull()?.name ?: ""
            else -> "已选择${size}个门店"
        }
    }

    val batchCompensationSetting: MutableLiveData<OperationCompensationSetting> = MutableLiveData(
        OperationCompensationSetting()
    )


    fun save(v: View) {
        if (selectShops.value.isNullOrEmpty() || null == batchCompensationSetting.value) return
        launch({
            ApiRepository.dealApiResult(
                mShopRepo.saveBatchCompensationSetting(
                    ApiRepository.createRequestBody(
                        GsonUtils.any2Json(batchCompensationSetting.value?.apply {
                            shopIdList = selectShops.value?.map { it.id }?.toIntArray()
                        })
                    )
                )
            )
            withContext(Dispatchers.Main) {
                SToast.showToast(v.context, R.string.save_success)
            }
            jump.postValue(0)
        })
    }
}
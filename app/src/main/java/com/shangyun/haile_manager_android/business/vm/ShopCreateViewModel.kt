package com.shangyun.haile_manager_android.business.vm

import android.view.View
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.shangyun.haile_manager_android.business.apiService.ShopService
import com.shangyun.haile_manager_android.data.entities.ShopDetailsEntity
import com.shangyun.haile_manager_android.data.entities.ShopTypeEntity
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
class ShopCreateViewModel : BaseViewModel() {
    private val mRepo = ApiRepository.apiClient(ShopService::class.java)

    val shopDetails: MutableLiveData<ShopDetailsEntity> = MutableLiveData(ShopDetailsEntity())

    // 门店类型列表
    val shopTypeList: MutableLiveData<List<ShopTypeEntity>> = MutableLiveData()

    // 门店类型
    val shopTypeValue: MutableLiveData<ShopTypeEntity> = MutableLiveData()

    // 学校名称
    val schoolNameValue: MutableLiveData<String> = MutableLiveData()

    // 所在区域
    val areaValue: MutableLiveData<String> = MutableLiveData()

    // 业务类型
    val businessTypeValue: MutableLiveData<String> = MutableLiveData()

    fun requestData() {
        launch(
            {
                val types = ApiRepository.dealApiResult(mRepo.shopTypeList())
                shopTypeList.postValue(types)
            },
            {
                it.message?.let { it1 -> SToast.showToast(msg = it1) }
                Timber.d("请求失败或异常$it")
            },
            {
                Timber.d("请求结束")
            })


    }


    fun submit(view: View) {
    }
}
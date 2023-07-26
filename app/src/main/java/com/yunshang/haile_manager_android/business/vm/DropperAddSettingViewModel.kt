package com.yunshang.haile_manager_android.business.vm

import android.view.View
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.apiService.DeviceService
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.data.arguments.DeviceCreateParam
import com.yunshang.haile_manager_android.data.arguments.SearchSelectParam
import com.yunshang.haile_manager_android.data.entities.DosingConfigs
import com.yunshang.haile_manager_android.data.entities.ExtAttrBean
import com.yunshang.haile_manager_android.data.entities.SkuEntity
import com.yunshang.haile_manager_android.data.entities.SkuFuncConfigurationParam
import com.yunshang.haile_manager_android.data.model.ApiRepository
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
class DropperAddSettingViewModel : BaseViewModel() {

    private val mRepo = ApiRepository.apiClient(DeviceService::class.java)

    val configurationList: MutableLiveData<MutableList<SkuEntity>> by lazy {
        MutableLiveData()
    }

    // 返回参数
    val resultData: MutableLiveData<List<SkuFuncConfigurationParam>> by lazy {
        MutableLiveData()
    }


    // goodsId
    var goodsId: Int = -1

    // spuid
    var spuId: Int = -1

    /**
     * 获取数据
     */
    fun requestData() {
        if (-1 == spuId) {
            return
        }
        launch({
            val list = ApiRepository.dealApiResult(mRepo.sku(spuId))
            list?.let {
                it.forEach { sku ->
                    if (sku.extAttr.isNotEmpty()) {
                        sku.extAttrValue =
                            GsonUtils.json2List(sku.extAttr, ExtAttrBean::class.java)?.apply {
                                forEach { ext ->
                                    ext.isCheck = true
                                }
                            }
                    }
                }
                configurationList.postValue(it)
            }
        })
    }


    /**
     * 提交设置
     */
    fun submit(view: View) {
        if (goodsId == -1) {
            val params = configurationList.value?.map {
                it.getDispenserRequestParams()
            } ?: arrayListOf()
            resultData.postValue(params)
        } else {
            launch({
                val params = configurationList.value?.map {
                    it.getDispenserRequestParams()
                } ?: arrayListOf()
                ApiRepository.dealApiResult(
                    mRepo.deviceUpdate(
                        ApiRepository.createRequestBody(
                            hashMapOf(
                                "id" to goodsId,
                                "items" to params,
                            )
                        )
                    )
                )
                withContext(Dispatchers.Main) {
                    LiveDataBus.post(BusEvents.DEVICE_DETAILS_STATUS, true)
                    SToast.showToast(view.context, R.string.update_success)
                }
                jump.postValue(0)
            })
        }
    }


}
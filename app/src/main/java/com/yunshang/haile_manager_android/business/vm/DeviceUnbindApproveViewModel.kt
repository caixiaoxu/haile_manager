package com.yunshang.haile_manager_android.business.vm

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.network.exception.CommonCustomException
import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.apiService.CategoryService
import com.yunshang.haile_manager_android.business.apiService.DeviceService
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.data.arguments.SearchSelectParam
import com.yunshang.haile_manager_android.data.entities.CategoryEntity
import com.yunshang.haile_manager_android.data.entities.DeviceUnbindApproveEntity
import com.yunshang.haile_manager_android.data.entities.ShopAndPositionSelectEntity
import com.yunshang.haile_manager_android.data.model.ApiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * Title :
 * Author: Lsy
 * Date: 2024/1/16 13:42
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class DeviceUnbindApproveViewModel : BaseViewModel() {
    private val mDeviceRepo = ApiRepository.apiClient(DeviceService::class.java)
    private val mCategoryRepo = ApiRepository.apiClient(CategoryService::class.java)

    val isBatch: MutableLiveData<Boolean> = MutableLiveData(false)

    // 选择的店铺
    val selectShopList: MutableLiveData<MutableList<SearchSelectParam>> by lazy {
        MutableLiveData()
    }

    val selectShopVal: LiveData<String> = selectShopList.map {
        when (val count: Int? = it?.size) {
            null, 0 -> ""
            1 -> it.firstOrNull()?.name ?: ""
            else -> "选择${count}家"
        }
    }

    // 选择的店铺点位
    val selectPositionList: MutableLiveData<MutableList<ShopAndPositionSelectEntity>> by lazy {
        MutableLiveData()
    }

    val selectPositionVal: LiveData<String> = selectPositionList.map {
        val positionList = it?.flatMap { item -> item.positionList ?: listOf() }
        when (val count: Int? = positionList?.size) {
            null, 0 -> ""
            1 -> positionList.firstOrNull()?.name ?: ""
            else -> "选择${count}个"
        }
    }

    // 设备类型
    val categoryList: MutableLiveData<List<CategoryEntity>> = MutableLiveData()

    // 选择的设备类型
    val selectDeviceCategoryList: MutableLiveData<List<CategoryEntity>?> by lazy {
        MutableLiveData()
    }

    val selectDeviceCategoryVal: LiveData<String> = selectDeviceCategoryList.map {
        when (val count: Int? = it?.size) {
            null, 0 -> "全部设备"
            1 -> it.firstOrNull()?.name ?: "全部设备"
            else -> "选择${count}个"
        }
    }

    // 选择的状态
    val selectStatus: MutableLiveData<SearchSelectParam> =
        MutableLiveData(SearchSelectParam(1, "未处理"))

    val isAll: MutableLiveData<Boolean> = MutableLiveData(false)

    val selectBatchNum: MutableLiveData<Int> = MutableLiveData(0)

    val selectBatchNumVal: LiveData<String> = selectBatchNum.map {
        if (0 == it) "" else "${StringUtils.getString(R.string.selected)} $it"
    }

    fun requestData() {
        launch({
            // 类目
            ApiRepository.dealApiResult(
                mCategoryRepo.category(1)
            )?.let {
                it.add(
                    0,
                    CategoryEntity(
                        id = 0,
                        name = StringUtils.getString(R.string.all_device)
                    ).apply {
                        onlyOne = true
                        isCheck = selectDeviceCategoryList.value?.contains(this) ?: false
                    })
                categoryList.postValue(it)
            }
        })
    }

    fun requestDeviceUnbindApproveList(
        page: Int,
        pageSize: Int,
        callBack: (responseList: ResponseList<out DeviceUnbindApproveEntity>?) -> Unit
    ) {
        launch({
            val result = ApiRepository.dealApiResult(
                mDeviceRepo.requestDeviceUnbindApproveList(
                    ApiRepository.createRequestBody(
                        hashMapOf(
                            "page" to page,
                            "pageSize" to pageSize,
                            "shopIdList" to selectShopList.value?.map { item -> item.id },
                            "positionIdList" to selectPositionList.value?.flatMap { item ->
                                item.positionList?.mapNotNull { pos -> pos.id } ?: listOf()
                            },
                            "categoryIdList" to selectDeviceCategoryList.value?.mapNotNull { item -> item.code },
                            "status" to if (0 == selectStatus.value?.id) null else selectStatus.value?.id
                        )
                    )
                )
            )
            withContext(Dispatchers.Main) {
                callBack(result)
            }
        }, {
            // 自己定义的错误显示报错提示
            Timber.d("请求失败或异常$it")
            withContext(Dispatchers.Main) {
                if (it is CommonCustomException) {
                    it.message?.let { it1 -> SToast.showToast(msg = it1) }
                } else {
                    SToast.showToast(msg = "网络开小差~")
                }
                callBack(null)
            }
        })
    }

    fun refreshSelectBatchNum(list: MutableList<DeviceUnbindApproveEntity>) {
        selectBatchNum.value = list.count { item -> item.selected }
        isAll.value =
            if (list.isNotEmpty()) list.all { item -> 1 != item.status || item.selected } else false
    }

    fun disposeDeviceUnbind(context: Context, idList: List<Int>, operateType: Int) {
        launch({
            ApiRepository.dealApiResult(
                mDeviceRepo.batchDeviceUnbindApprove(
                    ApiRepository.createRequestBody(
                        hashMapOf(
                            "idList" to idList,
                            "operateType" to operateType,
                        )
                    )
                )
            )
            LiveDataBus.post(BusEvents.DEVICE_UNBIND_APPROVE_STATUS, true)
            isBatch.postValue(false)
            withContext(Dispatchers.Main) {
                SToast.showToast(context, R.string.operate_success)
            }
        })
    }
}
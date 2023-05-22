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
import com.shangyun.haile_manager_android.business.apiService.CategoryService
import com.shangyun.haile_manager_android.business.apiService.ShopService
import com.shangyun.haile_manager_android.business.apiService.SubAccountService
import com.shangyun.haile_manager_android.business.event.BusEvents
import com.shangyun.haile_manager_android.data.arguments.SearchSelectParam
import com.shangyun.haile_manager_android.data.entities.CategoryEntity
import com.shangyun.haile_manager_android.data.entities.ShopBusinessTypeEntity
import com.shangyun.haile_manager_android.data.entities.StaffEntity
import com.shangyun.haile_manager_android.data.model.ApiRepository
import com.shangyun.haile_manager_android.utils.DateTimeUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

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
class SubAccountCreateViewModel : BaseViewModel() {
    private val mRepo = ApiRepository.apiClient(ShopService::class.java)
    private val mCategoryRepo = ApiRepository.apiClient(CategoryService::class.java)
    private val mSubAccountRepo = ApiRepository.apiClient(SubAccountService::class.java)

    // 分账账号
    val subAccount: MutableLiveData<StaffEntity> by lazy {
        MutableLiveData()
    }
    var userId: Int = -1

    // 业务类型
    val businessTypeList: MutableLiveData<List<ShopBusinessTypeEntity>> = MutableLiveData()
    val businessType: MutableLiveData<ShopBusinessTypeEntity> by lazy {
        MutableLiveData()
    }

    // 设备类型
    val categoryList: MutableLiveData<List<CategoryEntity>> = MutableLiveData()
    val deviceCategory: MutableLiveData<List<CategoryEntity>> by lazy {
        MutableLiveData()
    }
    val deviceCategoryVal: LiveData<String> = deviceCategory.map { list ->
        if (list.isNullOrEmpty()) "" else list.joinToString("，") { it.name }
    }
    var categoryIds: IntArray = intArrayOf()

    // 分账门店类型
    val subAccountShops: MutableLiveData<List<SearchSelectParam>> = MutableLiveData(arrayListOf())
    val subAccountShopsVal: LiveData<String> = subAccountShops.map { list ->
        StringUtils.getString(if (list.isNullOrEmpty()) R.string.no_configure else R.string.configured)
    }
    var shopIds: IntArray = intArrayOf()

    // 分账比例
    val subAccountRatio: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    // 生效日
    val effectiveDate: MutableLiveData<Date> by lazy {
        MutableLiveData()
    }
    val effectiveDateVal: LiveData<String> = effectiveDate.map {
        if (null == it) "" else DateTimeUtils.formatDateTime(it, "yyyy-MM-dd")
    }

    // 是否可提交
    val canSubmit: MediatorLiveData<Boolean> = MediatorLiveData(false).apply {
        addSource(subAccount) {
            value = checkSubmit()
        }
        addSource(deviceCategory) {
            value = checkSubmit()
        }
        addSource(subAccountShops) {
            value = checkSubmit()
        }
        addSource(subAccountRatio) {
            value = checkSubmit()
        }
        addSource(effectiveDate) {
            value = checkSubmit()
        }
    }

    /**
     * 检测是否可提交
     */
    private fun checkSubmit(): Boolean =
        ((-1 != userId || (null != subAccount.value && !deviceCategory.value.isNullOrEmpty()
                && !subAccountShops.value.isNullOrEmpty())) && null != effectiveDate.value && try {
            if (subAccountRatio.value.isNullOrEmpty())
                false
            else {
                subAccountRatio.value?.toInt()
                true
            }
        } catch (e: NumberFormatException) {
            e.printStackTrace()
            false
        })

    fun requestData() {
        launch({
            businessTypeList.postValue(ApiRepository.dealApiResult(mRepo.shopBusinessType()))
        })
    }

    fun requestDeviceCategoryList() {
        if (null == businessType.value) return

        launch({
            categoryList.postValue(ApiRepository.dealApiResult(mCategoryRepo.category(businessType.value!!.type)))
        })
    }

    fun submit(view: View) {
        launch({
            ApiRepository.dealApiResult(
                mSubAccountRepo.createSubAccount(
                    ApiRepository.createRequestBody(
                        hashMapOf(
                            "id" to if (-1 != userId) userId else subAccount.value!!.id,
                            "categoryIds" to if (-1 != userId) categoryIds else deviceCategory.value!!.map { it.id },
                            "shopIds" to if (-1 != userId) shopIds else subAccountShops.value!!.map { it.id },
                            "ratio" to subAccountRatio.value!!.toInt(),
                            "effectiveDate" to "${
                                DateTimeUtils.formatDateTime(
                                    effectiveDate.value!!,
                                    "yyyy-MM-dd"
                                )
                            } 00:00:00",
                        )
                    )
                )
            )
            if (-1 == userId) {
                LiveDataBus.post(BusEvents.SUB_ACCOUNT_LIST_STATUS, true)
                withContext(Dispatchers.Main) {
                    SToast.showToast(view.context, R.string.add_success)
                }
            } else {
                withContext(Dispatchers.Main) {
                    SToast.showToast(view.context, R.string.update_success)
                }
                LiveDataBus.post(BusEvents.SUB_ACCOUNT_DETAIL_STATUS, true)
            }
            jump.postValue(0)
        })
    }
}
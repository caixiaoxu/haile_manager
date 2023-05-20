package com.shangyun.haile_manager_android.business.vm

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.StringUtils
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.apiService.CategoryService
import com.shangyun.haile_manager_android.business.apiService.ShopService
import com.shangyun.haile_manager_android.data.arguments.SearchSelectParam
import com.shangyun.haile_manager_android.data.entities.CategoryEntity
import com.shangyun.haile_manager_android.data.entities.ShopBusinessTypeEntity
import com.shangyun.haile_manager_android.data.entities.StaffEntity
import com.shangyun.haile_manager_android.data.model.ApiRepository
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

    // 分账账号
    val subAccount: MutableLiveData<StaffEntity> by lazy {
        MutableLiveData()
    }

    // 业务类型
    val businessTypeList: MutableLiveData<List<ShopBusinessTypeEntity>> = MutableLiveData()
    val businessType: MutableLiveData<List<ShopBusinessTypeEntity>> by lazy {
        MutableLiveData()
    }
    val businessTypeVal: LiveData<String> = businessType.map { list ->
        if (list.isNullOrEmpty()) "" else list.joinToString("，") { it.businessName }
    }

    // 设备类型
    val categoryList: MutableLiveData<List<CategoryEntity>> = MutableLiveData()
    val deviceCategory: MutableLiveData<List<CategoryEntity>> by lazy {
        MutableLiveData()
    }
    val deviceCategoryVal: LiveData<String> = deviceCategory.map { list ->
        if (list.isNullOrEmpty()) "" else list.joinToString("，") { it.name }
    }

    // 分账门店类型
    val subAccountShops: MutableLiveData<List<SearchSelectParam>> = MutableLiveData()
    val subAccountShopsVal: LiveData<String> = subAccountShops.map { list ->
        StringUtils.getString(if (list.isNullOrEmpty()) R.string.no_configure else R.string.configured)
    }

    // 分账比例
    val subAccountRatio: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    // 生效日
    val effectiveDate: MutableLiveData<Date> by lazy {
        MutableLiveData()
    }

    // 是否可提交
    val canSubmit: MediatorLiveData<Boolean> = MediatorLiveData(false).apply {
        addSource(subAccount) {
            value = checkSubmit()
        }
        addSource(businessType) {
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
    private fun checkSubmit(): Boolean = false

    fun requestData() {
        launch({
            businessTypeList.postValue(ApiRepository.dealApiResult(mRepo.shopBusinessType()))
            categoryList.postValue(ApiRepository.dealApiResult(mCategoryRepo.category(1)))
        })
    }

    fun submit(view: View) {

    }
}
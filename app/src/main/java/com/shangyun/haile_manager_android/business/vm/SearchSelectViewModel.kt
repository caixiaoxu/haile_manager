package com.shangyun.haile_manager_android.business.vm

import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.StringUtils
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.apiService.ShopService
import com.shangyun.haile_manager_android.data.model.ApiRepository
import com.shangyun.haile_manager_android.data.rule.SearchSelectEntity
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
class SearchSelectViewModel : BaseViewModel() {
    private val mRepo = ApiRepository.apiClient(ShopService::class.java)

    companion object {
        const val SEARCH_TYPE = "searchType"

        const val SCHOOL: Int = 0
        const val LOCATION: Int = 1
    }

    // 搜索类型
    val searchType: MutableLiveData<Int> = MutableLiveData()

    // 搜索提示语
    val searchTitles = arrayOf(
        StringUtils.getString(R.string.school),
        StringUtils.getString(R.string.location_select),
    )

    // 搜索提示语
    val searchHints = arrayOf(
        StringUtils.getString(R.string.input_school_hint),
        StringUtils.getString(R.string.input_location_hint),
    )

    // 搜索内容
    val searchContent: MutableLiveData<String> = MutableLiveData()

    //
    val searchList: MutableLiveData<MutableList<out SearchSelectEntity>> = MutableLiveData()

    fun search() {
        launch({
            when (searchType.value) {
                SCHOOL -> searchSchoolList()
                LOCATION->searchLocationList()
            }
        }, {
            it.message?.let { it1 -> SToast.showToast(msg = it1) }
            Timber.d("请求失败或异常$it")
        }, {
            Timber.d("请求结束")
        }, false)
    }

    /**
     * 搜索学校列表
     */
    private suspend fun searchSchoolList() {
        val list = ApiRepository.dealApiResult(
            mRepo.schoolList(
                ApiRepository.createRequestBody(
                    hashMapOf(
                        "name" to (searchContent.value ?: "")
                    )
                )
            )
        )
        searchList.postValue(list)
    }

    /**
     * 搜索位置列表
     */
    private suspend fun searchLocationList() {
        val list = ApiRepository.dealApiResult(
            mRepo.schoolList(
                ApiRepository.createRequestBody(
                    hashMapOf(
                        "name" to (searchContent.value ?: "")
                    )
                )
            )
        )
        searchList.postValue(list)
    }
}
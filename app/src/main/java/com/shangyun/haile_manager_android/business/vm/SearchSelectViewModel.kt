package com.shangyun.haile_manager_android.business.vm

import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
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
        const val AREA: Int = 1
        const val MANSION: Int = 2
    }

    // 搜索类型
    val searchType: MutableLiveData<Int> = MutableLiveData()

    // 搜索提示语
    val searchTitles = arrayOf(
        "学校",
        "地址选择",
        "地址选择",
    )

    // 搜索提示语
    val searchHints = arrayOf(
        "请输入学校名称",
        "请输入您的详细地址",
        "请输入您的小区或大厦名称",
    )

    // 搜索内容
    val searchContent: MutableLiveData<String> = MutableLiveData()

    //
    val searchList: MutableLiveData<MutableList<out SearchSelectEntity>> = MutableLiveData()

    fun search() {
        launch({
            when (searchType.value) {
                SCHOOL -> searchSchoolList()
            }
        }, {
            it.message?.let { it1 -> SToast.showToast(msg = it1) }
            Timber.d("请求失败或异常$it")
        }, {
            Timber.d("请求结束")
        }, false)
    }

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
}
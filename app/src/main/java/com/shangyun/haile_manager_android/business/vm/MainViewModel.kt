package com.shangyun.haile_manager_android.business.vm

import com.lsy.framelib.ui.base.BaseViewModel
import com.shangyun.haile_manager_android.data.apiService.CommService
import com.shangyun.haile_manager_android.data.model.ApiRepository
import timber.log.Timber

/**
 * Title :
 * Author: Lsy
 * Date: 2023/3/17 17:02
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class MainViewModel : BaseViewModel() {
    private val mRepo = ApiRepository.apiClient(CommService::class.java)

    fun requestData() {
        Timber.d("开始请求")
//        launch(
//            {
//                val response = mRepo.test("aaa")
//                Timber.d("请求成功$response")
//            }, {
//                Timber.d("请求失败或异常$it")
//            }, {
//                Timber.d("请求结束")
//            }
//        )
    }
}
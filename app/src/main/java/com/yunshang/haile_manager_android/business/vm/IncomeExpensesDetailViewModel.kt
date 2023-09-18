package com.yunshang.haile_manager_android.business.vm

import com.lsy.framelib.ui.base.BaseViewModel
import com.yunshang.haile_manager_android.business.apiService.CapitalService
import com.yunshang.haile_manager_android.data.model.ApiRepository

/**
 * Title :
 * Author: Lsy
 * Date: 2023/9/18 11:40
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class IncomeExpensesDetailViewModel: BaseViewModel() {
    private val mCapitalRepo = ApiRepository.apiClient(CapitalService::class.java)

}
package com.yunshang.haile_manager_android.business.vm

import com.yunshang.haile_manager_android.business.apiService.CommonService
import com.yunshang.haile_manager_android.business.vm.base.BaseComposeViewModel
import com.yunshang.haile_manager_android.data.model.ApiRepository

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
class MainNewViewModel : BaseComposeViewModel() {
    private val mCommonRepo = ApiRepository.apiClient(CommonService::class.java)
}
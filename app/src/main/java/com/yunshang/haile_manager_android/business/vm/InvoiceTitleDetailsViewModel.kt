package com.yunshang.haile_manager_android.business.vm

import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.ui.base.BaseViewModel
import com.yunshang.haile_manager_android.data.entities.InvoiceTitleEntity

/**
 * Title :
 * Author: Lsy
 * Date: 2023/12/19 14:07
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class InvoiceTitleDetailsViewModel : BaseViewModel() {
    val invoiceTitle: MutableLiveData<InvoiceTitleEntity> by lazy {
        MutableLiveData()
    }

}
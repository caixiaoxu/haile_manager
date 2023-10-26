package com.yunshang.haile_manager_android.data.arguments

import androidx.databinding.BaseObservable
import com.yunshang.haile_manager_android.data.entities.RewardsConfig

/**
 * Title :
 * Author: Lsy
 * Date: 2023/6/15 15:51
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class HaixinSchemeConfigCreateParams(
    var id: Int? = null,
    var balanceLimit: String? = null,
    var configName: String? = null,
    var discountProportion: Long? = null,
    var isAllowRefund: Int = 0,
    var isForceUse: Int? = null,
    var operatorId: Int? = null,
    var rewards: List<RewardsConfig> = ArrayList(),
    var updateRewards: List<RewardsConfig> = ArrayList(),
    var shopId: Int? = null,
    var shopIds: List<Int>? = null,
    @Transient
    var exchangeRate: Int = 1,
) : BaseObservable() {
}
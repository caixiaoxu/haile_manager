package com.yunshang.haile_manager_android.data.entities

import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.R

/**
 * Title :
 * Author: Lsy
 * Date: 2023/9/5 16:35
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class BankCardEntity(
    val bankAccountNo: String,
    val bankCardTypeName: String,
    val bankImage: String,
    val bankName: String,
    val id: Int,
    val state: Int
) {
    val nameVal: String
        get() = "${bankName}${
            if (bankAccountNo.length > 4) "（${
                bankAccountNo.substring(
                    bankAccountNo.length - 4
                )
            }）" else ""
        }"

    val stateVal: String
        get() = StringUtils.getStringArray(R.array.bank_card_state_arr)[state]

    val bankCardNo: String
        get() {
            val sb = StringBuilder()
            val len = bankAccountNo.length
//            for (i in 0 until len - 4) {
//                if (!bankAccountNo[i].equals(" ")) {
//                    sb.append("•")
//                } else {
//                    sb.append(" ")
//                }
//            }
//            sb.append(bankAccountNo.substring(len - 4))

            if (len >= 4){
                sb.append("••••  ••••  •••• ")
                sb.append(bankAccountNo.substring(len - 4))
            }
            return sb.toString()
        }
}
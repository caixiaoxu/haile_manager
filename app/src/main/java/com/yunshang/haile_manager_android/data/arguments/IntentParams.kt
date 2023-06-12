package com.yunshang.haile_manager_android.data.arguments

import android.content.Intent
import android.os.Bundle
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.data.entities.RealNameAuthDetailEntity

/**
 * Title :
 * Author: Lsy
 * Date: 2023/6/9 11:31
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
object IntentParams {

    object WalletParams {
        private const val TotalBalance = "totalBalance"
        private const val RealNameAuthStatus = "realNameAuthStatus"

        /**
         * 包装参数
         */
        fun pack(balance: String, isAuth: Boolean): Bundle = Bundle().apply {
            putString(TotalBalance, balance)
            putBoolean(RealNameAuthStatus, isAuth)
        }

        /**
         * 解析TotalBalance
         */
        fun parseTotalBalance(intent: Intent): String? = intent.getStringExtra(TotalBalance)

        /**
         * 解析TotalBalance
         */
        fun parseRealNameAuthStatus(intent: Intent): Boolean =
            intent.getBooleanExtra(RealNameAuthStatus, false)
    }

    object WalletWithdrawParams{
        private const val TotalBalance = "totalBalance"

        /**
         * 包装参数
         */
        fun pack(balance: String): Bundle = Bundle().apply {
            putString(TotalBalance, balance)
        }

        /**
         * 解析TotalBalance
         */
        fun parseTotalBalance(intent: Intent): String? = intent.getStringExtra(TotalBalance)
    }

    object RealNameAuthParams {
        private const val AuthInfo = "authInfo"

        /**
         * 包装参数
         */
        fun pack(authInfo: RealNameAuthDetailEntity): Bundle = Bundle().apply {
            putString(AuthInfo, GsonUtils.any2Json(authInfo))
        }

        fun parseAuthInfo(intent: Intent): RealNameAuthDetailEntity? = GsonUtils.json2Class(
            intent.getStringExtra(
                AuthInfo
            ), RealNameAuthDetailEntity::class.java
        )
    }

}
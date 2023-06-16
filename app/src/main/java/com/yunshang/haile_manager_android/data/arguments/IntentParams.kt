package com.yunshang.haile_manager_android.data.arguments

import android.content.Intent
import android.os.Bundle
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.business.vm.SearchSelectRadioViewModel
import com.yunshang.haile_manager_android.data.entities.AppVersionEntity
import com.yunshang.haile_manager_android.data.entities.RealNameAuthDetailEntity
import com.yunshang.haile_manager_android.data.entities.SchemeConfigsDetailEntity
import com.yunshang.haile_manager_android.ui.activity.common.SearchSelectRadioActivity

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

    object CommonParams {
        private const val ID = "id"

        /**
         * 包装参数
         */
        fun pack(id: Int): Bundle = Bundle().apply {
            putInt(ID, id)
        }

        fun parseId(intent: Intent): Int = intent.getIntExtra(ID, -1)
    }


    object ShopParams {
        private const val ShopId = "shopId"
        private const val ShopName = "shopName"

        /**
         * 包装参数
         */
        fun pack(shopId: Int, shopName: String): Bundle = Bundle().apply {
            putInt(ShopId, shopId)
            putString(ShopName, shopName)
        }

        fun parseShopId(intent: Intent): Int = intent.getIntExtra(ShopId, -1)

        fun parseShopName(intent: Intent): String? = intent.getStringExtra(ShopName)
    }

    object SearchSelectTypeParam {
        const val SearchSelectType = "searchSelectType"
        const val StaffId = "staffId"
        const val CategoryId = "categoryId"
        const val ShopResultCode = 0x90001
        const val DeviceModelResultCode = 0x90002
        const val ResultData = "resultData"

        const val SearchSelectTypeShop = 0
        const val SearchSelectTypeDeviceModel = 1
        const val SearchSelectTypeTakeChargeShop = 2
        const val SearchSelectTypeRechargeShop = 4

        fun pack(
            searchSelectType: Int? = null,
            categoryId: Int? = null,
            staffId: Int? = null
        ): Bundle = Bundle().apply {
            searchSelectType?.let {
                putInt(SearchSelectType, it)
            }
            categoryId?.let {
                putInt(CategoryId, it)
            }
            staffId?.let {
                putInt(StaffId, it)
            }
        }
    }

    object WalletParams {
        private const val RealNameAuthStatus = "realNameAuthStatus"

        /**
         * 包装参数
         */
        fun pack(isAuth: Boolean): Bundle = Bundle().apply {
            putBoolean(RealNameAuthStatus, isAuth)
        }

        /**
         * 解析TotalBalance
         */
        fun parseRealNameAuthStatus(intent: Intent): Boolean =
            intent.getBooleanExtra(RealNameAuthStatus, false)
    }

    object WalletWithdrawParams {
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

    object RechargeSuccessParams {
        private const val AMOUNT = "amount"

        /**
         * 包装参数
         */
        fun pack(amount: String): Bundle = Bundle().apply {
            putString(AMOUNT, amount)
        }

        fun parseAmount(intent: Intent): String? = intent.getStringExtra(AMOUNT)
    }

    object VersionParams {
        private const val VERSIONINFO = "versionInfo"

        /**
         * 包装参数
         */
        fun pack(appVersion: AppVersionEntity): Bundle = Bundle().apply {
            putString(VERSIONINFO, GsonUtils.any2Json(appVersion))
        }

        fun parseVersionInfo(intent: Intent): AppVersionEntity? =
            GsonUtils.json2Class(intent.getStringExtra(VERSIONINFO), AppVersionEntity::class.java)
    }
}
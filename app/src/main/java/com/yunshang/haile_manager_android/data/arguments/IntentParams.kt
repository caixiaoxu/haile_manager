package com.yunshang.haile_manager_android.data.arguments

import android.content.Intent
import android.os.Bundle
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.data.entities.AppVersionEntity
import com.yunshang.haile_manager_android.data.entities.MessageSubTypeEntity
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

    object UserParams {
        private const val UserId = "userId"
        private const val UserName = "userName"
        private const val Phone = "phone"

        /**
         * 包装参数
         */
        fun pack(userId: Int? = null, userName: String? = null, phone: String? = null): Bundle =
            Bundle().apply {
                userId?.let {
                    putInt(UserId, userId)
                }
                userName?.let {
                    putString(UserName, userName)
                }
                phone?.let {
                    putString(Phone, phone)
                }
            }

        fun parseUserId(intent: Intent): Int = intent.getIntExtra(UserId, -1)

        fun parseUserName(intent: Intent): String? = intent.getStringExtra(UserName)
        fun parsePhone(intent: Intent): String? = intent.getStringExtra(Phone)
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

    object OrderParams {
        private const val OrderId = "orderId"
        private const val OrderNo = "orderNo"

        /**
         * 包装参数
         */
        fun pack(orderId: Int? = null, orderNo: String? = null): Bundle = Bundle().apply {
            orderId?.let {
                putInt(OrderId, orderId)
            }
            orderNo?.let {
                putString(OrderNo, orderNo)
            }
        }

        fun parseOrderId(intent: Intent): Int = intent.getIntExtra(OrderId, -1)
        fun parseOrderNo(intent: Intent): String? = intent.getStringExtra(OrderNo)
    }

    object OrderDetailParams {
        private const val IsAppoint = "isAppoint"

        /**
         * 包装参数
         */
        fun pack(orderId: Int, isAppoint: Boolean = false): Bundle = Bundle().apply {
            putAll(OrderParams.pack(orderId))
            putBoolean(IsAppoint, isAppoint)
        }

        fun parseOrderId(intent: Intent): Int = OrderParams.parseOrderId(intent)
        fun parseIsAppoint(intent: Intent): Boolean = intent.getBooleanExtra(IsAppoint, false)
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

    object RechargeAccountDetailParams {

        /**
         * 包装参数
         */
        fun pack(userId: Int, shopId: Int, shopName: String): Bundle = Bundle().apply {
            putAll(UserParams.pack(userId))
            putAll(ShopParams.pack(shopId, shopName))
        }

        fun parseUserId(intent: Intent): Int = UserParams.parseUserId(intent)
        fun parseShopId(intent: Intent): Int = ShopParams.parseShopId(intent)
        fun parseShopName(intent: Intent): String? = ShopParams.parseShopName(intent)
    }

    object HaiXinParams {
        private const val Reach = "reach"
        private const val Reward = "reward"

        /**
         * 包装参数
         */
        fun pack(reach: Int, reward: Int): Bundle = Bundle().apply {
            putInt(Reach, reach)
            putInt(Reward, reward)
        }

        fun parseReach(intent: Intent): Int = intent.getIntExtra(Reach, 0)
        fun parseReward(intent: Intent): Int = intent.getIntExtra(Reward, 0)

    }

    object MessageListParams {
        private const val TypeId = "typeId"
        private const val MessageName = "messageName"

        /**
         * 包装参数
         */
        fun pack(
            typeId: Int,
            messageName: String,
        ): Bundle =
            Bundle().apply {
                putInt(TypeId, typeId)
                putString(MessageName, messageName)
            }

        fun parseTypeId(intent: Intent): Int = intent.getIntExtra(TypeId, -1)
        fun parseMessageName(intent: Intent): String? = intent.getStringExtra(MessageName)
    }

    object MessageSettingParams {
        private const val SubTypeList = "subTypeList"

        /**
         * 包装参数
         */
        fun pack(json: String): Bundle =
            Bundle().apply { putString(SubTypeList, json) }

        fun parseSubTypeList(intent: Intent): MutableList<MessageSubTypeEntity>? =
            GsonUtils.json2List(
                intent.getStringExtra(SubTypeList),
                MessageSubTypeEntity::class.java
            )
    }

    object RechargeRecycleParams {

        /**
         * 包装参数
         */
        fun pack(
            userId: Int,
            phone: String,
            shopId: Int,
            shopName: String,
            reach: Int,
            reward: Int
        ): Bundle =
            Bundle().apply {
                putAll(UserParams.pack(userId, phone = phone))
                putAll(ShopParams.pack(shopId, shopName))
                putAll(HaiXinParams.pack(reach, reward))
            }

        fun parseUserId(intent: Intent): Int = UserParams.parseUserId(intent)
        fun parsePhone(intent: Intent): String? = UserParams.parsePhone(intent)

        fun parseShopId(intent: Intent): Int = ShopParams.parseShopId(intent)

        fun parseShopName(intent: Intent): String? = ShopParams.parseShopName(intent)
        fun parseReach(intent: Intent): Int = HaiXinParams.parseReach(intent)
        fun parseReward(intent: Intent): Int = HaiXinParams.parseReward(intent)
    }

    object WebViewParams {
        private const val Url = "url"
        private const val NeedFilter = "needFilter"
        private const val Title = "title"
        private const val AutoWebTitle = "autoWebTitle"

        /**
         * 包装参数
         */
        fun pack(
            url: String,
            needFilter: Boolean = false,
            title: String? = null,
            autoWebTitle: Boolean = true
        ): Bundle = Bundle().apply {
            putString(Url, url)
            putBoolean(NeedFilter, needFilter)
            title?.let {
                putString(Title, title)
            }
            putBoolean(AutoWebTitle, autoWebTitle)
        }

        fun parseUrl(intent: Intent): String? = intent.getStringExtra(Url)
        fun parseNeedFilter(intent: Intent): Boolean = intent.getBooleanExtra(NeedFilter, false)
        fun parseTitle(intent: Intent): String? = intent.getStringExtra(NeedFilter)
        fun parseAutoWebTitle(intent: Intent): Boolean = intent.getBooleanExtra(AutoWebTitle, true)
    }
}
package com.yunshang.haile_manager_android.data.arguments

import android.content.Intent
import android.os.Bundle
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.data.common.DeviceCategory
import com.yunshang.haile_manager_android.data.entities.*

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

    object LoginParams {
        private const val Phone = "phone"

        /**
         * 包装参数
         */
        fun pack(phone: String): Bundle = Bundle().apply {
            putString(Phone, phone)
        }

        fun parsePhone(intent: Intent): String? = intent.getStringExtra(Phone)
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

    object DeviceParams {
        private const val CategoryId = "categoryId"
        private const val CategoryCode = "categoryCode"
        private const val CommunicationType = "communicationType"

        /**
         * 包装参数
         */
        fun pack(
            categoryId: Int? = -1,
            categoryCode: String?,
            communicationType: Int? = -1
        ): Bundle =
            Bundle().apply {
                categoryId?.let {
                    putInt(CategoryId, categoryId)
                }
                categoryCode?.let {
                    putString(CategoryCode, categoryCode)
                }
                communicationType?.let {
                    putInt(CommunicationType, communicationType)
                }
            }

        fun parseCategoryId(intent: Intent): Int = intent.getIntExtra(CommunicationType, -1)
        fun parseCategoryCode(intent: Intent): String? = intent.getStringExtra(CategoryCode)
        fun parseCommunicationType(intent: Intent): Int = intent.getIntExtra(CommunicationType, -1)
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

    object ShopBusinessHoursParams {
        private const val ShopBusinessHours = "ShopBusinessHours"
        const val ResultCode = 0x70001

        fun pack(hours: MutableList<BusinessHourEntity>? = null): Bundle =
            Bundle().apply {
                hours?.let {
                    putString(ShopBusinessHours, GsonUtils.any2Json(hours))
                }
            }

        fun parseShopBusinessHoursJson(intent: Intent): String? =
            intent.getStringExtra(ShopBusinessHours)

        fun parseShopBusinessHours(intent: Intent): MutableList<BusinessHourEntity>? =
            GsonUtils.json2List(
                intent.getStringExtra(ShopBusinessHours),
                BusinessHourEntity::class.java
            )
    }

    object ShopPaySettingsParams {
        private const val ShopIds = "shopIds"
        private const val ShopPaySettings = "shopPaySettings"
        const val ResultCode = 10003

        /**
         * 包装参数
         */
        fun pack(
            shopIds: IntArray? = null,
            shopPaySettings: ShopPaySettingsEntity? = null
        ): Bundle =
            Bundle().apply {
                shopIds?.let {
                    putIntArray(ShopIds, shopIds)
                }
                shopPaySettings?.let {
                    putString(ShopPaySettings, GsonUtils.any2Json(shopPaySettings))
                }
            }

        fun parseShopIds(intent: Intent): IntArray? = intent.getIntArrayExtra(ShopIds)

        fun parseShopPaySettings(intent: Intent): ShopPaySettingsEntity? = GsonUtils.json2Class(
            intent.getStringExtra(ShopPaySettings),
            ShopPaySettingsEntity::class.java
        )

        /**
         * 包装参数
         */
        fun packResult(shopPaySettings: ShopPaySettingsEntity): Bundle = Bundle().apply {
            putString(ShopPaySettings, GsonUtils.any2Json(shopPaySettings))
        }
    }

    object SearchSelectTypeParam {
        const val SearchSelectType = "searchSelectType"
        const val StaffId = "staffId"
        const val CategoryId = "categoryId"
        const val ShopIdList = "shopIdList"
        const val MustSelect = "mustSelect"
        const val MoreSelect = "moreSelect"
        const val SelectList = "selectList"
        const val ShopResultCode = 0x90001
        const val DeviceModelResultCode = 0x90002
        const val ResultData = "resultData"

        const val SearchSelectTypeShop = 0
        const val SearchSelectTypeDeviceModel = 1
        const val SearchSelectTypeTakeChargeShop = 2
        const val SearchSelectTypeRechargeShop = 4
        const val SearchSelectTypePaySettingsShop = 5

        fun pack(
            searchSelectType: Int? = null,
            categoryId: Int? = null,
            staffId: Int? = null,
            shopIdList: IntArray? = null,
            mustSelect: Boolean = true,
            moreSelect: Boolean = false,
            selectArr: IntArray = intArrayOf()
        ): Bundle = Bundle().apply {
            searchSelectType?.let {
                putInt(SearchSelectType, it)
            }
            shopIdList?.let {
                putIntArray(ShopIdList, shopIdList)
            }
            categoryId?.let {
                putInt(CategoryId, it)
            }
            staffId?.let {
                putInt(StaffId, it)
            }
            putBoolean(MustSelect, mustSelect)
            putBoolean(MoreSelect, moreSelect)
            putIntArray(SelectList, selectArr)
        }

        fun parseSearchSelectType(intent: Intent): Int = intent.getIntExtra(SearchSelectType, -1)
        fun parseCategoryId(intent: Intent): Int = intent.getIntExtra(CategoryId, -1)
        fun parseStaffId(intent: Intent): Int = intent.getIntExtra(StaffId, -1)
        fun parseShopIdList(intent: Intent): IntArray? = intent.getIntArrayExtra(ShopIdList)
        fun parseMustSelect(intent: Intent): Boolean = intent.getBooleanExtra(MustSelect, true)
        fun parseMoreSelect(intent: Intent): Boolean = intent.getBooleanExtra(MoreSelect, false)
        fun parseSelectList(intent: Intent): IntArray? = intent.getIntArrayExtra(SelectList)
    }

    object DeviceCategoryModelParams {
        const val ResultCode = 0x90002
        private const val SpuId = "SpuId"
        private const val CategoryName = "categoryName"
        private const val DeviceFeature = "DeviceFeature"
        private const val ExtAttrDto = "ExtAttrDto"
        fun packResult(
            sId: Int,
            cName: String?,
            feature: String,
            cId: Int?,
            code: String?,
            communicationType: Int,
            ignorePayCodeFlag: Boolean,
            extAttrDto: SpuExtAttrDto
        ): Bundle = Bundle().apply {
            putInt(SpuId, sId)
            cName?.let {
                putString(CategoryName, cName)
            }
            putString(DeviceFeature, feature)
            cId?.let {
                putInt(DeviceCategory.CategoryId, cId)
            }
            code?.let {
                putString(DeviceCategory.CategoryCode, code)
            }
            putInt(DeviceCategory.CommunicationType, communicationType)
            putBoolean(DeviceCategory.IgnorePayCodeFlag, ignorePayCodeFlag)
            putString(ExtAttrDto, GsonUtils.any2Json(extAttrDto))
        }

        fun parseSpuId(intent: Intent): Int = intent.getIntExtra(SpuId, -1)
        fun parseCategoryName(intent: Intent): String? = intent.getStringExtra(CategoryName)
        fun parseDeviceFeature(intent: Intent): String? = intent.getStringExtra(DeviceFeature)
        fun parseCategoryId(intent: Intent): Int = intent.getIntExtra(DeviceCategory.CategoryId, -1)
        fun parseCategoryCode(intent: Intent): String? =
            intent.getStringExtra(DeviceCategory.CategoryCode)

        fun parseCommunicationType(intent: Intent): Int =
            intent.getIntExtra(DeviceCategory.CommunicationType, -1)

        fun parseIgnorePayCodeFlag(intent: Intent): Boolean =
            intent.getBooleanExtra(DeviceCategory.IgnorePayCodeFlag, false)

        fun parseExtAttrDtoJson(intent: Intent): String? = intent.getStringExtra(ExtAttrDto)

        fun parseExtAttrDto(intent: Intent): SpuExtAttrDto? =
            GsonUtils.json2Class(parseExtAttrDtoJson(intent), SpuExtAttrDto::class.java)
    }

    object DeviceFunctionConfigurationParams {
        private const val GoodId = "goodId"
        private const val SpuId = "spuId"
        private const val OldFuncConfiguration = "oldFuncConfiguration"
        const val ResultCode = 0x90003
        const val ResultData = "ResultData"

        /**
         * 包装参数
         */
        fun pack(
            goodId: Int? = -1,
            spuId: Int? = -1,
            categoryCode: String?,
            communicationType: Int? = -1,
            oldFuncConfiguration: List<SkuFuncConfigurationParam>?
        ): Bundle = Bundle().apply {
            putAll(
                DeviceParams.pack(
                    categoryCode = categoryCode,
                    communicationType = communicationType
                )
            )
            goodId?.let {
                putInt(GoodId, goodId)
            }
            spuId?.let {
                putInt(SpuId, spuId)
            }
            oldFuncConfiguration?.let {
                putString(OldFuncConfiguration, GsonUtils.any2Json(oldFuncConfiguration))
            }
        }

        fun parseGoodId(intent: Intent): Int = intent.getIntExtra(GoodId, -1)
        fun parseSpuId(intent: Intent): Int = intent.getIntExtra(SpuId, -1)
        fun parseOldFuncConfiguration(intent: Intent): List<SkuFuncConfigurationParam>? =
            GsonUtils.json2List(
                intent.getStringExtra(OldFuncConfiguration),
                SkuFuncConfigurationParam::class.java
            )

        fun packResult(skuFuncConfiguration: List<SkuFuncConfigurationParam>) = Bundle().apply {
            putString(ResultData, GsonUtils.any2Json(skuFuncConfiguration))
        }

        fun parseSkuFuncConfiguration(intent: Intent): List<SkuFuncConfigurationParam>? =
            GsonUtils.json2List(
                intent.getStringExtra(ResultData),
                SkuFuncConfigurationParam::class.java
            )
    }

    object DeviceFunConfigurationV2Params {
        private const val SpuId = "spuId"
        private const val GoodId = "goodId"
        private const val ExtAttrDto = "SpuExtAttrDto"
        const val ResultCode = 0x90003
        private const val SkuExtAttrDto = "SkuExtAttrDto"

        /**
         * 包装参数
         */
        fun pack(
            spuId: Int? = -1,
            categoryCode: String?,
            communicationType: Int? = -1,
            extJson: String? = null,
            skuExtAttrDto: MutableList<SkuFunConfigurationV2Param>? = null,
            goodId: Int? = null
        ): Bundle = Bundle().apply {
            spuId?.let {
                putInt(SpuId, spuId)
            }
            putAll(
                DeviceParams.pack(
                    categoryCode = categoryCode,
                    communicationType = communicationType
                )
            )
            extJson?.let {
                putString(ExtAttrDto, it)
            }
            skuExtAttrDto?.let {
                putString(SkuExtAttrDto, GsonUtils.any2Json(it))
            }
            goodId?.let {
                putInt(GoodId, goodId)
            }
        }

        fun parseSpuId(intent: Intent): Int = intent.getIntExtra(SpuId, -1)

        fun parseExtAttrDtoJson(intent: Intent): String? = intent.getStringExtra(ExtAttrDto)

        fun parseExtAttrDto(intent: Intent): SpuExtAttrDto? =
            GsonUtils.json2Class(parseExtAttrDtoJson(intent), SpuExtAttrDto::class.java)

        fun packResult(json: String): Bundle = Bundle().apply {
            putString(SkuExtAttrDto, json)
        }

        fun parseSkuExtAttrDto(intent: Intent): MutableList<SkuFunConfigurationV2Param>? =
            GsonUtils.json2List(
                intent.getStringExtra(SkuExtAttrDto),
                SkuFunConfigurationV2Param::class.java
            )

        fun parseGoodId(intent: Intent): Int = intent.getIntExtra(GoodId, -1)
    }

    object DeviceFunConfigurationAddV2Params {
        private const val SkuId = "skuId"
        private const val CanAdd = "canAdd"
        private const val SkuExtAttrDto = "SkuExtAttrDto"

        fun pack(
            skuId: Int,
            skuExtAttrDto: MutableList<ExtAttrDtoItem>,
            canAdd: Boolean = false,
        ): Bundle =
            Bundle().apply {
                putInt(SkuId, skuId)
                putBoolean(CanAdd, canAdd)
                putString(SkuExtAttrDto, GsonUtils.any2Json(skuExtAttrDto))
            }

        fun parseSkuId(intent: Intent): Int = intent.getIntExtra(SkuId, -1)
        fun parseCanAdd(intent: Intent): Boolean = intent.getBooleanExtra(CanAdd, false)

        fun parseSkuExtAttrDto(intent: Intent): MutableList<ExtAttrDtoItem>? =
            GsonUtils.json2List(
                intent.getStringExtra(SkuExtAttrDto),
                ExtAttrDtoItem::class.java
            )
    }

    object DeviceManagerParams {
        const val CategoryBigType_WashDryer = 0
        const val CategoryBigType_Hair = 1
        const val CategoryBigType_Shower = 2
        const val CategoryBigType_Dispenser = 3
        const val CategoryBigType_Drink = 4

        private const val Shop = "Shop"
        private const val CategoryBigType = "categoryBigType"

        /**
         * 包装参数
         */
        fun pack(shop: SearchSelectParam? = null, categoryBigType: Int = -1): Bundle =
            Bundle().apply {
                shop?.let {
                    putString(Shop, GsonUtils.any2Json(shop))
                }
                putInt(CategoryBigType, categoryBigType)
            }

        fun parseShop(intent: Intent): SearchSelectParam? = GsonUtils.json2Class(
            intent.getStringExtra(Shop), SearchSelectParam::class.java
        )

        fun parseCategoryBigType(intent: Intent): Int = intent.getIntExtra(CategoryBigType, -1)
    }

    object WalletParams {
        private const val RealNameAuthStatus = "realNameAuthStatus"

        /**
         * 包装参数
         */
        fun pack(authInfo: RealNameAuthDetailEntity): Bundle = Bundle().apply {
            putString(RealNameAuthStatus, GsonUtils.any2Json(authInfo))
        }

        /**
         * 解析TotalBalance
         */
        fun parseRealNameAuthStatus(intent: Intent): RealNameAuthDetailEntity? =
            GsonUtils.json2Class(
                intent.getStringExtra(RealNameAuthStatus),
                RealNameAuthDetailEntity::class.java
            )
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

    object SearchParams {
        private const val KeyWord = "keyWord"

        /**
         * 包装参数
         */
        fun pack(keyWord: String? = null): Bundle = Bundle().apply {
            putString(KeyWord, keyWord)
        }

        fun parseKeyWord(intent: Intent): String? = intent.getStringExtra(KeyWord)
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

    object LocationSelectParams {
        private const val City = "city"
        private const val ShopTypeName = "shopTypeName"

        /**
         * 包装参数
         */
        fun packCity(city: String?, shopTypeName: String?): Bundle = Bundle().apply {
            city?.let {
                putString(City, city)
            }
            shopTypeName?.let {
                putString(ShopTypeName, shopTypeName)
            }
        }

        fun parseCity(intent: Intent): String? = intent.getStringExtra(City)
        fun parseShopTypeName(intent: Intent): String? = intent.getStringExtra(ShopTypeName)
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
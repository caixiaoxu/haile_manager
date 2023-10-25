package com.yunshang.haile_manager_android.data.arguments

import android.content.Intent
import android.os.Bundle
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.data.common.DeviceCategory
import com.yunshang.haile_manager_android.data.entities.*
import com.yunshang.haile_manager_android.utils.DateTimeUtils
import java.util.*

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
        fun pack(phone: String?): Bundle = Bundle().apply {
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
        fun pack(shopId: Int, shopName: String? = null): Bundle = Bundle().apply {
            putInt(ShopId, shopId)
            shopName?.let {
                putString(ShopName, shopName)
            }
        }

        fun parseShopId(intent: Intent): Int = intent.getIntExtra(ShopId, -1)
        fun parseShopName(intent: Intent): String? = intent.getStringExtra(ShopName)
    }

    object ShopPositionSelectorParams {
        const val ShopPositionSelectorResultCode = 0x50001
        private const val CanMultiSelect = "canMultiSelect"
        private const val ShowPosition = "showPosition"
        private const val CanSelectAll = "canSelectAll"
        private const val MustSelect = "mustSelect"
        private const val SelectList = "selectList"

        /**
         * 包装参数
         */
        fun pack(
            canMultiSelect: Boolean = true,
            showPosition: Boolean = true,
            canSelectAll: Boolean = true,
            mustSelect: Boolean = true,
            selectList: MutableList<ShopAndPositionSelectEntity>? = null
        ): Bundle =
            Bundle().apply {
                putBoolean(CanMultiSelect, canMultiSelect)
                putBoolean(ShowPosition, showPosition)
                putBoolean(CanSelectAll, canSelectAll)
                putBoolean(MustSelect, mustSelect)
                putString(SelectList, GsonUtils.any2Json(selectList))
            }

        fun parseCanMultiSelect(intent: Intent): Boolean =
            intent.getBooleanExtra(CanMultiSelect, true)

        fun parseShowPosition(intent: Intent): Boolean =
            intent.getBooleanExtra(ShowPosition, true)

        fun parseCanSelectAll(intent: Intent): Boolean =
            intent.getBooleanExtra(CanSelectAll, true)

        fun parseMustSelect(intent: Intent): Boolean =
            intent.getBooleanExtra(MustSelect, true)

        /**
         * 包装返回参数
         */
        fun packResult(selectList: MutableList<ShopAndPositionSelectEntity>?): Bundle =
            Bundle().apply {
                putString(SelectList, GsonUtils.any2Json(selectList))
            }

        fun parseSelectList(intent: Intent): MutableList<ShopAndPositionSelectEntity>? =
            GsonUtils.json2List(
                intent.getStringExtra(SelectList),
                ShopAndPositionSelectEntity::class.java
            )
    }

    object ProfitStatisticsParams {
        private const val ShopIds = "shopIds"
        private const val ShopName = "shopName"
        private const val GoodId = "goodId"
        private const val CategoryCodes = "categoryCodes"
        private const val StartTime = "startTime"
        private const val EndTime = "endTime"
        private const val FormType = "formType"//0全不显示，1显示门店，2显示设备，3全显示

        /**
         * 包装参数
         */
        fun pack(
            shopIds: IntArray? = null,
            shopName: String? = null,
            goodId: Int? = null,
            categoryCodes: Array<String>? = null,
            startTime: Date? = null,
            endTime: Date? = null,
            formType: Int? = 0
        ): Bundle =
            Bundle().apply {
                shopIds?.let {
                    putIntArray(ShopIds, shopIds)
                }
                shopName?.let {
                    putString(ShopName, shopName)
                }
                goodId?.let {
                    putInt(GoodId, goodId)
                }
                categoryCodes?.let {
                    putStringArray(CategoryCodes, categoryCodes)
                }
                startTime?.let {
                    putString(StartTime, DateTimeUtils.formatDateTime(it))
                }
                endTime?.let {
                    putString(EndTime, DateTimeUtils.formatDateTime(it))
                }
                formType?.let {
                    putInt(FormType, formType)
                }
            }

        fun parseShopIds(intent: Intent): IntArray? = intent.getIntArrayExtra(ShopIds)
        fun parseShopName(intent: Intent): String? = intent.getStringExtra(ShopName)
        fun parseGoodId(intent: Intent): Int = intent.getIntExtra(GoodId, -1)
        fun parseCategoryCodes(intent: Intent): Array<String>? =
            intent.getStringArrayExtra(CategoryCodes)

        fun parseStartTime(intent: Intent): Date? =
            DateTimeUtils.formatDateFromString(intent.getStringExtra(StartTime))

        fun parseEndTime(intent: Intent): Date? =
            DateTimeUtils.formatDateFromString(intent.getStringExtra(EndTime))

        fun parseFormType(intent: Intent): Int = intent.getIntExtra(FormType, 0)
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

    object ShopOperationSettingParams {
        private const val VolumeVisibleState = "VolumeVisibleState"
        const val ResultCode = 0x71001

        /**
         * 包装参数
         */
        fun pack(
            volumeVisibleState: Int? = null,
            shopId: Int? = null
        ): Bundle =
            Bundle().apply {
                volumeVisibleState?.let {
                    putInt(VolumeVisibleState, volumeVisibleState)
                }
                shopId?.let {
                    putAll(ShopParams.pack(shopId))
                }
            }

        fun parseVolumeVisibleState(intent: Intent): Int = intent.getIntExtra(VolumeVisibleState, 0)
    }

    object ShopPositionCreateParams {
        private const val PositionDetail = "positionDetail"

        /**
         * 包装参数
         */
        fun pack(
            positionDetail: ShopPositionDetailEntity? = null,
        ): Bundle =
            Bundle().apply {
                positionDetail?.let {
                    putString(PositionDetail, GsonUtils.any2Json(positionDetail))
                }
            }

        fun parseShopPositionDetail(intent: Intent): ShopPositionDetailEntity? =
            GsonUtils.json2Class(
                intent.getStringExtra(PositionDetail),
                ShopPositionDetailEntity::class.java
            )
    }

    object ShopPaySettingsParams {
        private const val ShopId = "shopId"
        private const val ShopIds = "shopIds"
        private const val ShopPaySettings = "shopPaySettings"
        const val ResultCode = 10003

        /**
         * 包装参数
         */
        fun pack(
            shopIds: IntArray? = null,
            shopPaySettings: ShopPaySettingsEntity? = null,
            shopId: Int? = null
        ): Bundle =
            Bundle().apply {
                shopIds?.let {
                    putIntArray(ShopIds, shopIds)
                }
                shopPaySettings?.let {
                    putString(ShopPaySettings, GsonUtils.any2Json(shopPaySettings))
                }
                shopId?.let {
                    putInt(ShopId, it)
                }
            }

        fun parseShopId(intent: Intent): Int = intent.getIntExtra(ShopId, -1)
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
        const val PositionIdList = "positionIdList"
        const val MustSelect = "mustSelect"
        const val MoreSelect = "moreSelect"
        const val HasAll = "hasAll"
        const val SelectList = "selectList"
        const val NoUpdateList = "noUpdateList"
        const val ShopResultCode = 0x90001
        const val DeviceModelResultCode = 0x90002
        const val ResultData = "resultData"

        const val SearchSelectTypeShop = 0
        const val SearchSelectTypeDeviceModel = 1
        const val SearchSelectTypeTakeChargeShop = 2
        const val SearchSelectTypeRechargeShop = 4
        const val SearchSelectTypePaySettingsShop = 5
        const val SearchSelectTypeCouponShop = 6
        const val SearchSelectStatisticsShop = 7

        fun pack(
            searchSelectType: Int? = null,
            categoryId: Int? = null,
            staffId: Int? = null,
            shopIdList: IntArray? = null,
            positionIdList: IntArray? = null,
            mustSelect: Boolean = true,
            moreSelect: Boolean = false,
            hasAll: Boolean = false,
            selectArr: IntArray = intArrayOf(),
            noUpdateArr: IntArray = intArrayOf()
        ): Bundle = Bundle().apply {
            searchSelectType?.let {
                putInt(SearchSelectType, it)
            }
            shopIdList?.let {
                putIntArray(ShopIdList, shopIdList)
            }
            positionIdList?.let {
                putIntArray(PositionIdList, positionIdList)
            }
            categoryId?.let {
                putInt(CategoryId, it)
            }
            staffId?.let {
                putInt(StaffId, it)
            }
            putBoolean(MustSelect, mustSelect)
            putBoolean(MoreSelect, moreSelect)
            putBoolean(HasAll, hasAll)
            putIntArray(SelectList, selectArr)
            putIntArray(NoUpdateList, noUpdateArr)
        }

        fun parseSearchSelectType(intent: Intent): Int = intent.getIntExtra(SearchSelectType, -1)
        fun parseCategoryId(intent: Intent): Int = intent.getIntExtra(CategoryId, -1)
        fun parseStaffId(intent: Intent): Int = intent.getIntExtra(StaffId, -1)
        fun parseShopIdList(intent: Intent): IntArray? = intent.getIntArrayExtra(ShopIdList)
        fun parsePositionIdList(intent: Intent): IntArray? = intent.getIntArrayExtra(PositionIdList)
        fun parseMustSelect(intent: Intent): Boolean = intent.getBooleanExtra(MustSelect, true)
        fun parseMoreSelect(intent: Intent): Boolean = intent.getBooleanExtra(MoreSelect, false)
        fun parseHasAll(intent: Intent): Boolean = intent.getBooleanExtra(HasAll, false)
        fun parseSelectList(intent: Intent): IntArray? = intent.getIntArrayExtra(SelectList)
        fun parseNoUpdateList(intent: Intent): IntArray? = intent.getIntArrayExtra(NoUpdateList)
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
            oldFuncConfiguration: List<SkuFuncConfigurationParam>?,
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
        private const val Title = "title"
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
            goodId: Int? = null,
            title: String? = null
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
            title?.let {
                putString(Title, it)
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

        fun parseTitle(intent: Intent): String? = intent.getStringExtra(Title)
    }

    object DeviceParamsUpdateParams {
        private val UpdateParams = "UpdateParams"
        private val Type = "Type"
        private val OriginData = "OriginData"

        const val typeChangeModel = 0
        const val typeChangePayCode = 1
        const val typeChangeName = 2
        const val typeChangeFloor = 3

        const val ResultCode = 0x70001
        const val ResultData = "ResultData"

        fun pack(
            updateParams: String,
            type: Int? = null,
            originData: String? = null,
        ): Bundle =
            Bundle().apply {
                putString(UpdateParams, updateParams)
                type?.let {
                    putInt(Type, type)
                }
                originData?.let {
                    putString(OriginData, originData)
                }
            }

        fun parseUpdateParamsJson(intent: Intent): String? = intent.getStringExtra(UpdateParams)
        fun parseUpdateParamsType(intent: Intent): Int = intent.getIntExtra(Type, 0)
        fun parseUpdateParamsOriginData(intent: Intent): String? = intent.getStringExtra(OriginData)

        fun packResult(
            type: Int?,
            updateVal: String?,
        ): Bundle =
            Bundle().apply {
                type?.let {
                    putInt(Type, type)
                }
                updateVal?.let {
                    putString(ResultData, updateVal)
                }
            }
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
        fun pack(shop: ShopAndPositionSelectEntity? = null, categoryBigType: Int = -1): Bundle =
            Bundle().apply {
                shop?.let {
                    putString(Shop, GsonUtils.any2Json(shop))
                }
                putInt(CategoryBigType, categoryBigType)
            }

        fun parseShop(intent: Intent): ShopAndPositionSelectEntity? = GsonUtils.json2Class(
            intent.getStringExtra(Shop), ShopAndPositionSelectEntity::class.java
        )

        fun parseCategoryBigType(intent: Intent): Int = intent.getIntExtra(CategoryBigType, -1)
    }

    object LocationParams {
        const val LocationResultCode = 10002
        private const val LocationResultData = "LocationResultData"

        /**
         * 包装参数
         */
        fun pack(poiData: PoiResultData?): Bundle = Bundle().apply {
            putString(LocationResultData, GsonUtils.any2Json(poiData))
        }

        /**
         * 解析LocationResultData
         */
        fun parseLocationResultData(intent: Intent): PoiResultData? =
            GsonUtils.json2Class(
                intent.getStringExtra(LocationResultData),
                PoiResultData::class.java
            )
    }

    object WalletParams {
        private const val RealNameAuthStatus = "realNameAuthStatus"

        /**
         * 包装参数
         */
        fun pack(authInfo: RealNameAuthDetailEntity?): Bundle = Bundle().apply {
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

    object BindSmsVerifyParams {
        private const val VerifyType = "VerifyType"
        private const val NeedBack = "NeedBack"

        /**
         * 包装参数
         */
        fun pack(verifyType: Int, needBack: Boolean = false): Bundle = Bundle().apply {
            putInt(VerifyType, verifyType)
            putBoolean(NeedBack, needBack)
        }

        /**
         * 解析VerifyType
         */
        fun parseVerifyType(intent: Intent): Int = intent.getIntExtra(VerifyType, 0)

        /**
         * 解析NeedBack
         */
        fun parseNeedBack(intent: Intent): Boolean = intent.getBooleanExtra(NeedBack, false)
    }

    object WithdrawBindAlipayParams {
        private const val AuthCode = "authCode"

        /**
         * 包装参数
         */
        fun pack(authCode: String): Bundle = Bundle().apply {
            putString(AuthCode, authCode)
        }

        /**
         * 解析AuthCode
         */
        fun parseAuthCode(intent: Intent): String? = intent.getStringExtra(AuthCode)
    }

    object BinkCardBindParams {
        private const val BankCardDetail = "BankCardDetail"

        /**
         * 包装参数
         */
        fun pack(authCode: String, bankCardDetail: BankCardDetailEntity? = null): Bundle =
            Bundle().apply {
                putAll(WithdrawBindAlipayParams.pack(authCode))
                putString(BankCardDetail, GsonUtils.any2Json(bankCardDetail))
            }

        /**
         * 解析BankCardDetail
         */
        fun parseBankCardDetail(intent: Intent): BankCardDetailEntity? = GsonUtils.json2Class(
            intent.getStringExtra(BankCardDetail),
            BankCardDetailEntity::class.java
        )
    }

    object SearchLetterParams {
        private const val SearchLetterType = "SearchLetterType"
        private const val BankCode = "BankCode"
        private const val ResultData = "ResultData"

        /**
         * 包装参数
         * @param bankCode 银行联行号
         */
        fun pack(searchLetterType: Int = -1, bankCode: String? = null): Bundle = Bundle().apply {
            putInt(SearchLetterType, searchLetterType)
            bankCode?.let {
                putString(BankCode, bankCode)
            }
        }

        /**
         * 解析SearchLetterType
         */
        fun parseSearchLetterType(intent: Intent): Int = intent.getIntExtra(SearchLetterType, -1)

        /**
         * 解析BankCode
         */
        fun parseBankCode(intent: Intent): String? = intent.getStringExtra(BankCode)

        /**
         * 包装返回参数
         * @param bankCode 银行联行号
         */
        fun packResult(resultData: String): Bundle = Bundle().apply {
            putString(ResultData, resultData)
        }

        /**
         * 解析ResultData
         */
        fun parseResultData(intent: Intent): String? = intent.getStringExtra(ResultData)

    }

    object RealNameAuthParams {
        private const val AuthInfo = "authInfo"

        /**
         * 包装参数
         */
        fun pack(authInfo: RealNameAuthDetailEntity?): Bundle = Bundle().apply {
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

    object HaiXinSchemeConfigsCreateParams {
        private const val IsBatch = "isBatch"

        /**
         * 包装参数
         */
        fun pack(isBatch: Boolean): Bundle = Bundle().apply {
            putBoolean(IsBatch, isBatch)
        }

        fun parseIsBatch(intent: Intent): Boolean = intent.getBooleanExtra(IsBatch, false)
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
        fun packCity(city: String?, shopTypeName: String? = null): Bundle = Bundle().apply {
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
        private const val NoCache = "noCache"

        /**
         * 包装参数
         */
        fun pack(
            url: String,
            needFilter: Boolean = false,
            title: String? = null,
            autoWebTitle: Boolean = true,
            noCache: Boolean = false,
        ): Bundle = Bundle().apply {
            putString(Url, url)
            putBoolean(NeedFilter, needFilter)
            title?.let {
                putString(Title, title)
            }
            putBoolean(AutoWebTitle, autoWebTitle)
            putBoolean(NoCache, noCache)
        }

        fun parseUrl(intent: Intent): String? = intent.getStringExtra(Url)
        fun parseNeedFilter(intent: Intent): Boolean = intent.getBooleanExtra(NeedFilter, false)
        fun parseTitle(intent: Intent): String? = intent.getStringExtra(NeedFilter)
        fun parseAutoWebTitle(intent: Intent): Boolean = intent.getBooleanExtra(AutoWebTitle, true)
        fun parseNoCache(intent: Intent): Boolean = intent.getBooleanExtra(NoCache, false)
    }
}
package com.yunshang.haile_manager_android.utils

import com.yunshang.haile_manager_android.data.entities.UserPermissionEntity
import com.yunshang.haile_manager_android.data.model.SPRepository

/**
 * Title :
 * Author: Lsy
 * Date: 2023/3/28 13:56
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
</desc></version></time></author> */
object UserPermissionUtils {
    var userPermissionsMap: Map<String, UserPermissionEntity> = HashMap()
        get() {
            if (field.isEmpty()) {
                return dealUserPermissions(SPRepository.userPermissions)
            }
            return field
        }

    /**
     * 刷新数据
     */
    fun refreshData(userPermissionEntities: List<UserPermissionEntity>) {
        userPermissionsMap = dealUserPermissions(userPermissionEntities)
    }

    /**
     * 处理用户权限数据
     */
    private fun dealUserPermissions(userPermissionEntities: List<UserPermissionEntity>?): Map<String, UserPermissionEntity> =
        HashMap<String, UserPermissionEntity>().apply {
            userPermissionEntities?.let {
                for (bean in userPermissionEntities) {
                    this[bean.perms] = bean
                }
            }
        }

    /**
     * 是否含有收益权限
     *
     *
     * @return
     */
    @JvmStatic
    fun hasProfitPermission(): Boolean {
        return null != userPermissionsMap["league:profit"]
    }

    /**
     * 是否含有个人收益权限
     *
     *
     * @return
     */
    @JvmStatic
    fun hasProfitHomePermission(): Boolean {
        return null != userPermissionsMap["league:profit:home"]
    }

    /**
     * 是否含有数据统计权限
     *
     *
     * @return
     */
    @JvmStatic
    fun hasProfitDataStatisticsPermission(): Boolean {
        return null != userPermissionsMap["league:data:list"]
    }

    /**
     * 是否含有收益日历权限
     *
     *
     * @return
     */
    @JvmStatic
    fun hasProfitCalendarPermission(): Boolean {
        return null != userPermissionsMap["league:profit:calendar"]
    }

    /**
     * 是否含有收益明细权限
     *
     *
     * @return
     */
    @JvmStatic
    fun hasProfitDetailPermission(): Boolean {
        return null != userPermissionsMap["league:profit:detail"]
    }

    /**
     * 是否含有商家收益权限
     *
     *
     * @return
     */
    @JvmStatic
    fun hasProfitMerchantPermission(): Boolean {
        return null != userPermissionsMap["league:profit:merchant"]
    }

    /**
     * 是否含有消息权限
     *
     *
     * @return
     */
    @JvmStatic
    fun hasMessagePermission(): Boolean {
        return null != userPermissionsMap["league:message:list"]
    }

    /** ------------------------设备管理权限------------------------  */
    /**
     * 是否含有设备管理权限
     *
     *
     * @return
     */
    @JvmStatic
    fun hasDevicePermission(): Boolean {
        return null != userPermissionsMap["league:normal:goods"]
    }

    /**
     * 是否含有设备管理列表权限
     *
     *
     * @return
     */
    @JvmStatic
    fun hasDeviceListPermission(): Boolean {
        return null != userPermissionsMap["league:normal:goods:list"]
    }

    /**
     * 是否含有设备管理查看权限
     *
     *
     * @return
     */
    @JvmStatic
    fun hasDeviceInfoPermission(): Boolean {
        return null != userPermissionsMap["league:normal:goods:info"]
    }

    /**
     * 是否含有设备管理添加权限
     *
     *
     * @return
     */
    @JvmStatic
    fun hasDeviceAddPermission(): Boolean {
        return null != userPermissionsMap["league:normal:goods:add"]
    }

    /**
     * 是否含有设备管理删除权限
     *
     *
     * @return
     */
    @JvmStatic
    fun hasDeviceDeletePermission(): Boolean {
        return null != userPermissionsMap["league:normal:goods:delete"]
    }

    /**
     * 是否含有设备管理修改权限
     *
     *
     * @return
     */
    @JvmStatic
    fun hasDeviceUpdatePermission(): Boolean {
        return null != userPermissionsMap["league:normal:goods:update"]
    }

    /**
     * 是否含有高温筒自洁权限
     *
     *
     * @return
     */
    @JvmStatic
    fun hasHotCleanSelfPermission(): Boolean {
        return null != userPermissionsMap["league:normal:goods:clean:high"]
    }

    /**
     * 是否含有设备管理复位权限
     *
     *
     * @return
     */
    @JvmStatic
    fun hasDeviceResetPermission(): Boolean {
        return null != userPermissionsMap["league:normal:goods:reset"]
    }

    /**
     * 是否含有设备管理启动权限
     *
     *
     * @return
     */
    @JvmStatic
    fun hasDeviceStartPermission(): Boolean {
        return null != userPermissionsMap["league:normal:goods:start"]
    }

    /**
     * 是否含有设备管理筒自洁权限
     *
     *
     * @return
     */
    @JvmStatic
    fun hasDeviceCleanPermission(): Boolean {
        return null != userPermissionsMap["league:normal:goods:clean"]
    }

    /**
     * 是否含有设备管理付款码权限
     *
     *
     * @return
     */
    @JvmStatic
    fun hasDeviceQrcodePermission(): Boolean {
        return null != userPermissionsMap["league:normal:goods:qrcode"]
    }

    /**
     * 是否含有设备修改模块权限
     *
     *
     * @return
     */
    @JvmStatic
    fun hasDeviceUpdateModulePermission(): Boolean {
        return null != userPermissionsMap["league:normal:goods:update:module"]
    }

    /**
     * 是否含有设备迁移权限
     *
     *
     * @return
     */
    @JvmStatic
    fun hasDeviceExchangePermission(): Boolean {
        return null != userPermissionsMap["league:normal:goods:exchange"]
    }

    /**
     * 是否含有设备管理停用/启用权限
     *
     *
     * @return
     */
    @JvmStatic
    fun hasDeviceTiggerPermission(): Boolean {
        return null != userPermissionsMap["league:normal:goods:tigger"]
    }

    /**
     * 是否含有设备管理设备收益权限
     *
     *
     * @return
     */
    @JvmStatic
    fun hasDeviceProfitPermission(): Boolean {
        return null != userPermissionsMap["league:normal:goods:profit"]
    }

    /**
     * 是否含有设备预约收益权限
     *
     *
     * @return
     */
    @JvmStatic
    fun hasDeviceAppointmentPermission(): Boolean {
        return null != userPermissionsMap["league:normal:goods:appointment"]
    }

    /**
     * 是否含有设备解绑权限
     *
     *
     * @return
     */
    @JvmStatic
    fun hasDeviceUnbindPermission(): Boolean {
        return null != userPermissionsMap["league:normal:goods:audit"]
    }
    /** ------------------------设备管理权限------------------------  */
    /** ------------------------门店管理权限------------------------  */
    /**
     * 是否含有门店管理权限
     *
     *
     * @return
     */
    @JvmStatic
    fun hasShopPermission(): Boolean {
        return null != userPermissionsMap["league:shop"]
    }

    /**
     * 是否含有门店管理列表权限
     *
     *
     * @return
     */
    @JvmStatic
    fun hasShopListPermission(): Boolean {

        return null != userPermissionsMap["league:shop:list"]
    }

    /**
     * 是否含有门店管理查看权限
     *
     *
     * @return
     */
    @JvmStatic
    fun hasShopInfoPermission(): Boolean {

        return null != userPermissionsMap["league:shop:info"]
    }

    /**
     * 是否含有门店管理添加权限
     *
     *
     * @return
     */
    @JvmStatic
    fun hasShopAddPermission(): Boolean {
        return null != userPermissionsMap["league:shop:add"]
    }

    /**
     * 是否含有门店管理修改权限
     *
     *
     * @return
     */
    @JvmStatic
    fun hasShopUpdatePermission(): Boolean {

        return null != userPermissionsMap["league:shop:update"]
    }

    /**
     * 是否含有门店管理删除权限
     *
     *
     * @return
     */
    @JvmStatic
    fun hasShopDeletePermission(): Boolean {
        return null != userPermissionsMap["league:shop:delete"]
    }

    /**
     * 是否含有门店预约权限
     *
     * @return
     */
    fun hasShopAppointPermission(): Boolean {
        return null != userPermissionsMap["league:shop:appointment"]
    }

    /**
     * 是否含有门店管理删除权限
     *
     *
     * @return
     */
    @JvmStatic
    fun hasShopProfitPermission(): Boolean {
        return null != userPermissionsMap["league:shop:profit"]
    }

    /**
     * 是否含有门店批量支付设置权限
     *
     *
     * @return
     */
    @JvmStatic
    fun hasShopPayBatchPermission(): Boolean {
        return null != userPermissionsMap["league:shop:pay:batch"]
    }

    /** ------------------------门店管理权限------------------------  */
    /** ------------------------订单管理权限------------------------  */
    /**
     * 是否含有订单管理权限
     *
     *
     * @return
     */
    @JvmStatic
    fun hasOrderPermission(): Boolean {

        return null != userPermissionsMap["league:order"]
    }

    /**
     * 是否含有订单管理列表权限
     *
     *
     * @return
     */
    @JvmStatic
    fun hasOrderListPermission(): Boolean {

        return null != userPermissionsMap["league:order:list"]
    }

    /**
     * 是否含有订单管理查看权限
     *
     *
     * @return
     */
    @JvmStatic
    fun hasOrderInfoPermission(): Boolean {
        return null != userPermissionsMap["league:order:info"]
    }

    /**
     * 是否含有订单管理复位权限
     *
     *
     * @return
     */
    @JvmStatic
    fun hasOrderResetPermission(): Boolean {
        return null != userPermissionsMap["league:order:reset"]
    }

    /**
     * 是否含有订单管理启动权限
     *
     *
     * @return
     */
    @JvmStatic
    fun hasOrderStartPermission(): Boolean {
        return null != userPermissionsMap["league:order:start"]
    }

    /**
     * 是否含有订单管理退款权限
     *
     *
     * @return
     */
    @JvmStatic
    fun hasOrderRefundPermission(): Boolean {
        return null != userPermissionsMap["league:order:refund"]
    }

    /**
     * 是否含有订单管理补偿权限
     *
     *
     * @return
     */
    @JvmStatic
    fun hasOrderCompensatePermission(): Boolean {
        return null != userPermissionsMap["league:order:compensation"]
    }

    /** ------------------------订单管理权限------------------------  */
    /** ------------------------人员管理权限------------------------  */
    /**
     * 是否含有人员管理权限
     *
     *
     * @return
     */
    @JvmStatic
    fun hasPersonPermission(): Boolean {
        return null != userPermissionsMap["league:person"]
    }

    /**
     * 是否含有人员管理添加权限
     *
     *
     * @return
     */
    @JvmStatic
    fun hasPersonAddPermission(): Boolean {
        return null != userPermissionsMap["league:person:add"]
    }

    /**
     * 是否含有人员管理列表权限
     *
     *
     * @return
     */
    @JvmStatic
    fun hasPersonListPermission(): Boolean {
        return null != userPermissionsMap["league:person:list"]
    }

    /**
     * 是否含有人员管理详情权限
     *
     *
     * @return
     */
    @JvmStatic
    fun hasPersonInfoPermission(): Boolean {
        return null != userPermissionsMap["league:person:info"]
    }

    /**
     * 是否含有人员管理修改权限
     *
     *
     * @return
     */
    @JvmStatic
    fun hasPersonUpdatePermission(): Boolean {
        return null != userPermissionsMap["league:person:update"]
    }

    /**
     * 是否含有人员管理删除权限
     *
     *
     * @return
     */
    @JvmStatic
    fun hasPersonDeletePermission(): Boolean {
        return null != userPermissionsMap["league:person:delete"]
    }

    /** ------------------------人员管理权限------------------------  */
    /** ------------------------优惠管理权限------------------------  */
    /**
     * 是否含有优惠管理权限
     *
     *
     * @return
     */
    @JvmStatic
    fun hasMarketingPermission(): Boolean {
        return null != userPermissionsMap["league:marketing"]
    }

    /**
     * 是否含有优惠管理列表权限
     *
     *
     * @return
     */
    @JvmStatic
    fun hasMarketingListPermission(): Boolean {
        return null != userPermissionsMap["league:marketing:list"]
    }

    /**
     * 是否含有优惠管理详情权限
     *
     *
     * @return
     */
    @JvmStatic
    fun hasMarketingInfoPermission(): Boolean {
        return null != userPermissionsMap["league:marketing:info"]
    }

    /**
     * 是否含有优惠管理添加权限
     *
     *
     * @return
     */
    @JvmStatic
    fun hasMarketingAddPermission(): Boolean {
        return null != userPermissionsMap["league:marketing:add"]
    }

    /**
     * 是否含有优惠管理修改权限
     *
     *
     * @return
     */
    @JvmStatic
    fun hasMarketingUpdatePermission(): Boolean {
        return null != userPermissionsMap["league:marketing:update"]
    }

    /**
     * 是否含有优惠管理删除权限
     *
     *
     * @return
     */
    @JvmStatic
    fun hasMarketingDeletePermission(): Boolean {
        return null != userPermissionsMap["league:marketing:delete"]
    }

    /** ------------------------优惠管理权限------------------------  */

    /** ------------------------工单管理权限------------------------  */
    /**
     * 是否含有工单管理删除权限
     *
     * @return
     */
    fun hasWorkOrderPermission(): Boolean {
        return null != userPermissionsMap["league:work:order"]
    }
    /** ------------------------工单管理权限------------------------ **/

    /** ------------------------充值管理权限------------------------  */
    /**
     * 是否含有海星管理权限
     *
     * @return
     */
    fun hasVipPermission(): Boolean {
        return null != userPermissionsMap["league:vip"]
    }

    /**
     * 是否含有方案列表权限
     *
     * @return
     */
    fun hasVipListPermission(): Boolean {
        return null != userPermissionsMap["league:vip:list"]
    }

    /**
     * 是否含有方案添加权限
     *
     * @return
     */
    fun hasVipAddPermission(): Boolean {
        return null != userPermissionsMap["league:vip:add"]
    }

    /**
     * 是否含有方案修改权限
     *
     * @return
     */
    fun hasVipUpdatePermission(): Boolean {
        return null != userPermissionsMap["league:vip:update"]
    }

    /**
     * 是否含有方案删除权限
     *
     * @return
     */
    fun hasVipDeletePermission(): Boolean {
        return null != userPermissionsMap["league:vip:delete"]
    }


    /**
     * 是否含有方案详情权限
     *
     * @return
     */
    fun hasVipDetailPermission(): Boolean {
        return null != userPermissionsMap["league:vip:detail"]
    }

    /**
     * 是否含有海星充值管理详情权限
     *
     * @return
     */
    fun hasVipRechargePermission(): Boolean {
        return null != userPermissionsMap["league:vip:recharge"]
    }

    /**
     * 是否含有海星充值列表管理详情权限
     *
     * @return
     */
    fun hasVipRechargeListPermission(): Boolean {
        return null != userPermissionsMap["league:vip:recharge:list"]
    }

    /**
     * 是否含有海星充值用户管理详情权限
     *
     * @return
     */
    fun hasVipRechargeUserPermission(): Boolean {
        return null != userPermissionsMap["league:vip:recharge:user"]
    }

    /**
     * 是否含有海星充值回收管理详情权限
     *
     * @return
     */
    fun hasVipRechargeRecyclePermission(): Boolean {
        return null != userPermissionsMap["league:vip:recycle"]
    }

    /**
     * 是否含有退款记录权限
     *
     * @return
     */
    fun hasVipRefundListPermission(): Boolean {
        return null != userPermissionsMap["league:vip:refund:list"]
    }

    /**
     * 是否含有退款二维码权限
     *
     * @return
     */
    fun hasVipRefundCodePermission(): Boolean {
        return null != userPermissionsMap["league:vip:refund:code"]
    }

    /**
     * 是否含有退款详情权限
     *
     * @return
     */
    fun hasVipRefundDetailPermission(): Boolean {
        return null != userPermissionsMap["league:vip:refund:detail"]
    }

    /**
     * 是否含有退款审核权限
     *
     * @return
     */
    fun hasVipRefundApplyPermission(): Boolean {
        return null != userPermissionsMap["league:vip:refund:apply"]
    }

    /**
     * 是否含有钱包充值权限
     *
     * @return
     */
    fun hasWalletRechargePermission(): Boolean {
        return null != userPermissionsMap["league:wallet:recharge"]
    }
    /** ------------------------充值管理权限------------------------  */

    /** ------------------------分账管理权限------------------------  */
    /**
     * 是否含有分账管理权限
     *
     *
     * @return
     */
    @JvmStatic
    fun hasDistributionPermission(): Boolean {
        return null != userPermissionsMap["league:funds:distribution"]
    }

    /**
     * 是否含有分账管理列表权限
     *
     *
     * @return
     */
    @JvmStatic
    fun hasDistributionListPermission(): Boolean {
        return null != userPermissionsMap["league:funds:distribution:list"]
    }

    /**
     * 是否含有分账管理人员查看权限
     *
     *
     * @return
     */
    @JvmStatic
    fun hasDistributionPersonListPermission(): Boolean {
        return null != userPermissionsMap["league:funds:distribution:person:list"]
    }

    /**
     * 是否含有分账管理添加权限
     *
     *
     * @return
     */
    @JvmStatic
    fun hasDistributionAddPermission(): Boolean {
        return null != userPermissionsMap["league:funds:distribution:add"]
    }

    /**
     * 是否含有分账管理修改权限
     *
     *
     * @return
     */
    @JvmStatic
    fun hasDistributionUpdatePermission(): Boolean {
        return null != userPermissionsMap["league:funds:distribution:update"]
    }

    /**
     * 是否含有分账管理删除权限
     *
     *
     * @return
     */
    @JvmStatic
    fun hasDistributionDeletePermission(): Boolean {
        return null != userPermissionsMap["league:funds:distribution:delete"]
    }
    /** ------------------------分账管理权限------------------------  */
    /** ------------------------发票管理权限------------------------  */
    /**
     * 是否含有发票管理权限
     *
     *
     * @return
     */
    @JvmStatic
    fun hasInvoicePermission(): Boolean {
        return null != userPermissionsMap["league:finance:invoice"]
    }
    /** ------------------------发票管理权限------------------------  */
    /** ------------------------设备监控权限------------------------  */
    /**
     * 是否含有设备监控权限
     *
     *
     * @return
     */
    @JvmStatic
    fun hasDeviceMonitoringPermission(): Boolean {
        return null != userPermissionsMap["league:normal:goods:monitoring"]
    }
    /** ------------------------设备监控权限------------------------  */
    /** ------------------------数据统计权限------------------------  */
    /**
     * 是否含有数据统计权限
     *
     *
     * @return
     */
    @JvmStatic
    fun hasDataStatisticsListPermission(): Boolean {
        return null != userPermissionsMap["league:data:list"]
    }
    /** ------------------------数据统计权限------------------------  */
    /** ------------------------发券权限------------------------  */

    /**
     * 是否含有发券权限
     *
     *
     * @return
     */
    @JvmStatic
    fun hasSendCouponPermission(): Boolean {
        return null != userPermissionsMap["league:coupon:send"]
    }

    /**
     * 是否含有券列表权限
     *
     *
     * @return
     */
    @JvmStatic
    fun hasCouponListPermission(): Boolean {
        return null != userPermissionsMap["league:couponuser:list"]
    }

    /** ------------------------发券权限------------------------  */

    /** ------------------------公告权限------------------------  */

    /**
     * 是否含有公告权限
     *
     *
     * @return
     */
    @JvmStatic
    fun hasAnnouncementPermission(): Boolean {
        return null != userPermissionsMap["league:announcement"]
    }
    /** ------------------------公告权限------------------------  */

    /** ------------------------银行卡权限------------------------  */

    /**
     * 是否含有银行卡权限
     *
     *
     * @return
     */
    @JvmStatic
    fun hasWalletBankPermission(): Boolean {
        return null != userPermissionsMap["league:wallet:bank"]
    }
    /** ------------------------银行卡权限------------------------  */

    /** ------------------------报修权限------------------------  */

    /**
     * 是否含有报修权限
     *
     *
     * @return
     */
    @JvmStatic
    fun hasRepairsPermission(): Boolean {
        return null != userPermissionsMap["league:normal:deviceFix"]
    }

    /**
     * 是否含有设备报修列表权限
     *
     *
     * @return
     */
    @JvmStatic
    fun hasRepairsListPermission(): Boolean {
        return null != userPermissionsMap["league:normal:deviceFix:list"]
    }

    /**
     * 是否含有设备报修回复权限
     *
     *
     * @return
     */
    @JvmStatic
    fun hasRepairsReplyPermission(): Boolean {
        return null != userPermissionsMap["league:normal:deviceFix:reply"]
    }
    /** ------------------------报修权限------------------------  */
    /**
     * 是否含有备件申请权限
     *
     *
     * @return
     */
    @JvmStatic
    fun hasSparePartPermission(): Boolean {
        return null != userPermissionsMap["league:normal:sparePart"]
    }
}
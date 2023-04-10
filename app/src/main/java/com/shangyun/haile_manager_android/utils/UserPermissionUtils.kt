package com.shangyun.haile_manager_android.utils

import com.shangyun.haile_manager_android.data.entities.UserPermissionEntity
import com.shangyun.haile_manager_android.data.model.SPRepository

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
     * 是否含有设备管理桶自洁权限
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
     * 是否含有门店管理删除权限
     *
     *
     * @return
     */
    @JvmStatic
    fun hasShopProfitPermission(): Boolean {

        return null != userPermissionsMap["league:shop:profit"]
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
        return null != userPermissionsMap["league:order:compensate"]
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

    /** ------------------------订单管理权限------------------------  */
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
     * 是否含有优惠管理列表权限
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
}
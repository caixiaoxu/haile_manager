package com.yunshang.haile_manager_android.business.event

/**
 * Title : 事件
 * Author: Lsy
 * Date: 2023/4/6 17:30
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
object BusEvents {

    const val LOGIN_STATUS = "login_status"
    const val SHOP_LIST_STATUS = "shop_list_change"
    const val SHOP_DETAILS_STATUS = "shop_detail_change"
    const val DEVICE_LIST_STATUS = "device_list_change"
    const val DEVICE_LIST_ITEM_STATUS = "device_list_item_change"
    const val DEVICE_LIST_ITEM_DELETE_STATUS = "device_list_item_delete"
    const val DEVICE_DETAILS_STATUS = "device_detail_change"
    const val ORDER_LIST_STATUS = "order_list_change"
    const val STAFF_LIST_STATUS = "staff_list_change"
    const val STAFF_DETAILS_STATUS = "staff_detail_change"
    const val STAFF_LIST_ITEM_DELETE_STATUS = "staff_list_item_delete"
    const val DISCOUNTS_LIST_STATUS = "discount_list_change"
    const val DISCOUNTS_DETAIL_STATUS = "discount_detail_change"
    const val DISCOUNTS_LIST_ITEM_DELETE_STATUS = "discounts_list_item_delete"
    const val SUB_ACCOUNT_LIST_STATUS = "sub_account_list_change"
    const val SUB_ACCOUNT_DETAIL_STATUS = "sub_account_detail_change"
    const val REAL_NAME_AUTH_STATUS = "real_name_auth_status"
    const val BIND_WITHDRAW_ACCOUNT_STATUS = "bind_withdraw_account_status"
    const val BALANCE_STATUS = "balance_status"
    const val WXPAY_STATUS = "wxpay_status"
    const val HAIXIN_SCHEME_LIST_STATUS = "haixin_scheme_list_change"
    const val HAIXIN_SCHEME_DETAIL_STATUS = "haixin_scheme_detail_change"
    const val HAIXIN_SCHEME_LIST_ITEM_DELETE_STATUS = "haixin_scheme_list_item_delete"
    const val HAIXIN_USER_LIST_STATUS = "haixin_user_list_status"
    const val HAIXIN_REFUND_RECORD_LIST_STATUS = "haixin_refund_record_list_status"
    const val NOTICE_LIST_ITEM_DELETE_STATUS = "notice_list_item_delete"
    const val NOTICE_LIST_STATUS = "notice_list_change"
    const val NOTICE_DETAIL_CHANGE = "notice_detail_change"
    const val MESSAGE_READ_STATUS = "message_read_status"

    const val SCAN_CHANGE_STATUS = "scan_change_status"

    const val BANK_LIST_STATUS = "bank_list_status"
    const val BANK_LIST_DETAIL_STATUS = "bank_list_detail_status"
    const val BANK_LIST_DELETE_STATUS = "bank_list_delete_status"

    const val COUPON_LIST_STATUS = "coupon_list_status"
}
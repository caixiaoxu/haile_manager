package com.yunshang.haile_manager_android.business.vm

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.data.arguments.RegisterParams
import com.yunshang.haile_manager_android.utils.StringUtils

/**
 * Title :
 * Author: Lsy
 * Date: 2023/9/21 17:00
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class RegisterViewModel : BaseViewModel() {

    val registerParams: RegisterParams = RegisterParams()

    val isAgree: MutableLiveData<Boolean> by lazy {
        MutableLiveData()
    }

    fun register(v: View, shared: SharedViewModel) {
        if (registerParams.shopName.isNullOrEmpty()) {
            SToast.showToast(v.context, "请输入商户名称")
            return
        }
        if (StringUtils.checkShopName(registerParams.shopName!!)) {
            SToast.showToast(v.context, "商户名称需2-50个中英文大小写")
            return
        }
        if (registerParams.managerName.isNullOrEmpty()) {
            SToast.showToast(v.context, "请输入负责人名称")
            return
        }
        if (StringUtils.checkName(registerParams.managerName!!)) {
            SToast.showToast(v.context, "负责人名称需2-20个中英文大小写")
            return
        }
        if (registerParams.phone.isNullOrEmpty()) {
            SToast.showToast(v.context, "请输入手机号")
            return
        }
        if (registerParams.phone!!.length < 11) {
            SToast.showToast(v.context, "请输入正确的手机号")
            return
        }
        if (registerParams.code.isNullOrEmpty()) {
            SToast.showToast(v.context, "请输入验证码")
            return
        }
        if (registerParams.password.isNullOrEmpty()) {
            SToast.showToast(v.context, "请输入密码")
            return
        }
        if (StringUtils.checkPassword(registerParams.phone!!)) {
            SToast.showToast(v.context, "商户名称需6-20个中英文大小写和数字的组合")
            return
        }
        if (registerParams.password != registerParams.passwordAgain) {
            SToast.showToast(v.context, "两次密码输入不一致")
            return
        }
        if (null == registerParams.provinceId || null == registerParams.cityId
            || null == registerParams.areaId
        ) {
            SToast.showToast(v.context, "请选择所属地区")
            return
        }

        launch({
            // 注册
            shared.register()
            // 注册成功
            LiveDataBus.post(BusEvents.REGISTER_SUCCESS_STATUS, registerParams.phone!!)
        })
    }
}
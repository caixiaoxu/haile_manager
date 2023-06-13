package com.yunshang.haile_manager_android.utils

import android.content.Context
import android.graphics.Bitmap
import com.tencent.mm.opensdk.modelmsg.*
import com.tencent.mm.opensdk.modelpay.PayReq
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory

/**
 * Title :
 * Author: Lsy
 * Date: 2023/6/2 11:32
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
</desc></version></time></author> */
object WeChatHelper {
    const val APP_ID = "wx52943cd7b3d779a4"

    // IWXAPI 是第三方app和微信通信的openapi接口
    private var iwxapi: IWXAPI? = null

    /**
     * 初始化
     */
    fun init(context: Context?) {
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        iwxapi = WXAPIFactory.createWXAPI(context, APP_ID, false)
        iwxapi?.registerApp(APP_ID) // 将该app注册到微信
    }

    val isWxInstall: Boolean
        get() = iwxapi!!.isWXAppInstalled

    /**
     * 微信登陆
     */
    fun openWeChatLogin() {
        //调起微信接口
        iwxapi?.sendReq(SendAuth.Req().apply {
            scope = "snsapi_userinfo"
            state = "wechat_sdk"
        })
    }

    /**
     * 调起微信支付
     *
     * @param appId
     * @param partnerId
     * @param prepayId
     * @param nonceStr
     * @param timeStamp
     * @param sign
     */
    fun openWeChatPay(
        appId: String?, partnerId: String?, prepayId: String?, nonceStr: String?,
        timeStamp: String?, sign: String?
    ) {

        //调起微信支付
        iwxapi?.sendReq(PayReq().apply {
            this.appId = appId
            this.partnerId = partnerId
            this.prepayId = prepayId
            this.packageValue = "Sign=WXPay"
            this.nonceStr = nonceStr
            this.timeStamp = timeStamp
            this.sign = sign
        })
    }

    /**
     * 调起微信分享（图片分享到微信）
     *
     * @param bitmap 图片
     */
    fun openWeChatImgShare(bitmap: Bitmap?) {
        //调用 api 接口，发送数据到微信
        iwxapi?.sendReq(SendMessageToWX.Req().apply {
            //初始化一个WXImageObject
            val img = WXImageObject(bitmap)
            val msg = WXMediaMessage()
            msg.mediaObject = img
            message = msg
            //分享场景
            scene = SendMessageToWX.Req.WXSceneSession
        })
    }

    /**
     * 调起微信分享 （h5链接分享）
     *
     * @param title
     * @param description
     * @param scene       分享场景 Session微信好友  Timeline朋友圈
     */
    fun openWeChatShare(title: String?, description: String?, scene: String) {
        //调用 api 接口，发送数据到微信
        iwxapi!!.sendReq(SendMessageToWX.Req().apply {
            //初始化一个WXWebpageObject，填写url
            val webpage = WXWebpageObject()
            webpage.webpageUrl = "http://cms.iclickdesign.com/shareWx.html"
            //用 WXWebpageObject 对象初始化一个 WXMediaMessage 对象
            val msg = WXMediaMessage(webpage)
            msg.title = title
            msg.description = description
            //链接压缩图
            //Bitmap thumbBmp = BitmapFactory.decodeResource(getResources(), R.drawable
            // .send_music_thumb);
            //msg.thumbData =Util.bmpToByteArray(thumbBmp, true);

            message = msg
            //分享场景
            if (scene == "Session") {
                this.scene = SendMessageToWX.Req.WXSceneSession
            } else if (scene == "Timeline") {
                this.scene = SendMessageToWX.Req.WXSceneTimeline
            }
        })
    }
}
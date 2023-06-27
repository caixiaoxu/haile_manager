package com.yunshang.haile_manager_android

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.lsy.framelib.ui.base.activity.BaseActivity
import com.yunshang.haile_manager_android.business.apiService.CommonService
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.common.Constants
import com.yunshang.haile_manager_android.data.model.ApiRepository
import com.yunshang.haile_manager_android.data.model.SPRepository
import com.yunshang.haile_manager_android.databinding.ActivitySplashBinding
import com.yunshang.haile_manager_android.ui.activity.MainActivity
import com.yunshang.haile_manager_android.ui.activity.login.LoginActivity
import com.yunshang.haile_manager_android.web.WebViewActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SplashActivity : BaseActivity() {
    private val mCommonRepo = ApiRepository.apiClient(CommonService::class.java)

    private val delayTime = 1000L

    private val mSplashBinding: ActivitySplashBinding by lazy {
        ActivitySplashBinding.inflate(layoutInflater)
    }

    override fun isFullScreen(): Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mSplashBinding.root)
        initView()
    }

    private fun initView() {
        initStatusBarTxtColor(mSplashBinding.root)
        launch({
            val response = mCommonRepo.checkService()
            withContext(Dispatchers.Main) {
                when (response) {
                    0 -> checkDelayJump()
                    1 -> {
                        Constants.needHintServiceUpdate = true
                        checkDelayJump()
                    }
                    else -> startActivity(
                        Intent(
                            this@SplashActivity,
                            WebViewActivity::class.java
                        ).apply {
                            putExtras(
                                IntentParams.WebViewParams.pack("https://notice.haier-ioc.com/halt.html")
                            )
                        })
                }
            }
        })
    }

    /**
     * 延时跳转，根据是否登录判断
     */
    private fun checkDelayJump() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (SPRepository.isLogin()) {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            } else {
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
            }
            finish()

        }, delayTime)
    }
}
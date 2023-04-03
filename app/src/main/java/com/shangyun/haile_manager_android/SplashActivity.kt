package com.shangyun.haile_manager_android

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import com.lsy.framelib.ui.base.BaseActivity
import com.shangyun.haile_manager_android.data.SPRepository
import com.shangyun.haile_manager_android.databinding.ActivitySplashBinding
import com.shangyun.haile_manager_android.ui.activity.LoginActivity
import com.shangyun.haile_manager_android.ui.activity.MainActivity

class SplashActivity : BaseActivity() {
    private val delayTime = 1000L

    private val mSplashBinding: ActivitySplashBinding by lazy {
        ActivitySplashBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkDelayJump()
    }

    override fun isFullScreen(): Boolean = true

    override fun rooView(): View = mSplashBinding.root

    /**
     * 延时跳转，根据是否登录判断
     */
    private fun checkDelayJump() {
        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            if (SPRepository.isLogin()) {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            } else {
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
            }
            finish()

        }, delayTime)
    }
}
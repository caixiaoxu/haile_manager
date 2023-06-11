package com.yunshang.haile_manager_android.ui.activity

import android.util.SparseArray
import androidx.fragment.app.Fragment
import com.lsy.framelib.utils.ActivityUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.MainViewModel
import com.yunshang.haile_manager_android.data.model.SPRepository
import com.yunshang.haile_manager_android.databinding.ActivityMainBinding
import com.yunshang.haile_manager_android.ui.fragment.HomeFragment
import com.yunshang.haile_manager_android.ui.fragment.PersonalFragment

class MainActivity :
    BaseBusinessActivity<ActivityMainBinding, MainViewModel>(MainViewModel::class.java, BR.vm) {
    val FROM_TYPE = "from_type"
    val FROM_TYPE_LOGIN = 0
    val FROM_TYPE_SPLASH = 1

    // 当前的fragment
    private var curFragment: Fragment? = null

    private val fragments = SparseArray<Fragment>(2).apply {
        put(R.id.rb_main_tab_home, HomeFragment())
        put(R.id.rb_main_tab_personal, PersonalFragment())
    }

    override fun isFullScreen(): Boolean = true

    override fun layoutId(): Int = R.layout.activity_main

    override fun onBackListener() {
        ActivityUtils.doubleBack(this)
    }

    override fun initView() {
    }

    override fun initEvent() {
        super.initEvent()

        // 如果用户信息为空，重新请求
        if (null == SPRepository.userInfo) {
            mSharedViewModel.requestUserInfoAsync()
        }

        // 如果权限数据为空，重新请求
        mSharedViewModel.requestUserPermissionsAsync()

        mViewModel.checkId.observe(this) {
            when (it) {
                R.id.rb_main_tab_home -> showChildFragment(it)
                R.id.rb_main_tab_personal -> showChildFragment(it)
            }
        }
    }

    /**
     * 显示子布局
     */
    private fun showChildFragment(id: Int) {
        curFragment?.let {
            supportFragmentManager.beginTransaction().hide(it).commit()
        }

        fragments[id]?.let {
            if (it.isAdded) {
                supportFragmentManager.beginTransaction().show(it).commit()
            } else {
                supportFragmentManager.beginTransaction().add(R.id.fl_main_controller, it)
                    .commit()
            }
            curFragment = it
        }
    }

    override fun initData() {
    }

}
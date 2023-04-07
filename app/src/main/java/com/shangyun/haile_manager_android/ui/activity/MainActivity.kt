package com.shangyun.haile_manager_android.ui.activity

import android.util.SparseArray
import androidx.fragment.app.Fragment
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.vm.MainViewModel
import com.shangyun.haile_manager_android.databinding.ActivityMainBinding
import com.shangyun.haile_manager_android.ui.fragment.HomeFragment
import com.shangyun.haile_manager_android.ui.fragment.PersonalFragment

class MainActivity : BaseBusinessActivity<ActivityMainBinding, MainViewModel>() {

    private val mMainViewModel by lazy {
        getActivityViewModelProvider(this)[MainViewModel::class.java]
    }

    // 当前的fragment
    private var curFragment: Fragment? = null

    private val fragments = SparseArray<Fragment>(2).apply {
        put(R.id.rb_main_tab_home, HomeFragment())
        put(R.id.rb_main_tab_personal, PersonalFragment())
    }

    override fun isFullScreen(): Boolean = true

    override fun getVM(): MainViewModel = mMainViewModel
    override fun layoutId(): Int = R.layout.activity_main

    override fun initView() {
        mBinding.vm = mMainViewModel
        mMainViewModel.checkId.observe(this) {
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
                supportFragmentManager.beginTransaction().add(R.id.fl_main_controller, it).commit()
            }
            curFragment = it
        }
    }

    override fun initData() {
    }

}
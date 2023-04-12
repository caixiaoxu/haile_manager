package com.shangyun.haile_manager_android.business.vm

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.lsy.framelib.ui.base.BaseViewModel
import com.shangyun.haile_manager_android.R
import timber.log.Timber

/**
 * Title :
 * Author: Lsy
 * Date: 2023/3/17 17:02
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class MainViewModel : BaseViewModel() {
    //选择的id
    val checkId: MutableLiveData<Int> = MutableLiveData(R.id.rb_main_tab_home)

    // 是否选择首页
    val isShowHomeRB: LiveData<Int> =
        checkId.map { if (it == R.id.rb_main_tab_home) View.INVISIBLE else View.VISIBLE }
    val isShowHomeIcon: LiveData<Int> =
        checkId.map { if (it == R.id.rb_main_tab_home) View.VISIBLE else View.GONE }
}
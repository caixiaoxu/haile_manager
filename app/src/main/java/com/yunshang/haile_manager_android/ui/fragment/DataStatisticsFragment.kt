package com.yunshang.haile_manager_android.ui.fragment

import androidx.databinding.library.baseAdapters.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.DataStatisticsViewModel
import com.yunshang.haile_manager_android.databinding.FragmentDataStatisticsBinding

/**
 * Title :
 * Author: Lsy
 * Date: 2023/6/20 15:37
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class DataStatisticsFragment :
    BaseBusinessFragment<FragmentDataStatisticsBinding, DataStatisticsViewModel>(
        DataStatisticsViewModel::class.java,
        BR.vm
    ) {
    override fun layoutId(): Int = R.layout.fragment_data_statistics

    override fun initView() {
    }

    override fun initData() {
    }
}
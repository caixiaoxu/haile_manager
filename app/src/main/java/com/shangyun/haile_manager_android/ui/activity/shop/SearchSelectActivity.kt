package com.shangyun.haile_manager_android.ui.activity.shop

import android.content.Intent
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.lsy.framelib.utils.gson.GsonUtils
import com.shangyun.haile_manager_android.BR
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.vm.SearchSelectViewModel
import com.shangyun.haile_manager_android.business.vm.SearchSelectViewModel.Companion.SEARCH_TYPE
import com.shangyun.haile_manager_android.data.rule.SearchSelectEntity
import com.shangyun.haile_manager_android.databinding.ActivitySearchSelectBinding
import com.shangyun.haile_manager_android.databinding.ItemSearchSelectBinding
import com.shangyun.haile_manager_android.ui.activity.BaseBusinessActivity
import com.shangyun.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter

class SearchSelectActivity :
    BaseBusinessActivity<ActivitySearchSelectBinding, SearchSelectViewModel>() {
    private val mSearchSelectViewModel by lazy {
        getActivityViewModelProvider(this)[SearchSelectViewModel::class.java]
    }

    private val mAdapter: CommonRecyclerAdapter<ItemSearchSelectBinding, SearchSelectEntity> by lazy {
        CommonRecyclerAdapter(
            R.layout.item_search_select,
            BR.item
        ) { mBinding, d ->
            mBinding?.root?.setOnClickListener {
                setResult(ShopCreateActivity.SchoolResultCode, Intent().apply {
                    putExtra(ShopCreateActivity.SchoolResultData, GsonUtils.any2Json(d))
                })
                finish()
            }
        }
    }

    private val mHander: Handler by lazy {
        Handler(Looper.getMainLooper()) {
            if (0 == it.what) {
                mSearchSelectViewModel.search()
            }
            false
        }
    }

    override fun layoutId(): Int = R.layout.activity_search_select

    override fun getVM(): SearchSelectViewModel = mSearchSelectViewModel

    override fun backBtn(): View = mBinding.searchSelectTitleBar.getBackBtn()

    override fun initIntent() {
        intent.getIntExtra(SEARCH_TYPE, -1).run {
            mSearchSelectViewModel.searchType.postValue(this)
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE

        mBinding.rvSearchSelectList.layoutManager = LinearLayoutManager(this)
        mBinding.rvSearchSelectList.adapter = mAdapter

        mBinding.etSearchContent.addTextChangedListener {
            mHander.removeMessages(0)
            mHander.sendEmptyMessageDelayed(0, 500)
        }
    }

    override fun initEvent() {
        super.initEvent()

        mSearchSelectViewModel.searchList.observe(this) {
            mAdapter.refreshList(it, true)
        }
    }

    override fun initData() {
        mBinding.vm = mSearchSelectViewModel
    }
}
package com.shangyun.haile_manager_android.ui.activity.shop

import android.content.Intent
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.lsy.framelib.utils.gson.GsonUtils
import com.shangyun.haile_manager_android.BR
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.vm.SearchSelectViewModel
import com.shangyun.haile_manager_android.business.vm.SearchSelectViewModel.Companion.LOCATION
import com.shangyun.haile_manager_android.business.vm.SearchSelectViewModel.Companion.SCHOOL
import com.shangyun.haile_manager_android.business.vm.SearchSelectViewModel.Companion.SEARCH_TYPE
import com.shangyun.haile_manager_android.data.entities.LocationSelectEntity
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
        ) { mBinding, _, d ->
            mBinding?.root?.setOnClickListener {
                if (SCHOOL == mSearchSelectViewModel.searchType.value) {
                    setResult(ShopCreateActivity.SchoolResultCode, Intent().apply {
                        putExtra(ShopCreateActivity.SchoolResultData, GsonUtils.any2Json(d))
                    })
                } else if (LOCATION == mSearchSelectViewModel.searchType.value) {
                    setResult(RESULT_OK, Intent().apply {
                        putExtra(
                            ShopCreateActivity.LocationResultData,
                            GsonUtils.any2Json((d as LocationSelectEntity).poi)
                        )
                    })
                }
                finish()
            }
        }
    }

    private val mHandler: Handler by lazy {
        Handler(Looper.getMainLooper()) {
            if (0 == it.what) {
                mSearchSelectViewModel.search(this@SearchSelectActivity)
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
        intent.getStringExtra(SearchSelectViewModel.CityCode)?.run {
            mSearchSelectViewModel.cityCode = this
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE

        mBinding.rvSearchSelectList.layoutManager = LinearLayoutManager(this)
        mBinding.rvSearchSelectList.adapter = mAdapter

        mBinding.etSearchContent.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                mHandler.removeMessages(0)
                mHandler.sendEmptyMessage(0)
            }
            false
        }

        mBinding.etSearchContent.addTextChangedListener {
            mHandler.removeMessages(0)
            mHandler.sendEmptyMessageDelayed(0, 500)
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
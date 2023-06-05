package com.yunshang.haile_manager_android.ui.activity.common

import android.content.Intent
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.SearchSelectViewModel
import com.yunshang.haile_manager_android.business.vm.SearchSelectViewModel.Companion.LOCATION
import com.yunshang.haile_manager_android.business.vm.SearchSelectViewModel.Companion.SCHOOL
import com.yunshang.haile_manager_android.business.vm.SearchSelectViewModel.Companion.SEARCH_TYPE
import com.yunshang.haile_manager_android.data.entities.LocationSelectEntityI
import com.yunshang.haile_manager_android.data.rule.ISearchSelectEntity
import com.yunshang.haile_manager_android.databinding.ActivitySearchSelectBinding
import com.yunshang.haile_manager_android.databinding.ItemSearchSelectBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.activity.shop.ShopCreateAndUpdateActivity
import com.yunshang.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter

class SearchSelectActivity :
    BaseBusinessActivity<ActivitySearchSelectBinding, SearchSelectViewModel>(
        SearchSelectViewModel::class.java,
        BR.vm
    ) {

    private val mAdapter: CommonRecyclerAdapter<ItemSearchSelectBinding, ISearchSelectEntity> by lazy {
        CommonRecyclerAdapter(
            R.layout.item_search_select,
            BR.item
        ) { mBinding, _, d ->
            mBinding?.root?.setOnClickListener {
                if (SCHOOL == mViewModel.searchType.value) {
                    setResult(ShopCreateAndUpdateActivity.SchoolResultCode, Intent().apply {
                        putExtra(
                            ShopCreateAndUpdateActivity.SchoolResultData,
                            GsonUtils.any2Json(d)
                        )
                    })
                } else if (LOCATION == mViewModel.searchType.value) {
                    setResult(RESULT_OK, Intent().apply {
                        putExtra(
                            ShopCreateAndUpdateActivity.LocationResultData,
                            GsonUtils.any2Json((d as LocationSelectEntityI).poi)
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
                mViewModel.search(this@SearchSelectActivity)
            }
            false
        }
    }

    override fun layoutId(): Int = R.layout.activity_search_select

    override fun backBtn(): View = mBinding.searchSelectTitleBar.getBackBtn()

    override fun initIntent() {
        intent.getIntExtra(SEARCH_TYPE, -1).run {
            mViewModel.searchType.postValue(this)
        }
        intent.getStringExtra(SearchSelectViewModel.CityCode)?.run {
            mViewModel.cityCode = this
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

        mViewModel.searchList.observe(this) {
            mAdapter.refreshList(it, true)
        }
    }

    override fun initData() {
    }
}
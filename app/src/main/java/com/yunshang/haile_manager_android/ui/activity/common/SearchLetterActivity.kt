package com.yunshang.haile_manager_android.ui.activity.common

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.ui.base.activity.BaseActivity
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.apiService.CommonService
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.model.ApiRepository
import com.yunshang.haile_manager_android.data.rule.ISearchLetterEntity
import com.yunshang.haile_manager_android.databinding.ActivitySearchLetterBinding
import com.yunshang.haile_manager_android.databinding.ItemSearchLetterBinding
import com.yunshang.haile_manager_android.databinding.ItemSearchLetterLetterBinding
import com.yunshang.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter
import com.yunshang.haile_manager_android.ui.view.adapter.ViewBindingAdapter.visibility
import com.yunshang.haile_manager_android.ui.view.refresh.CommonRefreshRecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class SearchLetterActivity : BaseActivity() {
    private val mSearchLetterBinding: ActivitySearchLetterBinding by lazy {
        ActivitySearchLetterBinding.inflate(layoutInflater)
    }

    private val mCommonRepo = ApiRepository.apiClient(CommonService::class.java)

    private var searchLetterType = -1
    private var bankCode: String? = null

    private val mAdapter: CommonRecyclerAdapter<ItemSearchLetterBinding, ISearchLetterEntity> by lazy {
        CommonRecyclerAdapter(
            R.layout.item_search_letter,
            BR.item
        ) { mBinding, _, item ->
            mBinding?.tvItemSearchLetterTitle?.setOnClickListener {
                setResult(RESULT_OK, Intent().apply {
                    putExtras(intent)
                    putExtras(IntentParams.SearchLetterParams.packResult(GsonUtils.any2Json(item)))
                })
                finish()
            }
        }
    }

    private val mLetterAdapter: CommonRecyclerAdapter<ItemSearchLetterLetterBinding, String> by lazy {
        CommonRecyclerAdapter(
            R.layout.item_search_letter_letter,
            BR.item
        ) { mBinding, _, item ->
            mBinding?.root?.setOnClickListener {
                indexMap[item]?.let { index ->
                    mSearchLetterBinding.rvSearchLetterList.recyclerView().scrollToPosition(index)
                }
            }
        }
    }

    // 字母列表
    private val letterList: MutableLiveData<MutableSet<String>> = MutableLiveData(mutableSetOf())

    // 索引
    private val indexMap: MutableMap<String, Int> = mutableMapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mSearchLetterBinding.root)
        window.statusBarColor = Color.WHITE

        searchLetterType = IntentParams.SearchLetterParams.parseSearchLetterType(intent)
        bankCode = IntentParams.SearchLetterParams.parseBankCode(intent)

        mSearchLetterBinding.barSearchLetterTitle.setTitle(
            if (1 == searchLetterType) R.string.select_open_bank_sub_branch else R.string.select_bank
        )

        // 银行和支行列表
        mSearchLetterBinding.rvSearchLetterList.layoutManager = LinearLayoutManager(this)
        mSearchLetterBinding.rvSearchLetterList.requestData = object :
            CommonRefreshRecyclerView.OnRequestDataListener<ISearchLetterEntity>() {
            override fun requestData(
                isRefresh: Boolean,
                page: Int,
                pageSize: Int,
                callBack: (responseList: ResponseList<out ISearchLetterEntity>?) -> Unit
            ) {
                requestDataList(page, pageSize, callBack)
            }
        }
        mSearchLetterBinding.rvSearchLetterList.adapter = mAdapter

        // 字母来不及
        mSearchLetterBinding.rvSearchLetterLetterList.layoutManager = LinearLayoutManager(this)
        mSearchLetterBinding.rvSearchLetterLetterList.adapter = mLetterAdapter
        letterList.observe(this) {
            val notEmpty = !it.isNullOrEmpty()
            mSearchLetterBinding.rvSearchLetterLetterList.visibility(notEmpty)
            if (notEmpty) {
                mLetterAdapter.refreshList(it.toMutableList(), true)
            }
        }

        mSearchLetterBinding.rvSearchLetterList.requestRefresh(true)
    }

    override fun backBtn(): View = mSearchLetterBinding.barSearchLetterTitle.getBackBtn()

    private fun requestDataList(
        page: Int,
        pageSize: Int,
        callBack: (responseList: ResponseList<out ISearchLetterEntity>?) -> Unit
    ) {
        val keyword = mSearchLetterBinding.etSearchLetterKeyword.text.toString().trim()
        launch({
            val params = hashMapOf(
                "page" to page,
                "pageSize" to pageSize,
            )

            val list = ApiRepository.dealApiResult(
                if (0 == searchLetterType) {
                    mCommonRepo.bankList(
                        ApiRepository.createRequestBody(
                            params.apply {
                                if (!keyword.isNullOrEmpty()) {
                                    "bankName" to keyword
                                }
                            }
                        )
                    )
                } else {
                    mCommonRepo.subBankList(
                        ApiRepository.createRequestBody(
                            params.apply {
                                "bankCode" to bankCode
                                if (!keyword.isNullOrEmpty()) {
                                    "subBankName" to keyword
                                }
                            }
                        )
                    )
                }
            )?.also {
                if (0 == searchLetterType) {
                    val letters = letterList.value
                    if (1 == page) {
                        letters?.clear()
                    }
                    it.items.forEachIndexed { index, item ->
                        val letter = item.getLetter()
                        if (!letter.isNullOrEmpty() && true == letters?.add(letter)) {
                            item.showLetter = true
                            indexMap[letter] =
                                (if (mAdapter.itemCount > 0) mAdapter.itemCount - 1 else 0) + index
                        }
                    }
                    letterList.postValue(letters)
                }
            }
            withContext(Dispatchers.Main) {
                callBack(list)
            }
        }, { e ->
            Timber.d("请求失败或异常$e")
            withContext(Dispatchers.Main) {
                e.message?.let { it1 -> SToast.showToast(this@SearchLetterActivity, msg = it1) }
                callBack(null)
            }
        })
    }
}
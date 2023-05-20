package com.shangyun.haile_manager_android.ui.activity.subAccount

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.ui.base.activity.BaseBindingActivity
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.gson.GsonUtils
import com.shangyun.haile_manager_android.BR
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.apiService.StaffService
import com.shangyun.haile_manager_android.data.entities.StaffEntity
import com.shangyun.haile_manager_android.data.model.ApiRepository
import com.shangyun.haile_manager_android.databinding.ActivitySubAccountSelectBinding
import com.shangyun.haile_manager_android.databinding.ItemSubAccountSelectBinding
import com.shangyun.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter
import com.shangyun.haile_manager_android.ui.view.refresh.CommonRefreshRecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class SubAccountSelectActivity : BaseBindingActivity<ActivitySubAccountSelectBinding>() {
    private val mStaffRepo = ApiRepository.apiClient(StaffService::class.java)

    companion object {
        const val SubAccountData = "subAccountData"
    }

    override fun layoutId(): Int = R.layout.activity_sub_account_select

    override fun backBtn(): View = mBinding.barSubAccountAccountTitle.getBackBtn()

    private val mAdapter by lazy {
        CommonRecyclerAdapter<ItemSubAccountSelectBinding, StaffEntity>(
            R.layout.item_sub_account_select, BR.item
        ) { mItemBinding, pos, item ->
            mItemBinding?.ivSubAccountSelectItemRight?.setImageResource(if (item.isCheck) R.mipmap.icon_checked else R.mipmap.icon_uncheck)
            mItemBinding?.root?.setOnClickListener {
                changeListCheck(pos)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initData()
    }

    private fun initView() {
        initRightBtn()

        mBinding.rvSubAccountList.layoutManager = LinearLayoutManager(this)
        mBinding.rvSubAccountList.adapter = mAdapter
        mBinding.rvSubAccountList.requestData =
            object : CommonRefreshRecyclerView.OnRequestDataListener<StaffEntity>() {
                override fun requestData(
                    page: Int,
                    pageSize: Int,
                    callBack: (responseList: ResponseList<out StaffEntity>?) -> Unit
                ) {
                    requestSubAccountList(page, pageSize, callBack)
                }
            }
    }

    private fun initRightBtn() {
        mBinding.barSubAccountAccountTitle.getRightBtn().run {
            setText(R.string.finish)
            setTextColor(
                ContextCompat.getColor(
                    this@SubAccountSelectActivity,
                    R.color.colorPrimary
                )
            )
            setOnClickListener {
                mAdapter.list.find { e -> e.isCheck }?.let { e ->
                    setResult(RESULT_OK, Intent().apply {
                        putExtra(SubAccountData, GsonUtils.any2Json(e))
                    })
                    finish()
                }
            }
        }
    }

    private fun initData() {
        mBinding.rvSubAccountList.requestRefresh()
    }

    private fun requestSubAccountList(
        page: Int,
        pageSize: Int,
        result: (listWrapper: ResponseList<StaffEntity>?) -> Unit
    ) {
        launch({
            ApiRepository.dealApiResult(
                mStaffRepo.requestStaffList(
                    ApiRepository.createRequestBody(
                        hashMapOf(
                            "page" to page,
                            "pageSize" to pageSize,
                        )
                    )
                )
            )?.let { res ->
                intent.getStringExtra(SubAccountData)?.let {
                    GsonUtils.json2Class(it, StaffEntity::class.java)?.let { e ->
                        res.items.find { item -> item.id == e.id }?.let { item ->
                            item.isCheck = true
                        }
                    }
                }

                withContext(Dispatchers.Main) {
                    result.invoke(res)
                }
            }
        }, {
            Timber.d("请求失败或异常$it")
            withContext(Dispatchers.Main) {
                it.message?.let { it1 -> SToast.showToast(msg = it1) }
                result.invoke(null)
            }
        }, null, 1 == page)
    }

    /**
     * 改变列表选中
     */
    private fun changeListCheck(pos: Int) {
        mAdapter.list.forEachIndexed { index, e ->
            e.isCheck = (pos == index)
        }
        mAdapter.notifyDataSetChanged()
    }
}
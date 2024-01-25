package com.yunshang.haile_manager_android.ui.activity.staff

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.lsy.framelib.ui.base.activity.BaseBindingActivity
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.apiService.ShopService
import com.yunshang.haile_manager_android.data.entities.ShopSelectEntity
import com.yunshang.haile_manager_android.data.model.ApiRepository
import com.yunshang.haile_manager_android.databinding.ActivityStaffManagerShopListBinding
import com.yunshang.haile_manager_android.databinding.ItemStaffManagerShopListBinding
import com.yunshang.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter
import com.yunshang.haile_manager_android.ui.view.refresh.CustomDividerItemDecoration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StaffManagerShopListActivity :
    BaseBindingActivity<ActivityStaffManagerShopListBinding>() {
    private val mShopRepo = ApiRepository.apiClient(ShopService::class.java)

    private val mAdapter by lazy {
        CommonRecyclerAdapter<ItemStaffManagerShopListBinding, ShopSelectEntity>(
            R.layout.item_staff_manager_shop_list, BR.item
        ) { _, _, _ -> }
    }

    override fun layoutId(): Int = R.layout.activity_staff_manager_shop_list

    override fun backBtn(): View = mBinding.barStaffManagerShopListTitle.getBackBtn()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.WHITE

        mBinding.rvStaffManagerShopList.layoutManager =
            LinearLayoutManager(this@StaffManagerShopListActivity)
        mBinding.rvStaffManagerShopList.adapter = mAdapter

        requestData()
    }

    private fun requestData() {
        launch({
            ApiRepository.dealApiResult(
                mShopRepo.shopSelectList(
                    ApiRepository.createRequestBody(
                        hashMapOf()
                    )
                )
            )?.let {
                withContext(Dispatchers.Main) {
                    mAdapter.refreshList(it, true)
                }
            }
        })
    }
}
package com.yunshang.haile_manager_android.ui.activity.device

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.lsy.framelib.utils.DimensionUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.DeviceModelViewModel
import com.yunshang.haile_manager_android.data.arguments.DeviceModelParam
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.common.DeviceCategory
import com.yunshang.haile_manager_android.data.entities.Spu
import com.yunshang.haile_manager_android.databinding.ActivityDeviceModelBinding
import com.yunshang.haile_manager_android.databinding.ItemDeviceCategoryBinding
import com.yunshang.haile_manager_android.databinding.ItemDeviceModelBinding
import com.yunshang.haile_manager_android.databinding.ItemDeviceModelItemBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.view.GridSpaceItemDecoration
import com.yunshang.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter
import com.yunshang.haile_manager_android.utils.GlideUtils

/**
 * Title :
 * Author: Lsy
 * Date: 2023/4/25 14:37
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class DeviceModelActivity :
    BaseBusinessActivity<ActivityDeviceModelBinding, DeviceModelViewModel>(
        DeviceModelViewModel::class.java,
        BR.vm
    ) {

    private val mAdapter: CommonRecyclerAdapter<ItemDeviceModelBinding, DeviceModelParam> by lazy {
        CommonRecyclerAdapter(R.layout.item_device_model, BR.item) { mBinding, _, model ->
            // 嵌套RecyclerView
            if (null == mBinding?.rvDeviceModel?.adapter) {
                mBinding?.rvDeviceModel?.layoutManager =
                    GridLayoutManager(this@DeviceModelActivity, 3)
                mBinding?.rvDeviceModel?.addItemDecoration(
                    GridSpaceItemDecoration(
                        DimensionUtils.dip2px(this@DeviceModelActivity, 20f),
                        DimensionUtils.dip2px(this@DeviceModelActivity, 8f)
                    )
                )
                mBinding?.rvDeviceModel?.adapter =
                    CommonRecyclerAdapter<ItemDeviceModelItemBinding, Spu>(
                        R.layout.item_device_model_item, BR.item
                    ) { itemBinding, _, spu ->
                        itemBinding?.ivDeviceModelMain?.let {
                            GlideUtils.glideDefaultFactory(
                                it,
                                spu.mainPic,
                                R.mipmap.icon_device_model_default
                            ).fitCenter().into(it)
                        }
                        itemBinding?.root?.setOnClickListener {
                            setResult(
                                IntentParams.DeviceCategoryModelParams.ResultCode,
                                Intent().apply {
                                    putExtras(
                                        IntentParams.DeviceCategoryModelParams.packResult(
                                            spu.id,
                                            mViewModel.selectCategory.value?.name,
                                            spu.name + spu.feature,
                                            mViewModel.selectCategory.value?.id,
                                            mViewModel.selectCategory.value?.code,
                                            spu.communicationType,
                                            spu.getIgnorePayCodeFlag(),
                                            spu.extAttrDto
                                        )
                                    )
                                })
                            finish()
                        }
                    }
            }
            (mBinding?.rvDeviceModel?.adapter as CommonRecyclerAdapter<*, Spu>).refreshList(
                model.list,
                true
            )
        }
    }

    override fun layoutId(): Int = R.layout.activity_device_model

    override fun backBtn(): View = mBinding.barDeviceModelTitle.getBackBtn()

    override fun initView() {
        window.statusBarColor = Color.WHITE

        mBinding.rvDeviceModelList.layoutManager = LinearLayoutManager(this)
        ResourcesCompat.getDrawable(
            resources,
            R.drawable.divide_size8,
            null
        )?.let { drawable ->
            mBinding.rvDeviceModelList.addItemDecoration(
                DividerItemDecoration(this, DividerItemDecoration.VERTICAL).apply {
                    setDrawable(drawable)
                })
        }
        mBinding.rvDeviceModelList.adapter = mAdapter
    }

    override fun initEvent() {
        super.initEvent()

        //分类列表
        mViewModel.categoryList.observe(this) { list ->
            mBinding.rgDeviceModelCategory.removeAllViews()
            list?.let {
                it.forEachIndexed { index, entity ->
                    val categoryItem = LayoutInflater.from(this@DeviceModelActivity)
                        .inflate(R.layout.item_device_category, null, false)
                    val itemBinding = ItemDeviceCategoryBinding.bind(categoryItem)
                    itemBinding.root.id = index
                    if (0 == index) {
                        itemBinding.root.isChecked = true
                    }
                    itemBinding.rbDeviceCategory.text = entity.name
                    itemBinding.root.setOnClickListener {
                        mViewModel.selectCategory.value = entity
                    }
                    mBinding.rgDeviceModelCategory.addView(
                        itemBinding.root,
                        ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            DimensionUtils.dip2px(this@DeviceModelActivity, 36f)
                        )
                    )
                }
            }
        }

        mViewModel.selectCategory.observe(this) {
            mViewModel.requestData(1, it.id)
        }

        mViewModel.deviceModel.observe(this) {
            mAdapter.refreshList(it, isRefresh = true)
        }
    }

    override fun initData() {
        mViewModel.requestData()
    }
}
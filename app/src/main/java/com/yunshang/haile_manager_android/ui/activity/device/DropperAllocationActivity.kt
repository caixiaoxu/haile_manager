package com.yunshang.haile_manager_android.ui.activity.device

import android.content.Intent
import android.graphics.Color
import android.text.Editable
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.DropperAllocationViewModel
import com.yunshang.haile_manager_android.data.entities.DosingConfigs
import com.yunshang.haile_manager_android.data.entities.SkuEntity
import com.yunshang.haile_manager_android.databinding.ActivityDropperAllocationBinding
import com.yunshang.haile_manager_android.databinding.ItemDisposeUseDryerBinding
import com.yunshang.haile_manager_android.databinding.ItemDisposeUseItemBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter

class DropperAllocationActivity :
    BaseBusinessActivity<ActivityDropperAllocationBinding, DropperAllocationViewModel>(
        DropperAllocationViewModel::class.java, BR.vm
    ) {

    companion object {
        const val Amount = "amount"
        const val Price = "price"
        const val liquidType = "liquidType"
        const val isDefault = "isdefault"
        const val isOn = "ison"
        const val itemId = "itemid"
    }

    private var itemid: String = ""

    override fun layoutId(): Int = R.layout.activity_dropper_allocation

    override fun backBtn(): View = mBinding.barDeviceTitle.getBackBtn()

    override fun initIntent() {
        super.initIntent()
    }

    override fun initEvent() {
        super.initEvent()

        mBinding.etNumber.addTextChangedListener { text ->
            mViewModel.amount.postValue(text.toString())
        }
        mBinding.etPrice.addTextChangedListener { text ->
            mViewModel.price.postValue(text.toString())
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE
        var amount = intent.getStringExtra(Amount)
        itemid = intent.getStringExtra(itemId) ?: ""
        var price = intent.getStringExtra(Price)
        var liquidtype = intent.getIntExtra(liquidType, 0)
        var ison = intent.getBooleanExtra(isOn, false)
        var isdefault = intent.getBooleanExtra(isDefault, false)

        mBinding.etNumber.text = Editable.Factory.getInstance().newEditable(amount ?: "")
        mBinding.etPrice.text = Editable.Factory.getInstance().newEditable(price ?: "")
        mBinding.cbDefault.isChecked = isdefault
        mBinding.cbState.isChecked = ison

        mBinding.btnSubmit.setOnClickListener {
            setResult(RESULT_OK, Intent().apply {
                putExtra(Amount, mBinding.etNumber.text.toString())
                putExtra(Price, mBinding.etPrice.text.toString())
                putExtra(isDefault, mBinding.cbDefault.isChecked)
                putExtra(isOn, mBinding.cbState.isChecked)
                putExtra(itemId, itemid)
                putExtra(liquidType, liquidtype)
            })
            finish()
        }
    }

    override fun initData() {
    }
}
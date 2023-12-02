package com.yunshang.haile_manager_android.ui.view.dialog

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lsy.framelib.utils.DimensionUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.databinding.DialogPositionCreateBottomSheetBinding


/**
 * Title : 日期选择Dialog
 * Author: Lsy
 * Date: 2023/4/11 09:47
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class PositionCreateSheetDialog private constructor(private val builder: Builder) :
    BottomSheetDialogFragment() {
    private val POSITION_CREATE_BOTTOM_SHEET_TAG = "position_create_bottom_sheet_tag"
    private lateinit var mBinding: DialogPositionCreateBottomSheetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CommonBottomSheetDialogStyle)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            if (this is BottomSheetDialog) {
                setOnShowListener {
                    behavior.isHideable = false
                    // dialog上还有一层viewGroup，需要设置成透明
                    (requireView().parent as ViewGroup).setBackgroundColor(Color.TRANSPARENT)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.dialog_position_create_bottom_sheet,
            container,
            false
        )
        mBinding.setVariable(BR.build, builder)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 关闭
        mBinding.tvPositionCreateClose.setOnClickListener {
            dismiss()
        }

        mBinding.tvPositionCreateTitle.setText(if (1 == builder.addType) R.string.add_contact_phone else R.string.add_pt1)
        // 确定
        mBinding.tvPositionCreateSave.setOnClickListener {
            dismiss()
            if (0 == builder.addType) {
                builder.callback?.invoke(
                    builder.positionName.value,
                    builder.positionFloor.value
                )
            } else {
                builder.callback?.invoke(builder.contactPhone.value, null)
            }
        }

        builder.canSave.observe(this) {
            mBinding.tvPositionCreateSave.alpha = if (it) 1f else 0.4f
            mBinding.tvPositionCreateSave.isEnabled = it
        }

        mBinding.clPositionCreateTitle
    }

    /**
     * 默认显示
     */
    fun show(manager: FragmentManager) {
        show(manager, POSITION_CREATE_BOTTOM_SHEET_TAG)
    }

    /**
     * @param addType 0添加营业点 1添加联系电话
     */
    internal class Builder(
        val addType: Int,
        val callback: (value1: String?, value2: String?) -> Unit
    ) {

        val positionName: MutableLiveData<String> by lazy {
            MutableLiveData()
        }

        val positionFloor: MutableLiveData<String> by lazy {
            MutableLiveData()
        }

        val contactPhone: MutableLiveData<String> by lazy {
            MutableLiveData()
        }

        // 是否可提交
        val canSave: MediatorLiveData<Boolean> = MediatorLiveData(false).apply {
            addSource(positionName) {
                value = checkSave()
            }
            addSource(positionFloor) {
                value = checkSave()
            }
            addSource(contactPhone) {
                value = checkSave()
            }
        }

        private fun checkSave(): Boolean =
            (0 == addType && !positionName.value.isNullOrEmpty() && !positionFloor.value.isNullOrEmpty())
                    || (1 == addType && !contactPhone.value.isNullOrEmpty())

        /**
         * 构建
         */
        fun build(): PositionCreateSheetDialog = PositionCreateSheetDialog(this)
    }
}
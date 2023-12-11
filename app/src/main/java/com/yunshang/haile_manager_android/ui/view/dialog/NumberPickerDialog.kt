package com.yunshang.haile_manager_android.ui.view.dialog

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.databinding.DialogNumberPickerBinding
import com.yunshang.haile_manager_android.ui.view.dialog.dateTime.NumericWheelAdapter

/**
 * Title : 常用的dialog
 * Author: Lsy
 * Date: 2023/4/4 09:55
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class NumberPickerDialog private constructor(private val builder: Builder) :
    BottomSheetDialogFragment() {
    private val NUMBER_PICKER_TAG = "number_picker_tag"
    private lateinit var mBinding: DialogNumberPickerBinding

    private var selectIndex: Int = 0

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
        mBinding = DialogNumberPickerBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.tvCommonDialogTitle.text = builder.title

        mBinding.tvCommonDialogClose.setOnClickListener {
            dismiss()
        }

        mBinding.wvNumber.setCyclic(false)//不循环
        mBinding.wvNumber.setTextSize(22f)//字体
        mBinding.wvNumber.setItemsVisibleCount(7)//可显示7个
        mBinding.wvNumber.setDividerColor(Color.TRANSPARENT)
        mBinding.wvNumber.setBackgroundColor(Color.TRANSPARENT)
        mBinding.wvNumber.setTextColorCenter(Color.parseColor("#333333"))//中心颜色
        mBinding.wvNumber.setTextColorOut(Color.parseColor("#999999"))
        mBinding.wvNumber.setOnItemSelectedListener() { index ->
            selectIndex = index
        }
        selectIndex = builder.curVal - builder.minVal
        mBinding.wvNumber.currentItem = selectIndex // 默认选中
        mBinding.wvNumber.adapter = NumericWheelAdapter(builder.minVal, builder.maxVal)
        mBinding.wvNumber.invalidate()

        mBinding.btnNumberSure.setOnClickListener {
            dismiss()
            builder.callBack(builder.minVal + selectIndex)
        }
    }

    /**
     * 默认显示
     */
    fun show(manager: FragmentManager) {
        show(manager, NUMBER_PICKER_TAG)
    }

    internal class Builder(

        val minVal: Int,
        val maxVal: Int,
        val curVal: Int,
        val title: String? = "",
        val callBack: (value: Int) -> Unit
    ) {

        /**
         * 构建
         */
        fun build(): NumberPickerDialog = NumberPickerDialog(this)
    }
}
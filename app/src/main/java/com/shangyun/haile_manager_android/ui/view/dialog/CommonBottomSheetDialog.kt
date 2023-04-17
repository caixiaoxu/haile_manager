package com.shangyun.haile_manager_android.ui.view.dialog

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lsy.framelib.utils.DimensionUtils
import com.lsy.framelib.utils.SToast
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.data.entities.CommonBottomItemEntity
import com.shangyun.haile_manager_android.databinding.DialogCommonBottomSheetBinding
import com.shangyun.haile_manager_android.databinding.ItemCommonBottomSheetDialogBinding


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
class CommonBottomSheetDialog<D : CommonBottomItemEntity> private constructor(private val builder: Builder<D>) :
    BottomSheetDialogFragment() {
    private val COMMON_BOTTOM_SHEET_TAG = "common_bottom_sheet_tag"
    private lateinit var mBinding: DialogCommonBottomSheetBinding

    private var curEntity: D? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DateTimeStyle)
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
        mBinding = DialogCommonBottomSheetBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 关闭
        mBinding.tvCommonDialogClose.setOnClickListener {
            dismiss()
        }
        mBinding.tvCommonDialogCancel.setOnClickListener {
            dismiss()
        }

        mBinding.tvCommonDialogTitle.text = builder.title

        // 确定
        mBinding.tvCommonDialogSure.setOnClickListener {
            curEntity?.let { cur ->
                dismiss()
                builder.onValueSureListener?.onValue(cur)
            } ?: SToast.showToast(context, "您还没有选择选项")
        }

        //选项
        mBinding.llDialogCommonList.removeAllViews()
        builder.list.forEach { data ->
            val itemBinding = ItemCommonBottomSheetDialogBinding.bind(
                layoutInflater.inflate(
                    R.layout.item_common_bottom_sheet_dialog,
                    null
                )
            )
            mBinding.llDialogCommonList.addView(
                itemBinding.root.apply {
                    text = data.getTitle()
                    setOnCheckedChangeListener { _, isChecked ->
                        if (isChecked) curEntity = data
                    }
                },
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    DimensionUtils.dip2px(requireContext(), 54f)
                )
            )
        }
    }

    /**
     * 默认显示
     */
    fun show(manager: FragmentManager) {
        //不可取消
        isCancelable = builder.isCancelable
        show(manager, COMMON_BOTTOM_SHEET_TAG)
    }

    internal class Builder<D : CommonBottomItemEntity>(val title: String, val list: List<D>) {

        // 不可取消
        var isCancelable = true

        // 选择监听
        var onValueSureListener: OnValueSureListener<D>? = null

        /**
         * 构建
         */
        fun build(): CommonBottomSheetDialog<D> = CommonBottomSheetDialog(this)
    }

    interface OnValueSureListener<D : CommonBottomItemEntity> {
        fun onValue(data: D)
    }
}
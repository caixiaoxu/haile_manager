package com.yunshang.haile_manager_android.ui.view.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.core.view.children
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lsy.framelib.utils.DimensionUtils
import com.lsy.framelib.utils.SToast
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.data.rule.IMultiSelectBottomItemEntity
import com.yunshang.haile_manager_android.databinding.DialogMultiSelectBottomSheetBinding
import com.yunshang.haile_manager_android.databinding.ItemMultiSelectSheetDialogBinding


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
class MultiSelectBottomSheetDialog<D : IMultiSelectBottomItemEntity> private constructor(private val builder: Builder<D>) :
    BottomSheetDialogFragment() {
    private val MULTI_SELECT_BOTTOM_SHEET_TAG = "multi_select_sheet_tag"
    private lateinit var mBinding: DialogMultiSelectBottomSheetBinding

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
        mBinding = DialogMultiSelectBottomSheetBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 关闭
        mBinding.tvMultiSelectDialogClose.setOnClickListener {
            dismiss()
        }
        mBinding.tvMultiSelectDialogCancel.setOnClickListener {
            dismiss()
        }

        mBinding.tvMultiSelectDialogTitle.text = builder.title

        // 确定
        mBinding.tvMultiSelectDialogSure.setOnClickListener {
            // 提取选中数据
            val selectList = builder.list.filter { entity -> entity.isCheck }
            if (!builder.isCanSelectEmpty && selectList.isEmpty()) {
                SToast.showToast(context, "您还没有选择选项")
                return@setOnClickListener
            }
            builder.onValueSureListener?.onValue(selectList, builder.list)
            dismiss()
        }
        buildItem()
    }

    /**
     * 刷新item布局
     */
    private fun buildItem() {
        val first = mBinding.clMultiSelectList.getChildAt(0)
        mBinding.clMultiSelectList.removeAllViews()
        mBinding.clMultiSelectList.addView(first)
        builder.list.forEachIndexed { index, data ->
            val itemBinding = ItemMultiSelectSheetDialogBinding.bind(
                layoutInflater.inflate(
                    R.layout.item_multi_select_sheet_dialog,
                    null
                )
            )
            itemBinding.root.id = index + 1
            mBinding.clMultiSelectList.addView(
                itemBinding.root.apply {
                    text = data.getTitle()
                    isChecked = data.isCheck

                    setOnCheckClickListener {
                        if (builder.supportSingle) {
                            mBinding.clMultiSelectList.children.forEach { view ->
                                if (view is AppCompatCheckBox) {
                                    view.isChecked = it.id == view.id
                                }
                            }
                            true
                        } else
                            false
                    }

                    setOnCheckedChangeListener { _, isChecked ->
                        data.isCheck = isChecked
                    }
                }, ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    DimensionUtils.dip2px(requireContext(), 36f)
                )
            )
        }
        // 设置id
        val size = builder.list.size + if (builder.canAdd) 1 else 0
        if (builder.canAdd) {
            val itemBinding = ItemMultiSelectSheetDialogBinding.bind(
                layoutInflater.inflate(
                    R.layout.item_multi_select_sheet_dialog,
                    null
                )
            )
            itemBinding.root.id = size
            mBinding.clMultiSelectList.addView(
                itemBinding.root.apply {
                    text = builder.addTitle
                    setCompoundDrawablesWithIntrinsicBounds(
                        R.mipmap.icon_device_fun_configure_add, 0, 0, 0
                    )
                    setOnCheckClickListener {
                        builder.onAddValueListener?.invoke()
                        true
                    }
                }, ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    DimensionUtils.dip2px(requireContext(), 36f)
                )
            )
        }
        val idList = IntArray(size) { it + 1 }
        mBinding.flowMultiSelectList.referencedIds = idList
    }

    /**
     * 刷新选项列表
     */
    fun refreshListView(list: List<D>) {
        builder.list = list
        buildItem()
    }

    override fun onDismiss(dialog: DialogInterface) {
        builder.onCancelListener?.invoke(builder.list)
        super.onDismiss(dialog)
    }

    /**
     * 默认显示
     */
    fun show(manager: FragmentManager) {
        //不可取消
        isCancelable = builder.isCancelable
        show(manager, MULTI_SELECT_BOTTOM_SHEET_TAG)
    }

    internal class Builder<D : IMultiSelectBottomItemEntity>(
        val title: String,
        var list: List<D>,
    ) {
        var isCanSelectEmpty = false

        // 不可取消
        var isCancelable = true

        // 选择监听
        var onValueSureListener: OnValueSureListener<D>? = null

        // 取消监听
        var onCancelListener: ((datas: List<D>) -> Unit)? = null

        // 支持单选
        var supportSingle: Boolean = false

        // 是否可新增
        var canAdd: Boolean = false

        // 新增标题
        var addTitle: String = "新增"

        // 新增事件
        var onAddValueListener: (() -> Unit)? = null

        /**
         * 构建
         */
        fun build(): MultiSelectBottomSheetDialog<D> = MultiSelectBottomSheetDialog(this)
    }

    interface OnValueSureListener<D : IMultiSelectBottomItemEntity> {
        fun onValue(selectData: List<D>, allSelectData: List<D>)
    }
}
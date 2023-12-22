package com.yunshang.haile_manager_android.ui.view.dialog

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lsy.framelib.utils.SToast
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.data.rule.ICommonNewBottomItemEntity
import com.yunshang.haile_manager_android.databinding.DialogCommonNewBottomSheetBinding
import com.yunshang.haile_manager_android.ui.view.adapter.ViewBindingAdapter.visibility


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
class CommonNewBottomSheetDialog<D : ICommonNewBottomItemEntity, V : ViewDataBinding> private constructor(
    private val builder: Builder<D, V>
) :
    BottomSheetDialogFragment() {
    private val COMMON_BOTTOM_SHEET_TAG = "common_new_bottom_sheet_tag"
    private lateinit var mBinding: DialogCommonNewBottomSheetBinding

    private var curEntity: D? = null

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
        mBinding = DialogCommonNewBottomSheetBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 关闭
        mBinding.tvCommonNewDialogClose.setOnClickListener {
            dismiss()
        }

        mBinding.tvCommonNewDialogTitle.text = builder.title
        // 确定
        mBinding.tvCommonNewDialogSure.visibility(builder.multiSelect)
        mBinding.tvCommonNewDialogSure.setOnClickListener {
            if (builder.mustSelect && builder.list?.all { item -> !item.commonItemSelect } == true) {
                SToast.showToast(context, "您还没有选择选项")
                return@setOnClickListener
            }

            dismiss()
            builder.onSelectEvent()
        }

        buildSelectItemList()

        mBinding.btnCommonNew.visibility(builder.showBottomBtn)
        mBinding.btnCommonNew.text = builder.bottomBtnTxt
        mBinding.btnCommonNew.setOnClickListener {
            builder.bottomBtnEvent?.let { event ->
                when (event(builder.list)) {
                    1 -> buildSelectItemList()
                    2 -> dismiss()
                    else -> {}
                }
            }
        }
    }

    /**
     * 构建选项列表
     */
    private fun buildSelectItemList() {
        //选项
        mBinding.llCommonNewDialogChild.removeAllViews()
        if (!builder.list.isNullOrEmpty()) {
            builder.list.forEachIndexed { index, data ->
                mBinding.llCommonNewDialogChild.addView(
                    builder.buildItemView(index, data).also { binding ->
                        if (!builder.isCustomItemClick) {
                            binding.root.setOnClickListener {
                                if (builder.multiSelect) {
                                    data.commonItemSelect = !data.commonItemSelect
                                } else {
                                    builder.list.forEach {
                                        it.commonItemSelect =
                                            if (it == data) builder.mustSelect || !it.commonItemSelect else false
                                    }
                                }
                                builder.clickItemView?.invoke(binding, data)

                                if (!builder.multiSelect) {
                                    builder.onSelectEvent()
                                    dismiss()
                                }
                            }
                        }
                    }.root,
                    builder.lp ?: LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                )
            }
        }

        mBinding.tvCommonNewEmpty.visibility(builder.list.isNullOrEmpty())
    }

    /**
     * 默认显示
     */
    fun show(manager: FragmentManager) {
        //不可取消
        isCancelable = builder.isCancelable
        show(manager, COMMON_BOTTOM_SHEET_TAG)
    }

    internal class Builder<D : ICommonNewBottomItemEntity, V : ViewDataBinding>(
        val title: String,
        val list: List<D>?,
        val isCancelable: Boolean = true,// 不可取消
        val multiSelect: Boolean = false,//是否多选
        val mustSelect: Boolean = true,// 是否可不选
        val showBottomBtn: Boolean = false,// 是否显示底部按钮
        val bottomBtnTxt: String = "",// 是否显示底部按钮
        val bottomBtnEvent: ((list: List<D>?) -> Int)? = null,// 是否显示底部事件,0不做处理，1刷新选项列表，2关闭弹窗
        val lp: LinearLayout.LayoutParams? = null,
        val buildItemView: (index: Int, data: D) -> V,
        val isCustomItemClick: Boolean = false,
        val clickItemView: ((mItemBinding: V?, data: D) -> Unit)? = null,
        val onSelectEvent: () -> Unit,
    ) {
        /**
         * 构建
         */
        fun build(): CommonNewBottomSheetDialog<D, V> = CommonNewBottomSheetDialog(this)
    }
}
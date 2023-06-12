package com.yunshang.haile_manager_android.ui.view

import android.content.Context
import android.text.InputFilter.LengthFilter
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.setPadding
import androidx.core.widget.addTextChangedListener
import androidx.databinding.InverseBindingListener
import com.lsy.framelib.utils.DimensionUtils
import com.yunshang.haile_manager_android.R

/**
 * Title : 多类型ItemView
 * Author: Lsy
 * Date: 2023/4/17 10:15
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class MultiTypeItemView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayoutCompat(context, attrs) {

    // 类型
    private val type: Int

    // 标题界面
    val mTitleView: TextView by lazy {
        TextView(context).apply {
            textSize = 16f
            gravity = Gravity.CENTER
        }
    }

    // 内容
    var content = ""
        set(value) {
            // 内容不一样，通知变化
            val oldValue = field
            if (value == oldValue) {
                return
            }
            field = value
            onItemContentChange?.onChange()
        }
    val contentView: AppCompatEditText by lazy {
        AppCompatEditText(context).apply {
            textSize = 16f
            background = null
            setPadding(0)
            setHintTextColor(
                ResourcesCompat.getColor(
                    resources,
                    R.color.common_txt_hint_color,
                    null
                )
            )
            Gravity.CENTER_VERTICAL
            addTextChangedListener { content = it.toString() }
        }
    }

    // 单位界面界面
    val mTailView: TextView by lazy {
        TextView(context).apply {
            textSize = 16f
            setTextColor(ResourcesCompat.getColor(resources, R.color.common_txt_hint_color, null))
        }
    }

    // 选择事件
    var onSelectedEvent: (() -> Unit)? = null

    // 内容变化监听
    var onItemContentChange: InverseBindingListener? = null

    init {
        val array = context.obtainStyledAttributes(attrs, R.styleable.MultiTypeItemView)
        val canUpdate = array.getBoolean(R.styleable.MultiTypeItemView_canUpdate, true)
        val title = array.getString(R.styleable.MultiTypeItemView_title)
        val titleW = array.getDimensionPixelSize(
            R.styleable.MultiTypeItemView_titleW,
            DimensionUtils.dip2px(context, 104f)
        )
        val titleDrawableEnd =
            array.getResourceId(R.styleable.MultiTypeItemView_titleDrawableEnd, -1)
        val titleDrawablePadding =
            array.getDimensionPixelSize(R.styleable.MultiTypeItemView_titleDrawablePadding, -1)

        type = array.getInt(R.styleable.MultiTypeItemView_type, 0)
        val inputType = array.getInt(R.styleable.MultiTypeItemView_android_inputType, -1)
        val hint = array.getString(R.styleable.MultiTypeItemView_android_hint)
        val maxLength = array.getInt(R.styleable.MultiTypeItemView_android_maxLength, 0)
        val maxLines = array.getInt(R.styleable.MultiTypeItemView_android_maxLines, 0)
        val enabled = array.getBoolean(R.styleable.MultiTypeItemView_android_enabled, true)

        val showArrow = array.getBoolean(R.styleable.MultiTypeItemView_showArrow, false)
        val tailDrawable =
            array.getResourceId(R.styleable.MultiTypeItemView_tailDrawable, -1)
        val unitHint = array.getString(R.styleable.MultiTypeItemView_unitHint)
        array.recycle()

        // 添加标题
        addTitle(title, titleW, titleDrawableEnd, titleDrawablePadding, canUpdate)

        //添加内容
        addContent(inputType, hint, maxLength, maxLines, canUpdate, enabled)

        //添加尾部
        addTail(showArrow, tailDrawable, unitHint)
    }

    /**
     * 添加标题布局
     */
    private fun addTitle(
        title: String?,
        titleW: Int,
        titleDrawableEnd: Int,
        titleDrawablePadding: Int,
        canUpdate: Boolean
    ) {
        addView(
            LinearLayout(context).apply {
                gravity = Gravity.CENTER_VERTICAL
                addView(
                    mTitleView.apply {
                        text = title
                        setTextColor(
                            ResourcesCompat.getColor(
                                resources,
                                if (canUpdate) R.color.common_txt_color else R.color.common_txt_hint_color,
                                null
                            )
                        )
                        // 右图标
                        if (-1 != titleDrawableEnd) {
                            mTitleView.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                0, 0, titleDrawableEnd, 0
                            )
                            if (-1 != titleDrawablePadding) {
                                mTitleView.compoundDrawablePadding = titleDrawablePadding
                            }
                        }
                    },
                    LayoutParams(
                        LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT
                    )
                )
            },
            LayoutParams(
                titleW,
                LayoutParams.WRAP_CONTENT
            )
        )
    }

    /**
     * 添加内容控件
     */
    private fun addContent(
        inputType: Int,
        hint: String?,
        maxLength: Int,
        maxLines: Int,
        canUpdate: Boolean,
        enabled: Boolean
    ) {
        addView(
            contentView.also {
                isEnabled = enabled
                it.hint = hint
                    ?: if (1 == type) resources.getString(R.string.please_input) else resources.getString(
                        R.string.please_select
                    )
                it.setTextColor(
                    ResourcesCompat.getColor(
                        resources,
                        if (canUpdate) R.color.common_txt_color else R.color.common_txt_hint_color,
                        null
                    )
                )
                if (-1 != inputType) {
                    it.inputType = inputType
                }
                if (maxLength > 0) {
                    it.filters = arrayOf(LengthFilter(maxLength))
                }

                if (maxLines > 0) {
                    it.maxLines = maxLines
                }
                if (1 != type) {
                    it.isFocusable = false
                    it.isFocusableInTouchMode = false
                    it.setOnClickListener {
                        onSelectedEvent?.invoke()
                    }
                }
            },
            LayoutParams(
                0,
                LayoutParams.WRAP_CONTENT
            ).apply {
                weight = 1f
            }
        )
    }

    /**
     * 添加尾部
     */
    private fun addTail(showArrow: Boolean, tailDrawable: Int, unitHint: String?) {
        if (!showArrow && -1 == tailDrawable && unitHint.isNullOrEmpty()) {
            return
        }
        addView(
            mTailView.apply {
                gravity = Gravity.CENTER_VERTICAL
                if (!unitHint.isNullOrEmpty()) {
                    text = unitHint
                } else {
                    setCompoundDrawablesRelativeWithIntrinsicBounds(
                        0,
                        0,
                        if (-1 == tailDrawable) R.drawable.icon_arrow_right else tailDrawable,
                        0
                    )
                }
            },
            LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
            )
        )
    }
}
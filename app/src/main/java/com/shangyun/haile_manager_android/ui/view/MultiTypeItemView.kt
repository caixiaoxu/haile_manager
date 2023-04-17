package com.shangyun.haile_manager_android.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.addTextChangedListener
import androidx.databinding.InverseBindingListener
import com.lsy.framelib.utils.DimensionUtils
import com.shangyun.haile_manager_android.R

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
) : LinearLayout(context, attrs) {

    // 类型
    private val type: Int

    private val titleView: TextView by lazy {
        TextView(context).apply {
            textSize = 16f
            setTextColor(ResourcesCompat.getColor(resources, R.color.common_txt_color, null))
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, 0, DimensionUtils.dip2px(context, 8f), 0)
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

    private val contentInfoView: TextView by lazy {
        TextView(context).apply {
            textSize = 16f
            setTextColor(ResourcesCompat.getColor(resources, R.color.common_txt_color, null))
            setHintTextColor(
                ResourcesCompat.getColor(
                    resources,
                    R.color.common_txt_hint_color,
                    null
                )
            )
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, 0, DimensionUtils.dip2px(context, 8f), 0)
        }
    }
    private val contentInputView: EditText by lazy {
        EditText(context).apply {
            textSize = 16f
            setTextColor(ResourcesCompat.getColor(resources, R.color.common_txt_color, null))
            setHintTextColor(
                ResourcesCompat.getColor(
                    resources,
                    R.color.common_txt_hint_color,
                    null
                )
            )
            background = null
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, 0, DimensionUtils.dip2px(context, 8f), 0)

            addTextChangedListener { content = it.toString() }
        }
    }

    var onSelectedEvent: (() -> Unit)? = null

    // 内容变化监听
    var onItemContentChange: InverseBindingListener? = null

    init {
        val array = context.obtainStyledAttributes(attrs, R.styleable.MultiTypeItemView)
        val title = array.getString(R.styleable.MultiTypeItemView_title)
        type = array.getInt(R.styleable.MultiTypeItemView_type, 0)
        val hint = array.getString(R.styleable.MultiTypeItemView_hint)
        val showArrow = array.getBoolean(R.styleable.MultiTypeItemView_showArrow, true)
        array.recycle()

        // 底线
        setBackgroundResource(R.drawable.shape_bottom_stroke_dividing_efefef_mr8)

        // 添加布局
        // 标题
        titleView.text = title
        addTitle()
        // 内容
        // 提示语
        when (type) {
            // 显示/选择
            0 -> {
                getContentView().hint = hint ?: resources.getString(R.string.please_select)
                getContentView().setOnClickListener {
                    onSelectedEvent?.invoke()
                }
            }
            // 输入
            1 -> {
                getContentView().hint = hint ?: resources.getString(R.string.please_input)
            }
        }
        // 显示右箭头
        if (0 == type && showArrow) {
            contentInfoView.setCompoundDrawablesRelativeWithIntrinsicBounds(
                0,
                0,
                R.mipmap.icon_arrow_right,
                0
            )
        }
        addContent()
    }

    private fun addTitle() {
        addView(
            titleView,
            LayoutParams(
                DimensionUtils.dip2px(context, 104f),
                LayoutParams.MATCH_PARENT
            )
        )
    }

    private fun addContent() {
        addView(
            getContentView(),
            LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.MATCH_PARENT
            ).apply {
                weight = 1f
            }
        )
    }

    /**
     * 获取内容布局
     */
    fun getContentView() = when (type) {
        0 -> contentInfoView
        1 -> contentInputView
        else -> contentInfoView
    }
}
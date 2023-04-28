package com.shangyun.haile_manager_android.ui.view

import android.content.Context
import android.text.InputFilter.LengthFilter
import android.util.AttributeSet
import android.view.Gravity
import android.widget.EditText
import android.widget.FrameLayout
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

    // 单位界面界面
    private val unitView: TextView by lazy {
        TextView(context).apply {
            textSize = 16f
            setTextColor(ResourcesCompat.getColor(resources, R.color.common_txt_hint_color, null))
            setPadding(DimensionUtils.dip2px(context, 8f), 0, 0, 0)
        }
    }

    var onSelectedEvent: (() -> Unit)? = null

    // 内容变化监听
    var onItemContentChange: InverseBindingListener? = null

    init {
        val array = context.obtainStyledAttributes(attrs, R.styleable.MultiTypeItemView)
        val title = array.getString(R.styleable.MultiTypeItemView_title)
        val background = array.getResourceId(R.styleable.MultiTypeItemView_android_background, -1)
        val titleDrawableEnd =
            array.getResourceId(R.styleable.MultiTypeItemView_titleDrawableEnd, -1)
        val titleDrawablePadding =
            array.getDimensionPixelSize(R.styleable.MultiTypeItemView_titleDrawablePadding, -1)
        type = array.getInt(R.styleable.MultiTypeItemView_type, 0)
        val hint = array.getString(R.styleable.MultiTypeItemView_hint)
        val inputType = array.getInt(R.styleable.MultiTypeItemView_android_inputType, 0)
        val maxLength = array.getInt(R.styleable.MultiTypeItemView_android_maxLength, 0)
        val maxLines = array.getInt(R.styleable.MultiTypeItemView_android_maxLines, 0)
        val drawableEnd = array.getResourceId(R.styleable.MultiTypeItemView_android_drawableEnd, -1)
        val drawablePadding =
            array.getDimensionPixelSize(R.styleable.MultiTypeItemView_android_drawablePadding, -1)
        val showArrow = array.getBoolean(R.styleable.MultiTypeItemView_showArrow, true)
        val unitHint = array.getString(R.styleable.MultiTypeItemView_unitHint)
        array.recycle()

        // 背景
        if (-1 != background)
            setBackgroundResource(background)

        // 添加布局
        // 标题
        titleView.text = title
        if (-1 != titleDrawableEnd) {
            titleView.setCompoundDrawablesRelativeWithIntrinsicBounds(
                0, 0, titleDrawableEnd, 0
            )
            if (-1 != titleDrawablePadding) {
                titleView.compoundDrawablePadding = titleDrawablePadding
            }
        }
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
                if (inputType > 0) {
                    getContentView().inputType = inputType
                }
                if (maxLength > 0) {
                    getContentView().filters = arrayOf(LengthFilter(maxLength))
                }
                if (maxLines > 0) {
                    getContentView().maxLines = maxLines
                }
            }
        }
        // 显示右箭头
        if (0 == type) {
            if (showArrow) {
                contentInfoView.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    0, 0, R.drawable.icon_arrow_right, 0
                )
                if (-1 != drawablePadding) {
                    titleView.compoundDrawablePadding = drawablePadding
                }
            }
            if (-1 != drawableEnd) {
                contentInfoView.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    0, 0, drawableEnd, 0
                )
                if (-1 != drawablePadding) {
                    titleView.compoundDrawablePadding = drawablePadding
                }
            }
        }
        addContent()
        if (!unitHint.isNullOrEmpty()){
            unitView.text = unitHint
            addUnit()
        }
    }

    /**
     * 添加标题控件
     */
    private fun addTitle() {
        val frameLayout = FrameLayout(context)
        frameLayout.addView(
            titleView,
            FrameLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.CENTER_VERTICAL
            }
        )

        addView(
            frameLayout,
            LayoutParams(
                DimensionUtils.dip2px(context, 104f),
                LayoutParams.WRAP_CONTENT
            )
        )
    }

    /**
     * 添加内容控件
     */
    private fun addContent() {
        addView(
            getContentView(),
            LayoutParams(
                0,
                LayoutParams.WRAP_CONTENT
            ).apply {
                weight = 1f
            }
        )
    }

    /**
     * 添加单位控件
     */
    private fun addUnit() {
        addView(
            unitView,
            LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
            )
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
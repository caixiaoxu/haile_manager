package com.shangyun.haile_manager_android.ui.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.databinding.IncludeTitleActionBarBinding

/**
 * Title : 常用的标题栏
 * Author: Lsy
 * Date: 2023/4/3 14:33
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class CommonTitleActionBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {
    private val mBinding: IncludeTitleActionBarBinding

    init {
        mBinding = IncludeTitleActionBarBinding.bind(
            LayoutInflater.from(context).inflate(R.layout.include_title_action_bar, this)
        )

        val array = context.obtainStyledAttributes(attrs, R.styleable.CommonTitleActionBar)
        mBinding.root.setBackgroundColor(
            array.getInt(
                R.styleable.CommonTitleActionBar_bgColor,
                Color.TRANSPARENT
            )
        )
        val bgResId = array.getResourceId(
            R.styleable.CommonTitleActionBar_android_background,
            -1
        )
        if (-1 != bgResId){
            mBinding.root.setBackgroundResource(bgResId)
        }
        mBinding.tvTitleActionTitle.text = array.getString(R.styleable.CommonTitleActionBar_title)
        array.recycle()

    }

    /**
     * 获取后退按钮
     */
    fun getBackBtn() = mBinding.ibTitleActionBack

    /**
     * 获取标题
     */
    fun getTitle() = mBinding.tvTitleActionTitle

    /**
     * 获取右侧区域
     */
    fun getRightArea() = mBinding.llTitleActionRightArea

    /**
     * 设置标题
     * @param title 标题
     */
    fun setTitle(title: String) {
        getTitle().text = title
    }

    /**
     * 设置标题
     * @param titleResId 标题resId
     */
    fun setTitle(titleResId: Int) {
        getTitle().setText(titleResId)
    }

    /**
     * 设置回退事件
     */
    fun setOnBackListener(clickListener: OnClickListener) {
        getBackBtn().setOnClickListener(clickListener)
    }
}
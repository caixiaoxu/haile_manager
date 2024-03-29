package com.yunshang.haile_manager_android.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.lsy.framelib.utils.DimensionUtils
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.databinding.IncludeTitleActionBarBinding

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
        mBinding.tvTitleActionTitle.text = array.getString(R.styleable.CommonTitleActionBar_title)
        val rightModel = array.getInt(R.styleable.CommonTitleActionBar_rightModel, -1)
        if (-1 == rightModel) {
            mBinding.llTitleActionRightArea.visibility = View.GONE
        } else {
            mBinding.llTitleActionRightArea.visibility = View.VISIBLE
            if (0 == rightModel) {
                mBinding.btnTitleActionRight.visibility = View.VISIBLE
            }
        }
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
     * 获取右侧按钮
     */
    fun getRightBtn(hasBg: Boolean = false) = mBinding.btnTitleActionRight.apply {
        val pV = DimensionUtils.dip2px(context, 4f)
        if (hasBg) {
            setBackgroundResource(R.drawable.shape_sf0a258_r22)
            val pH = DimensionUtils.dip2px(context, 12f)
            setPadding(pH, pV, pH, pV)
        } else {
            setPadding(0, pV, 0, pV)
        }
    }

    /**
     * 设置标题
     * @param title 标题
     */
    fun setTitle(title: CharSequence) {
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
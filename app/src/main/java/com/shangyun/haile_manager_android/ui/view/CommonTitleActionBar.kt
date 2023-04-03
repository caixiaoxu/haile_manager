package com.shangyun.haile_manager_android.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.shangyun.haile_manager_android.R

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

    // 根布局
    private val titleActionBar by lazy {
        LayoutInflater.from(context).inflate(R.layout.include_title_action_bar, this)
    }

    // 返回按钮
    private val ibBack: ImageButton by lazy {
        titleActionBar.findViewById(R.id.ib_title_action_back)
    }

    // 标题
    private val tvTitle: TextView by lazy {
        titleActionBar.findViewById(R.id.tv_title_action_title)
    }

    // 右侧区域
    private val llRightArea: LinearLayout by lazy {
        titleActionBar.findViewById(R.id.ll_title_action_right_area)
    }

    /**
     * 获取后退按钮
     */
    fun getBackBtn() = ibBack

    /**
     * 获取标题
     */
    fun getTitle() = tvTitle

    /**
     * 获取右侧区域
     */
    fun getRightArea() = llRightArea

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
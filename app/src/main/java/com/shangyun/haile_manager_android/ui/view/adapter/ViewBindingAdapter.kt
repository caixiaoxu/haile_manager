package com.shangyun.haile_manager_android.ui.view.adapter

import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.MarginLayoutParamsCompat
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.lsy.framelib.utils.DimensionUtils
import com.shangyun.haile_manager_android.ui.view.*
import com.shangyun.haile_manager_android.utils.GlideUtils
import com.shangyun.haile_manager_android.utils.StringUtils

/**
 * Title :
 * Author: Lsy
 * Date: 2023/4/10 12:22
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
object ViewBindingAdapter {

    @BindingAdapter("marginStart")
    @JvmStatic
    fun AppCompatTextView.marginStart(mS: Float?) {
        if (layoutParams is ViewGroup.MarginLayoutParams)
            MarginLayoutParamsCompat.setMarginStart(
                layoutParams as ViewGroup.MarginLayoutParams,
                mS?.toInt() ?: 0
            )
    }

    /**
     * 显示标题
     */
    @BindingAdapter("title")
    @JvmStatic
    fun CommonTitleActionBar.setTitle(title: String?) {
        getTitle().text = title ?: ""
    }

    /**
     * TextView显示首字母
     */
    @BindingAdapter("firstLetter")
    @JvmStatic
    fun TextView.setFirstLetter(content: String) {
        if (content.isEmpty()) {
            text = ""
        } else {
            text = StringUtils.getFirstLetter(content[0])?.toString() ?: ""
        }
    }

    /**
     * TextView显示金额
     */
    @BindingAdapter("textPrice")
    @JvmStatic
    fun setPrice(view: TextView, price: Double) {
        view.text = StringUtils.createPriceStr(view.context, price)
    }

    /**
     * ImageView 加载本地图片
     */
    @BindingAdapter("imageRes")
    @JvmStatic
    fun loadImage(view: ImageView, res: Int) {
        view.setImageResource(res)
    }

    /**
     * ImageView 加载图络图片
     */
    @BindingAdapter("imgUrl")
    @JvmStatic
    fun loadImageUrl(view: ImageView, url: String) {
        GlideUtils.loadImage(view, url)
    }

    /**
     * ImageView 加载图络图片
     */
    @BindingAdapter("strokeWidth")
    @JvmStatic
    fun setStrokeWidth(view: CircleImageView, value: Int?) {
        value?.let {
            view.setStrokeWidth(DimensionUtils.dip2px(dipValue = it.toFloat()).toFloat())
        }
    }

    /**
     * 自定义itemContent
     */
    @BindingAdapter("itemContent")
    @JvmStatic
    fun MultiTypeItemView.setItemContent(content: String?) {
        getContentView().text = content ?: ""
        if (getContentView() is EditText) {
            (getContentView() as EditText).setSelection(getContentView().text.length)
        }
    }

    @InverseBindingAdapter(attribute = "itemContent", event = "itemContentAttrChanged")
    @JvmStatic
    fun MultiTypeItemView.getItemContent(): String = getContentView().text.toString()

    @BindingAdapter("itemContentAttrChanged")
    @JvmStatic
    fun MultiTypeItemView.itemContentChange(attrChange: InverseBindingListener) {
        onItemContentChange = attrChange
    }

    @BindingAdapter("bgResIds", "txtColors", "type")
    @JvmStatic
    fun MultiTypeTextView.setBgResIds(bgs: IntArray?, colors: IntArray?, type: Int?) {
        bgs?.let {
            bgResIds = it
        }
        colors?.let {
            txtColors = it
        }
        type?.let {
            this.type = it
        }
    }
}
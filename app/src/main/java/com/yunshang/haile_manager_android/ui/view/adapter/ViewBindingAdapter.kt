package com.yunshang.haile_manager_android.ui.view.adapter

import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.MarginLayoutParamsCompat
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.bumptech.glide.Glide
import com.lsy.framelib.utils.DimensionUtils
import com.yunshang.haile_manager_android.ui.view.*
import com.yunshang.haile_manager_android.utils.GlideUtils
import com.yunshang.haile_manager_android.utils.StringUtils

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

    @BindingAdapter("visibility")
    @JvmStatic
    fun View.visibility(show: Boolean?) {
        visibility = if (true == show) View.VISIBLE else View.GONE
    }

    @BindingAdapter("width", "height", requireAll = false)
    @JvmStatic
    fun View.setWH(width: Int?, height: Int?) {
        width?.let {
            layoutParams?.width = width
        }
        height?.let {
            layoutParams?.height = height
        }
    }

    @BindingAdapter("marginStart", "textColor", requireAll = false)
    @JvmStatic
    fun TextView.marginStart(mS: Float?, txtColor: Int?) {
        if (layoutParams is ViewGroup.MarginLayoutParams)
            MarginLayoutParamsCompat.setMarginStart(
                layoutParams as ViewGroup.MarginLayoutParams,
                mS?.toInt() ?: 0
            )

        txtColor?.let {
            setTextColor(txtColor)
        }
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
     * ImageView 加载本地图片/图络图片
     */
    @BindingAdapter("imgRes", "imgUrl", "imgCenterUrl", "imgHeadUrl", requireAll = false)
    @JvmStatic
    fun ImageView.loadImage(res: Int?=null, url: String?=null, imgCenterUrl: String?=null, imgHeadUrl: String?=null) {
        res?.let {
            setImageResource(res)
        }
        url?.let {
            Glide.with(this).load(url).fitCenter().into(this)
        }
        imgCenterUrl?.let {
            Glide.with(this).load(imgCenterUrl).centerCrop().into(this)
        }
        imgHeadUrl?.let {
            GlideUtils.loadImage(this, imgHeadUrl)
        }
    }

    /**
     * ImageView 加载图络图片
     */
    @BindingAdapter("strokeWidth")
    @JvmStatic
    fun CircleImageView.setStrokeWidth(value: Int?) {
        value?.let {
            setStrokeWidth(DimensionUtils.dip2px(dipValue = it.toFloat()).toFloat())
        }
    }

    /**
     * 自定义itemContent
     */
    @BindingAdapter("title", "android:enabled", requireAll = false)
    @JvmStatic
    fun MultiTypeItemView.setItemAttr(
        title: String?,
        enabled: Boolean?
    ) {
        title?.let {
            mTitleView.text = title
        }
        enabled?.let {
            contentView.isEnabled = enabled
        }
    }

    /**
     * 自定义itemContent
     */
    @BindingAdapter("itemContent")
    @JvmStatic
    fun MultiTypeItemView.setItemContent(content: String?) {
        content?.let {
            // 如果内容不一致，重新赋值
            if (contentView.text.toString().trim() != it) {
                dynamicValue = true
                contentView.setText(it)
                if (0 == contentView.selectionStart && !contentView.text.isNullOrEmpty()) {
                    contentView.setSelection(contentView.text?.length ?: 0)
                }
            }
        }
    }

    @InverseBindingAdapter(attribute = "itemContent", event = "itemContentAttrChanged")
    @JvmStatic
    fun MultiTypeItemView.getItemContent(): String = contentView.text.toString()

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

    @BindingAdapter("drawS", "drawT", "drawE", "drawB", requireAll = false)
    @JvmStatic
    fun TextView.divider(
        drawS: Int = 0,
        drawT: Int = 0,
        drawE: Int = 0,
        drawB: Int = 0
    ) {
        if (drawS != 0 || drawT != 0 || drawE != 0 || drawB != 0)
            setCompoundDrawablesWithIntrinsicBounds(drawS, drawT, drawE, drawB)
    }

    @BindingAdapter("paddingH", requireAll = false)
    @JvmStatic
    fun LinearLayout.divider(paddingH: Float?) {
        paddingH?.let {
            val ph = DimensionUtils.dip2px(context, paddingH)
            setPadding(ph, 0, ph, 0)
        }
    }
}
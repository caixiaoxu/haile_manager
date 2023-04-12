package com.shangyun.haile_manager_android.ui.adapter

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.lsy.framelib.utils.DimensionUtils
import com.shangyun.haile_manager_android.ui.view.CircleImageView
import com.shangyun.haile_manager_android.utils.GlideUtils
import com.shangyun.haile_manager_android.utils.NumberUtils
import com.shangyun.haile_manager_android.utils.StringUtils
import java.math.BigDecimal

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
}
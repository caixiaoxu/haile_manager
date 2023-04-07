package com.shangyun.haile_manager_android.utils

import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.View.OnClickListener
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.lsy.framelib.utils.SToast
import com.shangyun.haile_manager_android.R

/**
 * Title : 控件的工具类
 * Author: Lsy
 * Date: 2023/4/7 09:59
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
object ViewUtils {

    /**
     * 给textView设置spannable
     */
    fun initAgreementToTextView(textView:TextView,onClick:OnClickListener){
        textView.movementMethod = LinkMovementMethod.getInstance()
        textView.highlightColor = Color.TRANSPARENT
        textView.text =
            SpannableString(textView.context.resources.getString(R.string.login_agreement_hint)).apply {
                setSpan(
                    ForegroundColorSpan(
                        ContextCompat.getColor(
                            textView.context,
                            R.color.colorPrimary
                        )
                    ),
                    6,
                    length,
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE
                )
                setSpan(
                    object : ClickableSpan() {
                        override fun onClick(view: View) {
                            onClick.onClick(view)
                        }

                        override fun updateDrawState(ds: TextPaint) {
                            //去掉下划线
                            ds.isUnderlineText = false
                        }
                    }, 6,
                    length,
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE
                )
            }
    }
}
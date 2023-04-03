package com.lsy.framelib.ui.weight.loading

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.appcompat.app.AppCompatDialogFragment
import com.lsy.framelib.R

/**
 * Title : 加载弹窗
 * Author: Lsy
 * Date: 2023/3/20 17:57
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class LoadingDialog : AppCompatDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 去掉标题
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        // 去掉背景
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        // 不可取消
        dialog?.setCancelable(false)
        dialog?.setCanceledOnTouchOutside(false)
        return inflater.inflate(R.layout.dialog_loading, container, false);
    }
}
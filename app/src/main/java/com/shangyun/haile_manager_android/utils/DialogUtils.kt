package com.shangyun.haile_manager_android.utils

import androidx.appcompat.app.AppCompatActivity
import com.lsy.framelib.utils.StringUtils
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.data.arguments.SearchSelectParam
import com.shangyun.haile_manager_android.ui.view.dialog.CommonBottomSheetDialog

/**
 * Title :
 * Author: Lsy
 * Date: 2023/5/31 17:34
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
object DialogUtils {

    /**
     * 显示图片选择弹窗
     */
    fun showImgSelectorDialog(activity: AppCompatActivity) {
        CommonBottomSheetDialog.Builder(
            "", arrayListOf(
                SearchSelectParam(1, StringUtils.getString(R.string.for_take)),
                SearchSelectParam(2, StringUtils.getString(R.string.for_album)),
            )
        ).apply {
            onValueSureListener = object :
                CommonBottomSheetDialog.OnValueSureListener<SearchSelectParam> {
                override fun onValue(data: SearchSelectParam) {
                    if (1 == data.id) {
                        PictureSelectUtils.takePicture(activity)
                    } else {
                        PictureSelectUtils.pictureForAlbum(activity)
                    }
                }
            }
        }.build().show(activity.supportFragmentManager)
    }
}
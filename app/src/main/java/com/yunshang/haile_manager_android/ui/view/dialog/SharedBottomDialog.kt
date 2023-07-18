package com.yunshang.haile_manager_android.ui.view.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.databinding.DialogSharedBinding

/**
 * Title :
 * Author: Lsy
 * Date: 2023/5/15 18:12
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
</desc></version></time></author> */
class SharedBottomDialog(private val onSharedListener: ((type: Int) -> Unit)? = null) :
    BottomSheetDialogFragment() {
    private lateinit var mBinding: DialogSharedBinding

    companion object{
        const val SHARED_ALBUM = 0x01
        const val SHARED_WX = 0x02
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.CommonBottomSheetDialogStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DialogSharedBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.tvSharedCancel.setOnClickListener {
            dismiss()
        }
        mBinding.tvSharedWx.setOnClickListener {
            dismiss()
            onSharedListener?.invoke(SHARED_WX)
        }
        mBinding.tvSharedAlbum.setOnClickListener {
            dismiss()
            onSharedListener?.invoke(SHARED_ALBUM)
        }
    }
}
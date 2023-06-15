package com.yunshang.haile_manager_android.ui.activity.recharge

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.view.View
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import com.lsy.framelib.utils.DimensionUtils
import com.lsy.framelib.utils.SToast
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.HaiXinRechargeConfigsViewModel
import com.yunshang.haile_manager_android.databinding.ActivityHaixinRechargeConfigsBinding
import com.yunshang.haile_manager_android.databinding.IncludePersonalItemBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.ui.view.dialog.SharedBottomDialog
import com.yunshang.haile_manager_android.utils.FileUtils
import com.yunshang.haile_manager_android.utils.QrcodeUtils
import com.yunshang.haile_manager_android.utils.WeChatHelper


class HaiXinRechargeConfigsActivity :
    BaseBusinessActivity<ActivityHaixinRechargeConfigsBinding, HaiXinRechargeConfigsViewModel>(
        HaiXinRechargeConfigsViewModel::class.java, BR.vm
    ) {

    // 权限
    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            if (result) {
                showRefundQrCode()
            } else {
                SToast.showToast(this, R.string.empty_permission)
            }
        }

    override fun layoutId(): Int = R.layout.activity_haixin_recharge_configs

    override fun backBtn(): View = mBinding.barHaixinRechargeTitle.getBackBtn()

    override fun initView() {
        window.statusBarColor = Color.WHITE

        // items
        var group: LinearLayout? = null
        for (item in mViewModel.rechargeConfigItems) {
            if (null == item) {
                //null，把之前的加入布局，并创建新的group
                if (null != group && group.childCount > 0) {
                    mBinding.llHaixinRechargeItems.addView(group)
                }
                group = createItemGroup()
            } else {
                // 如果不显示，跳转
                if (!item.isShow) {
                    continue
                }
                // 不为空，加载子布局加入group
                if (null == group) {
                    group = createItemGroup()
                }
                val mPersonalItemBinding = DataBindingUtil.inflate<IncludePersonalItemBinding>(
                    layoutInflater,
                    R.layout.include_personal_item,
                    null, false
                )
                mPersonalItemBinding.lifecycleOwner = this
                mPersonalItemBinding.item = item
                mPersonalItemBinding.root.setOnClickListener {
                    if (item.icon == R.mipmap.icon_refund_qrcode_main) {
                        requestPermission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    } else {
                        item.clz?.let {
                            startActivity(
                                Intent(
                                    this@HaiXinRechargeConfigsActivity,
                                    item.clz
                                ).apply {
                                    item.bundle?.let { bundle ->
                                        putExtras(bundle)
                                    }
                                })
                        }
                    }
                }

                if (group.childCount > 0) {
                    group.addView(View(this@HaiXinRechargeConfigsActivity).apply {
                        setBackgroundColor(
                            ResourcesCompat.getColor(
                                resources,
                                R.color.dividing_line_color,
                                null
                            )
                        )
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            DimensionUtils.dip2px(this@HaiXinRechargeConfigsActivity, 0.5f)
                        )
                    })
                }

                group.addView(
                    mPersonalItemBinding.root, LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        resources.getDimensionPixelOffset(R.dimen.item_height)
                    )
                )
            }
        }
    }

    /**
     * 创建ItemGroup
     */
    private fun createItemGroup(): LinearLayout = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        setBackgroundResource(R.drawable.shape_sffffff_r8)
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(0, 0, 0, DimensionUtils.dip2px(this@HaiXinRechargeConfigsActivity, 8f))
        }
    }

    private fun showRefundQrCode() {
        SharedBottomDialog() {
            if (mViewModel.refundQrCode.isNotEmpty()) {
                val bitmap: Bitmap = QrcodeUtils.createQRCodeBitmap(
                    mViewModel.refundQrCode,
                    300, 300, "UTF-8", "M", "0", Color.parseColor("#000000"),
                    Color.parseColor("#FFFFFF")
                )
                when (it) {
                    SharedBottomDialog.SHARED_ALBUM ->                         // 保存到相册
                        try {
                            FileUtils.saveImageToGallery(
                                this@HaiXinRechargeConfigsActivity,
                                bitmap
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    SharedBottomDialog.SHARED_WX -> {
                        if (!WeChatHelper.isWxInstall) {
                            SToast.showToast(
                                this@HaiXinRechargeConfigsActivity,
                                R.string.err_not_install_wechat
                            )
                            return@SharedBottomDialog
                        }
                        // 分享到微信
                        WeChatHelper.openWeChatImgShare(bitmap)
                    }
                }
            }
        }.show(supportFragmentManager, "shared_dialog")
    }

    override fun initData() {
        mViewModel.requestData()
    }
}
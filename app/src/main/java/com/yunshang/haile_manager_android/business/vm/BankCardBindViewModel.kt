package com.yunshang.haile_manager_android.business.vm

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.business.apiService.CapitalService
import com.yunshang.haile_manager_android.business.apiService.CommonService
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.data.entities.BankCardDetailEntity
import com.yunshang.haile_manager_android.data.entities.RealNameAuthDetailEntity
import com.yunshang.haile_manager_android.data.model.ApiRepository
import com.yunshang.haile_manager_android.ui.fragment.BankCardBindCardInfoFragment
import com.yunshang.haile_manager_android.ui.fragment.BankCardBindShopInfoFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * Title :
 * Author: Lsy
 * Date: 2023/9/5 18:19
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class BankCardBindViewModel : BaseViewModel() {
    private val mCommonService = ApiRepository.apiClient(CommonService::class.java)
    private val mCapitalService = ApiRepository.apiClient(CapitalService::class.java)

    var authCode: String? = null

    val authInfo: MutableLiveData<RealNameAuthDetailEntity> by lazy {
        MutableLiveData()
    }

    val fragments = listOf(
        BankCardBindCardInfoFragment(),
        BankCardBindShopInfoFragment(),
    )

    val bindPage: MutableLiveData<Int> = MutableLiveData(0)

    val bankCardParams: MutableLiveData<BankCardDetailEntity> by lazy {
        MutableLiveData()
    }

    // 上传图片缓存
    private val imageCache: MutableMap<String, String> = mutableMapOf()

    /**
     * 上传证件照
     */
    fun uploadBankPhoto(path: String, callback: (isSuccess: Boolean, url: String?) -> Unit) {
        if (!imageCache[path].isNullOrEmpty()) {
            callback(true, imageCache[path])
        } else {
            launch({
                ApiRepository.dealApiResult(
                    mCommonService.updateLoadFile(
                        ApiRepository.createFileUploadBody(path)
                    )
                )?.let {
                    Timber.i("图片上传成功：$it")
                    imageCache[path] = it
                    withContext(Dispatchers.Main) {
                        callback(true, it)
                    }
                }
            }, {
                Timber.e("图片上传失败$it")
                withContext(Dispatchers.Main) {
                    callback(false, null)
                }
            })
        }
    }

    fun nextOrSubmit(v: View) {
        if (0 == bindPage.value) {
            if (bankCardParams.value?.bankAccountNoVal.isNullOrEmpty()) {
                SToast.showToast(v.context, "请先输入卡号")
                return
            }
            if (bankCardParams.value?.bankAreaVal.isNullOrEmpty()) {
                SToast.showToast(v.context, "请先选择开户行区域")
                return
            }
            if (bankCardParams.value?.bankNameVal.isNullOrEmpty()) {
                SToast.showToast(v.context, "请先选择开户银行")
                return
            }
            if (bankCardParams.value?.subBankNameVal.isNullOrEmpty()) {
                SToast.showToast(v.context, "请先选择开户支行")
                return
            }
            if (bankCardParams.value?.subBankCodeVal.isNullOrEmpty()) {
                SToast.showToast(v.context, "请先选择支行联行号")
                return
            }
            if (bankCardParams.value?.bankMobileNoVal.isNullOrEmpty()) {
                SToast.showToast(v.context, "请先输入开户手机号")
                return
            }

            if (2 == merchantType) {
                if (bankCardParams.value?.licenceForOpeningAccountImage.isNullOrEmpty()) {
                    SToast.showToast(v.context, "请先上传开户许可证")
                    return
                }
            } else {
                if (bankCardParams.value?.bankCardImage.isNullOrEmpty()) {
                    SToast.showToast(v.context, "请先上传银行卡照片")
                    return
                }
            }
            bindPage.value = 1
        } else {
            if (bankCardParams.value?.merchantNameAlias.isNullOrEmpty()) {
                SToast.showToast(v.context, "请先输入商户简称")
                return
            }
            if (bankCardParams.value?.area.isNullOrEmpty()) {
                SToast.showToast(v.context, "请先输入所在地区")
                return
            }
            if (bankCardParams.value?.address.isNullOrEmpty()) {
                SToast.showToast(v.context, "请先输入详细地址")
                return
            }
            if (bankCardParams.value?.contactName.isNullOrEmpty()) {
                SToast.showToast(v.context, "请先输入负责人")
                return
            }
            if (bankCardParams.value?.contactPhone.isNullOrEmpty()) {
                SToast.showToast(v.context, "请先输入负责人手机号")
                return
            }
            if (bankCardParams.value?.doorImage.isNullOrEmpty()) {
                SToast.showToast(v.context, "请先上传门店招牌照片")
                return
            }
            if (bankCardParams.value?.deviceImage.isNullOrEmpty()) {
                SToast.showToast(v.context, "请先上传设备场景照片")
                return
            }
            bankCardParams.value?.authCode = authCode
            launch({
                bankCardParams.value?.id?.let {
                    // 修改
                    ApiRepository.dealApiResult(
                        mCapitalService.updateBankCardDetail(
                            ApiRepository.createRequestBody(
                                GsonUtils.any2Json(bankCardParams.value)
                            )
                        )
                    )

                    LiveDataBus.post(BusEvents.BANK_LIST_DETAIL_STATUS, true)
                } ?: run {
                    // 绑定
                    authInfo.value?.verifyType?.let {
                        bankCardParams.value?.merchantType = it
                    }
                    authInfo.value?.idCardName?.let {
                        bankCardParams.value?.bankAccountName = it
                    }
                    ApiRepository.dealApiResult(
                        mCapitalService.createBankCard(
                            ApiRepository.createRequestBody(
                                GsonUtils.any2Json(bankCardParams.value)
                            )
                        )
                    )
                }
            })

            LiveDataBus.post(BusEvents.BANK_LIST_STATUS, true)
            jump.postValue(0)
        }
    }

    val merchantType: Int?
        get() = (authInfo.value?.verifyType ?: bankCardParams.value?.merchantType)

    val verifyTypeName: String?
        get() = (authInfo.value?.verifyTypeName ?: bankCardParams.value?.typeVal)

    val realName: String?
        get() = (authInfo.value?.idCardName ?: bankCardParams.value?.bankAccountName)
}
package com.yunshang.haile_manager_android.business.vm

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.StringUtils
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.apiService.CommonService
import com.yunshang.haile_manager_android.business.apiService.LoginUserService
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.data.arguments.SearchSelectParam
import com.yunshang.haile_manager_android.data.entities.RealNameAuthDetailEntity
import com.yunshang.haile_manager_android.data.model.ApiRepository
import com.yunshang.haile_manager_android.data.rule.ICommonBottomItemEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * Title :
 * Author: Lsy
 * Date: 2023/6/11 10:34
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class RealNameAuthBindingViewModel : BaseViewModel() {
    private val mCommonService = ApiRepository.apiClient(CommonService::class.java)
    private val mUserRepo = ApiRepository.apiClient(LoginUserService::class.java)

    var authCode: String? = null

    val authInfo: MutableLiveData<RealNameAuthDetailEntity> by lazy {
        MutableLiveData()
    }

    val verifyTypeList =
        StringUtils.getStringArray(R.array.verify_type_arr).mapIndexed { index, s ->
            SearchSelectParam(index + 1, s)
        }

    val inDateTypeList =
        StringUtils.getStringArray(R.array.indate_type_arr).mapIndexed { index, s ->
            SearchSelectParam(index + 1, s)
        }

    // 上传图片缓存
    private val imageCache: MutableMap<String, String> = mutableMapOf()

    /**
     * 上传证件照
     */
    fun uploadIdPhoto(path: String, callback: (isSuccess: Boolean, url: String?) -> Unit) {
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


    fun submit(v: View) {
        if (authCode.isNullOrEmpty()) {
            Timber.i("authCode为空")
            SToast.showToast(v.context, R.string.empty_params)
            return
        }

        if (null == authInfo.value?.verifyType) {
            SToast.showToast(v.context, "请选择类型")
            return
        }

        if (1 != authInfo.value?.verifyType && authInfo.value?.companyLicense.isNullOrEmpty()) {
            SToast.showToast(v.context, R.string.empty_company_license)
            return
        }

        if (authInfo.value?.idCardFont.isNullOrEmpty()) {
            SToast.showToast(v.context, R.string.empty_id_card_front)
            return
        }
        if (authInfo.value?.idCardReverse.isNullOrEmpty()) {
            SToast.showToast(v.context, R.string.empty_id_card_back)
            return
        }

        if (authInfo.value?.idCardName.isNullOrEmpty()) {
            SToast.showToast(
                v.context,
                if (3 == authInfo.value?.verifyType) R.string.empty_legal_person_name else R.string.empty_id_card_name
            )
            return
        }
        if (authInfo.value?.idCardNo.isNullOrEmpty()) {
            SToast.showToast(v.context, R.string.empty_id_card_no)
            return
        }

        if (null == authInfo.value?.idCardExpirationType) {
            SToast.showToast(v.context, R.string.empty_id_card_indate_type)
            return
        }

        if (1 == authInfo.value?.idCardExpirationType
            && authInfo.value?.idCardExpirationDate.isNullOrEmpty()
        ) {
            SToast.showToast(v.context, R.string.empty_id_card_indate)
            return
        }

        if (3 == authInfo.value?.verifyType && authInfo.value?.companyName.isNullOrEmpty()) {
            SToast.showToast(v.context, R.string.empty_company_name)
            return
        }

        if (1 != authInfo.value?.verifyType && authInfo.value?.companyUsci.isNullOrEmpty()) {
            SToast.showToast(v.context, R.string.empty_company_usci)
            return
        }
        // 验证码
        authInfo.value?.authCode = authCode

        launch({
            ApiRepository.dealApiResult(
                mUserRepo.verifyRealNameAuth(
                    ApiRepository.createRequestBody(GsonUtils.any2Json(authInfo.value))
                )
            )
            withContext(Dispatchers.Main) {
                SToast.showToast(v.context, R.string.submit_success)
            }
            LiveDataBus.post(BusEvents.REAL_NAME_AUTH_STATUS, true)
            jump.postValue(0)
        })
    }
}
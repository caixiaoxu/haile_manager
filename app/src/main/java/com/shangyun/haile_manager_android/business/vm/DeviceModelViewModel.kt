package com.shangyun.haile_manager_android.business.vm

import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.gson.GsonUtils
import com.shangyun.haile_manager_android.business.apiService.CategoryService
import com.shangyun.haile_manager_android.business.apiService.DeviceService
import com.shangyun.haile_manager_android.data.arguments.DeviceModelParam
import com.shangyun.haile_manager_android.data.entities.CategoryEntity
import com.shangyun.haile_manager_android.data.entities.Spu
import com.shangyun.haile_manager_android.data.model.ApiRepository
import timber.log.Timber

/**
 * Title :
 * Author: Lsy
 * Date: 2023/4/10 11:19
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class DeviceModelViewModel : BaseViewModel() {
    private val mCategoryRepo = ApiRepository.apiClient(CategoryService::class.java)
    private val mDeviceRepo = ApiRepository.apiClient(DeviceService::class.java)

    // 设备类别
    val categoryList: MutableLiveData<List<CategoryEntity>> = MutableLiveData()
    val selectCategory: MutableLiveData<CategoryEntity> by lazy {
        MutableLiveData()
    }

    // 设备型号缓存
    val deviceModelCache: MutableMap<Int, MutableList<DeviceModelParam>> = hashMapOf()
    val deviceModel: MutableLiveData<MutableList<DeviceModelParam>> = MutableLiveData()

    fun requestData(type: Int = 0, categoryId: Int? = null) {
        launch({
            when (type) {
                0 -> {
                    requestDeviceCategory()?.let {
                        selectCategory.postValue(it)
                    }
                }
                1 -> {
                    categoryId?.let {
                        requestDeviceModel(categoryId)
                    }
                }
            }
        }, {
            it.message?.let { it1 -> SToast.showToast(msg = it1) }
            Timber.d("请求失败或异常$it")
        }, {
            Timber.d("请求结束")
        })

    }

    /**
     * 请求设备类别
     * @return 第一个类别的id
     */
    private suspend fun requestDeviceCategory(): CategoryEntity? {
        val list = ApiRepository.dealApiResult(
            mCategoryRepo.category(1)
        )
        list?.let {
            categoryList.postValue(it)
        }
        return list?.get(0)
    }

    /**
     * 请求设备型号
     */
    private suspend fun requestDeviceModel(categoryId: Int) {
        if (deviceModelCache[categoryId].isNullOrEmpty()) {
            val list = ApiRepository.dealApiResult(mDeviceRepo.spuList(categoryId))
            if (list.isNullOrEmpty()) {
                deviceModel.postValue(null)
            } else {
                // 按名称分类
                val deviceModelMapByName: MutableMap<String, MutableList<Spu>> = mutableMapOf()
                val commonSpuList: MutableList<Spu> = mutableListOf()
                for (e in list) {
                    // 常用设备
                    for (bean in e.popular) {
                        try {
                            //型号按 "/" 分割，只显示第一个
                            val split: List<String> = bean.feature.split("/")
                            if (split.size > 1) {
                                bean.shortFeature = split[0]
                            }
                            commonSpuList.add(bean)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    //spu
                    for (bean in e.spu) {
                        //取出原有数据
                        var supList: MutableList<Spu>? = deviceModelMapByName[bean.name]
                        if (null == supList) {
                            deviceModelMapByName[bean.name] = mutableListOf()
                            supList = deviceModelMapByName[bean.name]
                        }
                        try {
                            //型号按 "/" 分割，分成多个数据
                            val split: List<String> = bean.feature.split("/")
                            if (1 >= split.size) {
                                supList!!.add(bean)
                            } else {
                                val json: String = GsonUtils.any2Json(bean)
                                for (sp in split) {
                                    val newBean =
                                        GsonUtils.json2Class(json, Spu::class.java)?.apply {
                                            shortFeature = sp
                                        }
                                    newBean?.let {
                                        supList!!.add(it)
                                    }
                                }
                            }
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                            supList!!.add(bean)
                        }
                        deviceModelMapByName[bean.name] = supList!!
                    }
                }

                //合并数据
                val merge: MutableList<DeviceModelParam> = mutableListOf()
                if (commonSpuList.isNotEmpty()) {
                    merge.add(
                        DeviceModelParam(
                            "常用${commonSpuList[0].categoryName}",
                            commonSpuList
                        )
                    )
                }
                for ((key, value) in deviceModelMapByName) {
                    merge.add(
                        DeviceModelParam(key, value)
                    )
                }
                // 缓存
                deviceModelCache[categoryId] = merge

                //刷新
                deviceModel.postValue(merge)
            }
        } else {
            deviceModel.postValue(deviceModelCache[categoryId])
        }
    }
}
package com.lsy.framelib.async

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.lsy.framelib.async.LiveDataBus.ObserverWrapper
import com.lsy.framelib.utils.UnPeekLiveData


/**
 * Title : 事件分发
 * Author: Lsy
 * Date: 2023/3/21 13:59
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
object LiveDataBus {

    private val bus: MutableMap<String, MutableLiveData<Any>> = mutableMapOf()

    /**
     * 注册消息（指定数据类型）
     */
    fun <T> with(key: String, type: Class<T>): MutableLiveData<T>? {
        if (!bus.containsKey(key)) bus[key] = BusMutableLiveData()
        return bus[key] as MutableLiveData<T>?
    }

    /**
     * 注册消息
     */
    fun with(key: String): MutableLiveData<Any>? {
        return with(key, Any::class.java)
    }

    /**
     * 发送消息（必须在主线程）
     */
    fun setValue(key: String, value: Any) {
        with(key)?.value = value
    }

    /**
     * 发送消息
     */
    fun post(key: String, value: Any) {
        with(key)?.postValue(value)
    }

    private class ObserverWrapper<T>(private val observer: Observer<T>?) : Observer<T> {

        override fun onChanged(value: T) {
            if (observer != null) {
                if (isCallOnObserve()) {
                    return
                }
                observer.onChanged(value)
            }
        }

        private fun isCallOnObserve(): Boolean {
            val stackTrace = Thread.currentThread().stackTrace
            if (stackTrace.isNotEmpty()) {
                for (element in stackTrace) {
                    if ("android.arch.lifecycle.LiveData" == element.className && "observeForever" == element.methodName) {
                        return true
                    }
                }
            }
            return false
        }
    }

    /**
     * BusLiveData，记录数据
     */
    private class BusMutableLiveData<T> : UnPeekLiveData<T>() {
        private val observerMap: MutableMap<Observer<in T>, Observer<in T>> = HashMap()

        override fun observeForever(observer: Observer<in T>) {
            if (!observerMap.containsKey(observer)) {
                observerMap[observer] = ObserverWrapper(observer)
            }
            super.observeForever(observer)
        }

        override fun removeObserver(observer: Observer<in T>) {
            val realObserver = if (observerMap.containsKey(observer)) {
                observerMap.remove(observer)!!
            } else {
                observer
            }
            super.removeObserver(realObserver)
        }
    }

}
package com.yunshang.haile_manager_android.ui.view.dialog.dateTime

import com.contrarywind.adapter.WheelAdapter

/**
 * The simple Array wheel adapter
 * @param <T> the element type
 */
class ArrayWheelAdapter<T>(private val items: List<T>) : WheelAdapter<Any?> {
    override fun getItem(index: Int): Any? {
        return if (index >= 0 && index < items.size) { items[index] } else ""
    }

    override fun getItemsCount(): Int {
        return items.size
    }

    override fun indexOf(o: Any?): Int {
        return items.indexOf(o)
    }
}
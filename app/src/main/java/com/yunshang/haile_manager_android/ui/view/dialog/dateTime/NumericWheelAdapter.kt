package com.yunshang.haile_manager_android.ui.view.dialog.dateTime

import com.contrarywind.adapter.WheelAdapter

/**
 * Numeric Wheel adapter.
 */
class NumericWheelAdapter(private val minValue: Int, private var maxValue: Int) :
    WheelAdapter<Int> {

    override fun getItem(index: Int): Int {
        return if (index in 0 until itemsCount) {
            minValue + index
        } else 0
    }

    override fun getItemsCount(): Int {
        return maxValue - minValue + 1
    }

    override fun indexOf(o: Int): Int {
        return o - minValue
    }
}
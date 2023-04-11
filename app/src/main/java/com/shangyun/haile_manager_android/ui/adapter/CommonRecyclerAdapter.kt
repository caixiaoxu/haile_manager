package com.shangyun.haile_manager_android.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView


/**
 * Title :
 * Author: Lsy
 * Date: 2023/4/11 20:32
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
abstract class CommonRecyclerAdapter<T : ViewDataBinding, D>(
    private val layoutId: Int,
    private val br: Int,
    protected val list: MutableList<D>
) :
    RecyclerView.Adapter<CommonRecyclerAdapter.MyViewHolder<T, D>>() {

    fun refreshList(list:MutableList<D>){
        this.list.clear()
        this.list.addAll(list)
    }

    override fun getItemCount(): Int = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder<T, D> {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(layoutId, parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder<T, D>, position: Int) {
        holder.bind(br, list[position])
        onBindView(holder.mBinding,list[position])
    }

    /**
     * 子类重写
     */
    abstract fun onBindView(mBinding: T?, d: D)

    class MyViewHolder<T : ViewDataBinding, D>(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val mBinding: T? = DataBindingUtil.bind(itemView)

        fun bind(br: Int, entity: D) = mBinding?.setVariable(br, entity)
    }
}
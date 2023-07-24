package com.example.a7minworkout


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.a7minworkout.databinding.CircleExerBinding


class ExerAdapter(val items:ArrayList<ExerciseModel>):
    RecyclerView.Adapter<ExerAdapter.MyViewHolder>() {

    class MyViewHolder(binding : CircleExerBinding) :
        RecyclerView.ViewHolder(binding.root) {

            val tvItem = binding.tvItem
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(CircleExerBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model : ExerciseModel = items[position]
        holder.tvItem.text = model.getId().toString()

        when{
            model.getIsSelected() ->{
                holder.tvItem.background =
                    ContextCompat.getDrawable(holder.itemView.context, R.drawable.rv_selected)

            }
            model.getIsComplete() ->{
                holder.tvItem.background =
                    ContextCompat.getDrawable(holder.itemView.context,R.drawable.rv_completed)

            }
            else ->{
                holder.tvItem.background =
                    ContextCompat.getDrawable(holder.itemView.context,R.drawable.exer_circle)
            }
        }
    }
}
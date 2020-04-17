package com.yazan98.groupyadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yazan98.groupyadmin.R
import com.yazan98.groupyadmin.models.IdeaModel
import kotlinx.android.synthetic.main.row_idea.view.*

class IdeasAdapter :
    RecyclerView.Adapter<IdeasAdapter.ViewHolder>() {

    lateinit var context: Context
    private val items: ArrayList<IdeaModel> by lazy {
        ArrayList<IdeaModel>()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView? = view.textView3
        val description: TextView? = view.Des
        val type: TextView? = view.Types
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.row_idea, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title?.apply {
            this.setText(items[position].title)
        }

        holder.description?.apply {
            this.setText(items[position].description)
        }

        holder.type?.apply {
            this.setText("${items[position].section}/${items[position].type}")
        }
    }

    fun add(model: IdeaModel) {
        this.items.add(model)
        notifyDataSetChanged()
    }

}
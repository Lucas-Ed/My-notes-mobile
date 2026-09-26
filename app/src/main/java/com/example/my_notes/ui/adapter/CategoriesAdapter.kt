package com.example.my_notes.ui.adapter

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.my_notes.R
import com.example.my_notes.data.model.Category
import com.example.my_notes.util.parseHexColor

/**
 * Adapter dos chips de categoria na tela principal (lista horizontal).
 * Tocar em um chip abre a edição da categoria.
 *
 * @param onClick Chamado ao tocar em um chip (abre edição)
 */
class CategoriesAdapter(
    private val onClick: (Category) -> Unit
) : ListAdapter<Category, CategoriesAdapter.CategoryViewHolder>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val root: View = itemView.findViewById(R.id.chipRoot)
        private val dot: View = itemView.findViewById(R.id.imgDot)
        private val name: TextView = itemView.findViewById(R.id.txtCategoryName)

        fun bind(category: Category) {
            name.text = category.name

            // Bolinha circular com a cor da categoria
            val dotDrawable = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(parseHexColor(category.color))
            }
            dot.background = dotDrawable

            root.setOnClickListener { onClick(category) }
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Category>() {
            override fun areItemsTheSame(oldItem: Category, newItem: Category) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Category, newItem: Category) =
                oldItem == newItem
        }
    }
}

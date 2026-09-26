package com.example.my_notes.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.my_notes.R
import com.example.my_notes.data.model.Note
import com.example.my_notes.util.contrastTextColor
import com.example.my_notes.util.parseHexColor
import com.google.android.material.card.MaterialCardView

/**
 * Adapter da lista de notas (RecyclerView).
 *
 * O fundo do card é a cor da categoria da nota e a cor da fonte é
 * escolhida automaticamente: texto preto sobre fundo claro,
 * texto branco sobre fundo escuro.
 *
 * @param noteColor Fornece a cor hex da categoria de cada nota
 * @param onClick Chamado ao tocar em uma nota (abre edição)
 */
class NotesAdapter(
    private val noteColor: (Note) -> String,
    private val onClick: (Note) -> Unit
) : ListAdapter<Note, NotesAdapter.NoteViewHolder>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val card: MaterialCardView = itemView.findViewById(R.id.cardNote)
        private val title: TextView = itemView.findViewById(R.id.txtNoteTitle)
        private val content: TextView = itemView.findViewById(R.id.txtNoteContent)

        fun bind(note: Note) {
            val bgColor = parseHexColor(noteColor(note))
            val textColor = bgColor.contrastTextColor()
            card.setCardBackgroundColor(bgColor)
            title.setTextColor(textColor)
            content.setTextColor(textColor)
            title.text = note.title
            content.text = note.content
            card.setOnClickListener { onClick(note) }
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Note>() {
            override fun areItemsTheSame(oldItem: Note, newItem: Note) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Note, newItem: Note) = oldItem == newItem
        }
    }
}

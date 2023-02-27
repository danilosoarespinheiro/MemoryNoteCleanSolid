package com.example.memorynotes.presentation

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.core.data.Note
import com.example.memorynotes.databinding.ItemNoteBinding
import java.text.SimpleDateFormat
import java.util.*

class NotesListAdapter(var notes: ArrayList<Note>, val actions: ListAction) :
    RecyclerView.Adapter<NotesListAdapter.NoteViewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun updateNotes(newNotes : List<Note>){
        notes.clear()
        notes.addAll(newNotes)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder =
        NoteViewHolder(ItemNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount(): Int = notes.size

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(notes[position])
    }

    inner class NoteViewHolder(binding: ItemNoteBinding) : RecyclerView.ViewHolder(binding.root) {

        private val layout = binding.noteLayout
        private val noteTitle = binding.title
        private val noteContent = binding.content
        private val noteDate = binding.date

        @SuppressLint("SimpleDateFormat", "SetTextI18n")
        fun bind(note: Note) {
            noteTitle.text = note.title
            noteContent.text = note.content
            val sdf = SimpleDateFormat("MMM dd, HH:mm:ss")
            val resulDate = Date(note.updateTime)
            noteDate.text = "Last updated : ${sdf.format(resulDate)}"

            layout.setOnClickListener { actions.onClick(note.id) }
        }
    }
}
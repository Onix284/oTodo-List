package com.example.firebase

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.firebase.databinding.NoteItemBinding

class MyAdapter(private val notes: List<NoteItem>, private val itemClickListener: OnItemClickListener): RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    interface OnItemClickListener{
        fun onEditClick(noteId: String, noteTitle: String, noteDescription: String)
        fun onDeleteClick(noteId: String)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = NoteItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val note = notes[position]
        holder.bind(note)
        holder.binding.editButton.setOnClickListener {
            itemClickListener.onEditClick(note.noteId, note.noteTitle, note.noteDescription)
        }
        holder.binding.deleteButton.setOnClickListener {
            itemClickListener.onDeleteClick(
                note.noteId,
            )
        }
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    class MyViewHolder(internal val binding: NoteItemBinding) : ViewHolder(binding.root){
        fun bind(note: NoteItem){
            binding.titleTextView.text = note.noteTitle
            binding.descriptionTextView.text = note.noteDescription
        }
    }
}

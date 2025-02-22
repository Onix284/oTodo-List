package com.example.firebase

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firebase.databinding.ActivityAllNotesBinding
import com.example.firebase.databinding.EditNoteBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AllNotes : AppCompatActivity(), MyAdapter.OnItemClickListener {
    private val binding: ActivityAllNotesBinding by lazy {
        ActivityAllNotesBinding.inflate(layoutInflater)
    }

    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth : FirebaseAuth
    private lateinit var recyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Initialize
        databaseReference = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()
        recyclerView = binding.noteRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        val currentUser = auth.currentUser
        currentUser?.let { user    ->
            val noteReference = databaseReference.child("users").child(user.uid).child("notes")
            noteReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val noteList = mutableListOf<NoteItem>()
                    for (noteSnapshot in snapshot.children) {
                        val note = noteSnapshot.getValue(NoteItem::class.java)
                        note?.let {
                            noteList.add(it)
                        }
                    }
                    noteList.reverse()
                    val adapter = MyAdapter(noteList, this@AllNotes)
                    recyclerView.adapter = adapter
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }
    }

    override fun onEditClick(noteId: String, currentTitle: String, currentDescription: String) {
        val dialogBinding = EditNoteBinding.inflate(LayoutInflater.from(this))
        val  dialog = AlertDialog.Builder(this).setView(dialogBinding.root)
            .setTitle("Edit Note")
            .setPositiveButton("Update"){ dialog, _ ->
                val newTitle = dialogBinding.updateNoteTitle.text.toString()
                val newDescription = dialogBinding.updateNoteDescription.text.toString()
                if(newTitle.isEmpty() || newDescription.isEmpty())
                {
                    Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                else{
                    updateNoteDatabase(noteId, newTitle, newDescription, dialog)
                    dialog.dismiss()
                }

            }
            .setNegativeButton("Cancel"){dialog, _ ->
                dialog.dismiss()
            }
            .create()
        dialogBinding.updateNoteTitle.setText(currentTitle)
        dialogBinding.updateNoteDescription.setText(currentDescription)
        dialog.show()
    }

    private fun updateNoteDatabase(noteId: String, newTitle: String, newDescription: String, update: Any) {
        val currentUser = auth.currentUser
        currentUser?.let { user ->
            val noteReference = databaseReference.child("users").child(user.uid).child("notes")
            val noteUpdates = NoteItem(newTitle, newDescription, noteId)
            noteReference.child(noteId).setValue(noteUpdates)
                .addOnCompleteListener { task ->
                    if(task.isSuccessful)
                    {
                        Toast.makeText(this, "Note Updated Successfully", Toast.LENGTH_SHORT).show()
                    }
                    else
                    {
                        Toast.makeText(this, "Failed to update note ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    override fun onDeleteClick(noteId: String) {
        val cuurrentUser = auth.currentUser
        cuurrentUser?.let {user ->
            val noteReference = databaseReference.child("users").child(user.uid).child("notes")
            noteReference.child(noteId).removeValue()

        }
    }

}
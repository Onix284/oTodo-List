package com.example.firebase

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.firebase.databinding.ActivityAddNoteBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AddNote : AppCompatActivity() {
    private val binding: ActivityAddNoteBinding by lazy {
        ActivityAddNoteBinding.inflate(layoutInflater)
    }
    private lateinit var  databaseReference: DatabaseReference
    private lateinit var  auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Initialize firebase database reference
        databaseReference = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()

        binding.saveBtn.setOnClickListener {

            val noteTitle = binding.etNoteTitle.text.toString()
            val noteDescription =  binding.etNoteDescription.text.toString()

            if (noteTitle.isEmpty() || noteDescription.isEmpty()) {
                Toast.makeText(this, "Please enter both title and description", Toast.LENGTH_SHORT).show()
            }
            else
            {
                val currenUser = auth.currentUser
                currenUser?.let { user->

                    //Generate a unique key for all notes
                    val noteKey = databaseReference.child("users").child(user.uid).child("notes").push().key
                    val noteItem =  NoteItem(noteTitle, noteDescription, noteKey?: "")

                    if(noteKey != null)
                    {
                        databaseReference.child("users").child(user.uid).child("notes").child(noteKey).setValue(noteItem)
                            .addOnCompleteListener { task ->
                                if(task.isSuccessful)
                                {
                                    Toast.makeText(this, "Note added successfully", Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(this, MainActivity::class.java))
                                    finish()
                                }
                                else
                                {
                                    Toast.makeText(this, "Failed to add note ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                }

            }
        }

    }
}
package com.example.firebase

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.firebase.databinding.ActivityLoginPageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginPage : AppCompatActivity() {

    private val binding : ActivityLoginPageBinding by lazy {
        ActivityLoginPageBinding.inflate(layoutInflater)
    }

    private lateinit var auth : FirebaseAuth

    override fun onStart() {
        super.onStart()
        val currentUser : FirebaseUser? = auth.currentUser

        if(currentUser != null)
        {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Check if user already loggged in

        val signUpBtn = findViewById<Button>(R.id.SignUpBtn_login)
        signUpBtn.setOnClickListener {
            val intent = Intent(this, SignUpPage::class.java)
            startActivity(intent)
            finish()
        }

        //Initialize firebase
        auth = FirebaseAuth.getInstance()

        binding.LoginBtnLogin.setOnClickListener {
            val userEmail = binding.emailLogin.text.toString()
            val userPassword = binding.passwordTextLogin.text.toString()

            if(userEmail.isEmpty() || userPassword.isEmpty()){
                Toast.makeText(this, "Please Fill All The Deatils", Toast.LENGTH_SHORT).show()
            }
            else
            {
                auth.signInWithEmailAndPassword(userEmail, userPassword)
                    .addOnCompleteListener { task ->
                        if(task.isSuccessful)
                        {
                            Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        }
                        else
                        {
                            Toast.makeText(this, "Login Failed ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                     }
            }
        }
    }
}
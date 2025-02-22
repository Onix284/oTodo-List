package com.example.firebase

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.firebase.databinding.ActivitySignUpPageBinding
import com.google.firebase.auth.FirebaseAuth

class SignUpPage : AppCompatActivity() {
    private val binding : ActivitySignUpPageBinding by lazy {
        ActivitySignUpPageBinding.inflate(layoutInflater)
    }
    private lateinit var auth : FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.signUpPage)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.LoginBtnSignup.setOnClickListener {
            val intent = Intent(this, LoginPage::class.java)
            startActivity(intent)
            finish()
        }


        binding.SignUpBtnSignup.setOnClickListener {

            //Initialize firebase auth
            auth = FirebaseAuth.getInstance()

            //Get Text Data From Edit Text Fields
            val name = binding.nameSignup.text.toString()
            val email = binding.emailSignup.text.toString()
            val password = binding.passwordTextSignup.text.toString()
            val repeatedPassword = binding.repeatPasswordSingup.text.toString()

            //Check all fields are filled
            if(name.isEmpty() || email.isEmpty() || password.isEmpty() || repeatedPassword.isEmpty()){
             Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
            else if(password != repeatedPassword)
            {
                Toast.makeText(this, "Please Enter Same Password", Toast.LENGTH_SHORT).show()
            }
            else
            {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) {task ->
                        if(task.isSuccessful)
                        {
                            Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, LoginPage::class.java)
                            intent.putExtra("fromSignUp", true)
                            startActivity(intent)
                            finish()
                        }
                        else
                        {
                            Toast.makeText(this, "Registration Failed ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }
}
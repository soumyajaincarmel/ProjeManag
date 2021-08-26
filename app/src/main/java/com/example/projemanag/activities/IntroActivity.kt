package com.example.projemanag.activities

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import com.example.projemanag.R

class IntroActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        val btnSignUpIntro = findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.btn_sign_up_intro)
        btnSignUpIntro.setOnClickListener{
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        val btnSignInIntro = findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.btn_sign_in_intro)
        btnSignInIntro.setOnClickListener{
            startActivity(Intent(this, SignInActivity::class.java))
        }


    }


}
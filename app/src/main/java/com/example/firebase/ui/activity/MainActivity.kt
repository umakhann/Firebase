package com.example.firebase.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.firebase.R
import com.example.firebase.checkLoggedIn
import com.example.firebase.databinding.ActivityMainBinding
import com.example.firebase.ui.fragments.Login
import com.example.firebase.ui.fragments.UserPage
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    lateinit var firstPage: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firstPage = if(!checkLoggedIn(auth))
             Login()
        else UserPage()

            supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, firstPage)
            .commit()

        }
    }
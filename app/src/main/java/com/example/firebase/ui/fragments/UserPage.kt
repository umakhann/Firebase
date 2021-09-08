package com.example.firebase.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.firebase.R
import com.example.firebase.checkLoggedIn
import com.example.firebase.databinding.UserBinding
import com.example.firebase.model.Person
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UserPage : Fragment(R.layout.user) {

    lateinit var auth: FirebaseAuth
    val persons = Firebase.firestore.collection("persons")

    override fun onResume() {
        super.onResume()

        if(!checkLoggedIn(auth))
            activity?.supportFragmentManager
                ?.beginTransaction()
                ?.replace(R.id.container, Login())
                ?.commit()


        }



    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        val binding = UserBinding.bind(view)

        binding.apply {
            logout.setOnClickListener{
                auth.signOut()

                activity?.supportFragmentManager
                    ?.beginTransaction()
                    ?.replace(R.id.container, Login())
                    ?.commit()

            }

            lifecycleScope.launch(Dispatchers.IO) {
                    try {
                        val query = persons
                            .whereEqualTo("email", auth.currentUser?.email)
                            .get().await()

                        val list: ArrayList<Person> = ArrayList()

                        for(document in query.documents)
                            document.toObject<Person>()?.let { it1 -> list.add(it1) }



                        withContext(Dispatchers.Main){
                            emailInfo.text = "Your email: ${list[0].email}"
                            nameInfo.text = "Your name: ${list[0].name}"

                        }

                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Error", Toast.LENGTH_SHORT)
                                .show()

                            Log.d("userpage", e.message.toString())
                        }
                    }
                }
        }
    }
}
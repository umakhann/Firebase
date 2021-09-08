package com.example.firebase.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import com.example.firebase.*
import com.example.firebase.databinding.LoginBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class Login : Fragment(R.layout.login) {

    companion object {

        private lateinit var auth: FirebaseAuth
        var _binding: LoginBinding? = null
        val TAG = "login"

    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: called")

        if(checkLoggedIn(auth))
            activity?.supportFragmentManager
                ?.beginTransaction()
                ?.replace(R.id.container, UserPage())
                ?.commit()

        _binding?.apply {
            pwdLayout.error = null
            mailLayout.error = null

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        val binding = LoginBinding.bind(view)
        _binding = binding

        binding.apply {


            mail.doAfterTextChanged() { text ->
                if (text.toString().length < 1)
                    mailLayout.error = "Required"
                else mailLayout.error = null
            }
            pwd.doAfterTextChanged() { text ->
                if (text.toString().length < 1)
                    pwdLayout.error = "Required"
                else pwdLayout.error = null
            }

//            pwdLayout.suffixTextView.setOnClickListener() {
//                Toast.makeText(context, "Forgotten Password", Toast.LENGTH_SHORT).show()
//            }

            goToSignUp.setOnClickListener {
                activity?.supportFragmentManager
                    ?.beginTransaction()
                    ?.addSharedElement(send, "button_transition")
                    ?.addSharedElement(header, "header_transition")
                    ?.replace(R.id.container, Signup())
                    ?.addToBackStack("signup")
                    ?.commit()
            }

            send.setOnClickListener {
                when {
                    !validateLogin(mail.text.toString(), pwd.text.toString()) ->
                        createSnackbar(binding.root, "Insufficient Material", resources.getColor(R.color.purple_700))

                    checkLoggedIn(auth) -> {
                        createSnackbar(
                            binding.root,
                            "You are already logged in",
                            resources.getColor(R.color.purple_700)
                        )
                        activity?.supportFragmentManager
                            ?.beginTransaction()
                            ?.replace(R.id.container, UserPage())
                            ?.commit()
                    }

                    context?.let { it1 -> isInternetOn(it1) } == true ->
                        CoroutineScope(Dispatchers.IO).launch {
                        try {
                            auth.signInWithEmailAndPassword(mail.text.toString(), pwd.text.toString()).await()
                            if(checkLoggedIn(auth))
                                activity?.supportFragmentManager
                                    ?.beginTransaction()
                                    ?.replace(R.id.container, UserPage())
                                    ?.commit()



                        } catch (e: Exception) {
                            e.message
                            createSnackbar(binding.root, "No such a user!", resources.getColor(R.color.purple_700))

                        }
                    }

                    else ->
                        createSnackbar(binding.root, "No internet connection!", resources.getColor(R.color.purple_700))
                }
            }
        }
    }



}
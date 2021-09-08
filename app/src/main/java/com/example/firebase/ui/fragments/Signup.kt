package com.example.firebase.ui.fragments

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.View
import androidx.core.view.updateLayoutParams
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.firebase.*
import com.example.firebase.databinding.RegisterBinding
import com.example.firebase.model.Person
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class Signup : Fragment(R.layout.register) {

    lateinit var auth: FirebaseAuth
    private val persons = Firebase.firestore.collection("persons")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        val binding = RegisterBinding.bind(view)


        val animation = TransitionInflater.from(context)
            .inflateTransition(
            android.R.transition.move
        )
        animation.duration = 100

        sharedElementEnterTransition = animation
        sharedElementReturnTransition = animation



        binding.apply {

            goToSignIn.setOnClickListener {
                activity?.supportFragmentManager?.popBackStack()
            }

            backButton.setOnClickListener {
                activity?.supportFragmentManager?.popBackStack()
            }

            name.doAfterTextChanged() { text ->
                if (text.toString().length < 3) {
                    nameLayout.error = "Name must contains at least 3 characters!"
                    nameLayout.helperText = null
                    nameLayout.minimumHeight = 100
                    nameLayout.updateLayoutParams {
                        height = generateDp(100, requireContext())
                    }

                } else if (text.toString().count { !it.isLetter() } >
                    text.toString().count { it.isWhitespace() }) {
                    nameLayout.error = "Name can contain only letters!"
                    nameLayout.helperText = null
                    nameLayout.updateLayoutParams {
                        height = generateDp(100, requireContext())
                    }

                } else {
                    nameLayout.error = null
                    nameLayout.helperText = null
                    nameLayout.updateLayoutParams {
                        height = generateDp(75, requireContext())
                    }

                }
            }

            mail.doAfterTextChanged() { text ->
                if (text.toString().length < 1) {
                    mailLayout.error = "Required!"
                    mailLayout.updateLayoutParams {
                        height = generateDp(100, requireContext())
                    }

                } else if (!text.toString().contains('@')) {
                mailLayout.error = "Email field!"
                mailLayout.updateLayoutParams {
                    height = generateDp(100, requireContext())
                }

                } else {
                    mailLayout.error = null
                    mailLayout.updateLayoutParams {
                        height = generateDp(75, requireContext())
                    }
                }
            }

            pwd.doAfterTextChanged() { text ->
                if (text.toString().length < 8) {
                    pwdLayout.error = "Password must contain at least 8 characters!"
                    pwdLayout.helperText = null
                    pwdLayout.updateLayoutParams {
                        height = generateDp(100, requireContext())
                    }

                }

                else if (text.toString().count() { it.isDigit() } < 1 ||
                    text.toString().count() { it.isUpperCase() } < 1) {

                    pwdLayout.error = "At least 1 number and capital letter!"
                    pwdLayout.helperText = null
                    pwdLayout.updateLayoutParams {
                        height = generateDp(100, requireContext())
                    }

                }

                else {
                    pwdLayout.error = null
                    pwdLayout.helperText = null
                    pwdLayout.updateLayoutParams {
                        height = generateDp(75, requireContext())
                    }
                }


                if (text.toString() != confirm.text.toString() && confirm.text.toString().length > 0) {
                    confirmLayout.error = "Passwords do not match!"
                    confirmLayout.helperText = null
                    confirmLayout.updateLayoutParams {
                        height = generateDp(100, requireContext())
                    }
                } else if (text.toString() == confirm.text.toString() && confirm.text.toString().length > 0) {
                    confirmLayout.helperText = "Passwords match!"
                    confirmLayout.error = null
                    confirmLayout.updateLayoutParams {
                        height = generateDp(100, requireContext())
                    }
                }
            }

            confirm.doAfterTextChanged { text ->
                if (text.toString() != pwd.text.toString()) {
                    confirmLayout.error = "Passwords do not match!"
                    confirmLayout.helperText = null
                    confirmLayout.updateLayoutParams {
                        height = generateDp(100, requireContext())
                    }

                } else if (text.toString().length < 8) {
                    confirmLayout.error = "Password must contain at least 8 characters!"
                    confirmLayout.helperText = null
                    confirmLayout.updateLayoutParams {
                        height = generateDp(100, requireContext())
                    }

                } else if (text.toString() == pwd.text.toString()) {
                    confirmLayout.error = null
                    confirmLayout.helperText = "Passwords match!"
                    confirmLayout.updateLayoutParams {
                        height = generateDp(100, requireContext())
                    }

                } else {
                    confirmLayout.error = null
                    confirmLayout.helperText = null
                    confirmLayout.updateLayoutParams {
                        height = generateDp(75, requireContext())
                    }
                }
            }



        signup.setOnClickListener {
            if(isInternetOn(requireContext()))
            register(
                name.text.toString(),
                mail.text.toString(),
                pwd.text.toString(),
                confirm.text.toString(),
                binding
            )

            else createSnackbar(binding.root, "No internet connection!", resources.getColor(R.color.purple_700))

            }
        }
    }

    private fun register(name: String, email: String, password: String, confirm: String, binding: RegisterBinding): Boolean {
        val bool = validateRegistration(name, email, password, confirm)

        if(bool) {
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    Firebase.firestore.runBatch { batch ->
                        val create: Task<AuthResult> =
                            auth.createUserWithEmailAndPassword(email, password)

                        persons.add(Person(name, email))

                    }

                    auth.signInWithEmailAndPassword(email, password).await()

                    if(checkLoggedIn(auth))
                        activity?.supportFragmentManager
                            ?.beginTransaction()
                            ?.replace(R.id.container, UserPage())
                            ?.commit()



                } catch (e: Exception) {
                    e.message
                }
            }

            return true

        } else createSnackbar(binding.root, "Insufficient material!", resources.getColor(R.color.purple_700))


        return false
    }
}

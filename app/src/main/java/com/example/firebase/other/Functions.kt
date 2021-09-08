package com.example.firebase

import android.content.Context
import android.net.ConnectivityManager
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth



fun checkLoggedIn(auth: FirebaseAuth): Boolean {
    auth.currentUser?.let {
        return true
    }
        return false
}

fun isInternetOn(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetworkInfo = connectivityManager.activeNetworkInfo
    return activeNetworkInfo != null && activeNetworkInfo.isConnected
}

fun generateDp(dps: Int, context: Context) : Int{
    val scale = context.resources.displayMetrics.density
    val pixels = (dps * scale + 0.5f)
    return pixels.toInt()
}

fun validateLogin(email: String, password: String) : Boolean{

    return when {
            email.isEmpty() ||
            password.length < 8 ||
            !email.contains('@')

        -> false

        else -> true

    }
}

fun validateRegistration(name: String, email: String, password: String, confirm: String) : Boolean{


    return when {
        name.length < 3 ||
        email.isEmpty() ||
        password.length < 8 ||
        name.count() {!it.isLetter()} > name.count() {it.isWhitespace()} ||
        !email.contains('@') ||
        password != confirm ||
        password.count(){it.isDigit()} < 1 ||
        password.count(){it.isUpperCase()} < 1

        -> false

        else -> true

    }
}


fun createSnackbar(view: View, text: String, color: Int){
    Snackbar.make(view, text, Snackbar.LENGTH_SHORT)
        .setAction("CLOSE") { }
        .setActionTextColor(color)
        .show()
}
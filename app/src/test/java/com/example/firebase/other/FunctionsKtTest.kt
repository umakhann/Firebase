package com.example.firebase.other

import com.example.firebase.validateLogin
import com.example.firebase.validateRegistration
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith


class FunctionsKtTest{

    @Test
    fun `name smaller than 3 returns false`(){
        val result = validateRegistration("as",
            "umakhan.shahin@gmail.com", "Shahin123", "Shahin123")

        assertThat(result).isFalse()
    }

    @Test
    fun `empty email returns false`(){
        var result = validateRegistration("Shahin",
            "", "Shahin123", "Shahin123")

        assertThat(result).isFalse()

        result = validateLogin("", "Shahin123")
        assertThat(result).isFalse()
    }

    @Test
    fun `password smaller than 8 returns false`(){
        var result = validateRegistration("Shahin",
            "umakhan.shahin@gmail.com", "Shahin1", "Shahin1")

        assertThat(result).isFalse()

        result = validateLogin("umakhan.shahin@gmail.com", "Shahin1")
        assertThat(result).isFalse()
    }

    @Test
    fun `email without @ character returns false`(){
        var result = validateRegistration("Shahin",
            "umakhan.shahingmail.com", "Shahin123", "Shahin123")

        assertThat(result).isFalse()

        result = validateLogin("umakhan.shahingmail.com", "Shahin1")
        assertThat(result).isFalse()
    }

    @Test
    fun `name containing digits or special characters returns false`(){
        var result = validateRegistration("Shahin2",
            "umakhan.shahin@gmail.com", "Shahin123", "Shahin123")

        assertThat(result).isFalse()

        result = validateRegistration("Shahin!",
            "umakhan.shahin@gmail.com", "Shahin123", "Shahin123")

        assertThat(result).isFalse()
    }


    @Test
    fun `mismatching passwords return false`(){
        val result = validateRegistration("Shahin",
            "umakhan.shahin@gmail.com", "Shahin123", "Shahin124")

        assertThat(result).isFalse()
    }


    @Test
    fun `password without number and uppercase letter returns false`(){
        var result = validateRegistration("Shahin",
            "umakhan.shahin@gmail.com", "ShahinUma", "ShahinUma")

        assertThat(result).isFalse()

        result = validateRegistration("Shahin",
            "umakhan.shahin@gmail.com", "shahin123", "shahin123")

        assertThat(result).isFalse()
    }






}
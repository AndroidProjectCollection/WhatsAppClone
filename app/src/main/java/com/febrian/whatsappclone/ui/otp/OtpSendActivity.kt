package com.febrian.whatsappclone.ui.otp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.febrian.whatsappclone.MainActivity
import com.febrian.whatsappclone.databinding.ActivityOtpSendBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit


class OtpSendActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOtpSendBinding
    private lateinit var mAuth: FirebaseAuth
    private var mCallbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpSendBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()

        userIsLoggedIn()

        binding.btnSend.setOnClickListener {
            when {
                binding.etPhone.text.toString().trim().isEmpty() -> {
                    Toast.makeText(applicationContext, "Invalid Phone Number", Toast.LENGTH_SHORT)
                        .show()
                }

                else -> {
                    otpSend()
                }
            }
        }
    }

    private fun userIsLoggedIn() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            startActivity(Intent(applicationContext, MainActivity::class.java))
            finish()
            return
        }
    }

    private fun otpSend() {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnSend.visibility = View.INVISIBLE

        mCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {

            }

            override fun onVerificationFailed(p0: FirebaseException) {
                binding.progressBar.visibility = View.GONE
                binding.btnSend.visibility = View.VISIBLE
                Toast.makeText(applicationContext, p0.localizedMessage, Toast.LENGTH_SHORT).show()
            }

            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(p0, p1)

                binding.progressBar.visibility = View.GONE
                binding.btnSend.visibility = View.VISIBLE
                Toast.makeText(
                    this@OtpSendActivity,
                    "OTP is successfully send.",
                    Toast.LENGTH_SHORT
                ).show()
                val intent = Intent(this@OtpSendActivity, OtpVerifyActivity::class.java)
                intent.putExtra("phone", binding.etPhone.text.toString().trim())
                intent.putExtra("verificationId", p0)
                startActivity(intent)
            }
        }

        val options = PhoneAuthOptions.newBuilder(mAuth)
            .setPhoneNumber("+62" + binding.etPhone.text.toString().trim())
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(mCallbacks as PhoneAuthProvider.OnVerificationStateChangedCallbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }
}
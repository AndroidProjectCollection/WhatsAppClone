package com.febrian.whatsappclone.ui.otp

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.febrian.whatsappclone.MainActivity
import com.febrian.whatsappclone.databinding.ActivityOtpVerifyBinding
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.concurrent.TimeUnit


class OtpVerifyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOtpVerifyBinding
    private var verificationId: String? = null

    private lateinit var auth: FirebaseAuth
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken

    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var phoneText: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOtpVerifyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        phoneText = String.format(
            "+62-%s", intent.getStringExtra("phone").toString()
        )

        binding.tvMobile.text = phoneText

        verificationId = intent.getStringExtra("verificationId")

        binding.tvResendBtn.setOnClickListener {

            resendVerificationCode()

            Toast.makeText(this@OtpVerifyActivity, "OTP Send Successfully.", Toast.LENGTH_SHORT)
                .show()
        }

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {

                Log.d(TAG, "onVerificationCompleted:$credential")
            }

            override fun onVerificationFailed(e: FirebaseException) {

                Log.w(TAG, "onVerificationFailed", e)

                if (e is FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
                    // Invalid request
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded

                    Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
                }

                // Show a message and update the UI
            }

            override fun onCodeSent(
                newVerificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:$newVerificationId")

                // Save verification ID and resending token so we can use them later
                verificationId = newVerificationId
                resendToken = token
            }
        }


        binding.btnVerify.setOnClickListener {
            binding.progressBarVerify.visibility = View.VISIBLE
            binding.btnVerify.visibility = View.INVISIBLE
            if (binding.code.text.toString().trim().isEmpty()) {
                Toast.makeText(applicationContext, "OTP is not Valid!", Toast.LENGTH_SHORT).show()
            } else {
                if (verificationId != null) {
                    val code = binding.code.text.toString().trim()

                    val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
                    auth
                        .signInWithCredential(credential)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                binding.progressBarVerify.visibility = View.VISIBLE
                                binding.btnVerify.visibility = View.INVISIBLE

                                registerUser()

                                Toast.makeText(
                                    this@OtpVerifyActivity,
                                    "Welcome...",
                                    Toast.LENGTH_SHORT
                                ).show()
                                val intent =
                                    Intent(this@OtpVerifyActivity, MainActivity::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                            } else {
                                binding.progressBarVerify.visibility = View.GONE
                                binding.btnVerify.visibility = View.VISIBLE
                                Toast.makeText(
                                    this@OtpVerifyActivity,
                                    "OTP is not Valid!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }
            }
        }
    }

    private fun registerUser(){
        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            val mUserDB = FirebaseDatabase.getInstance().reference.child("user").child(user.uid)
            mUserDB.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        val userMap: MutableMap<String, Any?> = HashMap()
                        userMap["phone"] = user.phoneNumber
                        userMap["name"] = user.phoneNumber
                        mUserDB.updateChildren(userMap)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }
    }

    private fun resendVerificationCode(
    ) {
        val optionsBuilder = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(
                phoneText
            )       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
        optionsBuilder.setForceResendingToken(resendToken)
        PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
    }
}


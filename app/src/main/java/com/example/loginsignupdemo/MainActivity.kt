package com.example.loginsignupdemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    companion object {
        const val TAG = "RegisterActivity"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button3.setOnClickListener{
            performRegister()
        }



        btnLogin.setOnClickListener({
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
            finish()
        })
    }

    private fun performRegister() {
        val email = email_edittext_register.text.toString()
        val password = password_edittext_register.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter text in email/pw", Toast.LENGTH_SHORT).show()
            return
        }
        Log.d(TAG, "Attempting to create user with email: $email")

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (!it.isSuccessful){

                        return@addOnCompleteListener
                    }
                    saveUserToFirebaseDatabase( )
                    // else if successful
                    Log.d(TAG, "Successfully created user with uid: ${it.result?.user?.uid}")
                }
                .addOnFailureListener {
                    Log.d(TAG, "Failed to create user: ${it.message}")
                    Toast.makeText(this, "Failed to create user: ${it.message}", Toast.LENGTH_SHORT).show()
                }

    }

    private fun saveUserToFirebaseDatabase() {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = User(uid, email_edittext_register.text.toString())

        ref.setValue(user)
                .addOnSuccessListener {
                    Log.d(TAG, "Finally we saved the user to Firebase Database")

                    val intent = Intent(this, PopUpWindow::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)   //USED TO BRING BACK TO DDESKTOP NOT AT PREVIOS ACTIVITY
                    startActivity(intent)
                }
                .addOnFailureListener {
                    Log.d(TAG, "Failed to set value to database: ${it.message}")

                }
    }
}
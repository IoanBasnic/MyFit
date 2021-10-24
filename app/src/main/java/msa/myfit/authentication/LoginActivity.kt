package msa.myfit.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import msa.myfit.MainActivity
import msa.myfit.R

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val textRegister: TextView = findViewById(R.id.tv_register)
        val btnLogin: Button = findViewById(R.id.btn_login)
        val textEmail: EditText = findViewById(R.id.editTextTextEmailAddress)
        val textPassword: EditText = findViewById(R.id.editTextTextPassword)

        textRegister.setOnClickListener{
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }

        btnLogin.setOnClickListener {
            val email: String = textEmail.text.toString().trim { it <= ' '}
            val password: String = textPassword.text.toString().trim { it <= ' '}
            when{
                TextUtils.isEmpty(email) -> {
                    Toast.makeText(
                        this@LoginActivity,
                        "Please enter email",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                TextUtils.isEmpty(password) -> {
                    Toast.makeText(
                        this@LoginActivity,
                        "Please enter password",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->

                                if(task.isSuccessful) {

                                    Toast.makeText(
                                        this@LoginActivity,
                                        "You were logged in successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    intent.putExtra("user_id", FirebaseAuth.getInstance().currentUser!!.uid)
                                    intent.putExtra("email_id", email)
                                    startActivity(intent)
                                    finish()
                                }
                                else{
                                    Toast.makeText(
                                        this@LoginActivity,
                                        task.exception!!.message.toString(),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                            }
                }
            }
        }


    }
}
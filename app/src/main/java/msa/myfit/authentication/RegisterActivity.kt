package msa.myfit.authentication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import msa.myfit.MainActivity
import msa.myfit.R

class RegisterActivity : AppCompatActivity() {

    override fun onStart() {
        super.onStart()
        val intentMain = Intent(this@RegisterActivity, MainActivity::class.java)

        if(FirebaseAuth.getInstance().currentUser != null){
            startActivity(intentMain)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val context: Context = this
        FirebaseApp.initializeApp(context)

        val textLogin: TextView = findViewById(R.id.tv_login)
        val btnRegister: Button = findViewById(R.id.btn_register)
        val textEmail: EditText = findViewById(R.id.editTextTextEmailAddress)
        val textPassword: EditText = findViewById(R.id.editTextTextPassword)
        val textConfirmPassword: EditText = findViewById(R.id.editTextTextConfirmPassword)

        textLogin.setOnClickListener{
            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
        }

        btnRegister.setOnClickListener {
            val email: String = textEmail.text.toString().trim { it <= ' '}
            val password: String = textPassword.text.toString().trim { it <= ' '}
            val confirmationPassword: String = textConfirmPassword.text.toString().trim { it <= ' '}
            when{
                TextUtils.isEmpty(email) -> {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Please enter email",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                TextUtils.isEmpty(password) -> {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Please enter password",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                TextUtils.isEmpty(confirmationPassword) -> {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Please enter confirmation password",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                password != confirmationPassword -> {
                    Toast.makeText(
                        this@RegisterActivity,
                        "The password differs from the confirmation password",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(
                            OnCompleteListener<AuthResult> { task ->

                                if(task.isSuccessful) {
                                    val firebaseUser: FirebaseUser= task.result!!.user!!

                                    Toast.makeText(
                                        this@RegisterActivity,
                                        "You were registered successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    val intent = Intent(
                                        this@RegisterActivity,
                                        MainActivity::class.java
                                    )
                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    intent.putExtra("user_id", firebaseUser.uid)
                                    intent.putExtra("email_id", email)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    Toast.makeText(
                                        this@RegisterActivity,
                                        task.exception!!.message.toString(),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                            }
                        )
                }
            }
        }
    }
}
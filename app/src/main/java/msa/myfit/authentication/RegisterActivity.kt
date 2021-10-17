package msa.myfit.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import msa.myfit.MainActivity
import msa.myfit.R

class RegisterActivity : AppCompatActivity() {
    private val btnRegister: Button = findViewById(R.id.btn_register)
    private val textEmail: EditText = findViewById(R.id.editTextTextEmailAddress)
    private val textPassword: EditText = findViewById(R.id.editTextTextPassword)
    private val textConfirmPassword: EditText = findViewById(R.id.editTextTextConfirmPassword)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

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

                                    val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    intent.putExtra("user_id", firebaseUser.uid)
                                    intent.putExtra("email_id", email)
                                    startActivity(intent)
                                    finish()
                                }
                                else{
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
package msa.myfit

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import msa.myfit.authentication.LoginActivity

class MainActivity : AppCompatActivity() {

    private val textViewUserId: TextView = findViewById(R.id.tv_user_id)
    private val textViewEmailId: TextView = findViewById(R.id.tv_email_id)
    private val btnLogout: TextView = findViewById(R.id.btn_logout)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val userId = intent.getStringExtra("user_id")
        val emailId = intent.getStringExtra("email_id")

        textViewUserId.text = "User id : $userId"
        textViewEmailId.text = "Email id : $emailId"

        btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            finish()
        }

    }
}
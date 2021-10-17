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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

         val textViewUserId: TextView = findViewById(R.id.tv_user_id)
         val textViewEmailId: TextView = findViewById(R.id.tv_email_id)
         val btnLogout: TextView = findViewById(R.id.btn_logout)

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
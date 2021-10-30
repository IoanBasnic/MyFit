package msa.myfit

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import msa.myfit.authentication.LoginActivity
import msa.myfit.fragment.HomeFragment
import msa.myfit.fragment.MyProfileFragment
import msa.myfit.fragment.SettingsFragment

class MainActivity : AppCompatActivity() {

    private val homeFragment = HomeFragment();
    private val myProfileFragment = MyProfileFragment();
    private val settingsFragment = SettingsFragment();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//         val textViewUserId: TextView = findViewById(R.id.tv_user_id)
//         val textViewEmailId: TextView = findViewById(R.id.tv_email_id)
//         val btnLogout: TextView = findViewById(R.id.btn_logout)

//        val userId = intent.getStringExtra("user_id")
//        val emailId = intent.getStringExtra("email_id")
//
//        textViewUserId.text = "User id : $userId"
//        textViewEmailId.text = "Email id : $emailId"

        replaceFragment(homeFragment)

        bottomNavigationView2.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.item1 -> replaceFragment(homeFragment)
                R.id.item2 -> replaceFragment(myProfileFragment)
                R.id.item3 -> replaceFragment(settingsFragment)
            }
            true
        }

//        btnLogout.setOnClickListener {
//            FirebaseAuth.getInstance().signOut()
//
//            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
//            finish()
//        }

    }

    private fun replaceFragment(fragment: Fragment) {
        if(fragment != null) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, fragment)
            transaction.commit()
        }
    }
}
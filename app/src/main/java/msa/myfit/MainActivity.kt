package msa.myfit

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_main.*
import msa.myfit.fragment.HomeFragment
import msa.myfit.fragment.MyProfileFragment
import msa.myfit.fragment.SettingsFragment
import javax.net.ssl.HttpsURLConnection

class MainActivity : AppCompatActivity() {

    private val homeFragment = HomeFragment(this);
    private val myProfileFragment = MyProfileFragment(this);
    private val settingsFragment = SettingsFragment(this);

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
        setContentView(R.layout.activity_main)

        replaceFragment(homeFragment)

        bottomNavigationView2.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.item1 -> replaceFragment(homeFragment)
                R.id.item2 -> replaceFragment(myProfileFragment)
                R.id.item3 -> replaceFragment(settingsFragment)
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        if(fragment != null) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, fragment)
            transaction.commit()
        }
    }
}
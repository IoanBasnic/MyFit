package msa.myfit.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.auth.FirebaseAuth
import msa.myfit.R
import msa.myfit.authentication.LoginActivity


class SettingsFragment(private val mainActivity: AppCompatActivity) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    fun replaceFragment(someFragment: Fragment?) {
        val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
        if (someFragment != null) {
            transaction.replace(R.id.SettingsFragmentId, someFragment)
        }
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val scoreboard: CardView = view.findViewById(R.id.btn_scoreboard)

        scoreboard.setOnClickListener { view ->
            when (view.id) {
                R.id.btn_scoreboard  -> {
                    replaceFragment(ScoreboardFragment(mainActivity))
                }
            }
        }

        val btnLogout: CardView = view.findViewById(R.id.btn_logout)
        btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}
package msa.myfit.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import msa.myfit.R

class HomeFragment(private val mainActivity: AppCompatActivity) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    fun replaceFragment(someFragment: Fragment?) {
        val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
        if (someFragment != null) {
            transaction.replace(R.id.HomeFragmentId, someFragment)
        }
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val caloriesEatenBtn: CardView = view.findViewById(R.id.calories_eaten)
        val routeBtn: CardView = view.findViewById(R.id.routes)
        val overviewBtn: CardView = view.findViewById(R.id.overview)

        caloriesEatenBtn.setOnClickListener { view ->
            when (view.id) {
                R.id.calories_eaten  -> {
                    replaceFragment(CaloriesEatenFragment(mainActivity))
                }
            }
        }

        routeBtn.setOnClickListener { view ->
            when (view.id) {
                R.id.routes  -> {
                    replaceFragment(RouteFragment(mainActivity))
                }
            }
        }

        overviewBtn.setOnClickListener { view ->
            when (view.id) {
                R.id.overview  -> {
                    replaceFragment(OverviewFragment(mainActivity))
                }
            }
        }
    }
}

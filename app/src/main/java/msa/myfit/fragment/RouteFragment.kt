package msa.myfit.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import msa.myfit.R

class RouteFragment(private val mainActivity: AppCompatActivity) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_route, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val childFragment: MapFragment = MapFragment()
        val transaction: FragmentTransaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.GoogleMapsFragment, childFragment).commit()

        val btnStartRoute : Button = view.findViewById(R.id.btn_start_route)

        btnStartRoute.setOnClickListener {
            btnStartRoute.setText("Finish route")
            val gridLayout: GridLayout = view.findViewById(R.id.gridLayout3)
            gridLayout.setVisibility(View.VISIBLE)
            btnStartRoute.setOnClickListener {
                btnStartRoute.setText("Start route")
                var fragment: Fragment? = null
                fragment = FinishRouteFragment(mainActivity)
                replaceFragment(fragment)
            }
        }
    }

    fun replaceFragment(someFragment: Fragment?) {
        val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
        if (someFragment != null) {
            transaction.replace(R.id.HomeFragmentId, someFragment)
        }
        transaction.addToBackStack(null)
        transaction.commit()
    }
}
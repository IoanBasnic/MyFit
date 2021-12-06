package msa.myfit.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import msa.myfit.MainActivity
import msa.myfit.R


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment(mainActivity: MainActivity) : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private val mainActivity = mainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }

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
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String, mainActivity: MainActivity) =
            HomeFragment(mainActivity).apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val caloriesEatenBtn: CardView = view.findViewById(R.id.calories_eaten)
        val routeBtn: CardView = view.findViewById(R.id.routes)
        val overviewBtn: CardView = view.findViewById(R.id.overview)

        caloriesEatenBtn.setOnClickListener { view ->
            var fragment: Fragment? = null
            when (view.id) {
                R.id.calories_eaten  -> {
                    fragment = CaloriesEatenFragment(mainActivity)
                    replaceFragment(fragment)
                }
            }
        }

        routeBtn.setOnClickListener { view ->
            var fragment: Fragment? = null
            when (view.id) {
                R.id.routes  -> {
                    fragment = RouteFragment()
                    replaceFragment(fragment)
                }
            }
        }

        overviewBtn.setOnClickListener { view ->
            var fragment: Fragment? = null
            when (view.id) {
                R.id.overview  -> {
                    fragment = OverviewFragment()
                    replaceFragment(fragment)
                }
            }
        }
    }

}

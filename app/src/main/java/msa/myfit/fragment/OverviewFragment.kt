package msa.myfit.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentTransaction
import msa.myfit.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [OverviewFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OverviewFragment(mainActivity: AppCompatActivity) : Fragment() {
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
        return inflater.inflate(R.layout.fragment_overview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val setGoal: CardView = view.findViewById(R.id.set_goal)
        val setTodayGoal: CardView = view.findViewById(R.id.set_today_goal)
        val dietOverview: CardView = view.findViewById(R.id.diet_overview)
        val workoutOverview: CardView = view.findViewById(R.id.workout_overview)
        val weightOverview: CardView = view.findViewById(R.id.weight_overview)

        setGoal.setOnClickListener { view ->
            var fragment: Fragment? = null
            when (view.id) {
                R.id.set_goal  -> {
                    fragment = WeightGoalkFragment()
                    replaceFragment(fragment)
                }
            }
        }

        setTodayGoal.setOnClickListener { view ->
            var fragment: Fragment? = null
            when (view.id) {
                R.id.set_today_goal  -> {
                    fragment = TodayGoalFragment()
                    replaceFragment(fragment)
                }
            }
        }

        dietOverview.setOnClickListener { view ->
            var fragment: Fragment? = null
            when (view.id) {
                R.id.diet_overview  -> {
                    fragment = DietOverviewFragment(mainActivity)
                    replaceFragment(fragment)
                }
            }
        }

        workoutOverview.setOnClickListener { view ->
            var fragment: Fragment? = null
            when (view.id) {
                R.id.workout_overview  -> {
                    fragment = WorkoutOverviewFragment()
                    replaceFragment(fragment)
                }
            }
        }

        weightOverview.setOnClickListener { view ->
            var fragment: Fragment? = null
            when (view.id) {
                R.id.weight_overview  -> {
                    fragment = WeightOverviewFragment()
                    replaceFragment(fragment)
                }
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
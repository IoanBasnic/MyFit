package msa.myfit.fragment

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import msa.myfit.R
import msa.myfit.domain.DatabaseVariables
import msa.myfit.firebase.FirebaseUtils
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.concurrent.TimeUnit

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
    var existingDocuments: MutableList<DocumentSnapshot>? = null
    var existingWeightGoal: MutableList<DocumentSnapshot>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val correlationId = FirebaseAuth.getInstance().currentUser!!.uid

        GlobalScope.launch {
            existingDocuments = getWeightsForUserForTodayFromDb(correlationId, OffsetDateTime.now().toLocalDate())
            Log.i("tag","Retrieved existing document $existingDocuments with correlation id $correlationId")
        }

        GlobalScope.launch {
            existingWeightGoal = getWeightGoalsForUserForTodayFromDb(correlationId, OffsetDateTime.now().toLocalDate())
            Log.i("tag","Retrieved existing document $existingDocuments with correlation id $correlationId")
        }


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_overview, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val setGoal: CardView = view.findViewById(R.id.set_goal)
        val setTodayGoal: CardView = view.findViewById(R.id.set_today_goal)
        val dietOverview: CardView = view.findViewById(R.id.diet_overview)
        val workoutOverview: CardView = view.findViewById(R.id.workout_overview)
        val weightOverview: CardView = view.findViewById(R.id.weight_overview)

        setGoal.setOnClickListener { view ->
            var fragment: Fragment? = null
            TimeUnit.SECONDS.sleep(1L)
            when (view.id) {
                R.id.set_goal  -> {
                    fragment = WeightGoalFragment(mainActivity, existingWeightGoal)
                    replaceFragment(fragment)
                }
            }
        }

        setTodayGoal.setOnClickListener { view ->
            var fragment: Fragment? = null
            TimeUnit.SECONDS.sleep(1L)
            when (view.id) {
                R.id.set_today_goal  -> {
                    fragment = TodayWeightFragment(mainActivity, existingDocuments)
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
                    fragment = WeightOverviewFragment(mainActivity)
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

    private suspend fun getWeightsForUserForTodayFromDb(correlationId: String, currentDate: LocalDate): MutableList<DocumentSnapshot> {
        return FirebaseUtils().firestoreDatabase.collection(DatabaseVariables.weightForToday)
            .whereEqualTo(DatabaseVariables.userId, correlationId)
            .whereEqualTo(DatabaseVariables.inputDate, currentDate.toString())
            .get()
            .await()
            .documents
    }

    private suspend fun getWeightGoalsForUserForTodayFromDb(correlationId: String, currentDate: LocalDate): MutableList<DocumentSnapshot> {
        return FirebaseUtils().firestoreDatabase.collection(DatabaseVariables.weightGoal)
            .whereEqualTo(DatabaseVariables.userId, correlationId)
            .get()
            .await()
            .documents
    }
}
package msa.myfit.fragment.overview

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

class OverviewFragment(private val mainActivity: AppCompatActivity) : Fragment() {
    var existingDocuments: MutableList<DocumentSnapshot>? = null
    var existingWeightGoal: MutableList<DocumentSnapshot>? = null

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
            Log.i("tag","Retrieved existing document $existingWeightGoal with correlation id $correlationId")
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
        val caloriesBurnt: CardView = view.findViewById(R.id.calories_burnt)

        setGoal.setOnClickListener { view ->
            TimeUnit.SECONDS.sleep(1L)
            when (view.id) {
                R.id.set_goal  -> {
                    replaceFragment(
                        WeightGoalFragment(
                            mainActivity,
                            existingWeightGoal
                        )
                    )
                }
            }
        }

        setTodayGoal.setOnClickListener { view ->
            TimeUnit.SECONDS.sleep(1L)
            when (view.id) {
                R.id.set_today_goal  -> {
                    replaceFragment(
                        TodayWeightFragment(
                            mainActivity,
                            existingDocuments
                        )
                    )
                }
            }
        }

        dietOverview.setOnClickListener { view ->
            when (view.id) {
                R.id.diet_overview  -> {
                    replaceFragment(
                        DietOverviewFragment(
                            mainActivity
                        )
                    )
                }
            }
        }

        workoutOverview.setOnClickListener { view ->
            when (view.id) {
                R.id.workout_overview  -> {
                    replaceFragment(
                        WorkoutOverviewFragment(
                            mainActivity
                        )
                    )
                }
            }
        }

        weightOverview.setOnClickListener { view ->
            when (view.id) {
                R.id.weight_overview  -> {
                    replaceFragment(
                        WeightOverviewFragment(
                            mainActivity
                        )
                    )
                }
            }
        }

        caloriesBurnt.setOnClickListener { view ->
            when (view.id) {
                R.id.calories_burnt  -> {
                    replaceFragment(
                        CaloriesBurntFragment(
                            mainActivity
                        )
                    )
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
        return FirebaseUtils().firestoreDatabase.collection(DatabaseVariables.weightConsumedDatabase)
            .whereEqualTo(DatabaseVariables.userId, correlationId)
            .whereEqualTo(DatabaseVariables.inputDate, currentDate.toString())
            .get()
            .await()
            .documents
    }

    private suspend fun getWeightGoalsForUserForTodayFromDb(correlationId: String, currentDate: LocalDate): MutableList<DocumentSnapshot> {
        return FirebaseUtils().firestoreDatabase.collection(DatabaseVariables.weightGoalDatabase)
            .whereEqualTo(DatabaseVariables.userId, correlationId)
            .get()
            .await()
            .documents
    }
}
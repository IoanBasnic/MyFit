package msa.myfit.fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.anychart.anychart.*
import com.anychart.anychart.AnyChart.cartesian
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import msa.myfit.R
import msa.myfit.domain.DatabaseVariables
import msa.myfit.domain.WeightToday
import msa.myfit.firebase.FirebaseUtils
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [WeightOverviewFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WeightOverviewFragment(mainActivity: AppCompatActivity) : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private val mainActivity = mainActivity
    private var retrievedGoal: MutableList<DocumentSnapshot>? = null
    private var retrievedWeights: MutableList<DocumentSnapshot>? = null

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
        return inflater.inflate(R.layout.fragment_weight_overview, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val correlationId = FirebaseAuth.getInstance().currentUser!!.uid

        GlobalScope.launch {
            getWeightsAndGoalsForUserForTodayAndUpdateView(
                correlationId,
                OffsetDateTime.now().toLocalDate(),
                view
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun getWeightsAndGoalsForUserForTodayAndUpdateView(
        userId: String,
        currentDate: LocalDate,
        view: View
    ){
        retrievedWeights = FirebaseUtils().firestoreDatabase.collection(DatabaseVariables.weightForToday)
            .whereEqualTo(DatabaseVariables.userId, userId)
            .whereEqualTo(DatabaseVariables.inputDate, currentDate.toString())
            .get()
            .await()
            .documents

        retrievedGoal = FirebaseUtils().firestoreDatabase.collection(DatabaseVariables.weightGoal)
            .whereEqualTo(DatabaseVariables.userId, userId)
            .get()
            .await()
            .documents

        mainActivity.runOnUiThread {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            //the list is the other way around, index 6 is the first chronological date and 0 is the last
            val weightThisWeek: MutableList<WeightToday> = mutableListOf()

            val currentDate = LocalDate.now()
            weightThisWeek.add(WeightToday(0.0f, currentDate.dayOfWeek, currentDate))
            weightThisWeek.add(
                WeightToday(
                    0.0f, currentDate.dayOfWeek.minus(1), currentDate.minusDays(
                        1
                    )
                )
            )
            weightThisWeek.add(
                WeightToday(
                    0.0f, currentDate.dayOfWeek.minus(2), currentDate.minusDays(
                        2
                    )
                )
            )
            weightThisWeek.add(
                WeightToday(
                    0.0f, currentDate.dayOfWeek.minus(3), currentDate.minusDays(
                        3
                    )
                )
            )
            weightThisWeek.add(
                WeightToday(
                    0.0f, currentDate.dayOfWeek.minus(4), currentDate.minusDays(
                        4
                    )
                )
            )
            weightThisWeek.add(
                WeightToday(
                    0.0f, currentDate.dayOfWeek.minus(5), currentDate.minusDays(
                        5
                    )
                )
            )
            weightThisWeek.add(
                WeightToday(
                    0.0f, currentDate.dayOfWeek.minus(6), currentDate.minusDays(
                        6
                    )
                )
            )


            if(retrievedWeights != null){
                retrievedWeights!!.asIterable().forEach {
                    val date = LocalDate.parse(
                        it.data!!.get(DatabaseVariables.inputDate).toString(), formatter
                    )

                    val dateDifference = LocalDate.now().dayOfYear.minus(date.dayOfYear)
                    if(dateDifference < 7) {
                        val weightToday = weightThisWeek.get(dateDifference)

                        val weight = it.data!!.get(DatabaseVariables.weight).toString().toFloat()

                        weightThisWeek.set(
                            dateDifference,
                            WeightToday(weight, weightToday.dayOfWeek, weightToday.dateTime)
                        )
                    }
                }
            }

            val pie = AnyChart.line()
            val data: MutableList<DataEntry> = ArrayList()
            val goalData: MutableList<DataEntry> = ArrayList()

            val daysOfTheWeek = hashMapOf(
                "MONDAY" to "Mon",
                "TUESDAY" to "Tue",
                "WEDNESDAY" to "Wed",
                "THURSDAY" to "Thu",
                "FRIDAY" to "Fri",
                "SATURDAY" to "Sat",
                "SUNDAY" to "Sun"
            )

            for(weight in weightThisWeek.asReversed()){
                data.add(
                    ValueDataEntry(
                        daysOfTheWeek.get(weight.dayOfWeek.name),
                        weight.weight.toDouble()
                    )
                )

                goalData.add(
                    ValueDataEntry(
                        daysOfTheWeek.get(weight.dayOfWeek.name),
                        retrievedGoal!![0].data!!.get(DatabaseVariables.weight).toString().toDouble()
                    )
                )
            }

            pie.data(data)

            //TODO: also add line for goal weight
//            pie.addSeries(goalData)

            val anyChartView : AnyChartView = view.findViewById(R.id.any_chart_view)
            anyChartView.setChart(pie)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment WeightOverviewFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String, mainActivity: AppCompatActivity) =
            WeightOverviewFragment(mainActivity).apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
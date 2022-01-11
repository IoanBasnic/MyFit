package msa.myfit.fragment.overview

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.core.cartesian.series.Line
import com.anychart.enums.Anchor
import com.anychart.enums.MarkerType
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
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class WeightOverviewFragment(private val mainActivity: AppCompatActivity) : Fragment() {
    private var retrievedGoal: MutableList<DocumentSnapshot>? = null
    private var retrievedWeights: MutableList<DocumentSnapshot>? = null

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
                view
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun getWeightsAndGoalsForUserForTodayAndUpdateView(
        userId: String,
        view: View
    ){
        retrievedWeights = FirebaseUtils().firestoreDatabase.collection(DatabaseVariables.weightConsumedDatabase)
            .whereEqualTo(DatabaseVariables.userId, userId)
            .get()
            .await()
            .documents

        retrievedGoal = FirebaseUtils().firestoreDatabase.collection(DatabaseVariables.weightGoalDatabase)
            .whereEqualTo(DatabaseVariables.userId, userId)
            .get()
            .await()
            .documents

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

                    val dateDifference = ChronoUnit.DAYS.between(date, LocalDate.now()).toInt()
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

        mainActivity.runOnUiThread {
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

                if(retrievedGoal != null) {
                    goalData.add(
                        ValueDataEntry(
                            daysOfTheWeek.get(weight.dayOfWeek.name),
                            retrievedGoal!![0].data!!.get(DatabaseVariables.weight).toString()
                                .toDouble()
                        )
                    )
                }
            }

            val series1: Line = pie.line(data)
            series1.name("Weight")
            series1.hovered().markers().enabled(true)
            series1.hovered().markers()
                .type(MarkerType.CIRCLE)
                .size(4.0)
            series1.tooltip()
                .position("right")
                .anchor(Anchor.LEFT_CENTER)
                .offsetX(5.0)
                .offsetY(5.0)

            val series2: Line = pie.line(goalData)
            series2.name("Weight goal")
            series2.hovered().markers().enabled(true)
            series2.hovered().markers()
                .type(MarkerType.CIRCLE)
                .size(4.0)
            series2.tooltip()
                .position("right")
                .anchor(Anchor.LEFT_CENTER)
                .offsetX(5.0)
                .offsetY(5.0)

            val anyChartView : AnyChartView = view.findViewById(R.id.any_chart_view)
            anyChartView.setChart(pie)
        }
    }
}
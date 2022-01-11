package msa.myfit.fragment.overview

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.anychart.APIlib
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import msa.myfit.R
import msa.myfit.domain.CaloriesToday
import msa.myfit.domain.DatabaseVariables
import msa.myfit.firebase.FirebaseUtils
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CaloriesBurntFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CaloriesBurntFragment(private val mainActivity: AppCompatActivity) : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    val daysOfTheWeek = hashMapOf(
        "MONDAY" to "Mon",
        "TUESDAY" to "Tue",
        "WEDNESDAY" to "Wed",
        "THURSDAY" to "Thu",
        "FRIDAY" to "Fri",
        "SATURDAY" to "Sat",
        "SUNDAY" to "Sun"
    )
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
        return inflater.inflate(R.layout.fragment_calories_burnt, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val correlationId = FirebaseAuth.getInstance().currentUser!!.uid

        GlobalScope.launch {
            getCaloriesBurntForLastWeekAndUpdateView(
                correlationId,
                view
            )
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun getCaloriesBurntForLastWeekAndUpdateView(userId: String, view: View) {
        val retrievedRoutes = FirebaseUtils().firestoreDatabase.collection(DatabaseVariables.routeDatabase)
            .whereEqualTo(DatabaseVariables.userId, userId)
            .get()
            .await()
            .documents

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        //the list is the other way around, index 6 is the first chronological date and 0 is the last
        val caloriesBurntThisWeek: MutableList<CaloriesToday> = mutableListOf()

        val currentDate = LocalDate.now()
        caloriesBurntThisWeek.add(CaloriesToday(0.0f, currentDate.dayOfWeek, currentDate))
        caloriesBurntThisWeek.add(CaloriesToday(0.0f, currentDate.dayOfWeek.minus(1), currentDate.minusDays(1)))
        caloriesBurntThisWeek.add(CaloriesToday(0.0f, currentDate.dayOfWeek.minus(2), currentDate.minusDays(2)))
        caloriesBurntThisWeek.add(CaloriesToday(0.0f, currentDate.dayOfWeek.minus(3), currentDate.minusDays(3)))
        caloriesBurntThisWeek.add(CaloriesToday(0.0f, currentDate.dayOfWeek.minus(4), currentDate.minusDays(4)))
        caloriesBurntThisWeek.add(CaloriesToday(0.0f, currentDate.dayOfWeek.minus(5), currentDate.minusDays(5)))
        caloriesBurntThisWeek.add(CaloriesToday(0.0f, currentDate.dayOfWeek.minus(6), currentDate.minusDays(6)))

        retrievedRoutes.asIterable().forEach {
            val date = LocalDate.parse(it.data!!.get(DatabaseVariables.startDate).toString().dropLast(13), formatter)

            val dateDifference = ChronoUnit.DAYS.between(date, LocalDate.now()).toInt()
            if(dateDifference < 7) {
                val caloriesBurntToday = caloriesBurntThisWeek.get(dateDifference)

                val calories = caloriesBurntToday.calorieSum + it.data!!.get(DatabaseVariables.caloriesBurnt).toString().toFloat()

                caloriesBurntThisWeek.set(
                    dateDifference,
                    CaloriesToday(calories, caloriesBurntToday.dayOfWeek, caloriesBurntToday.dateTime)
                )
            }
        }

        mainActivity.runOnUiThread {
            val pie = AnyChart.line()

            val data: MutableList<DataEntry> = ArrayList()

            for(calories in caloriesBurntThisWeek.asReversed()){
                data.add(ValueDataEntry(daysOfTheWeek.get(calories.dayOfWeek.name), calories.calorieSum.toDouble()))
            }

            pie.data(data)
            pie.title("Calories burnt this week")
            pie.yAxis("kcal")

            val anyChartView : AnyChartView = view.findViewById(R.id.any_chart_view)
            APIlib.getInstance().setActiveAnyChartView(anyChartView)
            anyChartView.setChart(pie)
        }
    }
}

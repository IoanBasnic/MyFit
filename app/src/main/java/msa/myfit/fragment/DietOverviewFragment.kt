package msa.myfit.fragment

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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import msa.myfit.R
import msa.myfit.domain.CaloriesToday
import msa.myfit.domain.DatabaseVariables
import msa.myfit.firebase.FirebaseUtils
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
/**
 * A simple [Fragment] subclass.
 * Use the [DietOverviewFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DietOverviewFragment(mainActivity: AppCompatActivity) : Fragment() {
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
        return inflater.inflate(R.layout.fragment_diet_overview, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val correlationId = FirebaseAuth.getInstance().currentUser!!.uid

        GlobalScope.launch {
            getCaloriesForUserForTodayFromDB(
                correlationId,
                view
            )
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun getCaloriesForUserForTodayFromDB(
        userId: String,
        view: View
    ){
        var retrievedFoods = FirebaseUtils().firestoreDatabase.collection(DatabaseVariables.foodDatabase)
            .whereEqualTo(DatabaseVariables.userId, userId)
            .get()
            .await()
            .documents

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        //the list is the other way around, index 6 is the first chronological date and 0 is the last
        val caloriesThisWeek: MutableList<CaloriesToday> = mutableListOf()

        val currentDate = LocalDate.now()
        caloriesThisWeek.add(CaloriesToday(0.0f, currentDate.dayOfWeek, currentDate))
        caloriesThisWeek.add(CaloriesToday(0.0f, currentDate.dayOfWeek.minus(1), currentDate.minusDays(1)))
        caloriesThisWeek.add(CaloriesToday(0.0f, currentDate.dayOfWeek.minus(2), currentDate.minusDays(2)))
        caloriesThisWeek.add(CaloriesToday(0.0f, currentDate.dayOfWeek.minus(3), currentDate.minusDays(3)))
        caloriesThisWeek.add(CaloriesToday(0.0f, currentDate.dayOfWeek.minus(4), currentDate.minusDays(4)))
        caloriesThisWeek.add(CaloriesToday(0.0f, currentDate.dayOfWeek.minus(5), currentDate.minusDays(5)))
        caloriesThisWeek.add(CaloriesToday(0.0f, currentDate.dayOfWeek.minus(6), currentDate.minusDays(6)))


        retrievedFoods.asIterable().forEach {
            val date = LocalDate.parse(it.data!!.get(DatabaseVariables.inputDate).toString(), formatter)

            val dateDifference = ChronoUnit.DAYS.between(date, LocalDate.now()).toInt()
            if(dateDifference < 7) {
                val caloriesEatenToday = caloriesThisWeek.get(dateDifference)

                val calories = caloriesEatenToday.calorieSum + it.data!!.get(DatabaseVariables.calories).toString().toFloat()

                caloriesThisWeek.set(
                    dateDifference,
                    CaloriesToday(calories,caloriesEatenToday.dayOfWeek, caloriesEatenToday.dateTime)
                )
            }
        }

        mainActivity.runOnUiThread {
            val pie = AnyChart.line()
            val data: MutableList<DataEntry> = ArrayList()

            val daysOfTheWeek = hashMapOf(
                "MONDAY" to "Mon",
                "TUESDAY" to "Tue",
                "WEDNESDAY" to "Wed",
                "THURSDAY" to "Thu",
                "FRIDAY" to "Fri",
                "SATURDAY" to "Sat",
                "SUNDAY" to "Sun"
            )

            for(calories in caloriesThisWeek.asReversed()){
                data.add(ValueDataEntry(daysOfTheWeek.get(calories.dayOfWeek.name), calories.calorieSum.toDouble()))
            }

            pie.data(data)
            pie.title("Calories consumed this week")
            pie.yAxis("kcal")

            val anyChartView : AnyChartView = view.findViewById(R.id.any_chart_view)
            anyChartView.setChart(pie)
        }
    }
}
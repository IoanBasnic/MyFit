package msa.myfit.fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.anychart.anychart.AnyChart
import com.anychart.anychart.AnyChartView
import com.anychart.anychart.DataEntry
import com.anychart.anychart.ValueDataEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import msa.myfit.R
import msa.myfit.domain.CaloriesEatenToday
import msa.myfit.domain.DatabaseVariables
import msa.myfit.firebase.FirebaseUtils
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

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
    private var retrievedFoods: MutableList<DocumentSnapshot>? = null


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

        //TODO: create fragment
        return inflater.inflate(R.layout.fragment_diet_overview, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val correlationId = FirebaseAuth.getInstance().currentUser!!.uid

        GlobalScope.launch {
            getCaloriesForUserForTodayFromDB(
                correlationId,
                OffsetDateTime.now().toLocalDate(),
                view
            )
        }

        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        //the list is the other way around, index 6 is the first chronological date and 0 is the last
        val caloriesEatenThisWeek: MutableList<CaloriesEatenToday> = mutableListOf()

        val currentDate = LocalDate.now()
        caloriesEatenThisWeek.add(CaloriesEatenToday(0.0f, currentDate.dayOfWeek, currentDate))
        caloriesEatenThisWeek.add(CaloriesEatenToday(0.0f, currentDate.dayOfWeek.minus(1), currentDate.minusDays(1)))
        caloriesEatenThisWeek.add(CaloriesEatenToday(0.0f, currentDate.dayOfWeek.minus(2), currentDate.minusDays(2)))
        caloriesEatenThisWeek.add(CaloriesEatenToday(0.0f, currentDate.dayOfWeek.minus(3), currentDate.minusDays(3)))
        caloriesEatenThisWeek.add(CaloriesEatenToday(0.0f, currentDate.dayOfWeek.minus(4), currentDate.minusDays(4)))
        caloriesEatenThisWeek.add(CaloriesEatenToday(0.0f, currentDate.dayOfWeek.minus(5), currentDate.minusDays(5)))
        caloriesEatenThisWeek.add(CaloriesEatenToday(0.0f, currentDate.dayOfWeek.minus(6), currentDate.minusDays(6)))


        if(retrievedFoods != null){
            retrievedFoods!!.asIterable().forEach {
                val date = LocalDate.parse(it.data!!.get(DatabaseVariables.inputDate).toString(), formatter)

                val dateDifference = LocalDate.now().dayOfYear.minus(date.dayOfYear)
                if(dateDifference < 7) {
                    val caloriesEatenToday = caloriesEatenThisWeek.get(dateDifference)

                    val calories = caloriesEatenToday.calorieSum + it.data!!.get(DatabaseVariables.calories).toString().toFloat()

                    caloriesEatenThisWeek.set(
                        dateDifference,
                        CaloriesEatenToday(calories,caloriesEatenToday.dayOfWeek, caloriesEatenToday.dateTime)
                    )
                }
            }
        }

        val pie = AnyChart.line()

        val data: MutableList<DataEntry> = ArrayList()
        data.add(ValueDataEntry("John", 10000))
        data.add(ValueDataEntry("Jake", 12000))
        data.add(ValueDataEntry("Peter", 18000))

        pie.data(data)

        val anyChartView : AnyChartView = view.findViewById(R.id.any_chart_view)
        anyChartView.setChart(pie)
    }


    private suspend fun getCaloriesForUserForTodayFromDB(
        userId: String,
        currentDate: LocalDate,
        view: View
    ){
        retrievedFoods = FirebaseUtils().firestoreDatabase.collection(DatabaseVariables.foodDatabase)
            .whereEqualTo(DatabaseVariables.userId, userId)
            .whereEqualTo(DatabaseVariables.inputDate, currentDate.toString())
            .get()
            .await()
            .documents

        mainActivity.runOnUiThread {
            //TODO: update data exposed on page
        }
    }
}
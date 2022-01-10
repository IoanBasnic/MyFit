package msa.myfit.fragment

import android.app.Dialog
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import msa.myfit.R
import msa.myfit.domain.AddFoodFragmentButton
import msa.myfit.domain.AddFoodFragmentFields
import msa.myfit.domain.DatabaseVariables
import msa.myfit.firebase.FirebaseUtils
import java.time.LocalDate
import java.time.OffsetDateTime

class CaloriesEatenFragment(private val mainActivity: AppCompatActivity) : Fragment() {

    private var retrievedFoods: MutableList<DocumentSnapshot>? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment.
        return inflater.inflate(R.layout.fragment_calories_eaten, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val correlationId = FirebaseAuth.getInstance().currentUser!!.uid

        val stk: TableLayout = view.findViewById(R.id.table_main)
        val tbrow0: TableRow = TableRow(activity)
        val tv0 : TextView = TextView(activity)
        tv0.setText(" No. ")
        tv0.setTextColor(Color.DKGRAY)
        tbrow0.addView(tv0)
        val tv1 : TextView = TextView(activity)
        tv1.setText(" Food ")
        tv1.setTextColor(Color.DKGRAY)
        tbrow0.addView(tv1)
        val tv2 : TextView= TextView(activity)
        tv2.setText(" Type ")
        tv2.setTextColor(Color.DKGRAY)
        tbrow0.addView(tv2)
        val tv3 : TextView= TextView(activity)
        tv3.setText("Kcal ")
        tv3.setTextColor(Color.DKGRAY)
        tbrow0.addView(tv3)
        stk.addView(tbrow0)

        GlobalScope.launch {
            getCaloriesForUserForTodayFromDB(
                correlationId,
                OffsetDateTime.now().toLocalDate(),
                view
            )
        }

        val addFoodFragmentButton = AddFoodFragmentButton(
            btnAddFood = view.findViewById(R.id.btn_add_food)
        )

        addFoodFragmentButton.btnAddFood.setOnClickListener {
            showCustomDialog(correlationId, view)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateFoodConsumedToday(view: View){
        var currentId = 0
        var caloriesEatenToday = 0.0

        val table: TableLayout = view.findViewById(R.id.table_main)
        val childCount: Int = table.childCount
        if (childCount > 1) {
            table.removeViews(1, childCount - 1)
        }

        if(retrievedFoods != null){
            retrievedFoods!!.asIterable().forEach{
                val tbrow : TableRow = TableRow(activity)
                val t1v : TextView = TextView(activity)
                t1v.setText(currentId.toString() + " ")
                t1v.setTextColor(Color.DKGRAY)
                t1v.setGravity(Gravity.CENTER)
                tbrow.addView(t1v)

                val t2v : TextView = TextView(activity)
                val foodName: String = it.data!!.get(DatabaseVariables.name).toString() + " "
                t2v.setText(foodName)
                t2v.setTextColor(Color.DKGRAY)
                t2v.setGravity(Gravity.CENTER)
                tbrow.addView(t2v)

                val t3v : TextView = TextView(activity)
                val type = it.data!!.get(DatabaseVariables.foodType)
                if(type == null)
                    t3v.setText("")
                else
                    t3v.setText(type.toString() + " ")
                t3v.setTextColor(Color.DKGRAY)
                t3v.setGravity(Gravity.CENTER)
                tbrow.addView(t3v)

                val t4v : TextView = TextView(activity)
                val caloriesFood: Float = it.data!!.get(DatabaseVariables.calories).toString().toFloat()
                caloriesEatenToday = caloriesEatenToday.plus(caloriesFood)
                t4v.setText(caloriesFood.toString())
                t4v.setTextColor(Color.DKGRAY)
                t4v.setGravity(Gravity.CENTER)
                tbrow.addView(t4v)
                table.addView(tbrow)

                currentId = currentId.plus(1)
            }

            val caloriesEatenTodayTv : TextView = view.findViewById(R.id.intake_calories)
            caloriesEatenTodayTv.setText("$caloriesEatenToday Kcal")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
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
            updateFoodConsumedToday(view)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun showCustomDialog(correlationId: String, view: View) {
        val dialog = activity?.let { Dialog(it) }
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.custom_dialog)

        val addFoodFragmentFields = AddFoodFragmentFields(
            calories = dialog.findViewById(R.id.calories_et),
            foodName = dialog.findViewById(R.id.food_et),
            foodType = dialog.findViewById(R.id.type_et)
        )

        val submitButton: Button = dialog.findViewById(R.id.submit_button)
        submitButton.setOnClickListener {

            if (addFoodFragmentFields.calories.text.isNotBlank()) {
                val foodToAdd = hashMapOf<String, Any?>(
                    DatabaseVariables.userId to correlationId,
                    DatabaseVariables.calories to addFoodFragmentFields.calories.text.toString(),
                    DatabaseVariables.name to addFoodFragmentFields.foodName.text.toString(),
                    DatabaseVariables.foodType to addFoodFragmentFields.foodType.text.toString(),
                    DatabaseVariables.inputDate to OffsetDateTime.now().toLocalDate().toString()
                )

                FirebaseUtils().firestoreDatabase.collection(DatabaseVariables.foodDatabase)
                    .add(foodToAdd)
                    .addOnSuccessListener {
                        Log.d(TAG, "Added food with ID ${it.id}")

                        GlobalScope.launch {
                            getCaloriesForUserForTodayFromDB(
                                correlationId,
                                OffsetDateTime.now().toLocalDate(),
                                view
                            )
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.w(TAG, "Error adding food $exception")
                    }
            }

            dialog.dismiss()
        }
        dialog.show()
    }
}

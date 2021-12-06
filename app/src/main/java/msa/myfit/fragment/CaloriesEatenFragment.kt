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
import msa.myfit.domain.DatabaseVariables
import msa.myfit.domain.DatabaseVariables.foodDatabase
import msa.myfit.domain.DatabaseVariables.inputDate
import msa.myfit.domain.DatabaseVariables.name
import msa.myfit.domain.DatabaseVariables.userId
import msa.myfit.firebase.FirebaseUtils
import java.time.LocalDate
import java.time.OffsetDateTime


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CaloriesEatenFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CaloriesEatenFragment(mainActivity: AppCompatActivity) : Fragment() {
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment.
        return inflater.inflate(R.layout.fragment_calories_eaten, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CaloriesEatenFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String, mainActivity: AppCompatActivity) =
            CaloriesEatenFragment(mainActivity).apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
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

//        val addFoodFragment = AddFoodFragmentData(
//            btnAddFood = view.findViewById(R.id.btn_add_food),
//            calories = view.findViewById(R.id.edit_calories),
//            foodName = view.findViewById(R.id.edit_food_name)
//        )

//        addFoodFragment.btnAddFood.setOnClickListener {
        val btn: Button = view.findViewById(R.id.btn_add_food)
        btn.setOnClickListener {
            showCustomDialog()


            val calories = 500.0
            val foodName = "food"

//            if (addFoodFragment.calories.text.isNotBlank()) {
            if (calories != 0.0) {
//                val foodToAdd = hashMapOf<String, Any?>(
//                    userId to correlationId,
//                    calories to addFoodFragment.calories.text.toString(),
//                    name to addFoodFragment.foodName.text.toString(),
//                    inputDate to OffsetDateTime.now().toString()
//                )

                val foodToAdd = hashMapOf<String, Any?>(
                    userId to correlationId,
                    DatabaseVariables.calories to calories.toString(),
                    name to foodName.toString(),
                    inputDate to OffsetDateTime.now().toLocalDate().toString()
                )

                FirebaseUtils().firestoreDatabase.collection(foodDatabase)
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
                val tbrow : TableRow = TableRow(activity);
                val t1v : TextView = TextView(activity);
                t1v.setText(currentId.toString());
                t1v.setTextColor(Color.DKGRAY);
                t1v.setGravity(Gravity.CENTER);
                tbrow.addView(t1v);

                val t2v : TextView = TextView(activity);
                val caloriesFood: Float = it.data!!.get(DatabaseVariables.calories).toString().toFloat()
                caloriesEatenToday = caloriesEatenToday.plus(caloriesFood)
                t2v.setText(caloriesFood.toString());
                t2v.setTextColor(Color.DKGRAY);
                t2v.setGravity(Gravity.CENTER);
                tbrow.addView(t2v);

                val t3v : TextView = TextView(activity);
                t3v.setText("Rs." + currentId);
                t3v.setTextColor(Color.DKGRAY);
                t3v.setGravity(Gravity.CENTER);
                tbrow.addView(t3v);

                val t4v : TextView = TextView(activity);
                t4v.setText(it.data!!.get(DatabaseVariables.calories).toString());
                t4v.setTextColor(Color.DKGRAY);
                t4v.setGravity(Gravity.CENTER);
                tbrow.addView(t4v);
                table.addView(tbrow);

                currentId = currentId.plus(1)
            }

            val caloriesEatenTodayTv : TextView = view.findViewById(R.id.intake_calories)
            caloriesEatenTodayTv.setText("$caloriesEatenToday Kcal");
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun getCaloriesForUserForTodayFromDB(
        userId: String,
        currentDate: LocalDate,
        view: View
    ){
        retrievedFoods = FirebaseUtils().firestoreDatabase.collection(foodDatabase)
            .whereEqualTo(DatabaseVariables.userId, userId)
            .whereEqualTo(inputDate, currentDate.toString())
            .get()
            .await()
            .documents

        mainActivity.runOnUiThread {
            updateFoodConsumedToday(view)
        }
    }

    fun showCustomDialog() {
        val dialog = activity?.let { Dialog(it) }
        //We have added a title in the custom layout. So let's disable the default title.
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        //The user will be able to cancel the dialog bu clicking anywhere outside the dialog.
        dialog!!.setCancelable(true)
        //Mention the name of the layout of your custom dialog.
        dialog!!.setContentView(R.layout.custom_dialog)

        //Initializing the views of the dialog.
        val nameEt: EditText = dialog!!.findViewById(R.id.food_et)
        val typeEt: EditText = dialog!!.findViewById(R.id.type_et)
        val ageEt: EditText = dialog!!.findViewById(R.id.calories_et)
        val submitButton: Button = dialog!!.findViewById(R.id.submit_button)
        submitButton.setOnClickListener {
            val name = nameEt.text.toString()
            val age = ageEt.text.toString()
            if (dialog != null) {
                dialog.dismiss()
            }
        }
        if (dialog != null) {
            dialog.show()
        }
    }
}

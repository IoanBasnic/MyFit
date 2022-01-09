package msa.myfit.fragment

import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.textfield.TextInputEditText
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
 * Use the [TodayWeightFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TodayWeightFragment(mainActivity: AppCompatActivity, existingDocuments: MutableList<DocumentSnapshot>?) : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private val mainActivity = mainActivity
    private var existingDocuments: MutableList<DocumentSnapshot>? = existingDocuments

    @RequiresApi(Build.VERSION_CODES.O)
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
        if(existingDocuments.isNullOrEmpty()){
            // Inflate the layout for this fragment
            return inflater.inflate(R.layout.fragment_today_weight, container, false)
        }

        return inflater.inflate(R.layout.fragment_today_weight_already_inputted, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TodayGoalFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String, mainActivity: AppCompatActivity, existingDocuments: MutableList<DocumentSnapshot>?) =
            TodayWeightFragment(mainActivity, existingDocuments).apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(!existingDocuments.isNullOrEmpty()){
            val textView: TextView = view.findViewById(R.id.weight_already_inputted)

            val existingDocument = existingDocuments!![0]
            if(existingDocument != null){
                textView.setText(existingDocument.data!![DatabaseVariables.weight].toString() + " Kg")
            }
        }
        else{
            val correlationId = FirebaseAuth.getInstance().currentUser!!.uid

            val setWeightTodayBtn: Button = view.findViewById(R.id.btn_set_weight_today)
            val weightToday: TextInputEditText = view.findViewById(R.id.edit_weight_for_today)

            setWeightTodayBtn.setOnClickListener {
                val weightToSave = weightToday.text.toString().trim { it <= ' '}

                when{
                    TextUtils.isEmpty(weightToSave) -> {
                        Toast.makeText(
                            mainActivity,
                            "Please enter your weight for today",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    !TextUtils.isDigitsOnly(weightToSave) -> {
                        Toast.makeText(
                            mainActivity,
                            "Please enter only digits for your weight",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    else -> {
                        val weightForTodayToAdd = hashMapOf<String, Any?>(
                            DatabaseVariables.userId to correlationId,
                            DatabaseVariables.weight to weightToSave,
                            DatabaseVariables.inputDate to OffsetDateTime.now().toLocalDate().toString()
                        )

                        FirebaseUtils().firestoreDatabase.collection(DatabaseVariables.weightForToday)
                            .add(weightForTodayToAdd)
                            .addOnSuccessListener {
                                Log.d(TAG, "Added weight for today with ID ${it.id}")

                                var fragment: Fragment? = HomeFragment(mainActivity)
                                replaceFragment(fragment)
                            }
                            .addOnFailureListener { exception ->
                                Log.w(TAG, "Error adding user profile $exception")
                            }
                    }
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
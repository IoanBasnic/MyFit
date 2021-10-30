package msa.myfit.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import msa.myfit.R
import msa.myfit.databinding.ActivityMainBinding
import msa.myfit.firebase.FirebaseUtils

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MyProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

const val TAG = "FIRESTORE"

class MyProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var binding: ActivityMainBinding? = null

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
        return inflater.inflate(R.layout.fragment_my_profile, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MyProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MyProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnUpdateProfile: Button = view.findViewById(R.id.btn_update_profile)
        val firstName: EditText = view.findViewById(R.id.edit_first_name)
        val lastName: EditText = view.findViewById(R.id.edit_last_name)
        val age: EditText = view.findViewById(R.id.edit_age)
        val height: EditText = view.findViewById(R.id.edit_height)

        val gender: Spinner = view.findViewById(R.id.spinner_gender)
        ArrayAdapter.createFromResource(
            this,
            R.array.gender_options_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            gender.adapter = adapter
        }
        val correlationId = FirebaseAuth.getInstance().currentUser!!.tenantId


        btnUpdateProfile.setOnClickListener {
            val hashMap = hashMapOf<String, Any?>(
                "correlationId" to correlationId,
                "firstName" to firstName.text.toString(),
                "lastName" to lastName.text.toString(),
                "gender" to gender.toString(),
                "age" to age.toString(),
                "height" to height.toString()
            )

            FirebaseUtils().firestoreDatabase.collection("user_profiles")
                .add(hashMap)
                .addOnSuccessListener {
                    Log.d(TAG, "Added user profile with ID ${it.id}")
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error adding user profile $exception")
                }
        }
    }
}

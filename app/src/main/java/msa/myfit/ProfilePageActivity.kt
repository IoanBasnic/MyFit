package msa.myfit

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import msa.myfit.databinding.ActivityMainBinding
import msa.myfit.firebase.FirebaseUtils

const val TAG = "FIRESTORE"

class ProfilePageActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(R.layout.profile_page)
        setContentView(binding?.root)

        val btnUpdateProfile: Button = findViewById(R.id.btn_update_profile)
        val firstName: EditText = findViewById(R.id.edit_first_name)
        val lastName: EditText = findViewById(R.id.edit_last_name)
        val age: EditText = findViewById(R.id.edit_age)
        val height: EditText = findViewById(R.id.edit_height)

        val gender: Spinner = findViewById(R.id.spinner_gender)
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
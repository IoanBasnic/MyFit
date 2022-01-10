package msa.myfit.fragment

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import msa.myfit.R
import msa.myfit.domain.DatabaseVariables
import msa.myfit.domain.DatabaseVariables.age
import msa.myfit.domain.DatabaseVariables.firstName
import msa.myfit.domain.DatabaseVariables.gender
import msa.myfit.domain.DatabaseVariables.height
import msa.myfit.domain.DatabaseVariables.lastName
import msa.myfit.domain.DatabaseVariables.userProfileDatabase
import msa.myfit.domain.UserProfileFragmentData
import msa.myfit.firebase.FirebaseUtils

const val TAG = "FIRESTORE"

class MyProfileFragment(private val mainActivity: AppCompatActivity) : Fragment() {
private var retrievedDocuments: MutableList<DocumentSnapshot>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fragmentActivity: Activity? = activity

        val correlationId = FirebaseAuth.getInstance().currentUser!!.uid

        val userProfileFragment = UserProfileFragmentData(
            btnUpdateProfile = view.findViewById(R.id.btn_update_profile),
            firstName = view.findViewById(R.id.edit_first_name),
            lastName = view.findViewById(R.id.edit_last_name),
            gender = view.findViewById(R.id.spinner_gender),
            age = view.findViewById(R.id.edit_age),
            height = view.findViewById(R.id.edit_height)
        )

        var existingDocument: DocumentSnapshot? = null
        GlobalScope.launch {
            existingDocument = getUserProfilesFromDbAndUpdateView(correlationId, userProfileFragment)
            Log.i("tag","Retrieved existing document $existingDocument with correlation id $correlationId")
        }

        if (fragmentActivity != null) {
            ArrayAdapter.createFromResource(
                fragmentActivity,
                R.array.gender_options_array,
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                // Apply the adapter to the spinner
                userProfileFragment.gender.adapter = adapter
            }
        }


        userProfileFragment.btnUpdateProfile.setOnClickListener {

            if(!retrievedDocuments.isNullOrEmpty())
                existingDocument = retrievedDocuments!![0]

            val userProfileToAdd = hashMapOf<String, Any?>(
                DatabaseVariables.correlationId to correlationId,
                firstName to userProfileFragment.firstName.text.toString(),
                lastName to userProfileFragment.lastName.text.toString(),
                gender to userProfileFragment.gender.selectedItem.toString(),
                age to userProfileFragment.age.text.toString(),
                height to userProfileFragment.height.text.toString()
            )

            if(existingDocument != null){
                FirebaseUtils().firestoreDatabase.collection(userProfileDatabase)
                    .document(existingDocument!!.id)
                    .set(userProfileToAdd, SetOptions.merge())
                    .addOnSuccessListener {
                        Log.d(TAG, "Updated user profile with correlation id $correlationId")

                        mainActivity.runOnUiThread {
                            updateUserProfileData(
                                userProfileToAdd,
                                userProfileFragment
                            )
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.w(TAG, "Error adding user profile $exception")
                    }
            }
            else{
                FirebaseUtils().firestoreDatabase.collection(userProfileDatabase)
                    .add(userProfileToAdd)
                    .addOnSuccessListener {
                        Log.d(TAG, "Added user profile with ID ${it.id}")

                        mainActivity.runOnUiThread {
                            updateUserProfileData(
                                userProfileToAdd,
                                userProfileFragment
                            )
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.w(TAG, "Error adding user profile $exception")
                    }
            }

            existingDocument = null
            retrievedDocuments = null
            GlobalScope.launch {
                retrievedDocuments = getUserProfilesFromDb(correlationId)
            }
        }
    }

    private suspend fun getUserProfilesFromDbAndUpdateView(correlationId: String, userProfileFragment: UserProfileFragmentData): DocumentSnapshot? {
        mainActivity.runOnUiThread{enableUserProfileFragment(userProfileFragment, false)}

        val userProfiles = getUserProfilesFromDb(correlationId)

        if(userProfiles.size > 1){
            Log.w(TAG, "Error user has more than one user profiles")
        }

        if(userProfiles.isNotEmpty()) {
            val existingUserProfile = userProfiles[0]

            mainActivity.runOnUiThread {
                setExistingUserProfileDataToFragment(
                    existingUserProfile,
                    userProfileFragment
                )
            }
        }

        mainActivity.runOnUiThread{enableUserProfileFragment(userProfileFragment, true)}

        if(userProfiles.isEmpty())
            return null
        return userProfiles[0]
    }

    private fun setExistingUserProfileDataToFragment(
        userProfile: DocumentSnapshot,
        userProfileFragment: UserProfileFragmentData
    ){
        val fn = userProfile.data!![firstName]
        if (fn != null) {
            userProfileFragment.firstName.setText(fn.toString())
        }

        val ln = userProfile.data!![lastName]
        if (ln != null) {
            userProfileFragment.lastName.setText(ln.toString())
        }

        val g = userProfile.data!![gender]
        if (g != null) {
            val spinnerPosition: Int = getIndex(userProfileFragment.gender, g.toString())
            userProfileFragment.gender.setSelection(spinnerPosition)
        }

        val a = userProfile.data!![age]
        if (a != null) {
            userProfileFragment.age.setText(a.toString())
        }

        val h = userProfile.data!![height]
        if (h != null) {
            userProfileFragment.height.setText(h.toString())
        }
    }

    private fun updateUserProfileData(userProfileToAdd: HashMap<String, Any?>, userProfileFragment: UserProfileFragmentData){
        val fn = userProfileToAdd[firstName]
        if (fn != null) {
            userProfileFragment.firstName.setText(fn.toString())
        }

        val ln = userProfileToAdd[lastName]
        if (ln != null) {
            userProfileFragment.lastName.setText(ln.toString())
        }

        val g = userProfileToAdd[gender]
        if (g != null) {
            val spinnerPosition: Int = getIndex(userProfileFragment.gender, g.toString())

            userProfileFragment.gender.setSelection(spinnerPosition)
        }

        val a = userProfileToAdd[age]
        if (a != null) {
            userProfileFragment.age.setText(a.toString())
        }

        val h = userProfileToAdd[height]
        if (h != null) {
            userProfileFragment.height.setText(h.toString())
        }
    }


    private fun getIndex(spinner: Spinner, myString: String): Int {
        for (i in 0 until spinner.count) {
            if (spinner.getItemAtPosition(i).toString().equals(myString, ignoreCase = true)) {
                return i
            }
        }
        return 0
    }

    private fun enableUserProfileFragment(userProfileFragment: UserProfileFragmentData, shouldBe: Boolean){
        userProfileFragment.firstName.isEnabled = shouldBe;
        userProfileFragment.lastName.isEnabled = shouldBe;
        userProfileFragment.gender.isEnabled = shouldBe;
        userProfileFragment.age.isEnabled = shouldBe;
        userProfileFragment.height.isEnabled = shouldBe;
    }

    private suspend fun getUserProfilesFromDb(correlationId: String): MutableList<DocumentSnapshot>{
        return FirebaseUtils().firestoreDatabase.collection(userProfileDatabase)
            .whereEqualTo(DatabaseVariables.correlationId, correlationId)
            .get()
            .await()
            .documents
    }
}

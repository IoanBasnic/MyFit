package msa.myfit.domain

import android.widget.Button
import android.widget.EditText
import android.widget.Spinner

data class UserProfileFragmentData(
    val btnUpdateProfile: Button,
    val firstName: EditText,
    val lastName: EditText,
    val gender: Spinner,
    val age: EditText,
    val height: EditText
)

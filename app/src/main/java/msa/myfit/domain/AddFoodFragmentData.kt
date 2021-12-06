package msa.myfit.domain

import android.widget.Button
import android.widget.EditText
import java.util.*

data class AddFoodFragmentButton(
    val btnAddFood: Button
)
data class AddFoodFragmentFields(
    val calories: EditText,
    val foodName: EditText,
    val foodType: EditText
)
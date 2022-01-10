package msa.myfit.fragment

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import msa.myfit.R
import msa.myfit.domain.DatabaseVariables
import msa.myfit.firebase.FirebaseUtils
import java.util.concurrent.TimeUnit

class ScoreboardFragment(private val mainActivity: AppCompatActivity) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_scoreboard, container, false)
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
        tv1.setText(" User ")
        tv1.setTextColor(Color.DKGRAY)
        tbrow0.addView(tv1)
        val tv2 : TextView = TextView(activity)
        tv2.setText(" Score ")
        tv2.setTextColor(Color.DKGRAY)
        tbrow0.addView(tv2)
        val tv3 : TextView = TextView(activity)
        tv3.setText(" You ")
        tv3.setTextColor(Color.DKGRAY)
        tbrow0.addView(tv3)
        stk.addView(tbrow0)

        GlobalScope.launch {
            getOrderedPointsForAllUsersFromDB(
                correlationId,
                view
            )
        }
    }

    data class DisplayNameAndScorePoints(
        val displayName: String,
        val score: Int
    )

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun getOrderedPointsForAllUsersFromDB(
        userId: String,
        view: View
    ){
        val retrievedPoints = FirebaseUtils().firestoreDatabase.collection(DatabaseVariables.routeDatabase)
            .get()
            .await()
            .documents

        val retrievedUsers = FirebaseUtils().firestoreDatabase.collection(DatabaseVariables.userProfileDatabase)
            .get()
            .await()
            .documents

        TimeUnit.SECONDS.sleep(1L)
        val usersAndScores = hashMapOf<String, DisplayNameAndScorePoints>()

        retrievedUsers.forEach {
            val firstName = it.data!!.get(DatabaseVariables.firstName).toString().trim { it <= ' ' }
            val lastName = it.data!!.get(DatabaseVariables.lastName).toString().trim { it <= ' ' }
            val correlationId = it.data!!.get(DatabaseVariables.correlationId).toString()
            var displayName = ""

            if(firstName.isBlank() && lastName.isBlank()){
                displayName = FirebaseAuth.getInstance().currentUser!!.displayName!!
                if(displayName.isBlank())
                    displayName = FirebaseAuth.getInstance().currentUser!!.email!!
            } else if(firstName.isBlank()){
                displayName = lastName
            } else if(lastName.isBlank()){
                displayName = firstName
            } else{
                displayName = "$firstName $lastName"
            }

            usersAndScores.put(correlationId, DisplayNameAndScorePoints(displayName, 0))
        }

        retrievedPoints.forEach {
            val routeCorrelationId = it.data!!.get(DatabaseVariables.userId).toString()
            val routePoints = it.data!!.get(DatabaseVariables.pointsEarned).toString().toInt()
            val newDisplayNameAndScorePoints = DisplayNameAndScorePoints(usersAndScores.get(routeCorrelationId)!!.displayName, usersAndScores.get(routeCorrelationId)!!.score.plus(routePoints))

            usersAndScores.replace(routeCorrelationId, newDisplayNameAndScorePoints)
        }


        mainActivity.runOnUiThread {
            updateScoreboard(view, usersAndScores, userId)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateScoreboard(view: View, usersAndScores: HashMap<String, DisplayNameAndScorePoints>, currentUserId: String){
        val sortedUsersAndScores = usersAndScores.toList().sortedByDescending { (_, value) -> value.score }.toMap()

        var currentId = 1

        val table: TableLayout = view.findViewById(R.id.table_main)
        val childCount: Int = table.childCount
        if (childCount > 1) {
            table.removeViews(1, childCount - 1)
        }

        if(!sortedUsersAndScores.isNullOrEmpty()){
            sortedUsersAndScores.asIterable().forEach{
                val tbrow : TableRow = TableRow(activity)
                val t1v : TextView = TextView(activity)
                t1v.setText(currentId.toString())
                t1v.setTextColor(Color.DKGRAY)
                t1v.setGravity(Gravity.CENTER)
                tbrow.addView(t1v)

                val t2v : TextView = TextView(activity)
                val displayName: String = it.value.displayName
                t2v.setText(displayName)
                t2v.setTextColor(Color.DKGRAY)
                t2v.setGravity(Gravity.CENTER)
                tbrow.addView(t2v)

                val t3v : TextView = TextView(activity)
                val score = it.value.score
                t3v.setText(score.toString())
                t3v.setTextColor(Color.DKGRAY)
                t3v.setGravity(Gravity.CENTER)
                tbrow.addView(t3v)

                val t4v : TextView = TextView(activity)
                val isCurrentUser = it.key.equals(currentUserId)
                var toDisplay = ""
                if(isCurrentUser)
                    toDisplay = "YOU"

                t4v.setText(toDisplay)
                t4v.setTextColor(Color.DKGRAY)
                t4v.setGravity(Gravity.CENTER)
                tbrow.addView(t4v)
                table.addView(tbrow)

                currentId = currentId.plus(1)
            }
        }
    }

}
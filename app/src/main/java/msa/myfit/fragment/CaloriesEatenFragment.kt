package msa.myfit.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import msa.myfit.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CaloriesEatenFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CaloriesEatenFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

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
        fun newInstance(param1: String, param2: String) =
            CaloriesEatenFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val stk: TableLayout = view.findViewById(R.id.table_main);
        val tbrow0: TableRow = TableRow(activity);
        val tv0 : TextView = TextView(activity);
        tv0.setText(" No. ");
        tv0.setTextColor(Color.DKGRAY);
        tbrow0.addView(tv0);
        val tv1 : TextView = TextView(activity);
        tv1.setText(" Food ");
        tv1.setTextColor(Color.DKGRAY);
        tbrow0.addView(tv1);
        val tv2 : TextView= TextView(activity);
        tv2.setText(" Type ");
        tv2.setTextColor(Color.DKGRAY);
        tbrow0.addView(tv2);
        val tv3 : TextView= TextView(activity);
        tv3.setText("Kcal ");
        tv3.setTextColor(Color.DKGRAY);
        tbrow0.addView(tv3);
        stk.addView(tbrow0);
        for (i in 0 ..25) {
            val tbrow : TableRow = TableRow(activity);
            val t1v : TextView = TextView(activity);
            t1v.setText("" + i);
            t1v.setTextColor(Color.DKGRAY);
            t1v.setGravity(Gravity.CENTER);
            tbrow.addView(t1v);
            val t2v : TextView = TextView(activity);
            t2v.setText("Product " + i);
            t2v.setTextColor(Color.DKGRAY);
            t2v.setGravity(Gravity.CENTER);
            tbrow.addView(t2v);
            val t3v : TextView = TextView(activity);
            t3v.setText("Rs." + i);
            t3v.setTextColor(Color.DKGRAY);
            t3v.setGravity(Gravity.CENTER);
            tbrow.addView(t3v);
            val t4v : TextView = TextView(activity);
            t4v.setText("" + i * 15 / 32 * 10);
            t4v.setTextColor(Color.DKGRAY);
            t4v.setGravity(Gravity.CENTER);
            tbrow.addView(t4v);
            stk.addView(tbrow);
        }
    }
}

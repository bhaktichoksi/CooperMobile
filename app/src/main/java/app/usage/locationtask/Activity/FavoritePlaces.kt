package app.usage.locationtask.Activity

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.usage.locationtask.Adapter.UserListAdapter
import app.usage.locationtask.R
import app.usage.locationtask.database.DataBaseHelper
import app.usage.locationtask.database.UserModel
import java.util.ArrayList

class FavoritePlaces : AppCompatActivity() {
    private var tvNoDataFound: TextView?=null
    private lateinit var databaseHelper: DataBaseHelper
    private lateinit var mArrylist: ArrayList<UserModel>
    private  var rc_home:RecyclerView?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite_places)
        databaseHelper = DataBaseHelper(this@FavoritePlaces)
        mArrylist = ArrayList()
        rc_home = findViewById(R.id.rc_home)
        tvNoDataFound = findViewById(R.id.tvNoDataFound)

        val mLayoutManager = LinearLayoutManager(applicationContext)    // set recycleview as linearLayout
        rc_home!!.layoutManager = mLayoutManager
        rc_home!!.itemAnimator = DefaultItemAnimator()
        rc_home!!.setHasFixedSize(true)

    }


    override fun onResume() {
        super.onResume()
        val getDataFromSQLite = GetDataFromSQLite()
        getDataFromSQLite.execute()

    }

    inner class GetDataFromSQLite : AsyncTask<Void, Void, List<UserModel>>() {

        override fun doInBackground(vararg p0: Void?): List<UserModel> {
            return databaseHelper.getAllUser()         // Calling all the values from the data base
        }

        override fun onPostExecute(result: List<UserModel>?) {    //When Post method Execution Happen
            super.onPostExecute(result)
            mArrylist.clear()     //Clear List If Any Data In it
            mArrylist.addAll(result!!) //Add Data In List Come From DataBaseHelper

            if(mArrylist.size>0){
                // Showing In recycleView Listb Come From DataBase
                tvNoDataFound!!.visibility = View.GONE
                rc_home!!.adapter = UserListAdapter(
                    this@FavoritePlaces,
                    mArrylist, databaseHelper     //sending to constructure
                )
            }else{
                tvNoDataFound!!.visibility = View.VISIBLE
            }



        }
    }
}
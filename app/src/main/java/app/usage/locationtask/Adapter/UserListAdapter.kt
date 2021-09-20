package app.usage.locationtask.Adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.usage.locationtask.R
import app.usage.locationtask.database.DataBaseHelper
import app.usage.locationtask.database.UserModel


class UserListAdapter(
    val mContext: Activity,
    private val mArrayList: ArrayList<UserModel>,
    val databaseHelper: DataBaseHelper,
) :
    RecyclerView.Adapter<UserListAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UserListAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_home, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UserListAdapter.MyViewHolder, position: Int) {
        holder.tv_idlogin.text = (position + 1).toString()
//        holder.tv_Email.text = mArrayList[position].lat + "," + mArrayList[position].Long
        holder.tv_Email.text = mArrayList[position].pinCode
        holder.tv_name.text = mArrayList[position].name


        holder.ivdelete.setOnClickListener {

            databaseHelper.deleteUser(mArrayList[position].id.toString())   //calling function
            mArrayList.removeAt(position)   //removing the data from the data base using id and removed all the data of paticular position
            notifyDataSetChanged()  //notifie Adapter that in database data is changed

        }
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv_idlogin = itemView.findViewById<TextView>(R.id.id_login)
        val tv_Email = itemView.findViewById<TextView>(R.id.tv_Email)
        val tv_name = itemView.findViewById<TextView>(R.id.tv_name)
        val ivdelete = itemView.findViewById<ImageView>(R.id.iv_Delete)

    }

    override fun getItemCount(): Int {
        return mArrayList.size
    }

}
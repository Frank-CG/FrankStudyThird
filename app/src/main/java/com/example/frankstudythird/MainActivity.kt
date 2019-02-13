package com.example.frankstudythird

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CalendarView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.frankstudythird.Protocols.ApiService
import com.example.frankstudythird.model.MeetingModel
import com.example.frankstudythird.model.MeetingStreamModel
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private val tag: String = "MainActivity"

    private lateinit var mAdapter: ExampleTimeLineAdapter
    private val mDataList = ArrayList<MeetingModel>()
    private lateinit var mLayoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

//        setActivityTitle("Example")

        setDataListItems()
        initRecyclerView()

        val calendarView = findViewById<CalendarView>(R.id.frankCalendarView)
        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
//            val msg = "Selected date is " + dayOfMonth + "/" + (month + 1) + "/" + year
//            Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
            val start_d = Calendar.getInstance()
            start_d.set(Calendar.YEAR, year)
            start_d.set(Calendar.MONTH, month)
            start_d.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            var end_d = start_d.clone() as Calendar
            end_d.add(Calendar.DATE,1)
            val formater = SimpleDateFormat("yyyyMMdd")
            AsyncTaskExample(this).execute(formater.format(start_d.time),formater.format(end_d.time))
        }

    }

    private fun setDataListItems() {
        val start_d = Calendar.getInstance()
        var end_d = start_d.clone() as Calendar
        end_d.add(Calendar.DATE,1)
        val formater = SimpleDateFormat("yyyyMMdd")
        AsyncTaskExample(this).execute(formater.format(start_d.time),formater.format(end_d.time))
    }

    private fun initRecyclerView() {
        mLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView.layoutManager = mLayoutManager
        mAdapter = ExampleTimeLineAdapter(mDataList)
        recyclerView.adapter = mAdapter
        //var el = ApiService.GetEventList("20190210", "20190211")
        //Log.d("MainActivity",el.toString());
    }

    @SuppressLint("StaticFieldLeak")
    class AsyncTaskExample(private var activity: MainActivity?) : AsyncTask<String, String, ArrayList<MeetingModel>>() {

        override fun onPreExecute() {
            super.onPreExecute()
            var mDataList = ArrayList<MeetingModel>()
            var mLayoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
            activity?.recyclerView?.layoutManager = mLayoutManager
            var mAdapter = ExampleTimeLineAdapter(mDataList)
            activity?.recyclerView?.adapter = mAdapter
            activity?.MyprogressBar?.visibility = View.VISIBLE
        }

        override fun doInBackground(vararg params: String?): ArrayList<MeetingModel> {
            var jsonMeetingList = ApiService.GetEventList(params[0],params[1])
            var jsonMeetingArray = jsonMeetingList?.getJSONArray("ContentEntityDatas")
            var mMeetingList = ArrayList<MeetingModel>()
            if(jsonMeetingArray!!.length() > 0){
                for (i in 0..(jsonMeetingArray.length() -1)){
                    var jsonMeeting = jsonMeetingArray.getJSONObject(i)

                    mMeetingList.add(
                        MeetingModel(jsonMeeting.getString("Title"),
                            jsonMeeting.getString("IconUri"),
                            jsonMeeting.getString("EntityStatus"),
                            jsonMeeting.getString("EntityStatusDesc"),
                            jsonMeeting.getString("Location"),
                            jsonMeeting.getString("Description"),
                            jsonMeeting.getString("ThumbnailUri"),
                            jsonMeeting.getString("ScheduledStart"),
                            jsonMeeting.getString("ScheduledEnd"),
                            jsonMeeting.getString("HasArchiveStream"),
                            jsonMeeting.getString("ActualStart"),
                            jsonMeeting.getString("ActualEnd"),
                            jsonMeeting.getString("LastModifiedTime"),
                            jsonMeeting.getString("CommitteeId"),
                            jsonMeeting.getString("VenueId"),
                            jsonMeeting.getString("AssemblyProgress"),
                            jsonMeeting.getString("AssemblyStatus"),
                            jsonMeeting.getString("ForeignKey"),
                            jsonMeeting.getString("Id"),
                            jsonMeeting.getString("Tag"),
                            ArrayList<MeetingStreamModel>()
                        )
                    )
                }
            }
            return mMeetingList
        }

        override fun onPostExecute(result: ArrayList<MeetingModel>?) {
            super.onPostExecute(result)
            activity?.MyprogressBar?.visibility = View.INVISIBLE
            if (result == null) {
                Log.d(activity?.tag,"Network Error!")
            } else {
                if(result.size == 0){
                    Log.d(activity?.tag,"No Meetings!")
                    result.add(MeetingModel(
                        "No meetings found!",
                        "",
                        "",
                        "No Meetings",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        ArrayList()
                    ))
                }
                var mLayoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
                activity?.recyclerView?.layoutManager = mLayoutManager
                var mAdapter = ExampleTimeLineAdapter(result)
                activity?.recyclerView?.adapter = mAdapter
            }
        }
    }
}

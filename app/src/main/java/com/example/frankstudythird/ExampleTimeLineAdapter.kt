package com.example.frankstudythird

import android.content.Intent
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.frankstudythird.model.MeetingModel

import com.github.vipulasri.timelineview.sample.utils.DateTimeUtils
import com.github.vipulasri.timelineview.sample.utils.VectorDrawableUtils
import com.github.vipulasri.timelineview.TimelineView

import kotlinx.android.synthetic.main.item_timeline.view.*

/**
 * Created by Vipul Asri on 13-12-2018.
 */

class ExampleTimeLineAdapter(private val mFeedList: List<MeetingModel>) : RecyclerView.Adapter<ExampleTimeLineAdapter.TimeLineViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return TimelineView.getTimeLineViewType(position, itemCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeLineViewHolder {
        val  layoutInflater = LayoutInflater.from(parent.context)
        return TimeLineViewHolder(layoutInflater.inflate(R.layout.item_timeline, parent, false), viewType)
    }

    override fun onBindViewHolder(holder: TimeLineViewHolder, position: Int) {

        val timeLineModel = mFeedList[position]

        if (timeLineModel.scheduledStart.isNotEmpty()) {
            holder.date.visibility = View.VISIBLE
            var time_start = DateTimeUtils.parseDateTime(timeLineModel.scheduledStart, "yyyy-MM-dd'T'HH:mm:ss", "h:mm a")
            var time_end = DateTimeUtils.parseDateTime(timeLineModel.scheduledEnd,"yyyy-MM-dd'T'HH:mm:ss","h:mm a")
            holder.date.text = time_start + " - " + time_end
        } else {
            holder.date.visibility = View.GONE
        }

        if(timeLineModel.description.isNotEmpty()){
            holder.desc.visibility = View.VISIBLE
            holder.desc.text = timeLineModel.description
        }else{
            holder.desc.visibility = View.GONE
        }

        if(timeLineModel.entityStatusDesc.isNotEmpty()){
            holder.status.visibility = View.VISIBLE
            holder.status.text = timeLineModel.entityStatusDesc
            when {
                timeLineModel.entityStatusDesc.equals("In Progress") -> {
                    setMarker(holder, R.drawable.ic_marker_active, R.color.material_green_500)
                    holder.status.setBackgroundColor(holder.status.getResources().getColor(R.color.material_green_500))
                }
                timeLineModel.entityStatusDesc.equals("Adjourned") -> {
                    setMarker(holder, R.drawable.ic_marker_active, R.color.material_blue_500)
                    holder.status.setBackgroundColor(holder.status.getResources().getColor(R.color.material_blue_500))
                }
                timeLineModel.entityStatusDesc.equals("Cancelled") -> {
                    setMarker(holder, R.drawable.ic_marker_inactive, R.color.material_pink_500)
                    holder.status.setBackgroundColor(holder.status.getResources().getColor(R.color.material_pink_500))
                }
                timeLineModel.entityStatusDesc.equals("Not Started") -> {
                    setMarker(holder, R.drawable.ic_marker_inactive, R.color.material_purple_500)
                    holder.status.setBackgroundColor(holder.status.getResources().getColor(R.color.material_purple_500))
                }
                timeLineModel.entityStatusDesc.equals("No Meetings") -> {
                    setMarker(holder, R.drawable.ic_marker_inactive, R.color.material_red_500)
                }
                else -> {
                    setMarker(holder, R.drawable.ic_marker, R.color.material_grey_500)
                }
            }
        }else{
            holder.status.visibility = View.GONE
            setMarker(holder, R.drawable.ic_marker, R.color.material_grey_500)
        }



        holder.message.text = timeLineModel.title
        holder.card.setOnClickListener { v: View? ->
            Log.d("TimeLineClick",timeLineModel.title)
            val intent = Intent(holder.timeline.context, VideoActivity::class.java).apply {
                putExtra(EXTRA_MESSAGE, timeLineModel)
            }
            holder.timeline.context.startActivity(intent)
        }
    }

    private fun setMarker(holder: TimeLineViewHolder, drawableResId: Int, colorFilter: Int) {
        holder.timeline.marker = VectorDrawableUtils.getDrawable(holder.itemView.context, drawableResId, ContextCompat.getColor(holder.itemView.context, colorFilter))
    }

    override fun getItemCount() = mFeedList.size

    inner class TimeLineViewHolder(itemView: View, viewType: Int) : RecyclerView.ViewHolder(itemView) {

        val date = itemView.text_timeline_date
        val message = itemView.text_timeline_title
        val timeline = itemView.timeline
        var card = itemView.timeline_card
        var desc = card.text_timeline_desc
        var status = card.text_timeline_status

        init {
            timeline.initLine(viewType)
        }
    }

}

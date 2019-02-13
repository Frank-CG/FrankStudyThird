package com.example.frankstudythird

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.VideoView
import android.app.ProgressDialog
import android.content.pm.ActivityInfo
import android.media.MediaPlayer.OnPreparedListener
import android.graphics.PixelFormat
import android.media.MediaPlayer
import android.net.Uri
import android.os.AsyncTask
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import com.example.frankstudythird.Protocols.ApiService
import com.example.frankstudythird.model.MeetingModel
import com.example.frankstudythird.model.MeetingStreamModel


class VideoActivity : AppCompatActivity() {

    private var progressDialog: ProgressDialog? = null

    var videourl = ""
    var videoView: VideoView? = null
    lateinit var meetingModel: MeetingModel

    var audioOnly: String = "false"
    var language: String = "fl"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        videoView = findViewById(R.id.video_View);

        meetingModel = intent.getParcelableExtra<MeetingModel>(EXTRA_MESSAGE)
        Log.d("VideoActivity","Meeting Id="+meetingModel.id)
        AsyncGetURL(this).execute()
    }

    private fun GetUrl(lang: String, audioOnly: String, meeting: MeetingModel): String {
        var rst = ""
        var streams = meeting.streams
        if(streams.size > 0){
            for(i in 0..(streams.size-1)){
                var st = streams.get(i)
                if(st.audioOnly == audioOnly && st.lang == lang){
                    rst = st.url
                }
            }
        }
        return rst
    }

    private fun PlayVideo() {
        try {
            window.setFormat(PixelFormat.TRANSLUCENT)

            val mediaController = MediaController(this@VideoActivity)
            mediaController.setAnchorView(videoView)

            val video = Uri.parse(videourl)
            videoView?.setMediaController(mediaController)
            videoView?.setVideoURI(video)
            videoView?.requestFocus()
            videoView?.setVisibility(View.VISIBLE)

            if(videoView!!.isPlaying()){
                progressDialog?.dismiss()
            }

            videoView?.setOnPreparedListener(OnPreparedListener {
                progressDialog?.dismiss()
                videoView!!.start()
            })
            videoView?.setOnCompletionListener(MediaPlayer.OnCompletionListener {
                finish()
            })
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//
//            window.decorView.systemUiVisibility =
//                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
//                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//
//            videoView!!.fitsSystemWindows = true

        } catch (e: Exception) {
            progressDialog?.dismiss()
            println("Video Play Error :" + e.toString())
            finish()
        }

    }


    @SuppressLint("StaticFieldLeak")
    class AsyncGetURL(private var activity: VideoActivity?) : AsyncTask<String, String, String>() {

        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: String?): String {
            var rst = ""
            var meetingId = activity?.meetingModel?.id
            var jsonMeetingStreamList = ApiService.GetStreamData(meetingId)
            var jsonMeetingStreamArray = jsonMeetingStreamList.getJSONArray("Streams")
            var mStreamList = ArrayList<MeetingStreamModel>()
            if(jsonMeetingStreamArray!!.length() > 0){
                for (j in 0..(jsonMeetingStreamArray.length()-1)){
                    var jsonMeetingStream = jsonMeetingStreamArray.getJSONObject(j)
                    mStreamList.add(
                        MeetingStreamModel(
                            jsonMeetingStream.getString("GlobalEssenceFormatId"),
                            jsonMeetingStream.getString("IsLive"),
                            jsonMeetingStream.getString("Enabled"),
                            jsonMeetingStream.getString("AudioOnly"),
                            jsonMeetingStream.getString("VideoIndex"),
                            jsonMeetingStream.getString("AudioIndex"),
                            jsonMeetingStream.getString("StreamFormatId"),
                            jsonMeetingStream.getString("Url"),
                            jsonMeetingStream.getString("Lang"),
                            jsonMeetingStream.getString("StreamAssemblerList"),
                            jsonMeetingStream.getString("PreRoll"),
                            jsonMeetingStream.getString("Duration"),
                            jsonMeetingStream.getString("Id"),
                            jsonMeetingStream.getString("Tag")
                        )
                    )
                    if(jsonMeetingStream.getString("AudioOnly") == activity?.audioOnly &&
                        jsonMeetingStream.getString("Lang") == activity?.language){
                        rst = jsonMeetingStream.getString("Url")
                    }
                }
                activity?.meetingModel?.streams = mStreamList
            }
            return rst
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            println("URL="+result)
            if(result != null && result != ""){
                activity?.videourl = result
                activity?.progressDialog = ProgressDialog.show(activity, "", "Buffering video...", true);
                activity?.progressDialog?.setCancelable(false);
                println("progressDialog!")
                activity?.PlayVideo();
            }else{
                activity?.videourl = ""
                println("Video Play Error: No Resource found!")
                activity?.finish()
            }
        }
    }

}

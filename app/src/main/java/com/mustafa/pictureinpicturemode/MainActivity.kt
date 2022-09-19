package com.mustafa.pictureinpicturemode

import android.app.PendingIntent
import android.app.PictureInPictureParams
import android.app.RemoteAction
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Rational
import android.widget.VideoView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.toAndroidRect
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.mustafa.pictureinpicturemode.ui.theme.PictureInPictureModeTheme

class MainActivity : ComponentActivity() {

    private val isPipPicture by lazy {
       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
           packageManager.hasSystemFeature(
               PackageManager.FEATURE_PICTURE_IN_PICTURE
           )
       }else{
           false
       }
    }

    private  var videoViewBounds = android.graphics.Rect()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PictureInPictureModeTheme {
                AndroidView(
                    factory = {
                        VideoView(it,null).apply {
                            setVideoURI(Uri.parse("android.resource://$packageName/${R.raw.sample}"))
                            start()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                        .onGloballyPositioned {
                            videoViewBounds = it.boundsInWindow().toAndroidRect()
                        }

                )

            }
        }
    }

    private fun uptadePictureParam() : PictureInPictureParams{
        return  PictureInPictureParams.Builder()
            .setSourceRectHint(videoViewBounds)
            .setAspectRatio(Rational(16,9))
            .setActions(
                listOf(
                    RemoteAction(
                        Icon.createWithResource(applicationContext,R.drawable.ic_baby),
                        "Baby Click",
                        "Baby Click",
                        PendingIntent.getBroadcast(applicationContext,1,Intent(applicationContext,MyReceiver::class.java),PendingIntent.FLAG_IMMUTABLE)
                    )
                )
            )
            .build()
    }



    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        if (!isPipPicture){
            return
        }
         uptadePictureParam()?.let {
             enterPictureInPictureMode(it)
         }

    }

}

class  MyReceiver: BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        println("Click on ")
    }

}


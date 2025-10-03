package com.example.testimmersiv.player

import android.net.Uri
import android.os.Bundle
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.ui.PlayerView
import androidx.xr.compose.spatial.Subspace

class PlayerActivity : ComponentActivity() {

    private var player: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PlayerScreen("https://hls-video-samples.r2.immersiv.cloud/bbb/master.m3u8")
        }
    }

    @OptIn(UnstableApi::class)
    @Composable
    fun PlayerScreen(url : String) {
        // Crée le player et prépare le flux
        val context = LocalContext.current

        val hlsDataSourceFactory = DefaultHttpDataSource.Factory()
        val uri = Uri.Builder().encodedPath(url).build()
        val hlsMediaItem = MediaItem.Builder().setUri(uri).build()
        val mediaSource =
            HlsMediaSource.Factory(hlsDataSourceFactory).createMediaSource(hlsMediaItem)
        player = ExoPlayer.Builder(context).build()
        player?.setMediaSource(mediaSource)
        player?.prepare()
        player?.play()
        // Intégration du PlayerView dans Compose
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = this@PlayerActivity.player
                    layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                    )
                }
            },
            modifier = Modifier
                .fillMaxSize()
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
    }
}
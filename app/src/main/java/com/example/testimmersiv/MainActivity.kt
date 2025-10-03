package com.example.testimmersiv

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.OptIn
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.ui.PlayerView
import androidx.xr.arcore.Anchor
import androidx.xr.compose.platform.LocalHasXrSpatialFeature
import androidx.xr.compose.platform.LocalSpatialCapabilities
import androidx.xr.compose.platform.LocalSpatialConfiguration
import androidx.xr.compose.spatial.EdgeOffset
import androidx.xr.compose.spatial.Orbiter
import androidx.xr.compose.spatial.OrbiterEdge
import androidx.xr.compose.spatial.Subspace
import androidx.xr.compose.subspace.SpatialColumn
import androidx.xr.compose.subspace.SpatialPanel
import androidx.xr.compose.subspace.SpatialRow
import androidx.xr.compose.subspace.layout.SpatialRoundedCornerShape
import androidx.xr.compose.subspace.layout.SubspaceModifier
import androidx.xr.compose.subspace.layout.alpha
import androidx.xr.compose.subspace.layout.fillMaxHeight
import androidx.xr.compose.subspace.layout.height
import androidx.xr.compose.subspace.layout.movable
import androidx.xr.compose.subspace.layout.padding
import androidx.xr.compose.subspace.layout.resizable
import androidx.xr.compose.subspace.layout.width
import com.example.testimmersiv.ui.theme.TestimmersivTheme
import com.example.testimmersiv.ui.theme.view.HomeView
import com.example.testimmersiv.ui.theme.view.MainViewModel
import com.google.ar.sceneform.AnchorNode
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private fun onClickVids(url: String) {
        val intent = Intent()
        intent.component =
            ComponentName("com.example.testimmersiv_player",
                "com.example.testimmersiv_player.PlayerActivity")
        intent.action = Intent.ACTION_SEND
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra(Intent.EXTRA_TEXT, url)
        intent.setType("text/plain")
        if (intent.resolveActivity(packageManager) != null)
            startActivity(intent)
    }




    @SuppressLint("QueryPermissionsNeeded")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TestimmersivTheme {
                val viewModel = ViewModelProvider(
                    this,
                    object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return MainViewModel(AppModule.getPostsUseCase) as T
                        }
                    }
                )[MainViewModel::class.java]
                val spatialConfiguration = LocalSpatialConfiguration.current

                val clickOnVid : (String) -> Unit = { url ->
                    onClickVids(url)
                }

                if (LocalSpatialCapabilities.current.isSpatialUiEnabled) {
                    Subspace {
                        MySpatialContent(
                            onRequestHomeSpaceMode = spatialConfiguration::requestHomeSpaceMode,
                            viewModel = viewModel,
                            clickOnVid
                        )
                    }
                } else {
                    WindowManager(
                        onRequestFullSpaceMode = spatialConfiguration::requestFullSpaceMode,
                        viewModel = viewModel,
                        clickOnVid
                    )
                }
            }
        }
    }

    @Composable
    fun WindowManager(onRequestFullSpaceMode: () -> Unit, viewModel: MainViewModel, onClick: (String) -> Unit) {
        Row(Modifier.background(colorResource(R.color.bg_gradient)).fillMaxWidth()) {
            Surface(
                Modifier
                    .background(Color.Transparent)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
            ) {
                Column {
                    My2DContent(
                        onRequestFullSpaceMode = onRequestFullSpaceMode,
                        viewModel = viewModel,
                        onClick = onClick
                    )
                }
            }
        }
    }

    @SuppressLint("RestrictedApi")
    @Composable
    fun MySpatialContent(onRequestHomeSpaceMode: () -> Unit, viewModel: MainViewModel, onClick: (String) -> Unit) {
        Log.e("Main", "Display Spatial Panel")

        SpatialPanel(SubspaceModifier.width(1280.dp).height(800.dp).resizable().movable()) {
            Surface {
                HomeView(viewModel, onClick)
            }
            Orbiter(
                position = OrbiterEdge.Top,
                offset = EdgeOffset.inner(offset = 20.dp),
                alignment = Alignment.End,
                shape = SpatialRoundedCornerShape(CornerSize(28.dp))
            ) {
                HomeSpaceModeIconButton(
                    onClick = onRequestHomeSpaceMode,
                    modifier = Modifier.size(56.dp)
                )
            }

        }
    }
}

@SuppressLint("RestrictedApi")
@Composable
fun My2DContent(onRequestFullSpaceMode: () -> Unit, viewModel: MainViewModel, onClick: (String) -> Unit) {
    Surface {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            HomeView(viewModel, onClick)
            if (LocalHasXrSpatialFeature.current) {
                FullSpaceModeIconButton(
                    onClick = onRequestFullSpaceMode,
                    modifier = Modifier.padding(32.dp)
                )
            }
        }
    }
}

@Composable
fun FullSpaceModeIconButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    IconButton(onClick = onClick, modifier = modifier) {
        Icon(
            painter = painterResource(id = R.drawable.ic_full_space_mode_switch),
            contentDescription = stringResource(R.string.switch_to_full_space_mode)
        )
    }
}

@Composable
fun HomeSpaceModeIconButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    FilledTonalIconButton(onClick = onClick, modifier = modifier) {
        Icon(
            painter = painterResource(id = R.drawable.ic_home_space_mode_switch),
            contentDescription = stringResource(R.string.switch_to_home_space_mode)
        )
    }
}
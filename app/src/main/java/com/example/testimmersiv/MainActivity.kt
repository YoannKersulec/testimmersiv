package com.example.testimmersiv

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.xr.compose.platform.LocalHasXrSpatialFeature
import androidx.xr.compose.platform.LocalSpatialCapabilities
import androidx.xr.compose.platform.LocalSpatialConfiguration
import androidx.xr.compose.spatial.EdgeOffset
import androidx.xr.compose.spatial.Orbiter
import androidx.xr.compose.spatial.OrbiterEdge
import androidx.xr.compose.spatial.Subspace
import androidx.xr.compose.subspace.SpatialPanel
import androidx.xr.compose.subspace.layout.SpatialRoundedCornerShape
import androidx.xr.compose.subspace.layout.SubspaceModifier
import androidx.xr.compose.subspace.layout.height
import androidx.xr.compose.subspace.layout.movable
import androidx.xr.compose.subspace.layout.resizable
import androidx.xr.compose.subspace.layout.width
import com.example.testimmersiv.data.HlsApi
import com.example.testimmersiv.data.HlsRepositoryImpl
import com.example.testimmersiv.domain.HlsRepository
import com.example.testimmersiv.player.PlayerActivity
import com.example.testimmersiv.ui.theme.TestimmersivTheme
import com.example.testimmersiv.ui.theme.view.HomeView
import com.example.testimmersiv.ui.theme.view.MainViewModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {


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
                    val intent = Intent(this@MainActivity, PlayerActivity::class.java)
                    startActivity(intent)
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
                    My2DContent(
                        onRequestFullSpaceMode = spatialConfiguration::requestFullSpaceMode,
                        viewModel = viewModel,
                        clickOnVid
                    )
                }
            }
        }
    }
}

@SuppressLint("RestrictedApi")
@Composable
fun MySpatialContent(onRequestHomeSpaceMode: () -> Unit, viewModel: MainViewModel, onClick: (String) -> Unit) {
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
fun MainContent(modifier: Modifier = Modifier) {
    Text(text = stringResource(R.string.hello_android_xr), modifier = modifier)
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

@Preview(showBackground = true)
@Composable
fun FullSpaceModeButtonPreview() {
    TestimmersivTheme {
        FullSpaceModeIconButton(onClick = {})
    }
}

@PreviewLightDark
@Composable
fun HomeSpaceModeButtonPreview() {
    TestimmersivTheme {
        HomeSpaceModeIconButton(onClick = {})
    }
}
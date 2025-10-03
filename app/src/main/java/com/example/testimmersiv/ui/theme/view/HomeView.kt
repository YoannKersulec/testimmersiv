package com.example.testimmersiv.ui.theme.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.testimmersiv.FullSpaceModeIconButton
import com.example.testimmersiv.ui.theme.TestimmersivTheme

@Composable
fun HomeView(viewModel: MainViewModel) {

    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadVids()
    }
    when {
        state.isLoading -> {
            CircularProgressIndicator()
        }
        state.error != null -> {
            Text("Erreur : ${state.error}")
        }
        else -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(16.dp)
            ) {
                items(state.vids) { vid ->
                    Card(
                        modifier = Modifier.fillMaxWidth(0.5f).padding(vertical = 8.dp),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(10.dp, 0.dp)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center,
                        ) {
                            AsyncImage(
                                modifier = Modifier.size(50.dp).align(Alignment.CenterStart),
                                model = vid.thumbnail,
                                contentDescription = "Translated description of what the image contains"
                            )
                            Text(
                                text = vid.title,
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }
            }
        }
    }
}
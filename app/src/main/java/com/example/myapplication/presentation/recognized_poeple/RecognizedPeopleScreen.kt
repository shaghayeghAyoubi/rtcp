package com.example.myapplication.presentation.recognized_poeple

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.domain.model.RecognizedPerson
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*

import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.presentation.event.base64ToBitmap
import com.example.myapplication.presentation.localization.LocalizationViewModel
import com.example.myapplication.presentation.localization.strings.Strings
import com.example.myapplication.presentation.localization.strings.StringsFa
import kotlinx.coroutines.flow.collect


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecognizedPeopleScreen(
    viewModel: RecognizedPeopleViewModel = hiltViewModel() ,
    localizationViewModel: LocalizationViewModel = hiltViewModel()
) {
    val state by viewModel.recognizedPeopleState.collectAsState()
    val listState = rememberLazyListState()
    val strings by localizationViewModel.strings.collectAsState()

    // Infinite scroll trigger
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleIndex ->
                val total = state.recognizedPeople.size
                if (lastVisibleIndex == total - 1 && !state.isLoading && !state.endReached) {
                    viewModel.fetchNextPage()
                }
            }
    }

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.isLoading && state.recognizedPeople.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.error != null && state.recognizedPeople.isEmpty()) {
                Text(
                    text = "Error: ${state.error}",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                ) {
                    items(state.recognizedPeople) { person ->
                        RecognizedPersonItem(person, strings)
                    }

                    if (state.isLoading && state.recognizedPeople.isNotEmpty()) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RecognizedPersonItem(person: RecognizedPerson, strings: Strings) {
    val layoutDirection = when (strings) {
        is StringsFa -> LayoutDirection.Rtl
        else -> LayoutDirection.Ltr
    }
    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(modifier = Modifier.padding(12.dp)) {
                val bitmap = remember(person.croppedFaceUrl) {
                    base64ToBitmap(person.croppedFaceUrl)
                }

                if (bitmap != null) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Recognized Face",
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(Color.Gray, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No Img", color = Color.White, fontSize = 10.sp)
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text("📷 ${person.camera.title}", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        "📅 ${person.recognizedDate.take(16)}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        "🔍 ${strings.similarity}: ${person.similarity}%",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

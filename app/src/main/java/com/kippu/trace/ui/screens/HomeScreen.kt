package com.kippu.trace.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kippu.trace.model.DateEvent
import com.kippu.trace.ui.components.NormalEventCard
import com.kippu.trace.ui.components.PinnedEventCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    events: List<DateEvent>,
    onAddClick: () -> Unit,
    onEventClick: (DateEvent) -> Unit
) {
    val pinnedEvent = events.find { it.isPinned }
    val otherEvents = events.filter { !it.isPinned }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "TimeTrace",
                            style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.secondary)
                        )
                        Text(
                            text = "时间轴",
                            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onAddClick) {
                        Icon(Icons.Default.Add, contentDescription = "Add")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { innerPadding ->
        if (events.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "点击右上角 + 开始记录时间",
                    style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.secondary)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Pinned Card (if any)
                pinnedEvent?.let {
                    item {
                        PinnedEventCard(
                            event = it,
                            onClick = { onEventClick(it) }
                        )
                    }
                }

                // Normal Cards List
                items(otherEvents) { event ->
                    NormalEventCard(
                        event = event,
                        onClick = { onEventClick(event) }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF8F9FA)
@Composable
fun HomeScreenPreview() {
    com.kippu.trace.ui.theme.KIPPU_TraceTheme {
        HomeScreen(events = emptyList(), onAddClick = {}, onEventClick = {})
    }
}

package com.kippu.trace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kippu.trace.ui.screens.HomeScreen
import com.kippu.trace.ui.theme.KIPPU_TraceTheme
import com.kippu.trace.viewmodel.EventViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KIPPU_TraceTheme {
                MainApp()
            }
        }
    }
}

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Home : Screen("home", "日子", Icons.Default.DateRange)
    object Detail : Screen("detail", "详情", Icons.Default.Info)
    object Settings : Screen("settings", "我的", Icons.Default.Settings)
    object Editor : Screen("editor", "编辑", Icons.Default.Add)
}

@Composable
fun MainApp(eventViewModel: EventViewModel = viewModel()) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    val events by eventViewModel.allEvents.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (currentDestination?.route in listOf(Screen.Home.route, Screen.Detail.route, Screen.Settings.route)) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    val items = listOf(Screen.Home, Screen.Detail, Screen.Settings)
                    items.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.label) },
                            label = { Text(screen.label) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(if (currentDestination?.route == Screen.Editor.route) PaddingValues(0.dp) else innerPadding)
        ) {
            composable(route = Screen.Home.route) {
                HomeScreen(
                    events = events,
                    onAddClick = { navController.navigate(Screen.Editor.route) },
                    onEventClick = { /* TODO: Open Detail */ }
                )
            }
            composable(route = Screen.Editor.route) {
                com.kippu.trace.ui.screens.EditorScreen(
                    onDismiss = { navController.popBackStack() },
                    onSave = { newEvent ->
                        eventViewModel.addEvent(newEvent)
                        navController.popBackStack()
                    }
                )
            }
            composable(route = Screen.Detail.route) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Text(text = "详情页流转", modifier = Modifier.padding(16.dp))
                }
            }
            composable(route = Screen.Settings.route) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Text(text = "设置", modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainAppPreview() {
    KIPPU_TraceTheme {
        MainApp()
    }
}

package com.example.yourapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            YourApp()
        }
    }
}

@Serializable
data class ItemData(
    val title: String,
    val description: String
)

@Composable
fun YourApp() {
    val navController = rememberNavController()
    var currentScreen by remember { mutableStateOf("home") }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = currentScreen == "home",
                    onClick = {
                        currentScreen = "home"
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }
                    },
                    label = { Text("Home") },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") }
                )
                NavigationBarItem(
                    selected = currentScreen == "detail",
                    onClick = {
                        val dummy = Json.encodeToString(ItemData("Contoh", "Data contoh dari nav bar"))
                        currentScreen = "detail"
                        navController.navigate("detail/$dummy")
                    },
                    label = { Text("Detail") },
                    icon = { Icon(Icons.Default.Info, contentDescription = "Detail") }
                )
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            composable("home") {
                currentScreen = "home"
                HomeScreen(navController)
            }
            composable(
                "detail/{jsonData}",
                arguments = listOf(navArgument("jsonData") { type = NavType.StringType })
            ) { backStackEntry ->
                val jsonData = backStackEntry.arguments?.getString("jsonData")
                currentScreen = "detail"
                DetailScreen(navController, jsonData)
            }
        }
    }
}

@Composable
fun HomeScreen(navController: NavHostController) {
    var title by remember { mutableStateOf(TextFieldValue("")) }
    var description by remember { mutableStateOf(TextFieldValue("")) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Home Screen", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                val data = ItemData(title.text, description.text)
                val json = Json.encodeToString(data)
                navController.navigate("detail/$json")
            }
        ) {
            Text("Kirim ke Detail Screen")
        }
    }
}

@Composable
fun DetailScreen(navController: NavHostController, jsonData: String?) {
    val item = jsonData?.let { Json.decodeFromString<ItemData>(it) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Detail Screen", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        item?.let {
            Text("Judul: ${it.title}", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Deskripsi: ${it.description}", style = MaterialTheme.typography.bodyLarge)
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = { navController.popBackStack() }) {
            Text("Kembali ke Home")
        }
    }
}

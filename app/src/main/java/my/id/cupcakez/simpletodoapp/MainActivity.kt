package my.id.cupcakez.simpletodoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import my.id.cupcakez.simpletodoapp.auth.LoginScreen
import my.id.cupcakez.simpletodoapp.auth.SignUpScreen
import my.id.cupcakez.simpletodoapp.todo.TodoScreen
import my.id.cupcakez.simpletodoapp.ui.theme.SimpleTodoAppTheme
import my.id.cupcakez.simpletodoapp.user.UserScreen

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val viewModel : MainViewModel = hiltViewModel<MainViewModel>()
            navController.addOnDestinationChangedListener {_ , destination,_  ->
                destination?.route.let {
                    viewModel.currentRoute.value = it.toString()
                    when (it) {
                        "user", "todo" -> viewModel.showBottomBar.value = true
                        else -> viewModel.showBottomBar.value = false
                    }
                }
            }

            SimpleTodoAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize(), bottomBar = {
                    if (viewModel.showBottomBar.value) {
                        BottomAppBar(
                            modifier = Modifier.height(100.dp),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column (
                                    modifier = Modifier.clickable(onClick = {
                                        navController.navigate("todo")
                                    }).weight(1f),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Home,
                                        contentDescription = "Home",
                                    )
                                    Text("Home")
                                }
                                Column (
                                    modifier = Modifier.clickable(onClick = {
                                        navController.navigate("user")
                                    }).weight(1f),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "Profile"
                                    )
                                    Text("Profile")
                                }
                            }
                        }
                    }
                }) {
                    NavHost(navController = navController, startDestination = "todo", modifier = Modifier.padding(it)) {
                        composable("login") {
                            LoginScreen(navController)
                        }
                        composable("signup"){
                            SignUpScreen(navController)
                        }
                        composable("todo"){
                            TodoScreen(navController)
                        }
                        composable("user"){
                            UserScreen(navController)
                        }
                    }
                }
            }
        }
    }
}

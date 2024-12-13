package my.id.cupcakez.simpletodoapp.user

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import my.id.cupcakez.simpletodoapp.R
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import my.id.cupcakez.simpletodoapp.data.User


@Composable
fun UserScreen(
    navController: NavController,
) {
    val viewModel: UserViewModel = hiltViewModel()
    val user by viewModel.user.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.fetchLoggedInUser()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Edit, contentDescription = "Edit Profile")
            }
        }
    ) { paddingValues ->
        user?.let { loggedInUser ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                AsyncImage(
                    model = loggedInUser.photoProfile,
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(128.dp)
                        .clip(CircleShape)
                        .background(Color.Gray),
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = loggedInUser.name,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Text(
                    text = loggedInUser.email,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        viewModel.logout {
                            navController.navigate("login") {
                                popUpTo("user") { inclusive = true }
                            }
                        }
                    }
                ) {
                    Text("Log Out")
                }

                if (showDialog) {
                    UserDialog(
                        user = loggedInUser,
                        onDismiss = { showDialog = false },
                        onSave = { updatedUser, photoUri ->
                            if (photoUri != null) {
                                viewModel.uploadPhoto(photoUri) { photoUrl ->
                                    if (photoUrl != null) {
                                        val newUser = updatedUser.copy(photoProfile = photoUrl)
                                        viewModel.updateLoggedInUser(newUser) { success ->
                                            if (success) {
                                                viewModel.fetchLoggedInUser()
                                                showDialog = false
                                            }
                                        }
                                    }
                                }
                            } else {
                                viewModel.updateLoggedInUser(updatedUser) { success ->
                                    if (success) {
                                        viewModel.fetchLoggedInUser()
                                        showDialog = false
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun UserDialog(
    user: User?,
    onDismiss: () -> Unit,
    onSave: (User, Uri?) -> Unit
) {
    var name by remember { mutableStateOf(user?.name) }
    var photoUri by remember { mutableStateOf<Uri?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            photoUri = uri
        }
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Edit Profile") },
        text = {
            Column {
                TextField(
                    value = name.orEmpty(),
                    onValueChange = { name = it },
                    label = { Text("Name") })
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = {
                    photoPickerLauncher.launch("image/*")
                }) {
                    Text("Upload Photo")
                }
                Spacer(modifier = Modifier.height(8.dp))
                photoUri?.let {
                    Text(text = "Selected photo: $it")
                } ?: run {
                    Text(text = "No photo selected")
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val updatedUser = user?.copy(name = name.orEmpty())
                onSave(updatedUser!!, photoUri)
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}






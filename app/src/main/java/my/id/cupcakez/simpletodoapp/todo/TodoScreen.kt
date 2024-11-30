package my.id.cupcakez.simpletodoapp.todo

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import my.id.cupcakez.simpletodoapp.data.Todo
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.text.style.TextDecoration

@Composable
fun TodoScreen(
    navController: NavController
) {

    val viewModel = hiltViewModel<TodoViewModel>()

    val todos by viewModel.todos.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var currentTodo by remember { mutableStateOf<Todo?>(null) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    val isUserLoggedIn by viewModel.isUserLoggedIn.collectAsState()

    // Check user authentication status
    LaunchedEffect(isUserLoggedIn) {
        if (!isUserLoggedIn) {
            navController.navigate("login") {
                popUpTo("todo") { inclusive = true }
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                currentTodo = null // Create new todo
                showDialog = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add Todo")
            }
        }
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(it)) {
            items(todos) { todo ->
                TodoItem(
                    todo = todo,
                    onEdit = {
                        currentTodo = it
                        showDialog = true
                    },
                    onDelete = {
                        currentTodo = it
                        showDeleteConfirmation = true
                    },
                    onCheck = { updatedTodo ->
                        viewModel.addOrUpdate(updatedTodo)
                    }
                )
            }
        }

        if (showDialog) {
            TodoDialog(
                todo = currentTodo,
                onDismiss = { showDialog = false },
                onSave = {
                    viewModel.addOrUpdate(it)
                    showDialog = false
                }
            )
        }

        if (showDeleteConfirmation && currentTodo != null) {
            DeleteConfirmationDialog(
                onConfirm = {
                    viewModel.delete(currentTodo!!.id)
                    showDeleteConfirmation = false
                },
                onDismiss = {
                    showDeleteConfirmation = false
                }
            )
        }
    }
}


@Composable
fun TodoItem(
    todo: Todo,
    onEdit: (Todo) -> Unit,
    onDelete: (Todo) -> Unit,
    onCheck: (Todo) -> Unit // Add a callback for when the checkbox is toggled
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox to mark the todo as done
            Checkbox(
                checked = todo.isDone,
                onCheckedChange = { onCheck(todo.copy(isDone = !todo.isDone)) }
            )
            Column(modifier = Modifier.weight(1f)) {
                // Strike-through text if the todo is done
                Text(
                    text = todo.title.toString(),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        textDecoration = if (todo.isDone) TextDecoration.LineThrough else TextDecoration.None
                    )
                )
                Text(
                    text = todo.description.toString(),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        textDecoration = if (todo.isDone) TextDecoration.LineThrough else TextDecoration.None
                    )
                )
                Text(
                    text = todo.date.toString(),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Remove edit and delete buttons if the todo is done
            if (!todo.isDone) {
                IconButton(onClick = { onEdit(todo) }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }
                IconButton(onClick = { onDelete(todo) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }
        }
    }
}

@Composable
fun DeleteConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirm Deletion") },
        text = { Text("Are you sure you want to delete this todo? This action cannot be undone.") },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Yes")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("No")
            }
        }
    )
}


@Composable
fun TodoDialog(
    todo: Todo? = null,
    onDismiss: () -> Unit,
    onSave: (Todo) -> Unit
) {
    var title by remember { mutableStateOf(todo?.title ?: "") }
    var description by remember { mutableStateOf(todo?.description ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = if (todo == null) "Add Todo" else "Edit Todo") },
        text = {
            Column {
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") }
                )
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") }
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(
                    Todo(
                        id = todo?.id ?: UUID.randomUUID().toString(),
                        title = title,
                        description = description,
                        isDone = todo?.isDone ?: false,
                        date = todo?.date ?: SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                    )
                )
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

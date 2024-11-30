package my.id.cupcakez.simpletodoapp.todo

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import my.id.cupcakez.simpletodoapp.data.Todo
import javax.inject.Inject

class TodoRepository @Inject constructor(
    private val db: FirebaseFirestore
) {
    fun getTodos(): Flow<List<Todo>> = callbackFlow {
        val listener = db.collection("todos")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    println("Error fetching todos: $error")
                    close(error)
                    return@addSnapshotListener
                }
                val todos = snapshot?.documents?.mapNotNull { it.toObject(Todo::class.java) } ?: emptyList()
                trySend(todos).isSuccess
            }
        awaitClose { listener.remove() }
    }

    suspend fun addOrUpdateTodo(todo: Todo) {
        db.collection("todos").document(todo.id).set(todo).await()
    }

    suspend fun deleteTodo(id: String) {
        db.collection("todos").document(id).delete().await()
    }
}

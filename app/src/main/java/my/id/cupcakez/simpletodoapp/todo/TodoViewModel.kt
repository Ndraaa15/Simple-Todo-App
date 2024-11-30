package my.id.cupcakez.simpletodoapp.todo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import my.id.cupcakez.simpletodoapp.data.Todo
import javax.inject.Inject

@HiltViewModel
class TodoViewModel @Inject constructor(
    private val repository: TodoRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _todos = MutableStateFlow<List<Todo>>(emptyList())
    val todos: StateFlow<List<Todo>> = _todos

    private val _isUserLoggedIn = MutableStateFlow(auth.currentUser != null)
    val isUserLoggedIn: StateFlow<Boolean> = _isUserLoggedIn

    init {
        checkUserAuthentication()
        if (_isUserLoggedIn.value) {
            viewModelScope.launch {
                repository.getTodos().collect { _todos.value = it }
            }
        }
    }

    fun checkUserAuthentication() {
        _isUserLoggedIn.value = auth.currentUser != null
    }

    fun addOrUpdate(todo: Todo) {
        viewModelScope.launch {
            repository.addOrUpdateTodo(todo)
        }
    }

    fun delete(todoId: String) {
        viewModelScope.launch {
            repository.deleteTodo(todoId)
        }
    }
}

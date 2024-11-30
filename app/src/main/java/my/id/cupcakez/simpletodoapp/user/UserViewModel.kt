package my.id.cupcakez.simpletodoapp.user

import android.net.Uri
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import my.id.cupcakez.simpletodoapp.data.User
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    fun fetchLoggedInUser() {
        userRepository.getLoggedInUserData { fetchedUser ->
            _user.value = fetchedUser
        }
    }

    fun updateLoggedInUser(user: User, onComplete: (Boolean) -> Unit) {
        userRepository.updateLoggedInUserData(user, onComplete)
    }

    fun uploadPhoto(uri: Uri, onResult: (String?) -> Unit) {
        userRepository.uploadPhoto(uri, onResult)
    }

    fun logout(onComplete: () -> Unit) {
        userRepository.logout()
        _user.value = null
        onComplete()
    }
}

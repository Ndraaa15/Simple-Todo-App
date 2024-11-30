package my.id.cupcakez.simpletodoapp

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor () : ViewModel(){
    val currentRoute = mutableStateOf("home")
    val showBottomBar = mutableStateOf(false)
}
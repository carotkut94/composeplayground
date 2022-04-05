package com.death.composeplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.death.composeplayer.ui.theme.ComposePlayerTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


data class User(val name: String, val income: Int)

data class ListOfItems(val items: List<User>)

data class UIState(
    val userItems: ListOfItems = ListOfItems(items = emptyList()),
    val isLoading: Boolean = true
)

class SampleViewModel : ViewModel() {

    private val _state = MutableStateFlow(UIState())
    val oState = _state.asStateFlow()

    fun toggleLoading(){
        _state.update{
            _state.value.copy(isLoading = !_state.value.isLoading)
        }
    }

    fun addUser(){
        val user = User(getRandomString(10), 1000)
        val items = _state.value.userItems.items.toMutableList().apply {
            add(user)
        }.toList()
        _state.update{
            _state.value.copy(userItems = ListOfItems(items), isLoading = false)
        }
    }

    fun getRandomString(length: Int) : String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }
}




class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val vm: SampleViewModel by viewModels()
        setContent {

            ComposePlayerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Column() {
                        Row {
                            Button(onClick = {
                                vm.toggleLoading()
                            }) {
                                Text(text = "Toggle Loading")
                            }
                            Button(onClick = {
                                vm.addUser()
                            }) {
                                Text(text = "Add User")
                            }
                        }
                        MainScreen(vm = vm)
                    }
                }
            }
        }
    }
}

@Composable
fun MainScreen(vm: SampleViewModel) {
    var recompCount by remember {
        mutableStateOf(0)
    }

    SideEffect {
        recompCount += 1
    }

    val uiState = vm.oState.collectAsState()

    Box {
        if (uiState.value.isLoading) {
            CircularProgressIndicator()
        }

        LazyColumn{
            item{
                Text("Composition count $recompCount")
            }
            items(uiState.value.userItems.items, key = {item->item.name}){ item->
                ListItemView(item = item)
            }
        }
    }
}

@Composable
fun ListItemView(item: User) {
    var recompCount by remember {
        mutableStateOf(0)
    }

    SideEffect {
        recompCount += 1
    }
    Card(modifier=Modifier.padding(8.dp)) {
        Column() {
            Text(item.name+"Recomposition for ${item.name} is $recompCount")
            Text(item.income.toString())
        }
    }
}
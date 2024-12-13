package my.id.cupcakez.simpletodoapp.data

data class Todo(
    var id: String = "",
    var userID: String = "",
    var title: String? = null,
    var description: String? = null,
    var isDone: Boolean = false,
    var date: String? = null
)


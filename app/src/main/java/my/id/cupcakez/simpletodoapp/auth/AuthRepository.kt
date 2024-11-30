package my.id.cupcakez.simpletodoapp.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import my.id.cupcakez.simpletodoapp.data.User
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    fun saveUserData(
        user: User
    ) {
        val currentUID = auth.currentUser?.uid
        currentUID?.let {
            user.id = currentUID
            db.collection("users")
                .document(it)
                .set(user)
                .addOnSuccessListener {
                    println("User data berhasil ditambah!")
                }
                .addOnFailureListener { e ->
                    println("Error dalam menambah data: $e")
                }
        }
    }
}
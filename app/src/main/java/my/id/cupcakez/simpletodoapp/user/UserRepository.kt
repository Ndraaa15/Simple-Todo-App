package my.id.cupcakez.simpletodoapp.user

import android.net.Uri
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import my.id.cupcakez.simpletodoapp.data.User
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth, // Firebase Authentication
    private val storage: FirebaseStorage // Firebase Storage
) {
    fun getLoggedInUserData(onResult: (User?) -> Unit) {
        val uid = auth.currentUser?.uid ?: return onResult(null)
        db.collection("users").document(uid)
            .get()
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)
                onResult(user)
            }
            .addOnFailureListener { exception ->
                // Improved error handling
                println("Error fetching user data: ${exception.message}")
                onResult(null)
            }
    }

    fun updateLoggedInUserData(user: User, onResult: (Boolean) -> Unit) {
        val uid = auth.currentUser?.uid ?: return onResult(false)
        println("User: $user")
        db.collection("users").document(uid)
            .set(user)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { exception ->
                // Improved error handling
                println("Error updating user data: ${exception.message}")
                onResult(false)
            }
    }

    fun uploadPhoto(uri: Uri, onResult: (String?) -> Unit) {
        val uid = auth.currentUser?.uid ?: return onResult(null)

        if (uri.scheme != "content" && uri.scheme != "file") {
            println("Invalid URI scheme: ${uri.scheme}")
            return onResult(null)
        }

        val storageRef = storage.reference.child("simple-todo-app/$uid.jpg")

        // Start uploading the file to Firebase Storage
        val uploadTask = storageRef.putFile(uri)

        // Add listeners to handle success, failure, and progress
        uploadTask
            .addOnSuccessListener {
                // Once uploaded, get the download URL
                storageRef.downloadUrl.addOnSuccessListener { url ->
                    onResult(url.toString())
                }
                    .addOnFailureListener { exception ->
                        // Handle failure of download URL retrieval
                        println("Error retrieving download URL: ${exception.message}")
                        onResult(null)
                    }
            }
            .addOnFailureListener { exception ->
                // Handle failure of upload
                println("Error uploading file: ${exception.message}")
                onResult(null)
            }
            .addOnProgressListener { taskSnapshot ->
                // Optional: Show upload progress
                val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toInt()
                println("Upload Progress: $progress%")
            }
    }

    fun logout() {
        auth.signOut()
    }
}



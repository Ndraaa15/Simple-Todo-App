package my.id.cupcakez.simpletodoapp.di

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import my.id.cupcakez.simpletodoapp.todo.TodoRepository
import my.id.cupcakez.simpletodoapp.user.UserRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideUserRepository(db: FirebaseFirestore, auth: FirebaseAuth, storage: FirebaseStorage): UserRepository {
        return UserRepository(db, auth, storage)
    }

    @Provides
    @Singleton
    fun provideTodoRepository(db: FirebaseFirestore): TodoRepository {
        return TodoRepository(db)
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        val db = FirebaseFirestore.getInstance()
        return db
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        val auth = FirebaseAuth.getInstance()
        return auth
    }

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage {
        val storage = FirebaseStorage.getInstance()
        return storage
    }
}
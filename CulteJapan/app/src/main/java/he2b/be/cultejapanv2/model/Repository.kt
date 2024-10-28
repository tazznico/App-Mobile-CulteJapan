package he2b.be.cultejapanv2.model

import android.content.Context
import android.util.Log
import he2b.be.cultejapanv2.database.UserDatabase
import he2b.be.cultejapanv2.database.UserItem
import he2b.be.cultejapanv2.data.User
import he2b.be.cultejapanv2.database.FavorItem

/**
 * Class Repository pour la gestion de la base de données

 */
object Repository {

    private var database: UserDatabase? = null

    /**
     * Fonction pour initialiser la base de données
     * @param context : Context
     */
    fun initDataBase(context: Context) {
        if (database == null) {
            database = UserDatabase.getInstance(context)
            Log.d("DatabaseInit", "Database initialized successfully")
        }
    }

    /**
     * Créer un utilisateur
     * @param userConnection : User
     */
    suspend fun createUser(userConnection: User) {
        database?.let { theDatabase ->
            val newUser = UserItem(email = userConnection.email, password = userConnection.password)
            theDatabase.theUserDAO().insertUser(newUser)
        }
    }

    /**
     * Vérifie si l'utilisateur existe
     * @param email : String
     * @return Int
     */
    suspend fun userExists(email: String): Int {
        Log.d("UserExists", "Email : $email")
        database?.let { theDatabase ->
            return theDatabase.theUserDAO().countEmail(email)
        }
        return 0
    }


    /**
     * Authentification de l'utilisateur
     * @param email : String
     * @param password : String
     * @return Int?
     */
    suspend fun authenticateUser(email: String, password: String): Int? {
        database?.let { theDatabase ->
            return theDatabase.theUserDAO()
                .getUserIdByEmailAndPassword(email = email, password = password)
        }
        return null
    }


    /**
     * Ajoute un favori
     * @param userId : Int
     * @param animeId : Int
     */
    suspend fun addFavorite(userId: Int, animeId: Int) {
        database?.favoritesDao()?.addFavorite(FavorItem(userId = userId, animeId = animeId))
    }


    /**
     * Supprime un favori
     * @param userId : Int
     * @param animeId : Int
     */
    suspend fun removeFavorite(userId: Int, animeId: Int) {
        database?.favoritesDao()?.removeFavorite(FavorItem(userId = userId, animeId = animeId))
    }


    /**
     * Récupère les favoris pour un utilisateur
     * @param userId : Int
     * @return Boolean
     */
    suspend fun getFavoritesForUser(userId: Int): List<Int> {
        database?.let { theDatabase ->
            return theDatabase.favoritesDao().getFavoritesForUser(userId)
        }
        return listOf()
    }
}
package he2b.be.cultejapanv2.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * Dao pour la table des utilisateurs
 */
@Dao
interface UsersDAO {

    /**
     * Rajout d'un utilisateur
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(item: UserItem)

    /**
     * Récupération de la liste des utilisateurs
     */
    @Query("SELECT * FROM Users")
    suspend fun getAllUsers(): List<UserItem>

    /**
     * Récupération d'un utilisateur par son id
     */
    @Query("SELECT COUNT(email) FROM Users WHERE email = :email")
    suspend fun countEmail(email: String): Int

    /**
     * Récupération d'un utilisateur par son email et son mot de passe
     */
    @Query("SELECT id FROM Users WHERE email = :email AND password = :password")
    suspend fun getUserIdByEmailAndPassword(email: String, password: String): Int?

}
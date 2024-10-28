package he2b.be.cultejapanv2.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * Dao pour la table des favoris
 */
@Dao
interface FavoritesDao {

    /**
     * Rajout d'un favori
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favorite: FavorItem)

    /**
     * supression d'un favori
     */
    @Delete
    suspend fun removeFavorite(favorite: FavorItem)

    /**
     * Récupération de la liste des favoris pour un utilisateur
     */
    @Query("SELECT animeId FROM favorites_Anime WHERE userId = :userId")
    suspend fun getFavoritesForUser(userId: Int): List<Int>

    /**
     * Vérification si un anime est favori pour un utilisateur
     */
    @Query("SELECT EXISTS(SELECT 1 FROM favorites_Anime WHERE userId = :userId AND animeId = :animeId)")
    suspend fun isFavorite(userId: Int, animeId: Int): Boolean
}
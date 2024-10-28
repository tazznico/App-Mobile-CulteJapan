package he2b.be.cultejapanv2.database

import androidx.room.Entity

/**
 * Classe représentant un favori(animé) lié à un user
 * @property animeId L'ID de l'anime
 * @property userId L'ID de l'utilisateur
 */
@Entity(
    tableName = "favorites_Anime",
    primaryKeys = ["animeId", "userId"]
)
data class FavorItem(
    val animeId: Int,
    val userId: Int,
)
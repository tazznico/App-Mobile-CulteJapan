package he2b.be.cultejapanv2.database

import androidx.room.Entity
import androidx.room.PrimaryKey


/**
 * data classe d'un utilisateur dans ma DB
 * @param id: Int
 * @param email: String
 * @param password: String
 */
@Entity(tableName = "Users")
data class UserItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val email: String,
    val password: String
)

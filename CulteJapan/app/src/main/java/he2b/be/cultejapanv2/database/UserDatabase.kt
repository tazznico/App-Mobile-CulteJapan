package he2b.be.cultejapanv2.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * La classe UserDatabase est une classe abstraite qui étend RoomDatabase.
 */
@Database(
    entities = [UserItem::class, FavorItem::class],
    version = 4,
    exportSchema = false
)
abstract class UserDatabase : RoomDatabase() {

    abstract fun theUserDAO(): UsersDAO
    abstract fun favoritesDao(): FavoritesDao

    companion object {

        private const val DATABASE_NAME = "CulteJapan_db"

        @Volatile
        private var INSTANCE: UserDatabase? = null

        fun getInstance(context: Context): UserDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): UserDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                UserDatabase::class.java,
                DATABASE_NAME
            ).fallbackToDestructiveMigration()
                .build()
        }
    }
}
package he2b.be.cultejapanv2.network

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url


/**
 * Interface pour les requêtes HTTP
 */
interface ConnectionHTTPClient {


    /**
     * Requête pour obtenir les animes
     */
    @GET("anime")
    suspend fun getAnimeSearch(
        @Query("q") search: String,
        @Query("limit") limit: Int = 10,
        @Query("offset") offset: Int = 0,
        @Query("fields") fields: String = "id,title,main_picture,mean,num_scoring_users,rank",
        @Header("Content-Type") content: String = "application/json",
        @Header("Authorization") token: String = "Bearer eyJ0eXAiO..."
    ): Animes

    /**
     * Requête pour obtenir les détails d'un anime
     */
    @GET("anime/{id}")
    suspend fun getAnimeDetails(
        //"id,title,main_picture,start_date,end_date,synopsis,rank,popularity,num_scoring_users,status,genres,num_episodes"
        @Path("id") id: Int,
        @Query("fields") fields: String = "id,title,main_picture,mean,num_scoring_users,synopsis,rank,genres",
        @Header("Content-Type") content: String = "application/json",
        @Header("Authorization") token: String = "Bearer eyJ0eXAiO..."
    ): AnimeDetail

    /**
     * Requête pour obtenir les animes de la saison
     */
    @GET("anime/season/2024/summer")
    suspend fun getSeasonAnime(
        @Query("limit") limit: Int = 10,
        @Query("offset") offset: Int = 0,
        @Query("fields") fields: String = "id,title,main_picture",
        @Header("Content-Type") content: String = "application/json",
        @Header("Authorization") token: String = "Bearer eyJ0eXAiO..."
    ): Animes

    /**
     * Requête pour obtenir la page suivante
     */
    @GET
    suspend fun getNextPage(
        @Url url: String,
        @Header("Content-Type") content: String = "application/json",
        @Header("Authorization") token: String = "Bearer eyJ0eXAiO..."
    ): Animes
}

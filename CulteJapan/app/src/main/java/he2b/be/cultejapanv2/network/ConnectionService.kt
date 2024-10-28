package he2b.be.cultejapanv2.network

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * ConnectionService est un objet qui permet de créer un client HTTP pour se connecter à l'API de MyAnimeList
 */
object ConnectionService {

    private const val BASE_URL_MAL = "https://api.myanimelist.net/v2/"

    val connectionClient: ConnectionHTTPClient by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_MAL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(ConnectionHTTPClient::class.java)
    }
}
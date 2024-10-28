package he2b.be.cultejapanv2.network

/**
 * Data class représentant l'objet complet reçu par l'API
 */
data class Animes(
    val data: List<Anime> = emptyList(),
    val paging: Paging = Paging()
)

/**
 * Data class représentant la pagination
 */
data class Paging(
    val previous: String = "None",
    val next: String = "None",
)

/**
 * Data class représentant un anime
 */
data class Anime(
    val node: AnimeDetail,
    val isFav: Boolean = false
)

/**
 * Data class représentant les détails d'un anime
 */
data class AnimeDetail(
    val id: Int = -1,
    val title: String = "None",
    val main_picture: Pictures = Pictures(),
    val num_episodes: Int = -1,
    val mean: Double = -1.0,
    val num_scoring_users: Int = -1,
    val synopsis: String = "None",
    val rank: Double = -1.0,
    val popularity: Int = -1,
    val status: String = "None",
    val genres: List<Genre> = emptyList()
)

/**
 * Data class représentant les images d'un anime
 */
data class Pictures(
    val medium: String = "None",
    val large: String = "None"
)

/**
 * Data class représentant un genre
 */
data class Genre(
    val id: Int = -1,
    val name: String = "None"
)

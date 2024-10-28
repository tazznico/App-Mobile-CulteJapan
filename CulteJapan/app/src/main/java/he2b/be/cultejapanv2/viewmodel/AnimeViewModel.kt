package he2b.be.cultejapanv2.viewmodel

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import he2b.be.cultejapanv2.network.ConnectionService
import he2b.be.cultejapanv2.network.Anime
import he2b.be.cultejapanv2.network.AnimeDetail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel pour la classe Anime
 * Elle a pour but de charger tous les animes pour la page d'acceuil
 */
class AnimeViewModel : ViewModel() {

    // LES ANIMES DE LA SAISON
    private val _animesSeason = MutableStateFlow<List<AnimeDetail>>(emptyList())
    val animesSeason: StateFlow<List<AnimeDetail>> = _animesSeason
    private var nextPageSeason: String = "None"

    // LES ANIMES RECUPERES PAR LA RECHERCHE
    private val _animesSearch = MutableStateFlow<List<Anime>>(emptyList())
    val animesSearch: StateFlow<List<Anime>> = _animesSearch
    private var nextPageSearch: String = "None"

    // LA RECHERCHE ACTUELLE
    var searchInput: MutableState<String> = mutableStateOf("one")

    /**
     * Charge les animes de la saison et ceux de la recherche
     *
     * @param userViewModel ViewModel de l'utilisateur
     */
    fun loadAnimes(userViewModel: UserViewModel) {
        seasonAnimes()
        searchAnimes(userViewModel = userViewModel)
    }


    /**
     * Charge les animes de la saison
     */
    private fun seasonAnimes() {
        viewModelScope.launch {
            try {
                val responseAnimeSeason = ConnectionService.connectionClient.getSeasonAnime()
                nextPageSeason = responseAnimeSeason.paging.next
                _animesSeason.value = responseAnimeSeason.data.map { it.node }
                Log.i("Anime_View_Model", "Load Animes Season : $responseAnimeSeason")
            } catch (e: Exception) {
                Log.e("Anime_View_Model", "Error Load Animes Season", e.fillInStackTrace())
            }
        }
    }


    /**
     * Recherche des animes
     *
     * @param userViewModel ViewModel de l'utilisateur
     */
    fun searchAnimes(userViewModel: UserViewModel) {
        viewModelScope.launch {
            try {
                val responseAnimeSearch =
                    ConnectionService.connectionClient.getAnimeSearch(search = searchInput.value)
                nextPageSearch = responseAnimeSearch.paging.next
                val favorites = userViewModel.userFavorites.value
                val animes =
                    responseAnimeSearch.data.map { it.copy(isFav = favorites.contains(it.node.id)) }
                _animesSearch.value = animes
                Log.i("Anime_View_Model", "Load Animes Search : $responseAnimeSearch")
            } catch (e: Exception) {
                Log.e("Anime_View_Model", "Error Load Animes Recherche", e.fillInStackTrace())
            }
        }
    }


    /**
     * Charge plus d'animes pour la recherche
     *
     * @param userViewModel ViewModel de l'utilisateur
     */
    fun loadMoreSearch(userViewModel: UserViewModel) {
        viewModelScope.launch {
            try {
                if (nextPageSearch != "None") {
                    val responseAnimeSearch =
                        ConnectionService.connectionClient.getNextPage(
                            url = nextPageSearch.removePrefix(
                                "https://api.myanimelist.net/v2/"
                            )
                        )
                    nextPageSearch = responseAnimeSearch.paging.next
                    val favorites = userViewModel.userFavorites.value
                    val newAnimes =
                        responseAnimeSearch.data.map { it.copy(isFav = favorites.contains(it.node.id)) }
                    _animesSearch.value = animesSearch.value + newAnimes
                    Log.i(
                        "Anime_View_Model",
                        "Load More Animes Search : $responseAnimeSearch next page : $nextPageSearch"
                    )
                } else {
                    Log.i("Anime_View_Model", "Load More Animes URL Recherche: $nextPageSearch")
                }
            } catch (e: Exception) {
                Log.e("Anime_View_Model", "Error Load More Animes Recherche", e.fillInStackTrace())
            }
        }
    }


    /**
     * Charge plus d'animes pour la saison
     */
    fun loadMoreSeason() {
        viewModelScope.launch {
            try {
                if (nextPageSeason != "None") {
                    val responseAnimeSeason =
                        ConnectionService.connectionClient.getNextPage(
                            url = nextPageSeason.removePrefix(
                                "https://api.myanimelist.net/v2/"
                            )
                        )
                    nextPageSeason = responseAnimeSeason.paging.next
                    _animesSeason.value =
                        animesSeason.value + responseAnimeSeason.data.map { it.node }
                    Log.i(
                        "Anime_View_Model",
                        "Load More Animes Season : $responseAnimeSeason next page : $nextPageSeason"
                    )
                } else {
                    Log.i("Anime_View_Model", "Load More Animes URL Season : $nextPageSeason")
                }
            } catch (e: Exception) {
                Log.e("Anime_View_Model", "Error Load More Animes Season", e.fillInStackTrace())
            }
        }
    }


    /**
     * Change l'état de favoris d'un anime
     *
     * @param anime anime à changer
     * @param userViewModel ViewModel de l'utilisateur
     */
    fun toggleFavorite(anime: Anime, userViewModel: UserViewModel) {
        viewModelScope.launch {
            if (anime.isFav) {
                userViewModel.removeFavorite(anime.node.id)
                _animesSearch.value = _animesSearch.value.map {
                    it.copy(isFav = if (it.node.id == anime.node.id) !it.isFav else it.isFav)
                }
            } else {
                userViewModel.addFavorite(anime.node.id)
                _animesSearch.value = _animesSearch.value.map {
                    it.copy(isFav = if (it.node.id == anime.node.id) !it.isFav else it.isFav)
                }
            }
        }
    }

}
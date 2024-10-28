package he2b.be.cultejapanv2.viewmodel

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import he2b.be.cultejapanv2.network.ConnectionService
import he2b.be.cultejapanv2.network.Anime
import kotlinx.coroutines.launch

/**
 * ViewModel pour la classe Anime favoris
 * Elle a pour but de charger les animes favoris de l'utilisateur
 */
class FavoriteListViewModel : ViewModel() {

    // LES ANIMES FAVORIS
    private val _animesFav = MutableLiveData<List<Anime>>()

    // LES ANIMES FAVORIS DE LA RECHERCHE
    private val _animesFavSearch = MutableLiveData<List<Anime>>()
    val animesFavSearch: LiveData<List<Anime>> = _animesFavSearch
    var searchInput: MutableState<String> = mutableStateOf("")


    /**
     * Charge les animes favoris de l'utilisateur
     *
     * @param userViewModel ViewModel de l'utilisateur
     */
    fun loadAnimes(userViewModel: UserViewModel) {
        getAnimesFav(userViewModel = userViewModel)
    }

    /**
     * Récupère les animes favoris de l'utilisateur
     *
     * @param userViewModel ViewModel de l'utilisateur
     */
    private fun getAnimesFav(userViewModel: UserViewModel) {
        viewModelScope.launch {
            try {
                val favorites = userViewModel.userFavorites.value
                val animeList = mutableListOf<Anime>()

                for (id in favorites) {
                    val responseAnimeDetail =
                        ConnectionService.connectionClient.getAnimeDetails(id = id)
                    val anime = Anime(node = responseAnimeDetail, isFav = true)
                    animeList.add(anime)
                }
                Log.i("Favorite_List_View_Model", "Load Animes Fav : $animeList")

                _animesFav.value = animeList
                _animesFavSearch.value = animeList
            } catch (e: Exception) {
                Log.i("Favorite_List_View_Model", "Error Load Animes Fav : ${e.message}")
            }

        }
    }


    /**
     * applique une recherche sur les animes favoris
     */
    fun searchMyAnimeList() {
        _animesFavSearch.value = _animesFav.value?.filter {
            it.node.title.contains(
                searchInput.value,
                ignoreCase = true
            )
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
                _animesFav.value = _animesFav.value?.filter { it.node.id != anime.node.id }
                _animesFavSearch.value =
                    _animesFavSearch.value?.filter { it.node.id != anime.node.id }
            }
        }
    }
}
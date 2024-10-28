package he2b.be.cultejapanv2.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import he2b.be.cultejapanv2.network.ConnectionService
import he2b.be.cultejapanv2.network.Anime
import kotlinx.coroutines.launch

/**
 * ViewModel pour la classe AnimeDetailsFragment
 * Elle a pour but de charger les détails d'un anime
 */
class AnimeDetailsViewModel : ViewModel() {
    private val _animeDetail = MutableLiveData<Anime>()
    val animeDetail: LiveData<Anime> = _animeDetail

    /**
     * Charge les détails d'un anime
     *
     * @param animeId id de l'anime
     * @param userViewModel ViewModel de l'utilisateur
     */
    fun loadAnimeDetails(animeId: Int, userViewModel: UserViewModel) {
        viewModelScope.launch {
            try {
                val responseAnimeDetail =
                    ConnectionService.connectionClient.getAnimeDetails(id = animeId)
                val favorites = userViewModel.userFavorites.value
                _animeDetail.value =
                    Anime(node = responseAnimeDetail, isFav = favorites.contains(animeId))
                Log.i(
                    "Anime_Details_View_Model",
                    "Load Anime With id $animeId : ${_animeDetail.value.toString()}"
                )
            } catch (e: Exception) {
                Log.e("Anime_Details_View_Model", "Error Load Anime With id", e.fillInStackTrace())
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
                _animeDetail.value = anime.copy(isFav = false)
                Log.i(
                    "Anime_Details_View_Model",
                    "Change favorite anime ${anime.node.id} to false"
                )
            } else {
                userViewModel.addFavorite(anime.node.id)
                _animeDetail.value = anime.copy(isFav = true)
                Log.i(
                    "Anime_Details_View_Model",
                    "Change favorite anime ${anime.node.id} to true"
                )
            }
        }
    }
}
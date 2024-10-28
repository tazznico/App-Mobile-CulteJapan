package he2b.be.cultejapanv2.viewmodel


import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import he2b.be.cultejapanv2.data.User
import he2b.be.cultejapanv2.model.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {


    // User
    val emailInput: MutableState<String> = mutableStateOf("")
    val passwordInput: MutableState<String> = mutableStateOf("")
    val idUser: MutableState<Int> = mutableIntStateOf(-1)

    // User Favorites
    private val _userFavorites =
        MutableStateFlow<Set<Int>>(emptySet()) //Demander c'est quoi le Set<Int>
    val userFavorites: StateFlow<Set<Int>> = _userFavorites


    /**
     * Connecte un utilisateur.
     */
    fun loginUser() {
        viewModelScope.launch {
            if (validConnection()) {
                idUser.value = Repository.authenticateUser(
                    email = emailInput.value,
                    password = passwordInput.value
                ) ?: -1
                if (idUser.value != -1) {
                    loadUserFavorites()
                    Log.i("User_View_Model", "User connected")
                } else {
                    Log.e("User_View_Model", "User not connected, Id = ${idUser.value}")
                }
            }
        }
    }

    /**
     * Crée un compte utilisateur.
     */
    fun createAccount() {
        viewModelScope.launch {
            if (validConnection()) {
                if (!emailExisting()) {
                    Repository.createUser(User(emailInput.value, passwordInput.value))
                    loginUser()
                }
            }

        }
    }

    /**
     * Charge les favoris de l'utilisateur.
     */
    private fun loadUserFavorites() {
        viewModelScope.launch {
            try {
                val favorites = Repository.getFavoritesForUser(idUser.value)
                _userFavorites.value = favorites.toSet()
                Log.i("User_View_Model", "favorites loaded : $favorites")
            } catch (e: Exception) {
                Log.e("User_View_Model", "Error loading user favorites", e)
            }
        }
    }

    /**
     * Ajoute un favori à l'utilisateur.
     */
    fun addFavorite(animeId: Int) {
        viewModelScope.launch {
            try {
                val currentFavorites = _userFavorites.value
                _userFavorites.value = currentFavorites + animeId // Ajout de l'ID à l'ensemble
                Repository.addFavorite(userId = idUser.value, animeId = animeId)
                Log.i("User_View_Model", "add favorite : ${_userFavorites.value}")
            } catch (e: Exception) {
                Log.e("User_View_Model", "Error adding favorite", e)
            }
        }
    }

    /**
     * Supprime un favori de l'utilisateur.
     */
    fun removeFavorite(animeId: Int) {
        viewModelScope.launch {
            try {
                val currentFavorites = _userFavorites.value
                _userFavorites.value = currentFavorites - animeId
                Repository.removeFavorite(userId = idUser.value, animeId = animeId)
                Log.i("User_View_Model", "remove favorite : ${_userFavorites.value}")
            } catch (e: Exception) {
                Log.e("User_View_Model", "Error removing favorite", e)
            }
        }
    }

    /**
     * Déconnecte l'utilisateur.
     */
    fun logOut() {
        idUser.value = -1
        _userFavorites.value = emptySet()
    }

    /**
     * Vérifie si l'email existe déjà dans la base de données.
     */
    private suspend fun emailExisting(): Boolean {
        return try {
            Repository.userExists(emailInput.value) > 0
        } catch (e: Exception) {
            Log.e("User_View_Model", "Error in emailExisting", e)
            false
        }
    }

    /**
     * Vérifie si la connection est valide.
     */
    private fun validConnection(): Boolean {
        return validEmail(emailInput.value) && validPassword(passwordInput.value)
    }

    /**
     * Vérifie si l'email est valide.
     */
    private fun validEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    /**
     * Vérifie si le mot de passe est valide.
     */
    private fun validPassword(password: String): Boolean {
        return password.length >= 8
    }

    /**
     * Pour les test rapide de connection
     */
    fun autoConnectAdmin() {
        emailInput.value = "tazznico@hotmail.com"
        passwordInput.value = "tazznico"
    }

    /**
     * Récupère les favoris scannés par l'utilisateur sous forme de JSON.
     */
    fun getUserFavoritesAsJson(): String {
        val favorites = _userFavorites.value
        return Gson().toJson(favorites)
    }
}
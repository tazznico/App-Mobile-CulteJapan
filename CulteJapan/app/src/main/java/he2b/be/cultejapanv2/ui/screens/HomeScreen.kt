package he2b.be.cultejapanv2.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import he2b.be.cultejapanv2.ui.components.HorizontalList
import he2b.be.cultejapanv2.ui.components.VerticalList
import he2b.be.cultejapanv2.viewmodel.AnimeViewModel
import he2b.be.cultejapanv2.viewmodel.UserViewModel

/**
 * Page d'acceuil de l'application
 * @param navController le contrôleur de navigation
 * @param userViewModel le ViewModel de l'utilisateur
 * @param animeViewModel le ViewModel des animes
 */
@Composable
fun HomePage(
    navController: NavController,
    userViewModel: UserViewModel,
    animeViewModel: AnimeViewModel = viewModel()
) {
    val animeSeason by animeViewModel.animesSeason.collectAsState()
    val animesSearch by animeViewModel.animesSearch.collectAsState()

    LaunchedEffect(key1 = true) {
        animeViewModel.loadAnimes(userViewModel = userViewModel)
    }

    Column {
        Box(modifier = Modifier.padding(4.dp)) {
/*            Text(
                text = "Anime of the season",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(8.dp)
            )*/
            HorizontalList(
                animes = animeSeason,
                navController = navController,
                animeViewModel = animeViewModel
            )
        }
        Divider(
            color = Color.Gray,
            thickness = 1.dp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Box(modifier = Modifier.padding(4.dp)) {
            SearchBar(
                search = animeViewModel.searchInput,
            ) { animeViewModel.searchAnimes(userViewModel = userViewModel) }
        }
        Box(modifier = Modifier.padding(4.dp)) {
            Divider(
                color = Color.Gray,
                thickness = 1.dp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            /*            Text(
                            text = "Anime search result of ???",
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(8.dp)
                        )*/
            VerticalList(
                animes = animesSearch,
                navController = navController,
                userViewModel = userViewModel,
                animeViewModel = animeViewModel
            ) { anime ->
                animeViewModel.toggleFavorite(anime, userViewModel)
            }
        }
    }
}

/**
 * Barre de recherche
 *
 * @param search le texte de la recherche
 * @param onSearch la fonction à appeler lors de la recherche
 */
@Composable
fun SearchBar(
    search: MutableState<String>,
    onSearch: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp), // Ajout de padding si nécessaire
        verticalAlignment = Alignment.CenterVertically // Aligner verticalement au centre
    ) {

        Spacer(modifier = Modifier.width(8.dp)) // Espacement entre le bouton et la barre de recherche

        TextField(
            value = search.value,
            onValueChange = { search.value = it },
            modifier = Modifier
                .weight(1f) // Prendre tout l'espace restant
                .background(Color.White, RoundedCornerShape(12.dp)), // Fond blanc et coins arrondis
            placeholder = { Text("Search") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    onSearch()
                }
            ),
            singleLine = true,
            textStyle = TextStyle(color = Color.Black, fontSize = 16.sp)
        )
    }
}




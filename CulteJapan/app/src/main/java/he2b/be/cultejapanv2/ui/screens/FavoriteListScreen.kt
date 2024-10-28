package he2b.be.cultejapanv2.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import he2b.be.cultejapanv2.R
import he2b.be.cultejapanv2.ui.components.QRCodeDialog
import he2b.be.cultejapanv2.ui.components.VerticalList
import he2b.be.cultejapanv2.viewmodel.FavoriteListViewModel
import he2b.be.cultejapanv2.viewmodel.UserViewModel

/**
 * Page affichant la liste des animes favoris de l'utilisateur
 *
 * @param navController le NavController
 * @param userViewModel le UserViewModel
 * @param favoriteListViewModel le FavoriteListViewModel
 */
@Composable
fun FavoriteListScreen(
    navController: NavController,
    userViewModel: UserViewModel,
    favoriteListViewModel: FavoriteListViewModel = viewModel()
) {
    val animeSeason by favoriteListViewModel.animesFavSearch.observeAsState(initial = emptyList())

    LaunchedEffect(key1 = true) {
        favoriteListViewModel.loadAnimes(userViewModel = userViewModel)
    }

    var showDialog by remember { mutableStateOf(false) }
    if (showDialog) {
        QRCodeDialog(userViewModel = userViewModel, onDismiss = { showDialog = false })
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Your anime List",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { showDialog = true }) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.round_qr_code_24),
                    contentDescription = "QR Code"
                )
            }
            SearchBar2(
                favoriteListViewModel = favoriteListViewModel,
                onSearch = { favoriteListViewModel.searchMyAnimeList() }
            )
        }
        VerticalList(
            animes = animeSeason,
            navController = navController,
            userViewModel = null,
            animeViewModel = null
        ) { anime ->
            favoriteListViewModel.toggleFavorite(anime, userViewModel)
        }
    }
}

/**
 * Barre de recherche
 *
 * @param favoriteListViewModel le FavoriteListViewModel
 * @param onSearch la fonction de recherche
 */
@Composable
fun SearchBar2(
    favoriteListViewModel: FavoriteListViewModel,
    onSearch: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(8.dp))

        TextField(
            value = favoriteListViewModel.searchInput.value,
            onValueChange = {
                favoriteListViewModel.searchInput.value = it
                onSearch()  // Appel de la fonction de recherche à chaque changement de saisie
            },
            modifier = Modifier
                .weight(1f)
                .background(Color.White, RoundedCornerShape(12.dp)),
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
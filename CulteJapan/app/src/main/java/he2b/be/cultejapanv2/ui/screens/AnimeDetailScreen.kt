package he2b.be.cultejapanv2.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import he2b.be.cultejapanv2.network.Anime
import he2b.be.cultejapanv2.ui.components.AnimeImage
import he2b.be.cultejapanv2.ui.components.ButtonAddOrRemove
import he2b.be.cultejapanv2.viewmodel.AnimeDetailsViewModel
import he2b.be.cultejapanv2.viewmodel.UserViewModel

/**
 * Page de détails d'un anime
 *
 * @param navController le contrôleur de navigation
 * @param userViewModel le ViewModel de l'utilisateur
 * @param animeDetailViewModel le ViewModel des détails de l'anime
 * @param animeId l'identifiant de l'anime
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimeDetailsPage(
    navController: NavController,
    userViewModel: UserViewModel,
    animeDetailViewModel: AnimeDetailsViewModel = viewModel(),
    animeId: Int
) {
    val animeDetail by animeDetailViewModel.animeDetail.observeAsState()

    LaunchedEffect(animeId) {
        animeDetailViewModel.loadAnimeDetails(animeId, userViewModel)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Anime Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->  // Utilisation du paramètre de padding
        Box(modifier = Modifier.padding(innerPadding)) {  // Applique le padding au contenu
            when (animeDetail) {
                null -> LoadingScreen()
                else -> AnimeDetailsScreen(
                    animeDetail = animeDetail,
                    onclickAddOrRemove = {
                        animeDetailViewModel.toggleFavorite(
                            anime = animeDetail!!,
                            userViewModel
                        )
                    }
                )
            }
        }
    }
}

/**
 * Permet d'affoficher un écran de chargement
 */
@Composable
fun LoadingScreen() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        CircularProgressIndicator()
    }
}

/**
 * Affiche les détails d'un anime
 *
 * @param animeDetail les détails de l'anime
 * @param onclickAddOrRemove la fonction à appeler lorsqu'on clique sur le bouton d'ajout/suppression
 */
@Composable
fun AnimeDetailsScreen(animeDetail: Anime?, onclickAddOrRemove: () -> Unit = {}) {
    if (animeDetail == null) {
        LoadingScreen()
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            // Image prend 1/3 de l'espace
            AnimeImage(
                image = animeDetail.node.main_picture,
                title = animeDetail.node.title,
                modifier = Modifier
                    .weight(1f)
                    .height(240.dp)
                    .clip(RoundedCornerShape(8.dp)),
            )

            // Détails prennent 2/3 de l'espace
            Column(
                verticalArrangement = Arrangement.Top,
                modifier = Modifier
                    .weight(2f)
                    .padding(4.dp)
            ) {
                Text(
                    text = animeDetail.node.title,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "Genres: ${animeDetail.node.genres.joinToString(", ") { it.name }}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "Score: ${animeDetail.node.mean} / ${animeDetail.node.num_scoring_users} users",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "Rating: ${animeDetail.node.rank}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Synopsis:",
            style = MaterialTheme.typography.bodyMedium
        )

        ExpandableText(animeDetail.node.synopsis)

        ButtonAddOrRemove(
            isFav = animeDetail.isFav,
            onClick = onclickAddOrRemove
        )
    }
}

/**
 * Affiche un texte qui peut être réduit ou étendu
 *
 * @param text le texte à afficher
 * @param maxLines le nombre de lignes maximum à afficher
 */
@Composable
fun ExpandableText(text: String, maxLines: Int = 5) {
    var expanded by remember { mutableStateOf(false) }

    // Texte à afficher, réduit ou complet
    val displayText = if (expanded) text else text

    Column(modifier = Modifier.clickable {
        expanded = !expanded
    }) { // Toggle l'expansion sur un clic
        Text(
            text = displayText,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = if (expanded) Int.MAX_VALUE else maxLines,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(8.dp)
        )
    }
}
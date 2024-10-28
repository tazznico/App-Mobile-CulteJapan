package he2b.be.cultejapanv2.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import he2b.be.cultejapanv2.R
import he2b.be.cultejapanv2.network.Anime
import he2b.be.cultejapanv2.network.AnimeDetail
import he2b.be.cultejapanv2.network.Pictures
import he2b.be.cultejapanv2.viewmodel.AnimeViewModel
import he2b.be.cultejapanv2.viewmodel.UserViewModel
import kotlinx.coroutines.launch

/**
 * Permet l'affichage d'une lazy liste verticale d'animes
 */
@Composable
fun VerticalList(
    animes: List<Anime>,
    navController: NavController,
    userViewModel: UserViewModel?,
    animeViewModel: AnimeViewModel?,
    onToggleFavorite: (Anime) -> Unit
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Permet de charger plus de données lorsque l'utilisateur atteint 50% de la liste
    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisibleItemIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
            val totalItemsCount = listState.layoutInfo.totalItemsCount
            lastVisibleItemIndex != null && lastVisibleItemIndex >= (totalItemsCount / 2)
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) {
            (userViewModel?.let {
                animeViewModel?.loadMoreSearch(it)
            })
        }
    }

    Box {
        LazyColumn(state = listState) {
            items(animes) { anime ->
                RectangleHorizontalItemCard(
                    animeDetail = anime.node,
                    isFav = anime.isFav,
                    onclickCard = { navController.navigate("details/${anime.node.id}") },
                    onclickAddOrRemove = { onToggleFavorite(anime) }
                )
            }
        }

        // Bouton flottant pour revenir au début
        if (remember { derivedStateOf { listState.firstVisibleItemIndex } }.value > 0) {
            FloatingActionButton(
                onClick = {
                    coroutineScope.launch {
                        listState.scrollToItem(0)
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(Icons.Rounded.KeyboardArrowUp, contentDescription = "Back to Top")
            }
        }
    }
}

/**
 * Permet l'affichage d'une lazy liste horizontale d'animes
 */
@Composable
fun HorizontalList(
    animes: List<AnimeDetail>,
    navController: NavController,
    animeViewModel: AnimeViewModel
) {
    val listState = rememberLazyListState()

    // Permet de charger plus de données lorsque l'utilisateur atteint 50% de la liste
    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisibleItemIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
            val totalItemsCount = listState.layoutInfo.totalItemsCount
            lastVisibleItemIndex != null && lastVisibleItemIndex >= (totalItemsCount / 2)
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) {
            animeViewModel.loadMoreSeason()
        }
    }


    LazyRow(state = listState) {
        items(animes) { anime ->
            RectangleVerticalItemCard(
                anime,
                onclickCard = { navController.navigate("details/${anime.id}") })
        }
    }
}

/**
 * Permet l'affichage d'un item de la lazy liste verticale
 */
@Composable
fun RectangleHorizontalItemCard(
    animeDetail: AnimeDetail,
    isFav: Boolean,
    onclickCard: () -> Unit = {},
    onclickAddOrRemove: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable(onClick = onclickCard)  // Appliquer le clickable sur toute la carte
    ) {
        Row(modifier = Modifier.padding(16.dp)) {

            AnimeImage(
                image = animeDetail.main_picture,
                title = animeDetail.title,
                modifier = Modifier.size(150.dp)
            )

            Spacer(Modifier.width(16.dp))  // Espacement entre l'image et le texte

            // Conteneur pour les détails textuels
            Column {
                Text(
                    text = animeDetail.title,
                    style = MaterialTheme.typography.titleSmall,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
                Text(
                    text = "Score: ${animeDetail.mean} / ${animeDetail.num_scoring_users} users",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Rank: ${animeDetail.rank}",
                    style = MaterialTheme.typography.bodySmall
                )
                ButtonAddOrRemove(
                    isFav = isFav,
                    onClick = onclickAddOrRemove
                )
            }
        }
    }
}

/**
 * Permet l'affichage d'un item de la lazy liste horizontale
 */
@Composable
fun RectangleVerticalItemCard(animeDetail: AnimeDetail, onclickCard: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .size(width = 100.dp, height = 150.dp) // Taille fixe
            .background(
                color = Color.White,
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 1.dp,
                color = Color.Gray,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onclickCard)  // Appliquer le clickable sur toute la carte
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            AnimeImage(
                image = animeDetail.main_picture,
                title = animeDetail.title,
                modifier = Modifier.size(100.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = animeDetail.title,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleSmall
            )
        }
    }
}

/**
 * Permet l'affichage l'affichage d'une image d'un animé
 */
@Composable
fun AnimeImage(image: Pictures, title: String, modifier: Modifier = Modifier) {
    AsyncImage(
        model = ImageRequest.Builder(context = LocalContext.current)
            .data(image.medium)
            .crossfade(true)
            .build(),
        error = painterResource(R.drawable.ic_broken_image),
        placeholder = painterResource(R.drawable.loading_img),
        contentDescription = "Main Picture of $title",
        modifier = modifier
    )
}

/**
 * Bouton pour ajouter ou retirer un animé des favoris de l'utilisateur courant
 */
@Composable
fun ButtonAddOrRemove(isFav: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
    ) {
        Text(if (isFav) "Remove" else "Add")

    }
}
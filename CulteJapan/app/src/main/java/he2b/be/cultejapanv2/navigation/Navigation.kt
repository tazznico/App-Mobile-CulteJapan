package he2b.be.cultejapanv2.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import he2b.be.cultejapanv2.R
import he2b.be.cultejapanv2.ui.screens.AnimeDetailsPage
import he2b.be.cultejapanv2.ui.screens.FavoriteListScreen
import he2b.be.cultejapanv2.ui.screens.HomePage
import he2b.be.cultejapanv2.ui.screens.LoginPage
import he2b.be.cultejapanv2.ui.screens.SettingsUserPage
import he2b.be.cultejapanv2.viewmodel.UserViewModel


/**
 * Composable qui gère la navigation de l'application
 */
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val userViewModel = UserViewModel()


    Scaffold(
        topBar = {
            Title(navController = navController, userViewModel = userViewModel)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("login") {
                LoginPage(navController = navController, userViewModel = userViewModel)
            }
            composable("home") {
                HomePage(navController = navController, userViewModel = userViewModel)
            }
            composable(
                "details/{animeId}",
                arguments = listOf(navArgument("animeId") { type = NavType.IntType })
            ) { backStackEntry ->
                val animeId = backStackEntry.arguments?.getInt("animeId") ?: 0
                AnimeDetailsPage(
                    navController = navController,
                    userViewModel = userViewModel,
                    animeId = animeId
                )
            }
            composable("profile") {
                SettingsUserPage(navController = navController, userViewModel = userViewModel)
            }
            composable("favoriteList") {
                FavoriteListScreen(navController = navController, userViewModel = userViewModel)
            }
            //Ajoutez d'autres routes ici ...
        }
    }
}

/**
 * Composable qui affiche le titre principale sur toute l'application
 * @param navController: NavHostController
 * @param userViewModel: UserViewModel
 * @param modifier: Modifier
 */
@Composable
fun Title(
    navController: NavHostController,
    userViewModel: UserViewModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 16.dp)
                .padding(8.dp), // Ajout d'un padding interne pour l'espacement des icônes et du texte
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (userViewModel.idUser.value != -1) {
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .size(24.dp)
                        .clickable { navController.navigate("profile") },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Settings,
                        contentDescription = "Settings User",
                        modifier = Modifier
                            .size(24.dp)
                    )
                }
            }
            Text(
                text = stringResource(id = R.string.app_name),
                color = Color.Black,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))  // Arrondit les coins du texte
                    .border(1.dp, Color.Gray, RoundedCornerShape(10.dp))  // Bordure pour le titre
                    .padding(horizontal = 10.dp, vertical = 5.dp)
                    .clickable {
                        if (navController.currentDestination?.route != "login") {
                            navController.navigate("home")
                        }
                    },
                textAlign = TextAlign.Center
            )
            if (userViewModel.idUser.value != -1) {
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .size(24.dp)
                        .clickable {
                            userViewModel.logOut()
                            navController.navigate("login")
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.round_logout_24),
                        contentDescription = stringResource(id = R.string.description_icon_logout),
                        modifier = Modifier
                            .size(24.dp)
                    )
                }
            }
        }
        Divider(
            color = Color.Gray,
            thickness = 1.dp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}
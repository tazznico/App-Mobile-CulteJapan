package he2b.be.cultejapanv2.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import he2b.be.cultejapanv2.R
import he2b.be.cultejapanv2.viewmodel.UserViewModel

/**
 * Page de connexion
 * @param navController
 * @param userViewModel
 */
@Composable
fun LoginPage(navController: NavController, userViewModel: UserViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {


        LaunchedEffect(userViewModel.idUser.value) {
            if (userViewModel.idUser.value != -1) {
                navController.navigate("home")
            }
        }

        // Espace pour la section connexion centrée
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxHeight()
        ) {
            Text("Connexion", fontSize = 18.sp)
            Spacer(modifier = Modifier.height(16.dp))


            // Champ Username
            OutlinedTextField(
                value = userViewModel.emailInput.value,
                onValueChange = { userViewModel.emailInput.value = it },
                label = { Text(text = stringResource(R.string.placeholder_field_email)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Champ Password
            OutlinedTextField(
                value = userViewModel.passwordInput.value,
                onValueChange = { userViewModel.passwordInput.value = it },
                label = { Text(text = stringResource(R.string.placeholder_field_password)) },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { userViewModel.loginUser() })
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Boutons Sign Up et Login
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(onClick = { userViewModel.createAccount() }) {
                    Text(text = stringResource(R.string.button_sign))
                }
                Button(onClick = { userViewModel.loginUser() }) {
                    Text(text = stringResource(R.string.button_login))
                }
            }
        }


    }
}

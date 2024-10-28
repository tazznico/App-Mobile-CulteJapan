package he2b.be.cultejapanv2.ui.components

import android.Manifest
import android.content.pm.PackageManager
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import he2b.be.cultejapanv2.utils.QRCodeAnalyzer
import he2b.be.cultejapanv2.utils.generateQRCode
import he2b.be.cultejapanv2.viewmodel.UserViewModel
import java.util.concurrent.Executors

/**
 * Composable affichant un dialog contenant un QR Code généré à partir des favoris de l'utilisateur.
 */
@Composable
fun QRCodeDialog(userViewModel: UserViewModel, onDismiss: () -> Unit) {
    val favoritesJson = remember { userViewModel.getUserFavoritesAsJson() }
    val qrCodeBitmap = remember { generateQRCode(favoritesJson) }
    var selectedTab by remember { mutableIntStateOf(0) }

    Dialog(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(16.dp)) {
            TabRow(selectedTabIndex = selectedTab) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                    Text(text = "Afficher QR Code")
                }
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                    Text(text = "Lire QR Code")
                }
            }

            when (selectedTab) {
                0 -> {
                    Box(
                        modifier = Modifier
                            .size(300.dp)
                            .padding(16.dp)
                    ) {
                        qrCodeBitmap?.let {
                            Image(
                                bitmap = it.asImageBitmap(),
                                contentDescription = "QR Code",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp)
                            )
                        } ?: run {
                            Text(text = "Failed to generate QR Code")
                        }
                    }
                }

                1 -> {
                    QrCodeScanner()
                }
            }
        }
    }
}

/**
 * Composable permettant de scanner un QR Code.
 */
@Composable
fun QrCodeScanner() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var showDialog by remember { mutableStateOf(false) }
    var scannedData by remember { mutableStateOf("") }
    val previewView = remember { PreviewView(context) }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    val cameraPermissionGranted by remember {
        derivedStateOf {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    if (cameraPermissionGranted) {
        LaunchedEffect(cameraProviderFuture) {
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }
            val imageAnalysis = ImageAnalysis.Builder().build().also { it ->
                it.setAnalyzer(cameraExecutor, QRCodeAnalyzer { result ->
                    result?.let {
                        scannedData = it
                        showDialog = true
                    }
                })
            }
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner, cameraSelector, preview, imageAnalysis
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())

    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "Scanned QR Code") },
            text = { Text(text = scannedData) },
            confirmButton = {
                Button(onClick = { showDialog = false }) {
                    Text("OK")
                }
            }
        )
    }
}
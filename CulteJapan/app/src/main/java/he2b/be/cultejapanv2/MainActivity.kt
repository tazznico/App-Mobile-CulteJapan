package he2b.be.cultejapanv2

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import he2b.be.cultejapanv2.model.Repository
import he2b.be.cultejapanv2.navigation.AppNavigation
import he2b.be.cultejapanv2.utils.PermissionUtils

/**
 * Activité principale de l'application.
 */
class MainActivity : ComponentActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted
            } else {
                showPermissionDeniedExplanation()
            }
        }

    /**
     * Crée l'activité principale.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        Repository.initDataBase(applicationContext)
        super.onCreate(savedInstanceState)
        setContent {
            AppNavigation()
        }

        sharedPreferences = getSharedPreferences("app_preferences", Context.MODE_PRIVATE)

        if (!isPermissionRequestedBefore()) {
            requestCameraPermission()
        }
    }

    /**
     * Vérifie si la permission de la caméra a déjà été demandée.
     */
    private fun isPermissionRequestedBefore(): Boolean {
        return sharedPreferences.getBoolean("camera_permission_requested", false)
    }

    /**
     * Enregistre que la permission de la caméra a été demandée.
     */
    private fun setPermissionRequested() {
        with(sharedPreferences.edit()) {
            putBoolean("camera_permission_requested", true)
            apply()
        }
    }

    /**
     * Demande la permission de la caméra.
     */
    private fun requestCameraPermission() {
        if (PermissionUtils.isCameraPermissionGranted(this)) {
            // Permission already granted
        } else if (PermissionUtils.shouldShowRequestPermissionRationale(this)) {
            showPermissionRationale()
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
            setPermissionRequested()
        }
    }

    /**
     * Affiche la demande de permission.
     */
    private fun showPermissionRationale() {
        AlertDialog.Builder(this)
            .setTitle("Camera Permission Needed")
            .setMessage("This app needs the Camera permission to function properly. Please grant the permission.")
            .setPositiveButton("OK") { _, _ ->
                requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
            }
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

    /**
     * Affiche l'explication de la permission refusée.
     */
    private fun showPermissionDeniedExplanation() {
        AlertDialog.Builder(this)
            .setTitle("Permission Denied")
            .setMessage("Without the Camera permission, this app cannot use the camera. You can grant the permission from the app settings.")
            .setPositiveButton("OK", null)
            .create()
            .show()
    }
}

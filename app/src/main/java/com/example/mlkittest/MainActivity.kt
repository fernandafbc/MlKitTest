package com.example.mlkittest

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.*
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.NamedNavArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.mlkittest.ui.screens.BarcodeDetectorScreen
import com.example.mlkittest.ui.screens.FaceDetectionScreen
import com.example.mlkittest.ui.screens.TextDetectionScreen
import com.example.mlkittest.ui.theme.MlKitTestTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MlKitTestTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MlKitTestsScreen()
                }
            }
        }
    }
}

@Composable
fun MlKitTestsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val navController = rememberNavController()
        NavHost(navController, startDestination = Routes.PERMISSION.route) {
            composable(route = Routes.PERMISSION.route) {
                CheckPermission(navController = navController)
            }
            composable(route = Routes.FACE_DETECTION.route) {
                FaceDetectionScreen()
            }
            composable(route = Routes.BARCODE_DETECTOR.route) {
                BarcodeDetectorScreen()
            }
            composable(route = Routes.TEXT_DETECTION.route) {
                TextDetectionScreen()
            }
        }
    }

}

@Composable
fun CheckPermission(
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val context = LocalContext.current
        var route by remember { mutableStateOf(Routes.FACE_DETECTION.route) }

        val permission = Manifest.permission.CAMERA
        val launcher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                navController.navigate(route)
            } else {
                Toast.makeText(context, "isNotGranted", Toast.LENGTH_SHORT).show()
            }
        }
        Button(
            onClick = {
                route = Routes.FACE_DETECTION.route
                checkAndRequestCameraPermission(
                    context,
                    permission,
                    launcher,
                    navController,
                    route
                )
            }
        ) {
            Text(text = "Face Detector")
        }

        Button(
            onClick = {
                route = Routes.BARCODE_DETECTOR.route
                checkAndRequestCameraPermission(
                    context,
                    permission,
                    launcher,
                    navController,
                    route
                )
            },
            modifier = Modifier.padding(top = 24.dp)
        ) {
            Text(text = "Barcode Detector")
        }

        Button(
            onClick = {
                route = Routes.TEXT_DETECTION.route
                checkAndRequestCameraPermission(
                    context,
                    permission,
                    launcher,
                    navController,
                    route
                )
            },
            modifier = Modifier.padding(top = 24.dp)
        ) {
            Text(text = "Text Detector")
        }
    }
}

fun NavGraphBuilder.composable(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    content: @Composable (NavBackStackEntry) -> Unit
) {
    addDestination(
        ComposeNavigator.Destination(provider[ComposeNavigator::class], content).apply {
            this.route = route
            arguments.forEach { (argumentName, argument) ->
                addArgument(argumentName, argument)
            }
            deepLinks.forEach { deepLink ->
                addDeepLink(deepLink)
            }
        }
    )
}

fun checkAndRequestCameraPermission(
    context: Context,
    permission: String,
    launcher: ManagedActivityResultLauncher<String, Boolean>,
    navController: NavController,
    route: String
) {
    val permissionCheckResult = ContextCompat.checkSelfPermission(context, permission)
    if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
        navController.navigate(route)
    } else {
        launcher.launch(permission)
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MlKitTestTheme {}
}

enum class Routes(val route: String) {
    PERMISSION("permission"),
    FACE_DETECTION("faceDetection"),
    BARCODE_DETECTOR("barcodeDetector"),
    TEXT_DETECTION("textDetection");
}
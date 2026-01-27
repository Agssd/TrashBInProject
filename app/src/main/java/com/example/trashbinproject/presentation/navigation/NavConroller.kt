import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.trashbinproject.data.storage.TokenManager
import com.example.trashbinproject.presentation.main.TrashBinViewModel
import com.example.trashbinproject.presentation.auth.AuthViewModel
import com.example.trashbinproject.presentation.auth.LoginScreen
import com.example.trashbinproject.presentation.auth.OnBoarding.OnboardingScreen1
import com.example.trashbinproject.presentation.auth.OnBoarding.OnboardingScreen2
import com.example.trashbinproject.presentation.auth.OnBoarding.OnboardingScreen3
import com.example.trashbinproject.presentation.auth.SplashScreen
import com.example.trashbinproject.presentation.profile.ProfileScreen
import com.example.trashbinproject.presentation.scanner.ResultScreen
import com.example.trashbinproject.presentation.scanner.ScannerScreen
import com.example.trashbinproject.presentation.scanner.ScannerViewModel
import com.example.zteam.trash.ProfileViewModel
import com.example.zteam.trash.TrashBinScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.getKoin
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AppNavHost(navController: NavHostController) {
    val scannerViewModel: ScannerViewModel = koinViewModel()

    val tokenManager: TokenManager = getKoin().get()
    val token by tokenManager.token.collectAsState(initial = null)

    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(500)
        isLoading = false
    }

    val startDestination = if (isLoading) {
        "splash"
    } else if (token.isNullOrEmpty()) {
        "onboarding1"
    } else {
        "trash_bin"
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = {
            slideInVertically(initialOffsetY = { 1000 }) + fadeIn() + scaleIn(initialScale = 0.9f)
        },
        exitTransition = {
            slideOutVertically(targetOffsetY = { -1000 }) + fadeOut() + scaleOut(targetScale = 0.9f)
        }
    ) {
        composable("splash") {
            SplashScreen()
        }
        composable("onboarding1") {
            OnboardingScreen1(
                onNext = { navController.navigate("onboarding2") },
                onSkip = { navController.navigate("login") }
            )
        }
        composable("onboarding2") {
            OnboardingScreen2(
                onNext = { navController.navigate("onboarding3") },
                onSkip = { navController.navigate("login") }
            )
        }
        composable("onboarding3") {
            val coroutineScope = rememberCoroutineScope()
            OnboardingScreen3(
                onNext = { navController.navigate("login") },
                onSkip = { navController.navigate("login") }
            )
        }

        composable("login") {
            val authViewModel: AuthViewModel = koinViewModel()
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = { navController.navigate("trash_bin") },
                onNavigateToRegister = { navController.navigate("register") }
            )
        }

        composable("register") {
            val authViewModel: AuthViewModel = koinViewModel()
            RegisterScreen(
                viewModel = authViewModel,
                onRegisterSuccess = {
                    navController.navigate("trash_bin") {
                        popUpTo("register") { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                }
            )
        }

        composable("trash_bin") {
            val trashBinViewModel: TrashBinViewModel = viewModel()
            val profileViewModel: ProfileViewModel = koinViewModel()
            TrashBinScreen(
                trashBinViewModel = trashBinViewModel,
                onNavigateToScanner = {
                    navController.navigate("scanner")
                },
                profileViewModel = profileViewModel,
                onProfileClick = {
                    navController.navigate("profile")
                }
            )
        }

        composable("profile") {
            val profileViewModel: ProfileViewModel = koinViewModel()
            ProfileScreen(
                profileViewModel = profileViewModel,
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("profile") { inclusive = true }
                    }
                }
            )
        }

        composable("scanner") {
            ScannerScreen(
                scannerViewModel = scannerViewModel,
                onPhotoCaptured = {
                    navController.navigate("preview")
                }
            )
        }

        composable("preview") {
            PreviewScreen(
                scannerViewModel = scannerViewModel,
                onRetake = { navController.popBackStack() },
                onAnalyzeComplete = {
                    navController.navigate("result")
                }
            )
        }

        composable("result") {
            val profileViewModel: ProfileViewModel = koinViewModel()
            ResultScreen(
                scannerViewModel = scannerViewModel,
                onBackToMain = {
                    navController.navigate("trash_bin") {
                        popUpTo("result") { inclusive = true }
                    }
                },
                profileViewModel = profileViewModel
            )
        }
    }
}


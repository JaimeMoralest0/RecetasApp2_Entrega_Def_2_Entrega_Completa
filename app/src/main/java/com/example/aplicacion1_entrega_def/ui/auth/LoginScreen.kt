package com.example.aplicacion1_entrega_def.ui.auth

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showSuccessMessage by remember { mutableStateOf(false) }
    var googleSignInError by remember { mutableStateOf<String?>(null) }
    var showLogoutMessage by remember { mutableStateOf(false) }
    
    val context = LocalContext.current
    val authState by viewModel.authState.collectAsState()
    val scope = rememberCoroutineScope()


    val googleSignInClient = remember(context) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("581194068433-kcabjn2dbcfrke9c3k73ait5p3f587md.apps.googleusercontent.com")
            .requestEmail()
            .build()
        GoogleSignIn.getClient(context, gso)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        when (result.resultCode) {
            Activity.RESULT_OK -> {
                try {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    val account = task.getResult(ApiException::class.java)
                    account.idToken?.let { token ->
                        scope.launch {
                            try {
                                viewModel.signInWithGoogle(token)
                            } catch (e: Exception) {
                                googleSignInError = "Error al iniciar sesión con Google: ${e.message}"
                            }
                        }
                    } ?: run {
                        googleSignInError = "No se pudo obtener el token de Google"
                    }
                } catch (e: ApiException) {
                    googleSignInError = when (e.statusCode) {
                        CommonStatusCodes.NETWORK_ERROR -> "Error de conexión. Verifica tu conexión a internet"
                        CommonStatusCodes.INTERNAL_ERROR -> "Error interno. Inténtalo de nuevo"
                        CommonStatusCodes.DEVELOPER_ERROR -> "Error de configuración. Contacta al soporte"
                        CommonStatusCodes.CANCELED -> "Inicio de sesión cancelado por el usuario"
                        else -> "Error al iniciar sesión con Google: ${e.message}"
                    }
                }
            }
            Activity.RESULT_CANCELED -> {
                googleSignInError = "Inicio de sesión cancelado por el usuario"
            }
            else -> {
                googleSignInError = "Error inesperado al iniciar sesión con Google"
            }
        }
    }

    // Función inicio de sesión con google
    fun signInWithGoogle() {
        googleSignInError = null
        scope.launch {
            try {
                // Primero intentamos cerrar sesión para forzar
                googleSignInClient.signOut().await()
                showLogoutMessage = true
                delay(1000)
                showLogoutMessage = false
                // lanzar el intent de inicio de sesión
                launcher.launch(googleSignInClient.signInIntent)
            } catch (e: Exception) {
                googleSignInError = "Error al iniciar el proceso de Google Sign-In: ${e.message}"
            }
        }
    }
    
    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            showSuccessMessage = true
            delay(2000)
            onLoginSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Iniciar Sesión",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        Button(
            onClick = { viewModel.signInWithEmailAndPassword(email, password) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            Text("Iniciar Sesión")
        }

        OutlinedButton(
            onClick = { signInWithGoogle() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            Text("Iniciar Sesión con Google")
        }

        OutlinedButton(
            onClick = { viewModel.signInAnonymously() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Text("Continuar como Invitado")
        }

        TextButton(onClick = onNavigateToRegister) {
            Text("¿No tienes cuenta? Regístrate")
        }

        if (authState is AuthState.Error) {
            Text(
                text = (authState as AuthState.Error).message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        if (googleSignInError != null) {
            Text(
                text = googleSignInError!!,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        if (showLogoutMessage) {
            Text(
                text = "Sesión cerrada correctamente",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        if (authState is AuthState.Loading) {
            CircularProgressIndicator(
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        if (showSuccessMessage) {
            Text(
                text = if (authState is AuthState.Success && viewModel.isGoogleSignIn) 
                    "¡Inicio de sesión con Google exitoso! Redirigiendo..." 
                else 
                    "¡Inicio de sesión exitoso! Redirigiendo...",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
} 
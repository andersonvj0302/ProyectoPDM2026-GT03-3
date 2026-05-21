package com.example.autotechs.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.autotechs.data.local.AppDatabase
import com.example.autotechs.data.local.SessionManager
import com.example.autotechs.data.remote.RetrofitClient
import com.example.autotechs.data.repository.UsuarioRepository
import com.example.autotechs.databinding.ActivityIniciarSesionBinding
import kotlinx.coroutines.launch

class iniciar_sesion : AppCompatActivity() {
    private lateinit var binding: ActivityIniciarSesionBinding
    private lateinit var viewModel: AuthViewModel
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityIniciarSesionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        sessionManager = SessionManager(this)
        
        // Verificar sesión activa
        if (sessionManager.isLoggedIn()) {
            val intent = Intent(this, inicio::class.java)
            startActivity(intent)
            finish()
            return
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Configuración manual del ViewModel
        val database = AppDatabase.getDatabase(this)
        val apiService = RetrofitClient.apiService
        val repository = UsuarioRepository(database.usuarioDao(), apiService)
        
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(repository, sessionManager) as T
            }
        }
        viewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        binding.btnClicRegistrarse.setOnClickListener {
            val email = binding.txtEmail.text.toString().trim()
            val pass = binding.txtPassword.text.toString().trim()

            if (email.isBlank() || pass.isBlank()) {
                Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.login(email, pass)
        }

        // Opción para ir a registro (podríamos añadir un TextView en el XML, por ahora lo dejamos así)

        lifecycleScope.launch {
            viewModel.authState.collect { state ->
                when (state) {
                    is AuthState.Loading -> {
                        binding.btnClicRegistrarse.isEnabled = false
                        binding.btnClicRegistrarse.text = "Iniciando..."
                    }
                    is AuthState.Success -> {
                        Toast.makeText(this@iniciar_sesion, "Bienvenido ${state.usuario.nombre}", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@iniciar_sesion, inicio::class.java)
                        startActivity(intent)
                        finish()
                    }
                    is AuthState.Error -> {
                        binding.btnClicRegistrarse.isEnabled = true
                        binding.btnClicRegistrarse.text = "Iniciar Sesion"
                        Toast.makeText(this@iniciar_sesion, state.error, Toast.LENGTH_SHORT).show()
                    }
                    else -> {}
                }
            }
        }
    }
}

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
import com.example.autotechs.databinding.ActivityRegistrarseBinding
import kotlinx.coroutines.launch

class registrarse : AppCompatActivity() {
    private lateinit var binding: ActivityRegistrarseBinding
    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegistrarseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Configuración manual del ViewModel
        val database = AppDatabase.getDatabase(this)
        val apiService = RetrofitClient.apiService
        val repository = UsuarioRepository(database.usuarioDao(), apiService)
        val sessionManager = SessionManager(this)
        
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(repository, sessionManager) as T
            }
        }
        viewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        binding.btnClicRegistrarse.setOnClickListener {
            val nombre = binding.editTextText.text.toString()
            val email = binding.editTextTextEmailAddress.text.toString()
            val pass = binding.editTextTextPassword.text.toString()
            val passConfirm = binding.editTextTextPassword2.text.toString()

            if (nombre.isBlank() || email.isBlank() || pass.isBlank()) {
                Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (pass != passConfirm) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.registrar(nombre, email, pass)
        }

        lifecycleScope.launch {
            viewModel.authState.collect { state ->
                when (state) {
                    is AuthState.Loading -> {
                        binding.btnClicRegistrarse.isEnabled = false
                        binding.btnClicRegistrarse.text = "Registrando..."
                    }
                    is AuthState.RegisterSuccess -> {
                        Toast.makeText(this@registrarse, "Registro exitoso", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@registrarse, iniciar_sesion::class.java)
                        startActivity(intent)
                        finish()
                    }
                    is AuthState.Error -> {
                        binding.btnClicRegistrarse.isEnabled = true
                        binding.btnClicRegistrarse.text = "REGISTRARSE"
                        Toast.makeText(this@registrarse, state.error, Toast.LENGTH_SHORT).show()
                    }
                    else -> {}
                }
            }
        }
    }
}

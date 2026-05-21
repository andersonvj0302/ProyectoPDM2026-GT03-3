package com.example.autotechs.ui

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.autotechs.R
import com.example.autotechs.data.local.AppDatabase
import com.example.autotechs.data.local.entity.UsuarioEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CrearDuenoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_crear_dueno)

        val mainView = findViewById<android.view.View>(R.id.main)
        ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnAtras = findViewById<ImageButton>(R.id.btnAtras)
        val btnCrear = findViewById<Button>(R.id.btnCrearDueno)
        val txtNombre = findViewById<EditText>(R.id.txtNombre)
        val txtEmail = findViewById<EditText>(R.id.txtEmail)
        val txtPassword = findViewById<EditText>(R.id.txtPassword)
        val txtPasswordConfirm = findViewById<EditText>(R.id.txtPasswordConfirm)

        btnAtras.setOnClickListener {
            finish()
        }

        btnCrear.setOnClickListener {
            val nombre = txtNombre.text.toString().trim()
            val email = txtEmail.text.toString().trim()
            val password = txtPassword.text.toString().trim()
            val passwordConfirm = txtPasswordConfirm.text.toString().trim()

            if (nombre.isEmpty() || email.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty()) {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != passwordConfirm) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Inserción en la BD en un hilo secundario
            val database = AppDatabase.getDatabase(this)
            val dao = database.usuarioDao()

            lifecycleScope.launch(Dispatchers.IO) {
                val existingUser = dao.getUserByEmail(email)
                if (existingUser != null) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@CrearDuenoActivity, "El email ya está registrado", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }

                val nuevoDueno = UsuarioEntity(
                    nombre = nombre,
                    email = email,
                    contrasena = password,
                    rol = "Dueño"
                )
                dao.insertUsuario(nuevoDueno)

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CrearDuenoActivity, "Dueño creado exitosamente", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }
}

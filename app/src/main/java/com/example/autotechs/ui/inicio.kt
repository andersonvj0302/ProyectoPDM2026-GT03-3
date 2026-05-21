package com.example.autotechs.ui

import com.example.autotechs.R

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat

import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import com.example.autotechs.data.local.SessionManager
import androidx.lifecycle.lifecycleScope
import com.example.autotechs.data.local.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class inicio : AppCompatActivity() {
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_inicio)
        
        sessionManager = SessionManager(this)
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBar)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)

        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // Aplicar lógica de roles al menú
        val role = sessionManager.getUserRole() ?: "Cliente"
        val menu = navigationView.menu

        when (role) {
            "Administrador" -> {
                // El administrador ve todo, incluido Crear Dueño
                menu.findItem(R.id.nav_tracking)?.isVisible = false
                menu.findItem(R.id.nav_crear_dueno)?.isVisible = true
            }
            "Dueño" -> {
                // El dueño ve todo excepto crear otro dueño y tracking de cliente
                menu.findItem(R.id.nav_tracking)?.isVisible = false
                menu.findItem(R.id.nav_crear_dueno)?.isVisible = false
            }
            "Tecnico" -> {
                // Solo ve siniestros, inventario y tracking
                menu.findItem(R.id.nav_clientes)?.isVisible = false
                menu.findItem(R.id.nav_vehiculos)?.isVisible = false
                menu.findItem(R.id.nav_facturacion)?.isVisible = false
                menu.findItem(R.id.nav_quejas)?.isVisible = false
                menu.findItem(R.id.nav_tracking)?.isVisible = false
                menu.findItem(R.id.nav_crear_dueno)?.isVisible = false
            }
            "Aseguradora" -> {
                menu.findItem(R.id.nav_clientes)?.isVisible = false
                menu.findItem(R.id.nav_vehiculos)?.isVisible = false
                menu.findItem(R.id.nav_inventario)?.isVisible = false
                menu.findItem(R.id.nav_tracking)?.isVisible = false
                menu.findItem(R.id.nav_crear_dueno)?.isVisible = false
            }
            "Cliente" -> {
                // El cliente SOLO ve el tracking de su reparación
                menu.findItem(R.id.nav_clientes)?.isVisible = false
                menu.findItem(R.id.nav_vehiculos)?.isVisible = false
                menu.findItem(R.id.nav_siniestros)?.isVisible = false
                menu.findItem(R.id.nav_inventario)?.isVisible = false
                menu.findItem(R.id.nav_facturacion)?.isVisible = false
                menu.findItem(R.id.nav_quejas)?.isVisible = false
                menu.findItem(R.id.nav_crear_dueno)?.isVisible = false
                // nav_tracking queda visible
            }
        }

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_clientes -> {
                    startActivity(Intent(this, ClientesActivity::class.java))
                }
                R.id.nav_vehiculos -> {
                    startActivity(Intent(this, Vehiculos::class.java))
                }
                R.id.nav_siniestros -> {
                    startActivity(Intent(this, Siniestros::class.java))
                }
                R.id.nav_inventario -> {
                    startActivity(Intent(this, Inventario::class.java))
                }
                R.id.nav_facturacion -> {
                    startActivity(Intent(this, Facturacion::class.java))
                }
                R.id.nav_quejas -> {
                    startActivity(Intent(this, QuejasActivity::class.java))
                }
                R.id.nav_tracking -> {
                    val userEmail = sessionManager.getUserEmail()
                    val userRole  = sessionManager.getUserRole() ?: "Cliente"
                    if (userRole == "Cliente" && userEmail != null) {
                        // Búsqueda en cascada: cliente → vehículo → expediente
                        lifecycleScope.launch(Dispatchers.IO) {
                            val db = AppDatabase.getDatabase(this@inicio)
                            val cliente = db.clienteDao().getClienteByEmail(userEmail)
                            if (cliente == null) {
                                withContext(Dispatchers.Main) {
                                    android.widget.Toast.makeText(
                                        this@inicio,
                                        "No se encontró su registro de cliente. Consulte con el taller.",
                                        android.widget.Toast.LENGTH_LONG
                                    ).show()
                                }
                                return@launch
                            }
                            val vehiculo = db.vehiculoDao().getVehiculoByClienteIdOnce(cliente.id)
                            if (vehiculo == null) {
                                withContext(Dispatchers.Main) {
                                    android.widget.Toast.makeText(
                                        this@inicio,
                                        "No tiene ningún vehículo registrado en el taller aún.",
                                        android.widget.Toast.LENGTH_LONG
                                    ).show()
                                }
                                return@launch
                            }
                            val expediente = db.recepcionDao().getExpedienteByVin(vehiculo.vin)
                            if (expediente == null) {
                                withContext(Dispatchers.Main) {
                                    android.widget.Toast.makeText(
                                        this@inicio,
                                        "No hay una reparación activa para su vehículo (${vehiculo.marca} ${vehiculo.modelo}).",
                                        android.widget.Toast.LENGTH_LONG
                                    ).show()
                                }
                                return@launch
                            }
                            withContext(Dispatchers.Main) {
                                val intent = Intent(this@inicio, TallerMonitorActivity::class.java)
                                intent.putExtra("expedienteId", expediente.id)
                                intent.putExtra("modoLectura", true)
                                startActivity(intent)
                            }
                        }
                    } else {
                        startActivity(Intent(this, TallerMonitorActivity::class.java))
                    }
                }
                R.id.nav_crear_dueno -> {
                    startActivity(Intent(this, CrearDuenoActivity::class.java))
                }
                R.id.nav_logout -> {
                    sessionManager.logout()
                    val intent = Intent(this, iniciar_sesion::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        // Cargar últimos vehículos reparados en el inicio
        cargarUltimosVehiculos()
    }

    private fun cargarUltimosVehiculos() {
        val layoutVehiculos = findViewById<android.widget.LinearLayout>(R.id.layout_ultimos_vehiculos)
        if (layoutVehiculos == null) return

        val database = AppDatabase.getDatabase(this)
        
        lifecycleScope.launch {
            database.vehiculoDao().getAllVehiculos().collect { list ->
                layoutVehiculos.removeAllViews()
                
                if (list.isEmpty()) {
                    // Vehículos de demostración si la base de datos está vacía
                    val demos = listOf(
                        Triple("Toyota Corolla 2022", "Alineación y balanceo completado.", R.drawable.img_car_sedan),
                        Triple("Jeep Grand Cherokee", "Reparación de suspensión completada.", R.drawable.img_car_suv),
                        Triple("Ford Mustang GT", "Revisión del sistema de frenado.", R.drawable.img_car_sport)
                    )
                    
                    for (demo in demos) {
                        val itemView = layoutInflater.inflate(R.layout.item_list_home, layoutVehiculos, false)
                        itemView.findViewById<android.widget.ImageView>(R.id.img_vehiculo)?.setImageResource(demo.third)
                        itemView.findViewById<android.widget.TextView>(R.id.txt_titulo)?.text = demo.first
                        itemView.findViewById<android.widget.TextView>(R.id.txt_descripcion)?.text = demo.second
                        layoutVehiculos.addView(itemView)
                    }
                } else {
                    // Cargar hasta 3 vehículos reales de la base de datos
                    val limitList = list.take(3)
                    for ((index, vehiculo) in limitList.withIndex()) {
                        val itemView = layoutInflater.inflate(R.layout.item_list_home, layoutVehiculos, false)
                        
                        val iconRes = when (index % 3) {
                            0 -> R.drawable.img_car_sedan
                            1 -> R.drawable.img_car_suv
                            else -> R.drawable.img_car_sport
                        }
                        
                        itemView.findViewById<android.widget.ImageView>(R.id.img_vehiculo)?.setImageResource(iconRes)
                        itemView.findViewById<android.widget.TextView>(R.id.txt_titulo)?.text = "${vehiculo.marca} ${vehiculo.modelo}"
                        itemView.findViewById<android.widget.TextView>(R.id.txt_descripcion)?.text = "Placa: ${vehiculo.placa} | VIN: ${vehiculo.vin}"
                        
                        layoutVehiculos.addView(itemView)
                    }
                }
            }
        }
    }
}

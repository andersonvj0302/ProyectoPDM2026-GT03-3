package com.example.autotechs

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

class inicio : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_inicio)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBar)

        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // Navegacion entre pantallas

        val navigationView = findViewById<NavigationView>(R.id.nav_view)



        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_clientes -> { // Asegúrate que este ID coincida con tu nav_menu.xml
                    val intent = Intent(this, ClientesActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_vehiculos -> {
                    val intent = Intent(this, Vehiculos::class.java)
                    startActivity(intent)
                }
                R.id.nav_siniestros -> {
                    val intent = Intent(this, Siniestros::class.java)
                    startActivity(intent)
                }
                R.id.nav_inventario -> {
                    val intent = Intent(this, Inventario::class.java)
                    startActivity(intent)
                }
                R.id.nav_facturacion -> {
                    val intent = Intent(this, Facturacion::class.java)
                    startActivity(intent)
                }

            }

            // Cerrar el menú después de hacer clic
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }
}
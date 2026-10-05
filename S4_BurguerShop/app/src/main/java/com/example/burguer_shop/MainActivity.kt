package com.example.burguer_shop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.burguer_shop.ui.theme.Burguer_shopTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    //CatalogoHamburguesas(catalogohamburguesas)
                }
            }
        }
    }

    data class Producto(
        val nombre: String,
        val precio: Double,
        val imanResId: Int
    )

    val catalogoHamburguesas = listOf(
        Producto("Hamburguesa Clásica", 9.99, R.drawable.burger_clasica),
        Producto("Hamburguesa BBQ", 12.99, R.drawable.burger_bbq),
        Producto("Hamburguesa Doble", 11.99, R.drawable.burger_doble),
        Producto("Hamburguesa de Pollo", 10.99, R.drawable.burger_pollo),
        Producto("Vegetariana", 13.99, R.drawable.burger_vegetariana),
        Producto("Picante Jalapeño", 14.99, R.drawable.burger_picante),
    )

}


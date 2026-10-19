package com.example.burguer_shop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    PantallaPrincipal()
                }
            }
        }
    }

    data class Producto(
        val nombre: String,
        val precio: Double,
        val imagenResId: Int
    )

    val catalogoHamburguesas = listOf(
        Producto(
            "Hamburguesa Clásica",
            9.99,
            R.drawable.burger_clasica
        ),
        Producto(
            "Hamburguesa BBQ",
            12.99,
            R.drawable.burger_bbq
        ),
        Producto(
            "Hamburguesa Doble",
            11.99,
            R.drawable.burger_doble
        ),
        Producto(
            "Hamburguesa de Pollo",
            10.99,
            R.drawable.burger_pollo
        ),
        Producto(
            "Vegetariana",
            13.99,
            R.drawable.burger_vegetariana
        ),
        Producto(
            "Picante Jalapeño",
            14.99,
            R.drawable.burger_picante
        ),
    )

    @Composable
    fun CatalogoHamburguesas(productos: List<Producto>) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(productos) { producto ->
                TarjetaProducto(producto)
            }
        }
    }

    @Composable
    fun TarjetaProducto(producto: Producto) {
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Image(
                    painter = painterResource(id = producto.imagenResId),
                    contentDescription = producto.nombre,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentScale = ContentScale.Crop
                )

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = producto.nombre,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${producto.precio} €",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Button(
                        onClick = {

                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Añadir al carrito")
                    }
                }
            }
        }
    }

    // PANTALLA DE PORTADA
    @Composable // PERMITE PINTAR ALGO EN LA PANTALLA
    fun PantallaPrincipal() {

        // Remember + mutableStateOf: crea una variable que Compose vigila.
        // Cuando su valor cambia, Compose vuelve a dibujar la pantalla solo
        // sin que tengas que hacer nada más.

        var mostrarPortada by remember { mutableStateOf(true) }

        LaunchedEffect(Unit) {  // Lanza una tarea que se ejecuta una sola vez.
            // Cuando la pantalla aparece, debe esperar 1.5 seg y luego
            // cambia de estado para ocultar la portada.
            delay(1500)
            mostrarPortada = false
        }

        if (mostrarPortada) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("🍔 BurgerShop", fontSize = 32.sp, fontWeight = FontWeight.Bold)
            }
        } else {
            CatalogoHamburguesas(catalogoHamburguesas)
        }
    }
}

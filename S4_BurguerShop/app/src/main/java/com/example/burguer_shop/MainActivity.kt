package com.example.burguer_shop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    CatalogoHamburguesas(catalogoHamburguesas)
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
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
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
                    )
                    Spacer(modifier = Modifier.height(4.dp))

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
}
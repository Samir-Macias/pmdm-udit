package com.example.myappli

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import java.io.File
import java.io.FileOutputStream
import java.net.URLEncoder

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider

import coil.compose.AsyncImage
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TarjetaPresentacionWrapper()
        }
    }
}

@Composable
fun TarjetaPresentacionWrapper() {
    var isDarkMode by remember { mutableStateOf(false) }

    MaterialTheme(
        colorScheme = if (isDarkMode) darkColorScheme() else lightColorScheme()
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            TarjetaPresentacion(
                isDarkMode = isDarkMode,
                onDarkModeChange = { isDarkMode = it }
            )
        }
    }
}

@Composable
fun TarjetaPresentacion(
    isDarkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit
) {
    val context = LocalContext.current

    // Estado para controlar qué QR mostrar en el diálogo
    var qrDialogData by remember { mutableStateOf<QrData?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(26.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Interruptor Modo Oscuro / Claro
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (isDarkMode) "Modo Oscuro" else "Modo Claro",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.width(8.dp))
            Switch(
                checked = isDarkMode,
                onCheckedChange = onDarkModeChange
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Imagen de perfil
        Image(
            painter = painterResource(id = R.drawable.foto_perfil),
            contentDescription = "Foto de perfil de usuario",
            modifier = Modifier
                .size(190.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
            alignment = Alignment.TopCenter
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Samir Macias",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "Desarrollador FullStack",
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Botón Descargar CV (Local)
        AnimatedButton(
            text = "Descargar mi CV",
            backgroundColor = MaterialTheme.colorScheme.primary,
            onClick = {
                abrirPdfLocal(
                    context = context,
                    rawResId = R.raw.cvsamirdev,
                    nombreArchivo = "cvsamirdev.pdf"
                )
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Botón LinkedIn (Abre QR)
        AnimatedButton(
            text = "Mi perfil de LinkedIn",
            backgroundColor = Color(0xFF0A66C2),
            onClick = {
                qrDialogData = QrData(
                    titulo = "LinkedIn",
                    url = "https://www.linkedin.com/in/samirmacias"
                )
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Botón GitHub (Abre QR)
        AnimatedButton(
            text = "Mi repositorio de GitHub",
            backgroundColor = Color(0xFF24292E),
            onClick = {
                qrDialogData = QrData(
                    titulo = "GitHub",
                    url = "https://github.com/Samir-Macias"
                )
            }
        )
    }

    // Diálogo emergente con el código QR
    qrDialogData?.let { data ->
        QrDialog(
            qrData = data,
            onDismiss = { qrDialogData = null },
            onOpenLink = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(data.url))
                context.startActivity(intent)
                qrDialogData = null
            }
        )
    }
}

// Modelo de datos para el QR
data class QrData(val titulo: String, val url: String)

// Función para generar un Bitmap de código QR localmente
fun generateQrCodeBitmap(content: String, size: Int = 512): Bitmap? {
    return try {
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(
                    x, y,
                    if (bitMatrix.get(x, y)) android.graphics.Color.BLACK else android.graphics.Color.WHITE
                )
            }
        }
        bitmap
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

// Componente para mostrar el diálogo con el QR
@Composable
fun QrDialog(
    qrData: QrData,
    onDismiss: () -> Unit,
    onOpenLink: () -> Unit
) {
    val qrBitmap = remember(qrData.url) {
        generateQrCodeBitmap(qrData.url)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Escanear ${qrData.titulo}") },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Escanea este código con otro móvil para abrir el enlace:",
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                if (qrBitmap != null) {
                    Image(
                        bitmap = qrBitmap.asImageBitmap(),
                        contentDescription = "Código QR de ${qrData.titulo}",
                        modifier = Modifier.size(200.dp)
                    )
                } else {
                    val encodedUrl = URLEncoder.encode(qrData.url, "UTF-8")
                    val qrApiUrl = "https://api.qrserver.com/v1/create-qr-code/?size=250x250&data=$encodedUrl"
                    AsyncImage(
                        model = qrApiUrl,
                        contentDescription = "Código QR de ${qrData.titulo}",
                        modifier = Modifier.size(200.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onOpenLink) {
                Text("Abrir en este dispositivo")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}

@Composable
fun AnimatedButton(
    text: String,
    backgroundColor: Color,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.93f else 1.0f,
        label = "ButtonScale"
    )

    Button(
        onClick = onClick,
        interactionSource = interactionSource,
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .scale(scale)
    ) {
        Text(text = text, color = Color.White)
    }
}

fun abrirPdfLocal(context: Context, rawResId: Int, nombreArchivo: String) {
    try {
        val cacheFile = File(context.cacheDir, nombreArchivo)

        context.resources.openRawResource(rawResId).use { inputStream ->
            FileOutputStream(cacheFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }

        val contentUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            cacheFile
        )

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(contentUri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
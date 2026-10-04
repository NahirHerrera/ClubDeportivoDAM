package com.example.clubdeportivo_equipo1

import android.content.ContentValues
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Comprobante de pago de ACTIVIDAD. Se abre desde ActividadActivity al confirmar el pago
class ComprobantePagoActivity : AppCompatActivity() {

    companion object {
        // El DNI viaja en CobrarCuotaActivity.EXTRA_DNI_SOCIO (igual que en el carnet)
        const val EXTRA_ACTIVIDADES = "actividades"   // ArrayList<String>
        const val EXTRA_MEDIO_PAGO = "medio_pago"
        const val EXTRA_MONTO = "monto"
    }

    private val formatoMoneda = NumberFormat.getCurrencyInstance(Locale("es", "AR"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_comprobante)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val dni = intent.getStringExtra(CobrarCuotaActivity.EXTRA_DNI_SOCIO)
        val socio = dni?.let { SociosRepository.buscarPorDni(it) }
        if (socio == null) {
            Toast.makeText(this, "No se pudo cargar el comprobante", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val actividades = intent.getStringArrayListExtra(EXTRA_ACTIVIDADES).orEmpty()
        val medioPago = intent.getStringExtra(EXTRA_MEDIO_PAGO) ?: "-"
        val monto = intent.getDoubleExtra(EXTRA_MONTO, 0.0)

        findViewById<TextView>(R.id.tvFecha).text =
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        findViewById<TextView>(R.id.tvNombre).text = socio.nombre.uppercase()
        findViewById<TextView>(R.id.tvApellido).text = socio.apellido.uppercase()
        findViewById<TextView>(R.id.tvMedioPago).text = medioPago
        findViewById<TextView>(R.id.tvTotal).text = formatoMoneda.format(monto)

        val btnDescargar = findViewById<Button>(R.id.btnDescargar)
        val btnVolver = findViewById<Button>(R.id.btnVolver)

        // Botón Descargar: genera un PDF de la pantalla (sin los botones)
        btnDescargar.setOnClickListener {
            val marca = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val guardado = guardarPdf(
                findViewById(R.id.main),
                "comprobante_actividad_${socio.dni}_$marca",
                listOf(btnDescargar, btnVolver)
            )
            Toast.makeText(
                this,
                if (guardado) "Comprobante guardado en Descargas" else "No se pudo guardar el comprobante",
                Toast.LENGTH_LONG
            ).show()
        }

        // Botón Volver
        btnVolver.setOnClickListener { finish() }
    }

    // Dibuja la pantalla en un PDF y lo guarda en Descargas. Los botones no salen en el PDF.
    private fun guardarPdf(vista: View, nombreArchivo: String, botones: List<View>): Boolean {
        if (vista.width == 0 || vista.height == 0) return false

        botones.forEach { it.visibility = View.INVISIBLE }
        val pdf = PdfDocument()
        return try {
            val info = PdfDocument.PageInfo.Builder(vista.width, vista.height, 1).create()
            val pagina = pdf.startPage(info)
            vista.draw(pagina.canvas)
            pdf.finishPage(pagina)

            val salida = abrirSalida(nombreArchivo) ?: return false
            salida.use { pdf.writeTo(it) }
            true
        } catch (e: Exception) {
            false
        } finally {
            pdf.close()
            botones.forEach { it.visibility = View.VISIBLE }
        }
    }

    private fun abrirSalida(nombre: String): OutputStream? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val valores = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, "$nombre.pdf")
                put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
                put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
            val uri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, valores)
                ?: return null
            contentResolver.openOutputStream(uri)
        } else {
            // Android 9 o menor: carpeta propia de la app (no necesita permisos)
            val carpeta = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) ?: return null
            FileOutputStream(File(carpeta, "$nombre.pdf"))
        }
    }
}
package com.ucne.r_aportes

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key.Companion.Calendar
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.room.Room
import com.ucne.r_aportes.data.local.database.AporteDb
import com.ucne.r_aportes.data.local.entities.AporteEntity
import com.ucne.r_aportes.presentation.AporteListScreen
import com.ucne.r_aportes.ui.theme.R_AportesTheme
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {
    private lateinit var aporteDb: AporteDb
    var aporte: AporteEntity = AporteEntity()

    private var showDialog by mutableStateOf(false)
    private var showDatePicker by mutableStateOf(false)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        aporteDb = Room.databaseBuilder(
            this,
            AporteDb::class.java,
            "Aporte.db"
        )
            .fallbackToDestructiveMigration()
            .build()

        enableEdgeToEdge()
        setContent {
            R_AportesTheme {
                Scaffold { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(8.dp)
                    ){
                        val aportes: List<AporteEntity> by getAportes().collectAsStateWithLifecycle(
                            initialValue = emptyList()
                        )

                        var persona by remember { mutableStateOf("") }
                        var observacion by remember { mutableStateOf("") }
                        var monto by remember { mutableStateOf<Double?>(null) }
                        var aporteId by remember { mutableStateOf("") }
                        var fecha by remember { mutableStateOf(LocalDate.now()) }

                        ElevatedCard(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            ) {

                                OutlinedTextField(
                                    label = { Text(text = "Fecha") },
                                    value = fecha.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")),
                                    readOnly = true,
                                    onValueChange = { },
                                    trailingIcon = {
                                        IconButton(
                                            onClick = {
                                                showDatePicker = true
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.DateRange,
                                                contentDescription = "Date Picker"
                                            )
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth().clickable(onClick = {showDatePicker = true})
                                )

                                OutlinedTextField(
                                    label = { Text(text = "Persona") },
                                    value = persona,
                                    onValueChange = { persona = it },
                                    trailingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = "Campo Persona"
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                OutlinedTextField(
                                    label = { Text(text = "Observación") },
                                    value = observacion,
                                    onValueChange = { observacion = it },
                                    trailingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Info,
                                            contentDescription = "Observación"
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                OutlinedTextField(
                                    label = { Text(text = "Monto") },
                                    value = monto.toString().replace("null", ""),
                                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                                    onValueChange = { monto = it.toDoubleOrNull() },
                                    trailingIcon = {
                                        Icon(
                                            painter = painterResource(id = R.drawable.icons8dollarblack),
                                            contentDescription = "Aporte realizado"
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.padding(2.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    OutlinedButton(
                                        onClick = {
                                            aporteId = ""
                                            persona = ""
                                            observacion = ""
                                            monto = null
                                            fecha = LocalDate.now()
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = "new button"
                                        )
                                        Text(text = "Nuevo")
                                    }
                                    OutlinedButton(
                                        onClick = {
                                            if(validar(persona, monto)){
                                                saveAporte(
                                                    AporteEntity(
                                                        aporteId = aporteId.toIntOrNull(),
                                                        fecha = fecha.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")).toString(),
                                                        persona = persona,
                                                        observacion = observacion,
                                                        monto = monto
                                                    )
                                                )
                                                persona = ""
                                                observacion = ""
                                                monto = null
                                                fecha = LocalDate.now()
                                            }
                                            //showDialog = true
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "save button"
                                        )
                                        Text(text = "Guardar")
                                    }
                                }
                            }

                        }

                        Spacer(modifier = Modifier.padding(2.dp))

                        AporteListScreen(
                            aportes = aportes,
                            onVerAporte = { aporteSeleccionado ->
                                aporteId = aporteSeleccionado.aporteId.toString()
                                persona = aporteSeleccionado.persona.toString()
                                observacion = aporteSeleccionado.observacion.toString()
                                monto = aporteSeleccionado.monto!!
                                fecha = LocalDate.parse(aporteSeleccionado.fecha, DateTimeFormatter.ofPattern("MM/dd/yyyy"))
                            },
                            onDeleteAporte = { aporte ->
                                deleteAporte(aporte)
                            }
                        )

                        if(showDatePicker){
                            val day: Int = fecha.dayOfMonth
                            val month: Int = fecha.monthValue - 1
                            val year: Int = fecha.year

                            val datePickerDialog = DatePickerDialog(
                                this@MainActivity, { _: DatePicker, year: Int, month: Int, day: Int ->
                                    val selectedDate = LocalDate.of(year, month + 1, day)
                                    fecha = selectedDate
                                }, year, month, day
                            )
                            datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
                            datePickerDialog.show()
                            showDatePicker = false
                        }
                    }
                }
            }
        }
    }
    private fun notification(message: String) {
        Toast.makeText(this@MainActivity, message, Toast.LENGTH_LONG).show()
    }
    private fun nuevo(){
        this.aporte.monto = 0.0
        this.aporte.persona = ""
        this.aporte.observacion = ""
    }

    private fun validar(persona: String, monto: Double?): Boolean {
        val regex = Regex("[a-zA-Z ]+") //para que persona no acepte digitos
        var pass = false
        if(persona.isNotEmpty() && monto!! >= 1){
            if(persona.matches(regex)){
                pass = true
                notification("Guardado con éxito!")
            }else {
                notification("Nombre de persona no valido $persona")
            }
        }else{
            notification("Complete los campos correctamente")
        }
        return pass
    }
    fun saveAporte(aporte: AporteEntity) {
        GlobalScope.launch {
            aporteDb.aporteDao().save(aporte)
        }
    }

    fun deleteAporte(aporte: AporteEntity) {
        GlobalScope.launch {
            aporteDb.aporteDao().delete(aporte)
        }
        Toast.makeText(this@MainActivity, "Eliminado con éxito", Toast.LENGTH_SHORT).show()
        //El this@MainActivity hace referencia a la instancia de la actividad actual (MainActivity) en la que se encuentra el código
    }

    fun getAportes(): Flow<List<AporteEntity>> {
        return aporteDb.aporteDao().getAll()
    }
}
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.room.Room
import com.ucne.r_aportes.data.local.database.AporteDb
import com.ucne.r_aportes.data.local.entities.AporteEntity
import com.ucne.r_aportes.presentation.AporteListScreen
import com.ucne.r_aportes.presentation.NavigationItem
import com.ucne.r_aportes.ui.theme.R_AportesTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    private lateinit var aporteDb: AporteDb
    var totalMonto: Double by mutableDoubleStateOf(0.0)
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
                        //NavigationItemStarts
                        val items = listOf(
                            NavigationItem(
                                title = "Registro",
                                selectedIcon = Icons.Filled.Home,
                                unselectedIcon = Icons.Outlined.Home,
                            ),
                            NavigationItem(
                                title = "Total de Aportaciones",
                                selectedIcon = Icons.Filled.Info,
                                unselectedIcon = Icons.Outlined.Info,
                                badgeCount = totalMonto
                            ),
                        )

                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                            val scope = rememberCoroutineScope()
                            var selectedItemIndex by rememberSaveable {
                                mutableStateOf(0)
                            }
                            ModalNavigationDrawer(
                                drawerContent = {
                                    ModalDrawerSheet {
                                        Spacer(modifier = Modifier.height(16.dp))
                                        items.forEachIndexed { index, item ->
                                            NavigationDrawerItem(
                                                label = {
                                                    Text(text = item.title)
                                                },
                                                selected = index == selectedItemIndex,
                                                onClick = {
//                                            navController.navigate(item.route) para futuras navigaciones
                                                    selectedItemIndex = index
                                                    if(selectedItemIndex == 0){
                                                        scope.launch {
                                                            drawerState.close()
                                                        }
                                                    }
                                                },
                                                icon = {
                                                    Icon(
                                                        imageVector = if (index == selectedItemIndex) {
                                                            item.selectedIcon
                                                        } else item.unselectedIcon,
                                                        contentDescription = item.title
                                                    )
                                                },
                                                badge = {
                                                        item.badgeCount?.let {
                                                            Text(text = "$"+ totalMonto.toString().format("%.2f"))
                                                        }
                                                },
                                                modifier = Modifier
                                                    .padding(NavigationDrawerItemDefaults.ItemPadding)
                                            )
                                        }
                                    }
                                },
                                drawerState = drawerState
                            ) {
                                Scaffold(
                                    topBar = {
                                        TopAppBar(
                                            title = {
                                                Text(text = "Registro de Aportes")
                                            },
                                            navigationIcon = {
                                                IconButton(onClick = {
                                                    selectedItemIndex = 1
                                                    scope.launch {
                                                        drawerState.open()
                                                    }
                                                }) {
                                                    Icon(
                                                        imageVector = Icons.Default.Menu,
                                                        contentDescription = "Menu"
                                                    )
                                                }
                                            }
                                        )
                                    }
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(it)
                                            .padding(8.dp)
                                    ){
                                        ElevatedCard(
                                            modifier = Modifier.fillMaxWidth()
                                        ){
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(8.dp)
                                            ){
                                                //RegistroAportes Starts
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
                                                    modifier = Modifier.fillMaxWidth().clickable(onClick = { showDatePicker = true })
                                                )

                                                OutlinedTextField(
                                                    label = { Text(text = "Persona") },
                                                    value = persona,
                                                    onValueChange = { persona = it },
                                                    //isError = persona.isEmpty() || !persona.matches(Regex("[a-zA-Z ]+")),
                                                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
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
                                                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
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
                                                    placeholder = { Text(text = "0.0") },
                                                    prefix = { Text(text = "$") },
                                                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                                                    onValueChange = {
                                                        val regex = Regex("[0-9]*\\.?[0-9]{0,2}")
                                                        if (it.matches(regex)) {
                                                            monto = it.toDoubleOrNull() ?: 0.0
                                                        }
                                                    },
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
                                                updateTotalMonto()
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
                                                updateTotalMonto()
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
                                    //RegistroAportes Ends
                                }
                            }
                        }
                        //NavigationItemEnds
                    }
                }
            }
        }
    }
    private fun notification(message: String) {
        Toast.makeText(this@MainActivity, message, Toast.LENGTH_LONG).show()
    }

    private fun validar(persona: String, monto: Double?): Boolean {
        val regex = Regex("[a-zA-Z ]+") //para que persona no acepte digitos
        var pass = false
        if(persona.isNotEmpty() && (monto ?: 0.0) > 0.0){
            if(persona.matches(regex) && persona.length <= 30){
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

    fun updateTotalMonto() {
        GlobalScope.launch{
            this@MainActivity.totalMonto = withContext(Dispatchers.IO) {
                aporteDb.aporteDao().getAllMontos().sum()
            }
        }
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
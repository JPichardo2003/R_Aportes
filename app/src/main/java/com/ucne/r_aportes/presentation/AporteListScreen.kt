package com.ucne.r_aportes.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ucne.r_aportes.data.local.entities.AporteEntity

@Composable
fun AporteListScreen(
    aportes: List<AporteEntity>,
    onVerAporte: (AporteEntity) -> Unit,
    onDeleteAporte: (AporteEntity) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var aporteToDelete by remember { mutableStateOf<AporteEntity?>(null) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp)
    ) {
        Row( //Encabezados de la lista
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            //Text(text = "ID", modifier = Modifier.weight(0.10f))
            //Text(text = "Fecha", modifier = Modifier.weight(0.40f))
            Text(text = "Persona", modifier = Modifier.weight(0.300f))
            Text(text = "Monto", modifier = Modifier.weight(0.25f))
            Text(text = "Observación", modifier = Modifier.weight(0.40f))
            Spacer(modifier = Modifier.weight(0.05f)) // Espacio adicional para el icono de basura
        }

        Divider() // Línea divisoria entre los títulos y los elementos de la lista

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            items(aportes) { aporte ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onVerAporte(aporte) }
                        .padding(16.dp)
                ) {
                    //Text(text = aporte.aporteId.toString(), modifier = Modifier.weight(0.10f))
                    //Text(text = aporte.fecha.toString(), modifier = Modifier.weight(0.400f))
                    Text(text = aporte.persona.toString(), modifier = Modifier.weight(0.395f))
                    Text(text = aporte.monto.toString(), modifier = Modifier.weight(0.35f))
                    Text(text = aporte.observacion.toString(), modifier = Modifier.weight(0.40f))

                    IconButton(
                        onClick = { aporteToDelete = aporte
                            showDialog = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Ticket"
                        )
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Eliminar Aporte") },
            text = { Text("¿Está seguro de que desea eliminar este aporte?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteAporte(aporteToDelete!!)
                        showDialog = false
                    }
                ) {
                    Text("Sí")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Cancelar")
                }
            },
        )
    }
}
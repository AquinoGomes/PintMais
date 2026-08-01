package com.example.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.HomeWork
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PricingConfig
import com.example.data.RoomEntity
import com.example.ui.QuoteViewModel
import com.example.ui.components.RoomCard
import java.util.Locale

@Composable
fun RoomsTab(
    viewModel: QuoteViewModel,
    rooms: List<RoomEntity>,
    config: PricingConfig,
    modifier: Modifier = Modifier
) {
    var showProjectDetails by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))

            // Card de Dados do Cliente e Projeto
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("project_header_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.HomeWork,
                                contentDescription = "Projeto",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Dados do Imóvel / Cliente",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        OutlinedButton(
                            onClick = { showProjectDetails = !showProjectDetails },
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.testTag("toggle_project_details_btn")
                        ) {
                            Text(if (showProjectDetails) "Ocultar" else "Editar")
                        }
                    }

                    if (showProjectDetails) {
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = config.clientName,
                            onValueChange = { viewModel.updateConfig(config.copy(clientName = it)) },
                            label = { Text("Nome do Cliente / Imóvel") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("client_name_input")
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = config.projectAddress,
                            onValueChange = { viewModel.updateConfig(config.copy(projectAddress = it)) },
                            label = { Text("Endereço / Cidade") },
                            leadingIcon = { Icon(Icons.Default.Place, contentDescription = null) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("project_address_input")
                        )
                    } else {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Cliente: ${config.clientName}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (config.projectAddress.isNotBlank()) {
                            Text(
                                text = "Endereço: ${config.projectAddress}",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Barra de Presets Rápidos de Cômodos
        item {
            Column {
                Text(
                    text = "Adicionar Cômodos Rápido:",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val presets = listOf("Sala", "Quarto 1", "Cozinha", "Banheiro", "Corredor", "Fachada / Varanda")
                    presets.forEach { presetName ->
                        SuggestionChip(
                            onClick = { viewModel.addDefaultRoom(presetName) },
                            label = { Text("+ $presetName") },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            modifier = Modifier.testTag("preset_chip_$presetName")
                        )
                    }
                }
            }
        }

        // Título da Lista
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Cômodos Cadastrados (${rooms.size})",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Button(
                    onClick = { viewModel.addDefaultRoom("Novo Cômodo") },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.testTag("add_room_btn")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Adicionar")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Adicionar")
                }
            }
        }

        // Renderização dos Cômodos
        items(
            items = rooms,
            key = { it.id }
        ) { room ->
            RoomCard(
                room = room,
                onUpdateRoom = { updated -> viewModel.updateRoom(updated) },
                onDeleteRoom = { viewModel.deleteRoom(room) },
                onDuplicateRoom = { viewModel.addDefaultRoom("${room.name} (Cópia)") }
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

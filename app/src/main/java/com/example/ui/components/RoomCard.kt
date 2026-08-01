package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.RoomEntity
import java.util.Locale

@Composable
fun RoomCard(
    room: RoomEntity,
    onUpdateRoom: (RoomEntity) -> Unit,
    onDeleteRoom: () -> Unit,
    onDuplicateRoom: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("room_card_${room.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Cabeçalho do Cômodo: Nome e Ações
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MeetingRoom,
                            contentDescription = "Ícone do Cômodo",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    OutlinedTextField(
                        value = room.name,
                        onValueChange = { onUpdateRoom(room.copy(name = it)) },
                        label = { Text("Nome do Cômodo") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("room_name_input_${room.id}")
                    )
                }

                Row {
                    IconButton(
                        onClick = onDuplicateRoom,
                        modifier = Modifier.testTag("duplicate_room_btn_${room.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Duplicar Cômodo",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(
                        onClick = onDeleteRoom,
                        modifier = Modifier.testTag("delete_room_btn_${room.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Excluir Cômodo",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Dimensões: Largura, Comprimento, Pé-direito
            Text(
                text = "Dimensões do Cômodo",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = if (room.width == 0.0) "" else room.width.toString(),
                    onValueChange = { input ->
                        val parsed = input.replace(',', '.').toDoubleOrNull() ?: 0.0
                        onUpdateRoom(room.copy(width = parsed))
                    },
                    label = { Text("Largura (m)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("room_width_${room.id}")
                )

                OutlinedTextField(
                    value = if (room.length == 0.0) "" else room.length.toString(),
                    onValueChange = { input ->
                        val parsed = input.replace(',', '.').toDoubleOrNull() ?: 0.0
                        onUpdateRoom(room.copy(length = parsed))
                    },
                    label = { Text("Comprimento (m)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("room_length_${room.id}")
                )

                OutlinedTextField(
                    value = if (room.height == 0.0) "" else room.height.toString(),
                    onValueChange = { input ->
                        val parsed = input.replace(',', '.').toDoubleOrNull() ?: 0.0
                        onUpdateRoom(room.copy(height = parsed))
                    },
                    label = { Text("Pé-Direito (m)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("room_height_${room.id}")
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Opções de Serviços
            Text(
                text = "Serviços a Realizar",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(6.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = room.paintWalls,
                            onCheckedChange = { onUpdateRoom(room.copy(paintWalls = it)) },
                            colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.testTag("paint_walls_cb_${room.id}")
                        )
                        Text(text = "Pintar Paredes", fontSize = 14.sp)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = room.paintCeiling,
                            onCheckedChange = { onUpdateRoom(room.copy(paintCeiling = it)) },
                            colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.testTag("paint_ceiling_cb_${room.id}")
                        )
                        Text(text = "Pintar Teto", fontSize = 14.sp)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = room.puttyWalls,
                            onCheckedChange = { onUpdateRoom(room.copy(puttyWalls = it)) },
                            colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.secondary),
                            modifier = Modifier.testTag("putty_walls_cb_${room.id}")
                        )
                        Text(text = "Emassar Paredes", fontSize = 14.sp)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = room.puttyCeiling,
                            onCheckedChange = { onUpdateRoom(room.copy(puttyCeiling = it)) },
                            colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.secondary),
                            modifier = Modifier.testTag("putty_ceiling_cb_${room.id}")
                        )
                        Text(text = "Emassar Teto", fontSize = 14.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Área de Desconto (Portas / Janelas / Vãos)
            OutlinedTextField(
                value = if (room.deductArea == 0.0) "" else room.deductArea.toString(),
                onValueChange = { input ->
                    val parsed = input.replace(',', '.').toDoubleOrNull() ?: 0.0
                    onUpdateRoom(room.copy(deductArea = parsed))
                },
                label = { Text("Descontar Portas/Janelas (m²)") },
                placeholder = { Text("Ex: 2.0 m² para 1 porta") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("deduct_area_input_${room.id}")
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Resumo de Área Líquida Calculada
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                    .padding(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Área Líquida de Pintura:",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${String.format(Locale.getDefault(), "%.2f", room.netPaintArea)} m²",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Área Líquida de Emassamento:",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${String.format(Locale.getDefault(), "%.2f", room.netPuttyArea)} m²",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }
    }
}

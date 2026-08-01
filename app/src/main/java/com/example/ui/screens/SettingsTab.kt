package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.PricingConfig
import com.example.ui.QuoteViewModel

@Composable
fun SettingsTab(
    viewModel: QuoteViewModel,
    config: PricingConfig,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Título e Presets Rápidos
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("pricing_presets_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Presets",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Perfís de Preço e Acabamento",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = false,
                        onClick = { viewModel.applyPreset("ECONOMICO") },
                        label = { Text("Econômico") },
                        modifier = Modifier.weight(1f).testTag("preset_economic_chip")
                    )
                    FilterChip(
                        selected = true,
                        onClick = { viewModel.applyPreset("PADRAO") },
                        label = { Text("Padrão") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.weight(1f).testTag("preset_standard_chip")
                    )
                    FilterChip(
                        selected = false,
                        onClick = { viewModel.applyPreset("PREMIUM") },
                        label = { Text("Premium") },
                        modifier = Modifier.weight(1f).testTag("preset_premium_chip")
                    )
                }
            }
        }

        // Seção 1: Rendimento dos Materiais e Demãos
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FormatPaint,
                        contentDescription = "Rendimento",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Rendimento dos Materiais & Demãos",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = config.paintYield.toString(),
                        onValueChange = { input ->
                            val v = input.replace(',', '.').toDoubleOrNull() ?: 10.0
                            viewModel.updateConfig(config.copy(paintYield = v))
                        },
                        label = { Text("Rendimento Tinta (m²/L/demão)") },
                        placeholder = { Text("Padrão: 10") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f).testTag("paint_yield_input")
                    )

                    OutlinedTextField(
                        value = config.paintCoats.toString(),
                        onValueChange = { input ->
                            val v = input.toIntOrNull() ?: 2
                            viewModel.updateConfig(config.copy(paintCoats = v))
                        },
                        label = { Text("N.º Demãos Tinta") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f).testTag("paint_coats_input")
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = config.puttyYield.toString(),
                        onValueChange = { input ->
                            val v = input.replace(',', '.').toDoubleOrNull() ?: 2.0
                            viewModel.updateConfig(config.copy(puttyYield = v))
                        },
                        label = { Text("Rendimento Massa (m²/kg/demão)") },
                        placeholder = { Text("Padrão: 2") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f).testTag("putty_yield_input")
                    )

                    OutlinedTextField(
                        value = config.puttyCoats.toString(),
                        onValueChange = { input ->
                            val v = input.toIntOrNull() ?: 2
                            viewModel.updateConfig(config.copy(puttyCoats = v))
                        },
                        label = { Text("N.º Demãos Massa") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f).testTag("putty_coats_input")
                    )
                }
            }
        }

        // Seção 2: Preços dos Materiais
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AttachMoney,
                        contentDescription = "Preço Materiais",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Valores de Embalagens (R$)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = config.paintCanPrice.toString(),
                    onValueChange = { input ->
                        val v = input.replace(',', '.').toDoubleOrNull() ?: 0.0
                        viewModel.updateConfig(config.copy(paintCanPrice = v))
                    },
                    label = { Text("Valor Lata de Tinta 18L (R$)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("paint_can_price_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = config.puttyBucketPrice.toString(),
                    onValueChange = { input ->
                        val v = input.replace(',', '.').toDoubleOrNull() ?: 0.0
                        viewModel.updateConfig(config.copy(puttyBucketPrice = v))
                    },
                    label = { Text("Valor Balde de Massa 25kg (R$)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("putty_bucket_price_input")
                )
            }
        }

        // Seção 3: Valor da Mão de Obra
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Build,
                        contentDescription = "Mão de Obra",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Valor da Mão de Obra (R$ por m²)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = config.paintLaborRate.toString(),
                        onValueChange = { input ->
                            val v = input.replace(',', '.').toDoubleOrNull() ?: 0.0
                            viewModel.updateConfig(config.copy(paintLaborRate = v))
                        },
                        label = { Text("Pintura (R$/m²)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f).testTag("paint_labor_rate_input")
                    )

                    OutlinedTextField(
                        value = config.puttyLaborRate.toString(),
                        onValueChange = { input ->
                            val v = input.replace(',', '.').toDoubleOrNull() ?: 0.0
                            viewModel.updateConfig(config.copy(puttyLaborRate = v))
                        },
                        label = { Text("Emassamento (R$/m²)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f).testTag("putty_labor_rate_input")
                    )
                }
            }
        }

        // Seção 4: Observações do Orçamento
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.NoteAdd,
                        contentDescription = "Observações",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Observações Adicionais para Impressão",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = config.notes,
                    onValueChange = { viewModel.updateConfig(config.copy(notes = it)) },
                    label = { Text("Notas (Ex: Validade de 15 dias, pagamento 50% entrada)") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth().testTag("notes_input")
                )
            }
        }

        // Botão Restaurar Padrões
        OutlinedButton(
            onClick = { viewModel.applyPreset("PADRAO") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
                .testTag("reset_defaults_btn")
        ) {
            Icon(Icons.Default.Refresh, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Restaurar Configurações Padrão")
        }
    }
}

package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.CalculatedQuoteSummary
import com.example.data.PricingConfig
import com.example.data.QuoteDatabase
import com.example.data.QuoteRepository
import com.example.data.RoomEntity
import com.example.data.SavedQuoteEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class QuoteViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: QuoteRepository

    val rooms: StateFlow<List<RoomEntity>>
    val config: StateFlow<PricingConfig>
    val summary: StateFlow<CalculatedQuoteSummary>
    val savedQuotes: StateFlow<List<SavedQuoteEntity>>

    init {
        val dao = QuoteDatabase.getDatabase(application).quoteDao()
        repository = QuoteRepository(dao)

        rooms = repository.rooms.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        config = repository.config.map { it ?: PricingConfig() }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PricingConfig()
        )

        summary = combine(rooms, config) { roomList, currentConfig ->
            CalculatedQuoteSummary.calculate(roomList, currentConfig)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CalculatedQuoteSummary.calculate(emptyList(), PricingConfig())
        )

        savedQuotes = repository.savedQuotes.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        // Inicializa com cômodo padrão caso esteja vazio pela primeira vez
        viewModelScope.launch {
            repository.rooms.collect { list ->
                if (list.isEmpty()) {
                    addDefaultRoom("Sala de Estar")
                }
            }
        }
    }

    // --- AÇÕES PARA CÔMODOS ---

    fun addDefaultRoom(name: String = "Novo Cômodo") {
        viewModelScope.launch {
            val newRoom = RoomEntity(
                name = name,
                width = 4.0,
                length = 3.5,
                height = 2.70,
                paintWalls = true,
                paintCeiling = true,
                puttyWalls = false,
                puttyCeiling = false,
                deductArea = 2.0 // Ex: 1 porta padrão (2.1 x 0.9 = ~1.9m²)
            )
            repository.insertRoom(newRoom)
        }
    }

    fun updateRoom(room: RoomEntity) {
        viewModelScope.launch {
            repository.updateRoom(room)
        }
    }

    fun deleteRoom(room: RoomEntity) {
        viewModelScope.launch {
            repository.deleteRoom(room)
        }
    }

    fun clearAllRooms() {
        viewModelScope.launch {
            repository.clearRooms()
        }
    }

    // --- AÇÕES PARA CONFIGURAÇÕES DE PREÇOS E RENDIMENTOS ---

    fun updateConfig(updatedConfig: PricingConfig) {
        viewModelScope.launch {
            repository.saveConfig(updatedConfig)
        }
    }

    /**
     * Aplica Presets Rápidos de Preço / Padrão de Acabamento
     */
    fun applyPreset(presetType: String) {
        val current = config.value
        val newConfig = when (presetType) {
            "ECONOMICO" -> current.copy(
                paintYield = 12.0,
                puttyYield = 2.5,
                paintCoats = 2,
                puttyCoats = 1,
                paintCanPrice = 220.0,
                puttyBucketPrice = 75.0,
                paintLaborRate = 12.0,
                puttyLaborRate = 14.0
            )
            "PREMIUM" -> current.copy(
                paintYield = 9.0,
                puttyYield = 1.8,
                paintCoats = 3,
                puttyCoats = 2,
                paintCanPrice = 480.0,
                puttyBucketPrice = 120.0,
                paintLaborRate = 22.0,
                puttyLaborRate = 25.0
            )
            else -> current.copy( // PADRÃO
                paintYield = 10.0,
                puttyYield = 2.0,
                paintCoats = 2,
                puttyCoats = 2,
                paintCanPrice = 320.0,
                puttyBucketPrice = 95.0,
                paintLaborRate = 15.0,
                puttyLaborRate = 18.0
            )
        }
        updateConfig(newConfig)
    }

    // --- HISTÓRICO DE ORÇAMENTOS ---

    fun saveCurrentQuoteToHistory(title: String) {
        val currentSummary = summary.value
        val currentConfig = config.value
        val saved = SavedQuoteEntity(
            title = title.ifBlank { "Orçamento ${currentConfig.clientName}" },
            clientName = currentConfig.clientName,
            totalArea = currentSummary.totalPaintArea + currentSummary.totalPuttyArea,
            totalCost = currentSummary.grandTotalCost,
            jsonContent = ""
        )
        viewModelScope.launch {
            repository.saveQuoteToHistory(saved)
        }
    }

    fun deleteSavedQuote(id: Long) {
        viewModelScope.launch {
            repository.deleteSavedQuote(id)
        }
    }

    // --- UTILITÁRIO DE FORMATÇÃO E COMPARTILHAMENTO ---

    fun formatCurrency(value: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
        return format.format(value)
    }

    /**
     * Gera relatório em texto formatado para envio direto via WhatsApp / Mensagem
     */
    fun generateTextSummaryForSharing(): String {
        val cfg = config.value
        val sum = summary.value
        val roomList = rooms.value

        val sb = StringBuilder()
        sb.append("🎨 *ORÇAMENTO DE PINTURA RESIDENCIAL*\n")
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n")
        sb.append("👤 *Cliente:* ${cfg.clientName}\n")
        if (cfg.projectAddress.isNotBlank()) sb.append("📍 *Endereço:* ${cfg.projectAddress}\n")
        if (cfg.projectDate.isNotBlank()) sb.append("📅 *Data:* ${cfg.projectDate}\n")
        sb.append("\n")

        sb.append("📋 *CÔMODOS (${roomList.size}):*\n")
        roomList.forEach { r ->
            sb.append("• *${r.name}* (${r.width}m x ${r.length}m | Pé-direito: ${r.height}m)\n")
            val pOps = mutableListOf<String>()
            if (r.paintWalls) pOps.add("Paredes (${String.format(Locale.getDefault(), "%.1f", r.netPaintArea)}m²)")
            if (r.paintCeiling) pOps.add("Teto (${String.format(Locale.getDefault(), "%.1f", r.ceilingArea)}m²)")
            if (r.puttyWalls || r.puttyCeiling) pOps.add("Emassamento (${String.format(Locale.getDefault(), "%.1f", r.netPuttyArea)}m²)")
            sb.append("   Service: ${pOps.joinToString(", ")}\n")
        }
        sb.append("\n")

        sb.append("🧮 *ÁREAS TOTAIS:*\n")
        sb.append("• Área de Pintura: *${String.format(Locale.getDefault(), "%.1f", sum.totalPaintArea)} m²*\n")
        sb.append("• Área de Emassamento: *${String.format(Locale.getDefault(), "%.1f", sum.totalPuttyArea)} m²*\n\n")

        sb.append("📦 *MATERIAIS NECESSÁRIOS:*\n")
        if (sum.paintCansNeeded > 0) {
            sb.append("• Tinta: *${sum.paintCansNeeded} lata(s) de 18L* (~${String.format(Locale.getDefault(), "%.1f", sum.paintLitersNeeded)}L) -> ${formatCurrency(sum.paintTotalCost)}\n")
        }
        if (sum.puttyBucketsNeeded > 0) {
            sb.append("• Massa Corrida: *${sum.puttyBucketsNeeded} balde(s) de 25kg* (~${String.format(Locale.getDefault(), "%.1f", sum.puttyKgNeeded)}kg) -> ${formatCurrency(sum.puttyTotalCost)}\n")
        }
        sb.append("• *Subtotal Materiais:* ${formatCurrency(sum.totalMaterialCost)}\n\n")

        sb.append("👷 *MÃO DE OBRA:* ${formatCurrency(sum.totalLaborCost)}\n")
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n")
        sb.append("💰 *VALOR TOTAL DO ORÇAMENTO: ${formatCurrency(sum.grandTotalCost)}*\n")
        if (cfg.notes.isNotBlank()) sb.append("\n📝 *Obs:* ${cfg.notes}\n")

        return sb.toString()
    }
}

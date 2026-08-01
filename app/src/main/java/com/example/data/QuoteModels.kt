package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.math.ceil
import kotlin.math.max

/**
 * Entidade que representa um Cômodo do imóvel.
 */
@Entity(tableName = "rooms")
data class RoomEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val quoteId: Long = 1, // ID do orçamento associado
    val name: String = "Cômodo",
    val width: Double = 0.0,      // Largura em metros
    val length: Double = 0.0,     // Comprimento em metros
    val height: Double = 2.70,    // Pé-direito (altura) em metros
    val paintCeiling: Boolean = true,  // Pintar teto?
    val puttyCeiling: Boolean = false, // Emassar teto?
    val paintWalls: Boolean = true,    // Pintar paredes?
    val puttyWalls: Boolean = false,   // Emassar paredes?
    val deductArea: Double = 0.0       // Área de desconto (portas, janelas em m²)
) {
    /**
     * Área do teto (Largura x Comprimento)
     */
    val ceilingArea: Double
        get() = max(0.0, width * length)

    /**
     * Área bruta das paredes (2 x (Largura + Comprimento) x Pé-direito)
     */
    val grossWallArea: Double
        get() = max(0.0, 2 * (width + length) * height)

    /**
     * Área bruta total de pintura antes dos descontos
     */
    val grossPaintArea: Double
        get() = (if (paintWalls) grossWallArea else 0.0) + (if (paintCeiling) ceilingArea else 0.0)

    /**
     * Área bruta total de emassamento antes dos descontos
     */
    val grossPuttyArea: Double
        get() = (if (puttyWalls) grossWallArea else 0.0) + (if (puttyCeiling) ceilingArea else 0.0)

    /**
     * Área líquida real para Pintura (descontando portas e janelas)
     */
    val netPaintArea: Double
        get() = max(0.0, grossPaintArea - deductArea)

    /**
     * Área líquida real para Emassamento (descontando portas e janelas)
     */
    val netPuttyArea: Double
        get() = max(0.0, grossPuttyArea - deductArea)
}

/**
 * Modelo de Configurações de Preços, Rendimentos e Dados do Cliente/Projeto
 */
@Entity(tableName = "pricing_config")
data class PricingConfig(
    @PrimaryKey val id: Long = 1,
    val clientName: String = "Cliente Residencia",
    val projectAddress: String = "",
    val projectDate: String = "",
    val notes: String = "",
    
    // Rendimentos padrão
    val paintYield: Double = 10.0,     // m² por litro por demão
    val puttyYield: Double = 2.0,      // m² por kg por demão
    val paintCoats: Int = 2,           // Número de demãos de tinta
    val puttyCoats: Int = 2,           // Número de demãos de massa
    
    // Preços dos materiais
    val paintCanPrice: Double = 320.0,   // R$ por lata de 18L
    val paintCanVolume: Double = 18.0,   // Volume da lata em litros
    val puttyBucketPrice: Double = 95.0, // R$ por balde de 25kg
    val puttyBucketWeight: Double = 25.0,// Peso do balde em kg
    
    // Mão de Obra
    val paintLaborRate: Double = 15.0,  // R$ por m² de pintura
    val puttyLaborRate: Double = 18.0   // R$ por m² de emassamento
)

/**
 * Entidade para Histórico de Orçamentos Salvos
 */
@Entity(tableName = "saved_quotes")
data class SavedQuoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val clientName: String,
    val dateTimestamp: Long = System.currentTimeMillis(),
    val totalArea: Double,
    val totalCost: Double,
    val jsonContent: String // Backup serializado
)

/**
 * Classe auxiliar para cálculo do orçamento completo
 */
data class CalculatedQuoteSummary(
    val totalPaintArea: Double,
    val totalPuttyArea: Double,
    
    // Tinta
    val paintLitersNeeded: Double,
    val paintCansNeeded: Int,
    val paintTotalCost: Double,
    
    // Massa Corrida/Acrílica
    val puttyKgNeeded: Double,
    val puttyBucketsNeeded: Int,
    val puttyTotalCost: Double,
    
    // Mão de Obra
    val paintLaborCost: Double,
    val puttyLaborCost: Double,
    val totalLaborCost: Double,
    
    // Totais
    val totalMaterialCost: Double,
    val grandTotalCost: Double
) {
    companion object {
        /**
         * Função pura para calcular o orçamento com base na lista de cômodos e preços
         */
        fun calculate(rooms: List<RoomEntity>, config: PricingConfig): CalculatedQuoteSummary {
            val totalPaintArea = rooms.sumOf { it.netPaintArea }
            val totalPuttyArea = rooms.sumOf { it.netPuttyArea }

            // Cálculo de Tinta
            // Litros necessários = (Área Total * N.º de Demãos) / Rendimento por Litro
            val safePaintYield = if (config.paintYield > 0) config.paintYield else 10.0
            val paintLitersNeeded = (totalPaintArea * config.paintCoats) / safePaintYield
            
            // Quantidade de latas (arredondando para cima)
            val safeCanVol = if (config.paintCanVolume > 0) config.paintCanVolume else 18.0
            val paintCansNeeded = if (paintLitersNeeded > 0) ceil(paintLitersNeeded / safeCanVol).toInt() else 0
            val paintTotalCost = paintCansNeeded * config.paintCanPrice

            // Cálculo de Massa Corrida / Acrílica
            // Quilos necessários = (Área Total * N.º de Demãos) / Rendimento por Kg
            val safePuttyYield = if (config.puttyYield > 0) config.puttyYield else 2.0
            val puttyKgNeeded = (totalPuttyArea * config.puttyCoats) / safePuttyYield
            
            // Quantidade de baldes (arredondando para cima)
            val safeBucketWeight = if (config.puttyBucketWeight > 0) config.puttyBucketWeight else 25.0
            val puttyBucketsNeeded = if (puttyKgNeeded > 0) ceil(puttyKgNeeded / safeBucketWeight).toInt() else 0
            val puttyTotalCost = puttyBucketsNeeded * config.puttyBucketPrice

            // Cálculo da Mão de Obra
            val paintLaborCost = totalPaintArea * config.paintLaborRate
            val puttyLaborCost = totalPuttyArea * config.puttyLaborRate
            val totalLaborCost = paintLaborCost + puttyLaborCost

            // Totais Finais
            val totalMaterialCost = paintTotalCost + puttyTotalCost
            val grandTotalCost = totalMaterialCost + totalLaborCost

            return CalculatedQuoteSummary(
                totalPaintArea = totalPaintArea,
                totalPuttyArea = totalPuttyArea,
                paintLitersNeeded = paintLitersNeeded,
                paintCansNeeded = paintCansNeeded,
                paintTotalCost = paintTotalCost,
                puttyKgNeeded = puttyKgNeeded,
                puttyBucketsNeeded = puttyBucketsNeeded,
                puttyTotalCost = puttyTotalCost,
                paintLaborCost = paintLaborCost,
                puttyLaborCost = puttyLaborCost,
                totalLaborCost = totalLaborCost,
                totalMaterialCost = totalMaterialCost,
                grandTotalCost = grandTotalCost
            )
        }
    }
}

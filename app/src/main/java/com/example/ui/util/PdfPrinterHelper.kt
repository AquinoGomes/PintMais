package com.example.ui.util

import android.content.Context
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.PrintManager
import android.webkit.WebView
import android.webkit.WebViewClient
import com.example.data.CalculatedQuoteSummary
import com.example.data.PricingConfig
import com.example.data.RoomEntity
import java.text.NumberFormat
import java.util.Locale

object PdfPrinterHelper {

    fun printQuote(
        context: Context,
        config: PricingConfig,
        rooms: List<RoomEntity>,
        summary: CalculatedQuoteSummary
    ) {
        val webView = WebView(context)
        val htmlContent = generateHtmlReport(config, rooms, summary)

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager
                val jobName = "Orcamento_Pintura_${config.clientName.replace(" ", "_")}"
                val printAdapter = webView.createPrintDocumentAdapter(jobName)
                printManager?.print(jobName, printAdapter, PrintAttributes.Builder().build())
            }
        }

        webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
    }

    private fun generateHtmlReport(
        cfg: PricingConfig,
        rooms: List<RoomEntity>,
        sum: CalculatedQuoteSummary
    ): String {
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

        val roomsRowsHtml = rooms.joinToString("") { room ->
            val paintOps = mutableListOf<String>()
            if (room.paintWalls) paintOps.add("Parede (${String.format(Locale.getDefault(), "%.1f", room.grossWallArea)}m²)")
            if (room.paintCeiling) paintOps.add("Teto (${String.format(Locale.getDefault(), "%.1f", room.ceilingArea)}m²)")
            
            val puttyOps = mutableListOf<String>()
            if (room.puttyWalls) puttyOps.add("Parede (${String.format(Locale.getDefault(), "%.1f", room.grossWallArea)}m²)")
            if (room.puttyCeiling) puttyOps.add("Teto (${String.format(Locale.getDefault(), "%.1f", room.ceilingArea)}m²)")

            """
            <tr>
                <td style="padding: 10px; border-bottom: 1px solid #e2e8f0; font-weight: bold; color: #1e293b;">${room.name}</td>
                <td style="padding: 10px; border-bottom: 1px solid #e2e8f0; color: #475569;">${room.width}m x ${room.length}m (h: ${room.height}m)</td>
                <td style="padding: 10px; border-bottom: 1px solid #e2e8f0; color: #475569;">${if (room.deductArea > 0) "${room.deductArea} m²" else "-"}</td>
                <td style="padding: 10px; border-bottom: 1px solid #e2e8f0; color: #0284c7;">${String.format(Locale.getDefault(), "%.1f", room.netPaintArea)} m²</td>
                <td style="padding: 10px; border-bottom: 1px solid #e2e8f0; color: #d97706;">${String.format(Locale.getDefault(), "%.1f", room.netPuttyArea)} m²</td>
            </tr>
            """.trimIndent()
        }

        return """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="utf-8">
            <title>Orçamento de Pintura Residencial</title>
            <style>
                body {
                    font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif;
                    margin: 0;
                    padding: 24px;
                    color: #1e293b;
                    background-color: #ffffff;
                }
                .header {
                    border-bottom: 3px solid #0284c7;
                    padding-bottom: 16px;
                    margin-bottom: 24px;
                    display: flex;
                    justify-content: space-between;
                }
                .title {
                    font-size: 24px;
                    font-weight: bold;
                    color: #0f172a;
                    margin: 0;
                }
                .subtitle {
                    font-size: 14px;
                    color: #64748b;
                    margin-top: 4px;
                }
                .info-box {
                    background-color: #f8fafc;
                    border: 1px solid #e2e8f0;
                    border-radius: 8px;
                    padding: 16px;
                    margin-bottom: 24px;
                }
                .info-grid {
                    display: table;
                    width: 100%;
                }
                .info-row {
                    display: table-row;
                }
                .info-cell {
                    display: table-cell;
                    padding: 4px 8px;
                    font-size: 14px;
                }
                .label {
                    font-weight: bold;
                    color: #475569;
                }
                table {
                    width: 100%;
                    border-collapse: collapse;
                    margin-bottom: 24px;
                    font-size: 13px;
                }
                th {
                    background-color: #f1f5f9;
                    color: #334155;
                    text-align: left;
                    padding: 10px;
                    font-weight: bold;
                    border-bottom: 2px solid #cbd5e1;
                }
                .totals-card {
                    background: linear-gradient(135deg, #0f172a, #1e293b);
                    color: #ffffff;
                    border-radius: 12px;
                    padding: 20px;
                    margin-top: 24px;
                }
                .grand-total {
                    font-size: 26px;
                    font-weight: bold;
                    color: #38bdf8;
                    margin-top: 8px;
                }
                .notes {
                    margin-top: 24px;
                    font-size: 12px;
                    color: #64748b;
                    border-top: 1px dashed #cbd5e1;
                    padding-top: 12px;
                }
            </style>
        </head>
        <body>
            <div class="header">
                <div>
                    <h1 class="title">ORÇAMENTO DE PINTURA RESIDENCIAL</h1>
                    <div class="subtitle">Relatório Técnico e Estimativa de Custos</div>
                </div>
            </div>

            <div class="info-box">
                <div class="info-grid">
                    <div class="info-row">
                        <div class="info-cell label">Cliente:</div>
                        <div class="info-cell">${cfg.clientName}</div>
                        <div class="info-cell label">Data:</div>
                        <div class="info-cell">${cfg.projectDate.ifBlank { "Sem data" }}</div>
                    </div>
                    <div class="info-row">
                        <div class="info-cell label">Endereço:</div>
                        <div class="info-cell" colspan="3">${cfg.projectAddress.ifBlank { "-" }}</div>
                    </div>
                </div>
            </div>

            <h3 style="color: #0f172a; border-left: 4px solid #0284c7; padding-left: 8px;">Especificações por Cômodo</h3>
            <table>
                <thead>
                    <tr>
                        <th>Cômodo</th>
                        <th>Dimensões</th>
                        <th>Descontos</th>
                        <th>Pintura (m²)</th>
                        <th>Emassamento (m²)</th>
                    </tr>
                </thead>
                <tbody>
                    $roomsRowsHtml
                </tbody>
            </table>

            <h3 style="color: #0f172a; border-left: 4px solid #0284c7; padding-left: 8px;">Resumo dos Materiais Estimados</h3>
            <table>
                <thead>
                    <tr>
                        <th>Item</th>
                        <th>Consumo Estimado</th>
                        <th>Embalagens Sugeridas</th>
                        <th>Valor Estimado</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td style="padding: 8px; border-bottom: 1px solid #e2e8f0;">Tinta Látex/Acrílica</td>
                        <td style="padding: 8px; border-bottom: 1px solid #e2e8f0;">${String.format(Locale.getDefault(), "%.1f", sum.paintLitersNeeded)} Litros (${cfg.paintCoats} demãos)</td>
                        <td style="padding: 8px; border-bottom: 1px solid #e2e8f0;">${sum.paintCansNeeded} Lata(s) de 18L</td>
                        <td style="padding: 8px; border-bottom: 1px solid #e2e8f0; font-weight: bold;">${currencyFormat.format(sum.paintTotalCost)}</td>
                    </tr>
                    <tr>
                        <td style="padding: 8px; border-bottom: 1px solid #e2e8f0;">Massa Corrida / Acrílica</td>
                        <td style="padding: 8px; border-bottom: 1px solid #e2e8f0;">${String.format(Locale.getDefault(), "%.1f", sum.puttyKgNeeded)} Kg (${cfg.puttyCoats} demãos)</td>
                        <td style="padding: 8px; border-bottom: 1px solid #e2e8f0;">${sum.puttyBucketsNeeded} Balde(s) de 25kg</td>
                        <td style="padding: 8px; border-bottom: 1px solid #e2e8f0; font-weight: bold;">${currencyFormat.format(sum.puttyTotalCost)}</td>
                    </tr>
                </tbody>
            </table>

            <div class="totals-card">
                <div style="display: flex; justify-content: space-between; font-size: 14px; margin-bottom: 8px;">
                    <span>Área Total Pintura: <strong>${String.format(Locale.getDefault(), "%.1f", sum.totalPaintArea)} m²</strong></span>
                    <span>Área Total Emassamento: <strong>${String.format(Locale.getDefault(), "%.1f", sum.totalPuttyArea)} m²</strong></span>
                </div>
                <hr style="border-color: #334155;">
                <div style="display: flex; justify-content: space-between; margin-top: 12px; font-size: 15px;">
                    <span>Total Estimado Materiais:</span>
                    <strong>${currencyFormat.format(sum.totalMaterialCost)}</strong>
                </div>
                <div style="display: flex; justify-content: space-between; margin-top: 8px; font-size: 15px;">
                    <span>Total Mão de Obra Profissional:</span>
                    <strong>${currencyFormat.format(sum.totalLaborCost)}</strong>
                </div>
                <hr style="border-color: #334155; margin-top: 12px;">
                <div style="display: flex; justify-content: space-between; align-items: center; margin-top: 12px;">
                    <span style="font-size: 18px; font-weight: bold;">VALOR TOTAL DO ORÇAMENTO:</span>
                    <div class="grand-total">${currencyFormat.format(sum.grandTotalCost)}</div>
                </div>
            </div>

            ${if (cfg.notes.isNotBlank()) "<div class=\"notes\"><strong>Observações do Profissional:</strong> ${cfg.notes}</div>" else ""}
        </body>
        </html>
        """.trimIndent()
    }
}

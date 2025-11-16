package com.example.spendsprout_opsc.utils

import android.content.Context
import android.os.Environment
import android.util.Log
import com.example.spendsprout_opsc.transactions.model.Transaction
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class PDFGenerator {
    
    companion object {
        private const val TAG = "PDFGenerator"
    }
    
    /**
     * Generate a PDF file with transactions
     * @param context Android context
     * @param transactions List of transactions to export
     * @param startDate Start date of the filter (null for all time)
     * @param endDate End date of the filter (null for all time)
     * @return File path if successful, null otherwise
     */
    fun generateTransactionsPDF(
        context: Context,
        transactions: List<Transaction>,
        startDate: Long? = null,
        endDate: Long? = null
    ): String? {
        return try {
            // Create file name with timestamp
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "SpendSprout_Transactions_$timestamp.pdf"
            
            // Get Downloads directory (works on Android 10+)
            val downloadsDir = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                // Use app-specific directory for Android 10+
                context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            } else {
                // Use public Downloads for older versions
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            }
            
            // Ensure directory exists
            downloadsDir?.mkdirs()
            
            if (downloadsDir == null) {
                Log.e(TAG, "Downloads directory is null")
                return null
            }
            
            val file = File(downloadsDir, fileName)
            val outputStream = FileOutputStream(file)
            
            // Initialize PDF writer and document
            val writer = PdfWriter(outputStream)
            val pdf = PdfDocument(writer)
            val document = Document(pdf, PageSize.A4)
            document.setMargins(50f, 50f, 50f, 50f)
            
            // Add title
            val title = Paragraph("SpendSprout Transaction Report")
                .setFontSize(24f)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(10f)
            document.add(title)
            
            // Add date range information
            val dateFormat = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
            val dateRangeText = if (startDate != null && endDate != null) {
                "Period: ${dateFormat.format(Date(startDate))} to ${dateFormat.format(Date(endDate))}"
            } else {
                "Period: All Time"
            }
            
            val dateRange = Paragraph(dateRangeText)
                .setFontSize(12f)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20f)
            document.add(dateRange)
            
            // Add summary statistics
            var totalIncome = 0.0
            var totalExpenses = 0.0
            
            transactions.forEach { transaction ->
                val amountStr = transaction.amount.replace("R ", "").replace(",", "").trim()
                val isNegative = amountStr.startsWith("-")
                val amount = try {
                    amountStr.replace("+", "").replace("-", "").toDoubleOrNull() ?: 0.0
                } catch (e: Exception) {
                    0.0
                }
                
                if (isNegative || amountStr.startsWith("-")) {
                    totalExpenses += amount
                } else {
                    totalIncome += amount
                }
            }
            
            val netAmount = totalIncome - totalExpenses
            val summary = Paragraph()
                .add("Total Income: R ${String.format("%.2f", totalIncome)}\n")
                .add("Total Expenses: R ${String.format("%.2f", totalExpenses)}\n")
                .add("Net Amount: R ${String.format("%.2f", netAmount)}")
                .setFontSize(11f)
                .setMarginBottom(20f)
            document.add(summary)
            
            // Create table for transactions
            if (transactions.isNotEmpty()) {
                // Define column widths
                val columnWidths = floatArrayOf(1f, 2f, 1f, 1f)
                val table = Table(UnitValue.createPercentArray(columnWidths))
                table.setWidth(UnitValue.createPercentValue(100f))
                
                // Add header row
                val headerCells = arrayOf("Date", "Description", "Category", "Amount")
                headerCells.forEach { header ->
                    val cell = Cell()
                        .add(Paragraph(header).setBold())
                        .setBackgroundColor(com.itextpdf.kernel.colors.DeviceRgb(33, 35, 39))
                        .setFontColor(ColorConstants.WHITE)
                        .setPadding(8f)
                    table.addHeaderCell(cell)
                }
                
                // Add transaction rows
                transactions.forEach { transaction ->
                    // Extract category from color or use "N/A"
                    val category = transaction.color?.let { 
                        // Try to get category name from color
                        "N/A"
                    } ?: "N/A"
                    
                    // Date
                    table.addCell(Cell().add(Paragraph(transaction.date)).setPadding(5f))
                    
                    // Description
                    table.addCell(Cell().add(Paragraph(transaction.description)).setPadding(5f))
                    
                    // Category (we don't have category name directly, so using placeholder)
                    table.addCell(Cell().add(Paragraph("N/A")).setPadding(5f))
                    
                    // Amount
                    val amountCell = Cell().add(Paragraph(transaction.amount))
                    amountCell.setPadding(5f)
                    // Color code: green for income, red for expenses
                    if (transaction.amount.startsWith("+") || !transaction.amount.contains("-")) {
                        amountCell.setFontColor(com.itextpdf.kernel.colors.DeviceRgb(119, 185, 80))
                    } else {
                        amountCell.setFontColor(com.itextpdf.kernel.colors.DeviceRgb(233, 68, 68))
                    }
                    table.addCell(amountCell)
                }
                
                document.add(table)
            } else {
                val noData = Paragraph("No transactions found in the selected period.")
                    .setFontSize(12f)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(20f)
                document.add(noData)
            }
            
            // Add footer
            val footer = Paragraph("Generated on ${SimpleDateFormat("d MMMM yyyy 'at' HH:mm", Locale.getDefault()).format(Date())}")
                .setFontSize(10f)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(30f)
                .setFontColor(com.itextpdf.kernel.colors.DeviceRgb(128, 128, 128))
            document.add(footer)
            
            // Close document
            document.close()
            
            Log.d(TAG, "PDF generated successfully: ${file.absolutePath}")
            file.absolutePath
            
        } catch (e: Exception) {
            Log.e(TAG, "Error generating PDF: ${e.message}", e)
            null
        }
    }
}


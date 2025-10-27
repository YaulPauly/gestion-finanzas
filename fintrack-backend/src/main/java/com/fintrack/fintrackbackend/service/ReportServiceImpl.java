package com.fintrack.fintrackbackend.service;

import com.fintrack.fintrackbackend.model.Transaction;
import com.fintrack.fintrackbackend.model.TransactionType;
import com.fintrack.fintrackbackend.model.User;
import com.fintrack.fintrackbackend.repository.TransactionRepository;
import com.fintrack.fintrackbackend.repository.UserRepository;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public byte[] generateMonthlyTransactionsReport(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        YearMonth currentMonth = YearMonth.now();
        LocalDate startDate = currentMonth.atDay(1);
        LocalDate endDate = LocalDate.now();

        List<Transaction> incomeTransactions = transactionRepository
                .findByUserIdAndTypeAndDateBetweenOrderByDateAsc(userId, TransactionType.INCOME, startDate, endDate);

        List<Transaction> expenseTransactions = transactionRepository
                .findByUserIdAndTypeAndDateBetweenOrderByDateAsc(userId, TransactionType.EXPENSE, startDate, endDate);

        BigDecimal totalIncome = sumAmounts(incomeTransactions);
        BigDecimal totalExpense = sumAmounts(expenseTransactions);
        BigDecimal netBalance = totalIncome.subtract(totalExpense);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, outputStream);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

            document.add(new Paragraph("FinTrack - Reporte Mensual", titleFont));
            document.add(new Paragraph(String.format("Usuario: %s (%s)", user.getName(), user.getEmail()), bodyFont));
            document.add(new Paragraph(String.format("Período: %s al %s",
                    DATE_FORMATTER.format(startDate),
                    DATE_FORMATTER.format(endDate)), bodyFont));
            document.add(new Paragraph(String.format("Generado: %s", DATE_FORMATTER.format(LocalDate.now())), bodyFont));
            document.add(new Paragraph(" "));

            addSummaryTable(document, totalIncome, totalExpense, netBalance, subtitleFont, bodyFont);
            document.add(new Paragraph(" "));

            addTransactionSection(document, "Ingresos", incomeTransactions, subtitleFont, bodyFont);
            document.add(new Paragraph(" "));
            addTransactionSection(document, "Gastos", expenseTransactions, subtitleFont, bodyFont);

            document.close();
            return outputStream.toByteArray();
        } catch (DocumentException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No se pudo generar el reporte en PDF");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error inesperado al generar el reporte");
        }
    }

    private void addSummaryTable(Document document,
                                 BigDecimal totalIncome,
                                 BigDecimal totalExpense,
                                 BigDecimal netBalance,
                                 Font headerFont,
                                 Font bodyFont) throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(60);
        table.setSpacingBefore(5);
        table.setSpacingAfter(5);

        addHeaderCell(table, "Resumen", headerFont);
        addHeaderCell(table, "", headerFont);

        addSummaryRow(table, "Total ingresos", totalIncome, bodyFont);
        addSummaryRow(table, "Total gastos", totalExpense, bodyFont);
        addSummaryRow(table, "Balance neto", netBalance, bodyFont);

        document.add(table);
    }

    private void addTransactionSection(Document document,
                                       String sectionTitle,
                                       List<Transaction> transactions,
                                       Font headerFont,
                                       Font bodyFont) throws DocumentException {
        document.add(new Paragraph(sectionTitle, headerFont));

        if (transactions.isEmpty()) {
            document.add(new Paragraph("Sin movimientos registrados en el período.", bodyFont));
            return;
        }

        PdfPTable table = new PdfPTable(new float[]{2f, 1.2f, 3f});
        table.setWidthPercentage(100);
        table.setSpacingBefore(5);

        addHeaderCell(table, "Fecha", headerFont);
        addHeaderCell(table, "Monto", headerFont);
        addHeaderCell(table, "Descripción", headerFont);

        for (Transaction transaction : transactions) {
            LocalDate date = transaction.getDate();
            String formattedDate = date != null ? DATE_FORMATTER.format(date) : "-";
            table.addCell(new PdfPCell(new Phrase(formattedDate, bodyFont)));
            table.addCell(new PdfPCell(new Phrase(formatAmount(transaction.getAmount()), bodyFont)));
            String description = transaction.getDescription() != null
                    ? transaction.getDescription()
                    : "(Sin descripción)";
            table.addCell(new PdfPCell(new Phrase(description, bodyFont)));
        }

        document.add(table);
    }

    private void addHeaderCell(PdfPTable table, String content, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(content, font));
        cell.setPadding(5);
        table.addCell(cell);
    }

    private void addSummaryRow(PdfPTable table, String label, BigDecimal amount, Font font) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, font));
        labelCell.setPadding(5);
        table.addCell(labelCell);

        PdfPCell amountCell = new PdfPCell(new Phrase(formatAmount(amount), font));
        amountCell.setPadding(5);
        table.addCell(amountCell);
    }

    private BigDecimal sumAmounts(List<Transaction> transactions) {
        return transactions.stream()
                .map(Transaction::getAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private String formatAmount(BigDecimal amount) {
        return amount != null ? String.format("S/ %.2f", amount) : "S/ 0.00";
    }
}

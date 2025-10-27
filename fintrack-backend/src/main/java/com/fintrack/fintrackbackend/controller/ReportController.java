package com.fintrack.fintrackbackend.controller;

import com.fintrack.fintrackbackend.service.ReportService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping(value = "/monthly-transactions", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> downloadMonthlyTransactionsReport(HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        byte[] pdf = reportService.generateMonthlyTransactionsReport(userId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename("fintrack-reporte-mensual.pdf")
                .build());

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdf);
    }
}

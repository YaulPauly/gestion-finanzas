package com.fintrack.fintrackbackend.service;

public interface ReportService {
    byte[] generateMonthlyTransactionsReport(Integer userId);
}

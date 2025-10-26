package com.fintrack.fintrackbackend.repository;

import com.fintrack.fintrackbackend.model.Transaction;
import com.fintrack.fintrackbackend.model.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Page<Transaction> findByUserIdAndType(Integer userId, TransactionType type, Pageable pageable);
    List<Transaction> findByUserIdOrderByCreatedAtDesc(Integer userId, Pageable pageable);
    List<Transaction> findByUserIdAndTypeAndDateBetweenOrderByDateAsc(Integer userId,
                                                                        TransactionType type,
                                                                        LocalDate startDate,
                                                                        LocalDate endDate);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
            "WHERE t.userId = :userId AND t.type = :type " +
            "AND t.date BETWEEN :startDate AND :endDate")
    BigDecimal sumByUserIdAndTypeBetweenDates(@Param("userId") Integer userId,
                                              @Param("type") TransactionType type,
                                              @Param("startDate") LocalDate startDate,
                                              @Param("endDate") LocalDate endDate);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
            "WHERE t.userId = :userId AND t.type = :type")
    BigDecimal sumByUserIdAndType(@Param("userId") Integer userId,
                                  @Param("type") TransactionType type);
}

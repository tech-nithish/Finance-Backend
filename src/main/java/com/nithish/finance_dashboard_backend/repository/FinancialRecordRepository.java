package com.nithish.finance_dashboard_backend.repository;

import com.nithish.finance_dashboard_backend.model.FinancialRecord;
import com.nithish.finance_dashboard_backend.model.RecordType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface FinancialRecordRepository extends MongoRepository<FinancialRecord, String> {

    // Paginated queries for filtering
    Page<FinancialRecord> findByDeletedFalse(Pageable pageable);
    Page<FinancialRecord> findByTypeAndDeletedFalse(RecordType type, Pageable pageable);
    Page<FinancialRecord> findByCategoryAndDeletedFalse(String category, Pageable pageable);
    Page<FinancialRecord> findByDateBetweenAndDeletedFalse(LocalDate startDate, LocalDate endDate, Pageable pageable);
    Page<FinancialRecord> findByTypeAndDateBetweenAndDeletedFalse(RecordType type, LocalDate startDate, LocalDate endDate, Pageable pageable);
    Page<FinancialRecord> findByCategoryAndDateBetweenAndDeletedFalse(String category, LocalDate startDate, LocalDate endDate, Pageable pageable);
    Page<FinancialRecord> findByTypeAndCategoryAndDeletedFalse(RecordType type, String category, Pageable pageable);

    // Single record lookup that respects soft-delete
    Optional<FinancialRecord> findByIdAndDeletedFalse(String id);

    // Non-paginated queries for dashboard aggregations
    List<FinancialRecord> findByTypeAndDeletedFalse(RecordType type);
    List<FinancialRecord> findByDeletedFalse();
}

package com.nithish.finance_dashboard_backend.service;

import com.nithish.finance_dashboard_backend.dto.FinancialRecordRequest;
import com.nithish.finance_dashboard_backend.exception.ResourceNotFoundException;
import com.nithish.finance_dashboard_backend.model.FinancialRecord;
import com.nithish.finance_dashboard_backend.model.RecordType;
import com.nithish.finance_dashboard_backend.repository.FinancialRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class FinancialRecordService {

    private final FinancialRecordRepository repository;

    public Page<FinancialRecord> getAllRecords(String type, String category, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        if (type != null && startDate != null && endDate != null) {
            return repository.findByTypeAndDateBetweenAndDeletedFalse(RecordType.valueOf(type.toUpperCase()), startDate, endDate, pageable);
        } else if (category != null && startDate != null && endDate != null) {
            return repository.findByCategoryAndDateBetweenAndDeletedFalse(category, startDate, endDate, pageable);
        } else if (type != null && category != null) {
            return repository.findByTypeAndCategoryAndDeletedFalse(RecordType.valueOf(type.toUpperCase()), category, pageable);
        } else if (startDate != null && endDate != null) {
            return repository.findByDateBetweenAndDeletedFalse(startDate, endDate, pageable);
        } else if (type != null) {
            return repository.findByTypeAndDeletedFalse(RecordType.valueOf(type.toUpperCase()), pageable);
        } else if (category != null) {
            return repository.findByCategoryAndDeletedFalse(category, pageable);
        }
        return repository.findByDeletedFalse(pageable);
    }

    public FinancialRecord getRecordById(String id) {
        // Fetch only non-deleted records
        return repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Financial record not found with id: " + id));
    }

    public FinancialRecord createRecord(FinancialRecordRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        FinancialRecord record = FinancialRecord.builder()
                .amount(request.getAmount())
                .type(request.getType())
                .category(request.getCategory())
                .date(request.getDate())
                .notes(request.getNotes())
                .createdBy(username)
                .deleted(false)
                .build();

        return repository.save(record);
    }

    public FinancialRecord updateRecord(String id, FinancialRecordRequest request) {
        FinancialRecord existing = getRecordById(id);

        existing.setAmount(request.getAmount());
        existing.setType(request.getType());
        existing.setCategory(request.getCategory());
        existing.setDate(request.getDate());
        existing.setNotes(request.getNotes());

        return repository.save(existing);
    }

    public void deleteRecord(String id) {
        FinancialRecord existing = getRecordById(id);
        existing.setDeleted(true);
        repository.save(existing);
    }
}

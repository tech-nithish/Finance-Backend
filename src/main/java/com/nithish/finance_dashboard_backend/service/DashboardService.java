package com.nithish.finance_dashboard_backend.service;

import com.nithish.finance_dashboard_backend.dto.DashboardSummaryResponse;
import com.nithish.finance_dashboard_backend.model.FinancialRecord;
import com.nithish.finance_dashboard_backend.repository.FinancialRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final FinancialRecordRepository repository;
    private final MongoTemplate mongoTemplate;

    public DashboardSummaryResponse getSummary() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("deleted").is(false)),
                Aggregation.group("type")
                        .sum("amount").as("totalAmount")
        );

        AggregationResults<Map> results = mongoTemplate.aggregate(aggregation, "financial_records", Map.class);

        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;

        for (Map result : results.getMappedResults()) {
            String type = (String) result.get("_id");
            Object rawAmount = result.get("totalAmount");
            if (rawAmount == null) continue;
            BigDecimal amount = new BigDecimal(rawAmount.toString());
            if ("INCOME".equals(type)) {
                totalIncome = amount;
            } else if ("EXPENSE".equals(type)) {
                totalExpense = amount;
            }
        }

        return DashboardSummaryResponse.builder()
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .netBalance(totalIncome.subtract(totalExpense))
                .build();
    }

    /**
     * Returns category-wise totals for ALL non-deleted records (both INCOME and EXPENSE),
     * broken down by type then category.
     */
    public Map<String, BigDecimal> getCategorySummary() {
        List<FinancialRecord> records = repository.findByDeletedFalse();
        return records.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getType().name() + " | " + r.getCategory(),
                        LinkedHashMap::new,
                        Collectors.reducing(BigDecimal.ZERO, FinancialRecord::getAmount, BigDecimal::add)
                ));
    }

    /**
     * Returns monthly income/expense breakdown using MongoDB aggregation pipeline.
     */
    @SuppressWarnings("rawtypes")
    public List<Map> getMonthlyTrends() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("deleted").is(false)),
                Aggregation.project()
                        .andExpression("month(date)").as("month")
                        .andExpression("year(date)").as("year")
                        .and("amount").as("amount")
                        .and("type").as("type"),
                Aggregation.group("year", "month", "type")
                        .sum("amount").as("totalAmount"),
                Aggregation.sort(Sort.Direction.ASC, "_id.year", "_id.month")
        );

        AggregationResults<Map> results = mongoTemplate.aggregate(aggregation, "financial_records", Map.class);
        return results.getMappedResults();
    }

    /**
     * Returns the 10 most recent non-deleted financial records ordered by date descending.
     */
    public List<FinancialRecord> getRecentActivity() {
        Query query = new Query()
                .addCriteria(Criteria.where("deleted").is(false))
                .with(Sort.by(Sort.Direction.DESC, "date"))
                .limit(10);
        return mongoTemplate.find(query, FinancialRecord.class);
    }
}

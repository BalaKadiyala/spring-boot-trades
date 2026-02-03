package com.example.stocks.repository;

import com.example.stocks.model.StockRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockRecordRepository extends JpaRepository<StockRecord, Long> {

    List<StockRecord> findByStock(String stock);

    @Query(
            value = "SELECT * FROM stock_records " +
                    "WHERE stock = :ticker AND quarter = :quarter " +
                    "ORDER BY date DESC",
            nativeQuery = true
    )
    List<StockRecord> findByStockAndQuarterNative(
            @Param("ticker") String ticker,
            @Param("quarter") Integer quarter
    );
}
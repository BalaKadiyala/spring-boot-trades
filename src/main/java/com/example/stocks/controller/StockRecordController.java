package com.example.stocks.controller;

import com.example.stocks.dto.UploadResult;
import com.example.stocks.model.StockRecord;
import com.example.stocks.service.StockRecordService;
import jakarta.validation.constraints.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/stock-data")
@Validated
public class StockRecordController {

    private static final Logger log = LoggerFactory.getLogger(StockRecordController.class);

    private final StockRecordService service;

    public StockRecordController(StockRecordService service) {
        this.service = service;
    }

    @PostMapping(value = "/bulk-insert", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UploadResult> uploadCsv(
            @RequestPart(name = "file", required = true) MultipartFile file) {
        log.info("uploadCsv invoked");
        UploadResult result = service.uploadFromCsv(file);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(result);
    }

    @GetMapping("/{ticker}")
    public ResponseEntity<List<StockRecord>> getByTicker(
            @PathVariable @Size(min = 2, max = 5) String ticker) {

        log.info("getByTicker invoked for {}", ticker);
        List<StockRecord> list = service.findByStock(ticker); // JPA
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{ticker}/quarter/{quarter}")
    public ResponseEntity<List<StockRecord>> getByTickerAndQuarter(
            @PathVariable @Size(min = 2, max = 5) String ticker,
            @PathVariable Integer quarter) {

        log.info("getByTickerAndQuarter invoked for {} Q{}", ticker, quarter);
        List<StockRecord> list = service.findByStockAndQuarterNative(ticker, quarter); // Native SQL
        return ResponseEntity.ok(list);
    }

    @PostMapping
    public ResponseEntity<StockRecord> addRecord(@RequestBody StockRecord record) {
        log.info("addRecord invoked");
        StockRecord saved = service.addRecord(record);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @DeleteMapping({ "", "/{ticker}" })
    public ResponseEntity<String> delete(@PathVariable(required = false) @Size(min = 1, max = 5) String ticker) {
        log.info("delete invoked for ticker {}", ticker);
        if (ticker == null) {
            int deleted = service.deleteAll();
            return ResponseEntity.ok("Deleted ALL stock records (" + deleted + " rows)");
        }
        int deleted = service.deleteByStock(ticker);
        if (deleted == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No records found for ticker: " + ticker);
        }
        return ResponseEntity.ok("Deleted " + deleted + " records for ticker: " + ticker);
    }
}
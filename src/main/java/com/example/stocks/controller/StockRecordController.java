package com.example.stocks.controller;

import com.example.stocks.dto.UploadResult;
import com.example.stocks.model.StockRecord;
import com.example.stocks.service.StockRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/stock-data")
@Tag(name = "Stock Records API", description = "Endpoints for uploading and querying stock records")
public class StockRecordController {

    private static final Logger log = LoggerFactory.getLogger(StockRecordController.class);

    private final StockRecordService service;

    public StockRecordController(StockRecordService service) {
        this.service = service;
    }

    @PostMapping(value = "/bulk-insert", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload CSV of stock records", description = "Parses and persists CSV; returns a concise upload summary")
    public ResponseEntity<UploadResult> uploadCsv(
            @Parameter(hidden = true) @RequestHeader("X-Client-Id") String clientId,
            @RequestPart("file") MultipartFile file) {

        log.info("Client ID received in uploadCsv: {}", clientId);

        UploadResult result = service.uploadFromCsv(file);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(result);
    }

    @GetMapping("/{stock}")
    @Operation(summary = "Get records by stock (JPA)", description = "Returns stock records for the given ticker using JPA query")
    public ResponseEntity<List<StockRecord>> getByStock(
            @Parameter(hidden = true) @RequestHeader("X-Client-Id") String clientId,
            @PathVariable String stock) {

        log.info("Client ID received in getByStock: {}", clientId);

        List<StockRecord> list = service.findByStock(stock);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{stock}/native")
    @Operation(summary = "Get records by stock (native SQL)", description = "Returns stock records for the given ticker using a native SQL query")
    public ResponseEntity<List<StockRecord>> getByStockNative(
            @Parameter(hidden = true)
            @RequestHeader("X-Client-Id") String clientId,
            @PathVariable String stock) {

        log.info("Client ID received in getByStockNative: {}", clientId);

        List<StockRecord> list = service.findByStockNative(stock);
        return ResponseEntity.ok(list);
    }

    @PostMapping
    @Operation(summary = "Add a single stock record", description = "Creates a new stock record")
    public ResponseEntity<StockRecord> addRecord(
            @Parameter(hidden = true) @RequestHeader("X-Client-Id") String clientId,
            @RequestBody StockRecord record) {

        log.info("Client ID received in addRecord: {}", clientId);

        StockRecord saved = service.addRecord(record);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}
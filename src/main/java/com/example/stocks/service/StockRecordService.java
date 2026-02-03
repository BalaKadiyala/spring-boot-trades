package com.example.stocks.service;

import com.example.stocks.dto.UploadResult;
import com.example.stocks.model.StockRecord;
import com.example.stocks.repository.StockRecordRepository;
import com.example.stocks.util.CsvHelper;
import com.opencsv.exceptions.CsvValidationException;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StockRecordService {

    private final StockRecordRepository repository;

    public StockRecordService(StockRecordRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public UploadResult uploadFromCsv(MultipartFile file) {
        try (InputStream is = file.getInputStream()) {
            CsvHelper.ParseResult parseResult = CsvHelper.parse(is);
            List<StockRecord> parsed = parseResult.getRecords();
            List<StockRecord> saved = repository.saveAll(parsed);
            return getUploadResult(parseResult, saved);
        } catch (IOException | CsvValidationException e) {
            throw new RuntimeException("Failed to parse and persist CSV", e);
        }
    }

    @Transactional(readOnly = true)
    public List<StockRecord> findByStock(String ticker) {
        return repository.findByStock(ticker);   // JPA
    }

    @Transactional(readOnly = true)
    public List<StockRecord> findByStockAndQuarterNative(String ticker, Integer quarter) {
        return repository.findByStockAndQuarterNative(ticker, quarter);   // Native SQL
    }

    @Transactional
    public StockRecord addRecord(StockRecord record) {
        return repository.save(record);
    }

    @NonNull
    private UploadResult getUploadResult(CsvHelper.ParseResult parseResult, List<StockRecord> saved) {
        List<Long> sampleIds = saved.stream().map(StockRecord::getId).limit(10).collect(Collectors.toList());
        if (!parseResult.getErrors().isEmpty()) {
            parseResult.getErrors().forEach(err -> System.err.println("CSV parse error: " + err));
        }
        return new UploadResult(saved.size(), sampleIds);
    }

    @Transactional
    public int deleteAll() {
        int count = (int) repository.count();
        repository.deleteAll();
        return count;
    }

    @Transactional
    public int deleteByStock(String stock) {
        List<StockRecord> records = repository.findByStock(stock);
        repository.deleteAll(records);
        return records.size();
    }
}
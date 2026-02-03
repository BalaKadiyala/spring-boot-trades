package com.example.stocks.util;

import com.example.stocks.model.StockRecord;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public final class CsvHelper {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("M/d/yyyy");

    private CsvHelper() {}

    public static ParseResult parse(InputStream inputStream) throws IOException, CsvValidationException {
        return parse(inputStream, 1000);
    }

    public static ParseResult parse(InputStream inputStream, int batchSize) throws IOException, CsvValidationException {
        if (inputStream == null) return new ParseResult(Collections.emptyList(), Collections.emptyList());

        List<StockRecord> out = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        try (InputStreamReader isr = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             CSVReader reader = new CSVReader(isr)) {

            AtomicLong rowIndex = new AtomicLong(0);
            String[] row;

            // Read header row
            String[] headerRow = reader.readNext();
            if (headerRow == null) return new ParseResult(Collections.emptyList(), Collections.emptyList());

            Map<String, Integer> indexMap = buildHeaderIndex(headerRow);

            List<StockRecord> batch = new ArrayList<>();

            while ((row = reader.readNext()) != null) {
                long idx = rowIndex.incrementAndGet();
                if (row.length == 0) continue;

                try {
                    StockRecord r = mapRow(row, indexMap);
                    batch.add(r);
                } catch (Exception ex) {
                    errors.add("row " + idx + ": " + ex.getMessage());
                }

                if (batch.size() >= batchSize) {
                    out.addAll(batch);
                    batch.clear();
                }
            }

            if (!batch.isEmpty()) out.addAll(batch);
        }

        return new ParseResult(out, errors);
    }

    private static Map<String, Integer> buildHeaderIndex(String[] headerRow) {
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < headerRow.length; i++) {
            map.put(headerRow[i].trim().toLowerCase(), i);
        }
        return map;
    }

    private static StockRecord mapRow(String[] row, Map<String,Integer> indexMap) {
        StockRecord r = new StockRecord();

        r.setQuarter(parseInt(get(row, indexMap, "quarter")));
        r.setStock(nullIfEmpty(get(row, indexMap, "stock")));
        r.setDate(parseDate(get(row, indexMap, "date")));
        r.setOpen(parseBigDecimal(get(row, indexMap, "open")));
        r.setHigh(parseBigDecimal(get(row, indexMap, "high")));
        r.setLow(parseBigDecimal(get(row, indexMap, "low")));
        r.setClose(parseBigDecimal(get(row, indexMap, "close")));
        r.setVolume(parseLong(get(row, indexMap, "volume")));
        r.setPercentChangePrice(parseBigDecimal(get(row, indexMap, "percentChangePrice")));
        r.setPercentChangeVolumeOverLastWk(parseBigDecimal(get(row, indexMap, "percentChangeVolumeOverLastWk")));
        r.setPreviousWeeksVolume(parseLong(get(row, indexMap, "previousWeeksVolume")));
        r.setNextWeeksOpen(parseBigDecimal(get(row, indexMap, "nextWeeksOpen")));
        r.setNextWeeksClose(parseBigDecimal(get(row, indexMap, "nextWeeksClose")));
        r.setPercentChangeNextWeeksPrice(parseBigDecimal(get(row, indexMap, "percentChangeNextWeeksPrice")));
        r.setDaysToNextDividend(parseInt(get(row, indexMap, "daysToNextDividend")));
        r.setPercentReturnNextDividend(parseBigDecimal(get(row, indexMap, "percentReturnNextDividend")));

        return r;
    }

    private static String get(String[] row, Map<String,Integer> indexMap, String column) {
        Integer idx = indexMap.get(column.toLowerCase());
        if (idx == null || idx < 0 || idx >= row.length) return "";
        String v = row[idx];
        return v == null ? "" : v.trim();
    }

    private static String nullIfEmpty(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }

    private static Integer parseInt(String s) {
        s = normalizeNumber(s);
        if (s.isEmpty()) return null;
        try { return Integer.parseInt(s); } catch (Exception e) { return null; }
    }

    private static Long parseLong(String s) {
        s = normalizeNumber(s);
        if (s.isEmpty()) return null;
        try {
            if (s.contains(".")) s = s.substring(0, s.indexOf('.'));
            return Long.parseLong(s);
        } catch (Exception e) { return null; }
    }

    private static BigDecimal parseBigDecimal(String s) {
        s = normalizeNumber(s);
        if (s.isEmpty()) return null;
        try { return new BigDecimal(s); } catch (Exception e) { return null; }
    }

    private static LocalDate parseDate(String s) {
        if (s == null || s.isBlank()) return null;
        try { return LocalDate.parse(s.trim(), DATE_FORMATTER); } catch (Exception e) { return null; }
    }

    private static String normalizeNumber(String s) {
        if (s == null) return "";
        return s.replace("$", "").replace(",", "").trim();
    }

    public static class ParseResult {
        private final List<StockRecord> records;
        private final List<String> errors;

        public ParseResult(List<StockRecord> records, List<String> errors) {
            this.records = records;
            this.errors = errors;
        }

        public List<StockRecord> getRecords() { return records; }
        public List<String> getErrors() { return errors; }
    }
}
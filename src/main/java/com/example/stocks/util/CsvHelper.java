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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
            boolean headerSkipped = false;
            List<StockRecord> batch = new ArrayList<>();

            while ((row = reader.readNext()) != null) {
                long idx = rowIndex.getAndIncrement();
                if (!headerSkipped) { headerSkipped = true; continue; }
                if (row.length == 0) continue;
                try {
                    StockRecord r = mapRow(row);
                    batch.add(r);
                } catch (Exception ex) {
                    errors.add("row " + (idx + 1) + ": " + ex.getMessage());
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

    private static StockRecord mapRow(String[] row) {
        StockRecord r = new StockRecord();
        r.setQuarter(parseInt(get(row, 0)));
        r.setStock(nullIfEmpty(get(row, 1)));
        r.setDate(parseDate(get(row, 2)));
        r.setOpen(parseBigDecimal(get(row, 3)));
        r.setHigh(parseBigDecimal(get(row, 4)));
        r.setLow(parseBigDecimal(get(row, 5)));
        r.setClose(parseBigDecimal(get(row, 6)));
        r.setVolume(parseLong(get(row, 7)));
        r.setPercentChangePrice(parseBigDecimal(get(row, 8)));
        r.setPercentChangeVolumeOverLastWk(parseBigDecimal(get(row, 9)));
        r.setPreviousWeeksVolume(parseLong(get(row, 10)));
        r.setNextWeeksOpen(parseBigDecimal(get(row, 11)));
        r.setNextWeeksClose(parseBigDecimal(get(row, 12)));
        r.setPercentChangeNextWeeksPrice(parseBigDecimal(get(row, 13)));
        r.setDaysToNextDividend(parseInt(get(row, 14)));
        r.setPercentReturnNextDividend(parseBigDecimal(get(row, 15)));
        return r;
    }

    private static String get(String[] row, int idx) {
        if (row == null || idx < 0 || idx >= row.length) return "";
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
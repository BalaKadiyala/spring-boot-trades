package com.example.stocks.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "stock_records")
public class StockRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    private Integer quarter;

    @Column(length = 32)
    private String stock;

    private LocalDate date;

    @Column(precision = 19, scale = 6)
    private BigDecimal open;

    @Column(precision = 19, scale = 6)
    private BigDecimal high;

    @Column(precision = 19, scale = 6)
    private BigDecimal low;

    @Column(precision = 19, scale = 6)
    private BigDecimal close;

    private Long volume;

    @Column(precision = 19, scale = 6)
    private BigDecimal percentChangePrice;

    @Column(precision = 19, scale = 6)
    private BigDecimal percentChangeVolumeOverLastWk;

    private Long previousWeeksVolume;

    @Column(precision = 19, scale = 6)
    private BigDecimal nextWeeksOpen;

    @Column(precision = 19, scale = 6)
    private BigDecimal nextWeeksClose;

    @Column(precision = 19, scale = 6)
    private BigDecimal percentChangeNextWeeksPrice;

    private Integer daysToNextDividend;

    @Column(precision = 19, scale = 6)
    private BigDecimal percentReturnNextDividend;
}
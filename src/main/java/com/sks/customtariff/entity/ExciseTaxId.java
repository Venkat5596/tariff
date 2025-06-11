package com.sks.customtariff.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExciseTaxId implements Serializable {

    @Column(name = "tariff_number")
    private String tariffNumber;

    @Column(name = "excise_tax_code")
    private String exciseTaxCode;

    @Column(name = "as_of_date")
    private LocalDateTime asOfDate;
}

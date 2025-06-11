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
public class CustomsDutyId implements Serializable {

    @Column(name = "tariff_item_number")
    private String tariffItemNumber;

    @Column(name = "tariff_treatment_code") // This is the key!
    private String tariffTreatmentCode; // Assuming this is part of the ID

    @Column(name = "as_of_date")
    private LocalDateTime asOfDate; // Effective date
}


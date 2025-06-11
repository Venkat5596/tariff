package com.sks.customtariff.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "customs_duties")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomsDuty {

    @EmbeddedId
    private CustomsDutyId id;

    private String title; // From API entry title
    private LocalDateTime updated; // From API entry updated

    // Add other fields from API if available in content properties, e.g.:
    // private String dutyRate;
    // private String description;
}




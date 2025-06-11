package com.sks.customtariff.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "excise_taxes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExciseTax {
    @EmbeddedId
    private ExciseTaxId id;


    private String title; // From entry title
    private LocalDateTime updated;
}

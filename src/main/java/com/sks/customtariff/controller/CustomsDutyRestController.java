package com.sks.customtariff.controller;// src/main/java/com/example/customsdutymanager/controller/CustomsDutyRestController.java
//package com.example.customsdutymanager.controller;

//import com.example.customsdutymanager.model.CustomsDuty;
//import com.example.customsdutymanager.model.CustomsDutyId;
//import com.example.customsdutymanager.service.CustomsDutyService;
import com.sks.customtariff.entity.CustomsDuty;
import com.sks.customtariff.entity.CustomsDutyId;
import com.sks.customtariff.service.CustomsDutyService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customs-duties")
public class CustomsDutyRestController {

    private final CustomsDutyService customsDutyService;

    public CustomsDutyRestController(CustomsDutyService customsDutyService) {
        this.customsDutyService = customsDutyService;
    }

    // GET all customs duties from your database
    @GetMapping
    @Operation(summary = "List Customs Duties", description = "List all Customs Duties")
    public List<CustomsDuty> getAllCustomsDuties() {
        return customsDutyService.getAllCustomsDuties();
    }

    // GET a specific customs duty by its composite ID
    @GetMapping("/{tariffNumber}/{customsDutyCode}/{asOfDate}")
    @Operation(summary = "Get Customs Duty by ID", description = "Get a Customs Duty by its composite ID")
    public ResponseEntity<CustomsDuty> getCustomsDutyById(
            @PathVariable String tariffNumber,
            @PathVariable String customsDutyCode,
            @PathVariable String asOfDate) { // AsOfDate as String to be parsed in service
        return customsDutyService.getCustomsDutyById(tariffNumber, customsDutyCode, asOfDate)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST (Create) a new customs duty in your database
    @PostMapping
    @Operation(summary = "Create Customs Duty", description = "Create a new Customs Duty")
    public ResponseEntity<CustomsDuty> createCustomsDuty(@RequestBody CustomsDuty customsDuty) {
        CustomsDuty createdDuty = customsDutyService.createCustomsDuty(customsDuty);
        return new ResponseEntity<>(createdDuty, HttpStatus.CREATED);
    }

    // PUT (Update) an existing customs duty in your database
    @PutMapping("/{tariffNumber}/{customsDutyCode}/{asOfDate}")
    @Operation(summary = "Update Customs Duty", description = "Update an existing Customs Duty by its composite ID")
    public ResponseEntity<CustomsDuty> updateCustomsDuty(
            @PathVariable String tariffNumber,
            @PathVariable String customsDutyCode,
            @PathVariable String asOfDate,
            @RequestBody CustomsDuty customsDuty) {
        try {
            CustomsDutyId id = new CustomsDutyId(
                    tariffNumber, customsDutyCode,
                    java.time.LocalDateTime.parse(asOfDate, java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            );
            CustomsDuty updatedDuty = customsDutyService.updateCustomsDuty(id, customsDuty);
            return ResponseEntity.ok(updatedDuty);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // DELETE a customs duty from your database
    @DeleteMapping("/{tariffNumber}/{customsDutyCode}/{asOfDate}")
    @Operation(summary = "Delete Customs Duty", description = "Delete a Customs Duty by its composite ID")
    public ResponseEntity<Void> deleteCustomsDuty(
            @PathVariable String tariffNumber,
            @PathVariable String customsDutyCode,
            @PathVariable String asOfDate) {
        try {
            CustomsDutyId id = new CustomsDutyId(
                    tariffNumber, customsDutyCode,
                    java.time.LocalDateTime.parse(asOfDate, java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            );
            customsDutyService.deleteCustomsDuty(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            // Handle case where ID might not exist, though deleteById is usually robust
            return ResponseEntity.notFound().build();
        }
    }

    // Endpoint to manually trigger fetching from the external API
    @PostMapping("/fetch-external")
    @Operation(summary = "Trigger External Fetch", description = "Trigger fetching from the external API")
    public ResponseEntity<String> triggerExternalFetch() {
        try {
            customsDutyService.fetchAndStoreAllCustomsDuties();
            return ResponseEntity.ok("Successfully triggered data fetch from external API.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching data from external API: " + e.getMessage());
        }
    }
}
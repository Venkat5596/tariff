package com.sks.customtariff.service;// src/main/java/com/example/customsdutymanager/service/CustomsDutyService.java
//package com.example.customsdutymanager.service;

//import com.example.customsdutymanager.dto.Entry;
//import com.example.customsdutymanager.dto.Feed;
//import com.example.customsdutymanager.model.CustomsDuty;
//import com.example.customsdutymanager.model.CustomsDutyId;
//import com.example.customsdutymanager.repository.CustomsDutyRepository;
import com.sks.customtariff.dto.Entry;
import com.sks.customtariff.dto.Feed;
import com.sks.customtariff.entity.CustomsDuty;
import com.sks.customtariff.entity.CustomsDutyId;
import com.sks.customtariff.repo.CustomsDutyRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class CustomsDutyService {

    private final RestTemplate restTemplate;
    private final CustomsDutyRepository customsDutyRepository;

    @Value("${api.customs-duty.url}")
    private String apiBaseUrl;

    // A placeholder for the external API endpoint suffix
    // You will need to find the specific URL format for 'customsDuties'
    // For now, I'm assuming it might be similar to exciseTaxes, but it's a guess.
    // The exact URL for a specific customsDuty entry will be needed to get a single record.
    private static final String CUSTOMS_DUTIES_ENDPOINT = "/v1/tariff-srv/customsDuties";

    // Example RegEx for parsing IDs like 'customsDuties(TariffNumber='XXXX',CustomsDutyCode='YYYY',AsOfDate=datetimev'ZZZZ')'
    private static final Pattern ID_PATTERN = Pattern.compile(
            ".*?\\(TariffItemNumber='([^']*)',TariffTreatmentCode='([^']*)',AsOfDate=datetimev?'([^']*)'\\)");

    // Standard date formatter for 'yyyy-MM-dd'T'HH:mm:ss'
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public CustomsDutyService(RestTemplate restTemplate, CustomsDutyRepository customsDutyRepository) {
        this.restTemplate = restTemplate;
        this.customsDutyRepository = customsDutyRepository;
    }

    // --- External API Fetching and Storing ---
    @Transactional
    public List<CustomsDuty> fetchAndStoreAllCustomsDuties() {
        // This URL is a guess for fetching ALL customs duties.
        // The previous curl was for a specific one.
        // You might need to consult the API documentation for a "get all" endpoint or pagination.
        String url = UriComponentsBuilder.fromHttpUrl(apiBaseUrl)
                .path(CUSTOMS_DUTIES_ENDPOINT)
                .build()
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_XML_VALUE);
        headers.set("Accept-Language", "EN");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            System.out.println("Fetching all customs duties from: " + url);
            Feed feed = restTemplate.exchange(url, HttpMethod.GET, entity, Feed.class).getBody();

            if (feed != null && feed.getEntries() != null ) {
                List<CustomsDuty> fetchedDuties = feed.getEntries().stream()
                        .map(this::mapEntryToCustomsDuty)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toList());

                // Save all fetched duties
                customsDutyRepository.saveAll(fetchedDuties);
                System.out.println("Successfully fetched and stored " + fetchedDuties.size() + " customs duties.");
                return fetchedDuties;
            } else {
                System.out.println("No customs duty entries found or empty feed received.");
                return List.of();
            }
        } catch (Exception e) {
            System.err.println("Error fetching or storing customs duties from external API: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to fetch customs duties from external API", e);
        }
    }

    // This method is for fetching a specific duty from the external API
    // Use this if the main GET endpoint requires specific parameters to retrieve data.
    @Transactional
    public Optional<CustomsDuty> fetchAndStoreSpecificCustomsDuty(String tariffNumber, String customsDutyCode, String asOfDate) {
        String formattedAsOfDate = "datetime%27" + asOfDate + "%27";
        String url = UriComponentsBuilder.fromHttpUrl(apiBaseUrl)
                .path(CUSTOMS_DUTIES_ENDPOINT)
                .path("(" +
                        "TariffNumber='" + tariffNumber + "'," +
                        "CustomsDutyCode='" + customsDutyCode + "'," +
                        "AsOfDate=" + formattedAsOfDate +
                        ")")
                .build()
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_XML_VALUE);
        headers.set("Accept-Language", "EN");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            System.out.println("Fetching specific customs duty from: " + url);
            // Note: If the API returns a 'feed' with a single entry for a specific query,
            // then Feed.class is correct. If it returns a direct 'entry' or 'properties',
            // you'd need to adjust the return type and DTO accordingly.
            Feed feed = restTemplate.exchange(url, HttpMethod.GET, entity, Feed.class).getBody();

            if (feed != null && feed.getEntries() != null && !feed.getEntries().isEmpty()) {
                Optional<CustomsDuty> duty = mapEntryToCustomsDuty(feed.getEntries().get(0));
                duty.ifPresent(customsDutyRepository::save);
                return duty;
            }
            return Optional.empty();
        } catch (Exception e) {
            System.err.println("Error fetching specific customs duty from external API: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to fetch specific customs duty from external API", e);
        }
    }

    // Helper to map DTO Entry to Entity
    private Optional<CustomsDuty> mapEntryToCustomsDuty(Entry entry) {
        if (entry == null || entry.getId() == null) {
            return Optional.empty();
        }

        Matcher matcher = ID_PATTERN.matcher(entry.getId());
        if (matcher.find()) {
            String tariffItemNumber = matcher.group(1);
            String tariffTreatmentCode = matcher.group(2);
            String asOfDateStr = matcher.group(3);

            asOfDateStr = asOfDateStr.replace("%3A", ":").replace("v", ""); // Cleanup date string

            LocalDateTime asOfDate = null;
            try {
                asOfDate = LocalDateTime.parse(asOfDateStr, DATE_TIME_FORMATTER);
            } catch (Exception e) {
                System.err.println("Could not parse AsOfDate from ID: " + asOfDateStr + " - " + e.getMessage());
                return Optional.empty();
            }

            CustomsDutyId customsDutyId = new CustomsDutyId(tariffItemNumber, tariffTreatmentCode, asOfDate);
            CustomsDuty customsDuty = new CustomsDuty();
            customsDuty.setId(customsDutyId);
            customsDuty.setTitle(entry.getTitle());
            try {
                if (entry.getUpdated() != null) {
                    customsDuty.setUpdated(LocalDateTime.parse(entry.getUpdated(), DateTimeFormatter.ISO_DATE_TIME));
                }
            } catch (Exception e) {
                System.err.println("Could not parse updated date for entry: " + entry.getUpdated() + " - " + e.getMessage());
            }

            // If 'content.properties' had fields, you'd extract them here.
            // e.g., if (entry.getContent() != null && entry.getContent().getProperties() != null) {
            //     customsDuty.setDutyRate(entry.getContent().getProperties().getDutyRate());
            // }

            return Optional.of(customsDuty);
        } else {
            System.err.println("Could not parse ID string for CustomsDuty: " + entry.getId());
            return Optional.empty();
        }
    }

    // --- Internal Database CRUD Operations ---

    public List<CustomsDuty> getAllCustomsDuties() {
        return customsDutyRepository.findAll();
    }

    public Optional<CustomsDuty> getCustomsDutyById(String tariffNumber, String customsDutyCode, String asOfDateStr) {
        LocalDateTime asOfDate = null;
        try {
            asOfDate = LocalDateTime.parse(asOfDateStr, DATE_TIME_FORMATTER);
        } catch (Exception e) {
            System.err.println("Error parsing AsOfDate for lookup: " + asOfDateStr + " - " + e.getMessage());
            return Optional.empty();
        }
        CustomsDutyId id = new CustomsDutyId(tariffNumber, customsDutyCode, asOfDate);
        return customsDutyRepository.findById(id);
    }

    @Transactional
    public CustomsDuty createCustomsDuty(CustomsDuty customsDuty) {
        // You might want to add validation here
        return customsDutyRepository.save(customsDuty);
    }

    @Transactional
    public CustomsDuty updateCustomsDuty(CustomsDutyId id, CustomsDuty updatedDuty) {
        if (customsDutyRepository.existsById(id)) {
            updatedDuty.setId(id); // Ensure the ID is set for update
            return customsDutyRepository.save(updatedDuty);
        } else {
            throw new IllegalArgumentException("Customs Duty with ID " + id + " not found.");
        }
    }

    @Transactional
    public void deleteCustomsDuty(CustomsDutyId id) {
        customsDutyRepository.deleteById(id);
    }
}
package com.sks.customtariff.service;

import com.sks.customtariff.dto.Entry;
import com.sks.customtariff.dto.Feed;
import com.sks.customtariff.entity.ExciseTax;
import com.sks.customtariff.entity.ExciseTaxId;
import com.sks.customtariff.repo.ExciseTaxRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//@Service
public class ExciseTaxService {
    private final RestTemplate restTemplate;
    private final ExciseTaxRepo exciseTaxRepository;

    @Value("${api.base-url}")
    private String apiBaseUrl;

    public ExciseTaxService(RestTemplate restTemplate, ExciseTaxRepo exciseTaxRepository) {
        this.restTemplate = restTemplate;
        this.exciseTaxRepository = exciseTaxRepository;
    }

    public void fetchAndStoreExciseTaxes(String tariffNumber, String exciseTaxCode, String asOfDate) {
        String url = buildApiUrl(tariffNumber, exciseTaxCode, asOfDate);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/xml");
        headers.set("Accept-Language", "EN"); // As per curl command

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            Feed feed = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    Feed.class
            ).getBody();

            if (feed != null && feed.getEntries() != null) {
                for (Entry entry : feed.getEntries()) {
                    ExciseTax exciseTax = mapEntryToExciseTax(entry);
                    if (exciseTax != null) {
                        exciseTaxRepository.save(exciseTax);
                        System.out.println("Saved Excise Tax: " + exciseTax);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching or storing excise taxes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String buildApiUrl(String tariffNumber, String exciseTaxCode, String asOfDate) {
        // Example: https://ccapi-ipacc.cbsa-asfc.cloud-nuage.canada.ca/v1/tariff-srv/exciseTaxes(TariffNumber='0602.90.90.90',ExciseTaxCode='E52',AsOfDate=datetime%272021-05-28T00%3A00%3A00%27)
        String formattedAsOfDate = "datetime%27" + asOfDate + "%27"; // The API expects this specific OData datetime format

        return UriComponentsBuilder.fromHttpUrl(apiBaseUrl)
                .pathSegment("v1", "tariff-srv", "exciseTaxes")
                .path("(" +
                        "TariffNumber='" + tariffNumber + "'," +
                        "ExciseTaxCode='" + exciseTaxCode + "'," +
                        "AsOfDate=" + formattedAsOfDate +
                        ")")
                .build()
                .toUriString();
    }

    private ExciseTax mapEntryToExciseTax(Entry entry) {
        if (entry == null || entry.getId() == null) {
            return null;
        }

        // Parse the ID string to extract TariffNumber, ExciseTaxCode, AsOfDate
        // Example ID: https://ccapi-ipacc.cbsa-asfc.cloud-nuage.canada.ca/v1/tariff-srv/exciseTaxes(TariffNumber=’0602.90.90.90’,ExciseTaxCode=’E52’,AsOfDate=datetimev2021-05-28T00%3A00%3A00’)
        Pattern pattern = Pattern.compile("TariffNumber='([^']*)',ExciseTaxCode='([^']*)',AsOfDate=datetimev?([^')]*)");
        Matcher matcher = pattern.matcher(entry.getId());

        if (matcher.find()) {
            String tariffNumber = matcher.group(1);
            String exciseTaxCode = matcher.group(2);
            String asOfDateStr = matcher.group(3);

            // Clean up the date string (remove 'v' if present, replace %3A with : if not already done by RestTemplate)
            asOfDateStr = asOfDateStr.replace("%3A", ":").replace("v", "");

            LocalDateTime asOfDate = null;
            try {
                // Example format: 2021-05-28T00:00:00
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
                asOfDate = LocalDateTime.parse(asOfDateStr, formatter);
            } catch (Exception e) {
                System.err.println("Could not parse AsOfDate: " + asOfDateStr + " - " + e.getMessage());
                return null;
            }

            ExciseTaxId exciseTaxId = new ExciseTaxId(tariffNumber, exciseTaxCode, asOfDate);
            ExciseTax exciseTax = new ExciseTax();
            exciseTax.setId(exciseTaxId);
            exciseTax.setTitle(entry.getTitle());
            try {
                // Assuming entry.getUpdated() is also in "yyyy-MM-dd'T'HH:mm:ss" format
                if (entry.getUpdated() != null) {
                    exciseTax.setUpdated(LocalDateTime.parse(entry.getUpdated(), DateTimeFormatter.ISO_DATE_TIME));
                }
            } catch (Exception e) {
                System.err.println("Could not parse updated date: " + entry.getUpdated() + " - " + e.getMessage());
            }

            return exciseTax;
        } else {
            System.err.println("Could not parse ID for entry: " + entry.getId());
            return null;
        }
    }
}


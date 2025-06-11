package com.sks.customtariff;

import com.sks.customtariff.service.CustomsDutyService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CustomTariffApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomTariffApplication.class, args);
    }

    @Bean
    CommandLineRunner initData(CustomsDutyService customsDutyService) {
        return args -> {
            System.out.println("Starting initial data fetch from external API...");
            // Call to fetch and store ALL customs duties (if that endpoint exists)
            // Or call specific ones based on your needs.
            // Example for one specific duty, if you know the parameters:
            // customsDutyService.fetchAndStoreSpecificCustomsDuty("someTariff", "someCode", "2023-01-01T00:00:00");

            // For now, let's assume the base /customsDuties URL returns a feed of entries.
            // WARNING: This API might require specific parameters or pagination.
            // If the base URL returns a "Not Found" or "Bad Request", you'll need to know
            // how to query for collections of customs duties.
            try {
                customsDutyService.fetchAndStoreAllCustomsDuties();
            } catch (Exception e) {
                System.err.println("Could not perform initial fetch of all customs duties. Ensure the API base URL for /customsDuties is correct and accepts requests without specific parameters.");
                System.err.println("Consider trying to fetch a specific duty if the 'all' endpoint doesn't work.");
            }

            System.out.println("Initial data fetch attempt complete.");
        };

    }
}
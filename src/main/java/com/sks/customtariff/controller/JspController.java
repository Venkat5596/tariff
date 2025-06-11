package com.sks.customtariff.controller;

import com.sks.customtariff.dto.CustomsDutyDisplayDto;
import com.sks.customtariff.entity.CustomsDuty;
import com.sks.customtariff.entity.CustomsDutyId;
import com.sks.customtariff.service.CustomsDutyService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Date;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/") // Base path for JSP views
public class JspController {

    private final CustomsDutyService customsDutyService;

    public JspController(CustomsDutyService customsDutyService) {
        this.customsDutyService = customsDutyService;
    }

    @GetMapping()
    @Operation(summary = "Home page", description = "The home page of the application")
    public String home() {
        return "index"; // Renders src/main/webapp/WEB-INF/jsp/index.jsp
    }

    // Inside listCustomsDuties method
    @GetMapping("/customs-duties")
    @Operation(summary = "List Customs Duties", description = "List all Customs Duties")
    public String listCustomsDuties(Model model) {
        List<CustomsDuty> duties = customsDutyService.getAllCustomsDuties();

        // Convert LocalDateTime to java.util.Date for JSP compatibility
        // (This is a workaround if JSTL/EL doesn't handle LocalDateTime directly)
        List<CustomsDutyDisplayDto> displayDuties = duties.stream().map(duty -> {
            CustomsDutyDisplayDto dto = new CustomsDutyDisplayDto();
            dto.setId(duty.getId()); // Keep original ID if it's not the problem
            dto.setTitle(duty.getTitle());
            // Convert LocalDateTime to java.util.Date
            if (duty.getUpdated() != null) {
                dto.setUpdated(Date.from(duty.getUpdated().atZone(ZoneId.systemDefault()).toInstant()));
            }
            if (duty.getId() != null && duty.getId().getAsOfDate() != null) {
                dto.setAsOfDateForDisplay(Date.from(duty.getId().getAsOfDate().atZone(ZoneId.systemDefault()).toInstant()));
            }
            return dto;
        }).collect(Collectors.toList());

        model.addAttribute("duties", displayDuties); // Pass the converted list
        return "customs-duties-list";
    }

    @PostMapping("/customs-duties/fetch-external")
    @Operation(summary = "Fetch Customs Duties from External API", description = "Fetch Customs Duties from the external API and store them in the database")
    public String fetchExternal(RedirectAttributes redirectAttributes) {
        try {
            customsDutyService.fetchAndStoreAllCustomsDuties();
            redirectAttributes.addFlashAttribute("message", "Data fetched and stored successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to fetch data: " + e.getMessage());
        }
        return "redirect:/customs-duties";
    }

    // Example for a simple create/update form (can be expanded)
    @GetMapping("/customs-duties/create")
    @Operation(summary = "Create Customs Duty", description = "Create a new Customs Duty")
    public String showCreateForm(Model model) {
        model.addAttribute("customsDuty", new CustomsDuty());
        return "customs-duty-form";
    }

    @PostMapping("/customs-duties/save")
    @Operation(summary = "Save Customs Duty", description = "Save a new Customs Duty or update an existing one")
    public String saveCustomsDuty(@ModelAttribute CustomsDuty customsDuty,
                                  @RequestParam String tariffNumber,
                                  @RequestParam String customsDutyCode,
                                  @RequestParam String asOfDateString, // Use String for input
                                  RedirectAttributes redirectAttributes) {
        try {
            // Parse the date string from form to LocalDateTime
            LocalDateTime asOfDate = LocalDateTime.parse(asOfDateString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            customsDuty.setId(new com.sks.customtariff.entity.CustomsDutyId(tariffNumber, customsDutyCode, asOfDate));

            customsDutyService.createCustomsDuty(customsDuty); // Or update if ID exists
            redirectAttributes.addFlashAttribute("message", "Customs Duty saved successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error saving Customs Duty: " + e.getMessage());
        }
        return "redirect:/customs-duties";
    }

    @PostMapping("/customs-duties/delete")
    @Operation(summary = "Delete Customs Duty", description = "Delete a Customs Duty by its composite ID")
    public String deleteCustomsDuty(@RequestParam String tariffNumber,
                                    @RequestParam String customsDutyCode,
                                    @RequestParam String asOfDate,
                                    RedirectAttributes redirectAttributes) {
        try {
            LocalDateTime parsedAsOfDate = LocalDateTime.parse(asOfDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            customsDutyService.deleteCustomsDuty(new CustomsDutyId(tariffNumber, customsDutyCode, parsedAsOfDate));
            redirectAttributes.addFlashAttribute("message", "Customs Duty deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting Customs Duty: " + e.getMessage());
        }
        return "redirect:/customs-duties";
    }
}

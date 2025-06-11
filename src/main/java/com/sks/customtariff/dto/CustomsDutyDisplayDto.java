package com.sks.customtariff.dto;// src/main/java/com/example/customsdutymanager/dto/CustomsDutyDisplayDto.java
//package com.example.customsdutymanager.dto;

//import com.example.customsdutymanager.model.CustomsDutyId;
import com.sks.customtariff.entity.CustomsDutyId;
import lombok.*;

import java.util.Date; // Using java.util.Date for JSP compatibility

@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class  CustomsDutyDisplayDto {
    // Keep the composite ID as is, as it's correctly mapped in the entity
    private CustomsDutyId id;

    private String title;

    // Convert LocalDateTime to java.util.Date for JSP rendering
    private Date updated;

    // The asOfDate is part of CustomsDutyId, which is already LocalDateTime.
    // If you need it as java.util.Date *separately* for display,
    // you might need an extra field or getter in this DTO,
    // or convert it when accessing `id.asOfDate` in the JSP if JSTL can handle it.
    // For simplicity, we'll assume the problem was with the 'updated' field,
    // and if 'asOfDate' within CustomsDutyId is still an issue, we'd address that specifically.
    // However, the error message often comes from any LocalDateTime.
    // To make it explicit for 'asOfDate' from the ID as well:
    private Date asOfDateForDisplay; // This field would explicitly hold the java.util.Date version of id.asOfDate
}
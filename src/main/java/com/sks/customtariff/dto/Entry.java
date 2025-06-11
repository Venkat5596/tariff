package com.sks.customtariff.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JacksonXmlRootElement(localName = "entry", namespace = "http://www.w3.org/2005/Atom") // Important to define localName and namespace
@JsonIgnoreProperties(ignoreUnknown = true)
public class Entry {

    @JacksonXmlProperty(localName = "id", namespace = "http://www.w3.org/2005/Atom")
    private String id;

    @JacksonXmlProperty(localName = "title", namespace = "http://www.w3.org/2005/Atom")
    private String title;

    @JacksonXmlProperty(localName = "updated", namespace = "http://www.w3.org/2005/Atom")
    private String updated; // Keep as String for initial parsing, convert later

    // The 'content' element typically holds the actual OData properties
    @JacksonXmlProperty(localName = "content", namespace = "http://www.w3.org/2005/Atom")
    private Content content;

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Content {
        @JacksonXmlProperty(localName = "properties", namespace = "http://schemas.microsoft.com/ado/2007/08/dataservices/metadata")
        private Properties properties;

        @Data
        @NoArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Properties {
            // These would be the actual data fields of your excise tax
            // Based on the given example, we extract from the 'id' string.
            // If the content section of the actual API response provides
            // fields like "TariffNumber", "ExciseTaxCode", "AsOfDate", "Description",
            // you'd add them here with @JacksonXmlProperty
            // For instance:
            // @JacksonXmlProperty(localName = "Description", namespace = "http://schemas.microsoft.com/ado/2007/08/dataservices")
            // private String description;
        }
    }
}

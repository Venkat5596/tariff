package com.sks.customtariff.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@JacksonXmlRootElement(localName = "feed", namespace = "http://www.w3.org/2005/Atom")
@JsonIgnoreProperties(ignoreUnknown = true) // Ignore xmlns attributes etc.
public class Feed {

    @JacksonXmlProperty(localName = "id", namespace = "http://www.w3.org/2005/Atom")
    private String id;

    @JacksonXmlProperty(localName = "title", namespace = "http://www.w3.org/2005/Atom")
    private String title;

    @JacksonXmlProperty(localName = "updated", namespace = "http://www.w3.org/2005/Atom")
    private String updated; // Keep as String for initial parsing, convert later

    @JacksonXmlProperty(localName = "author", namespace = "http://www.w3.org/2005/Atom")
    private Author author;

    @JacksonXmlElementWrapper(useWrapping = false) // No wrapper for entries
    @JacksonXmlProperty(localName = "entry", namespace = "http://www.w3.org/2005/Atom")
    private List<Entry> entries;

    // Nested classes for complex types
    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Author {
        @JacksonXmlProperty(localName = "name", namespace = "http://www.w3.org/2005/Atom")
        private String name;
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Link {
        @JacksonXmlProperty(isAttribute = true, localName = "href")
        private String href;

        @JacksonXmlProperty(isAttribute = true, localName = "rel")
        private String rel;

        @JacksonXmlProperty(isAttribute = true, localName = "title")
        private String title;
    }
}

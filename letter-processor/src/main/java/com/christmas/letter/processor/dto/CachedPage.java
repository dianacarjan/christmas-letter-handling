package com.christmas.letter.processor.dto;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class CachedPage<T> {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @JsonProperty("content")
    private List<T> content;
    @JsonProperty("pageNumber")
    private int pageNumber;
    @JsonProperty("pageSize")
    private int pageSize;
    @JsonProperty("offset")
    private long offset;
    @JsonProperty("totalElements")
    private Long totalElements;
    @JsonProperty("last")
    private boolean last;
    @JsonProperty("totalPages")
    private int totalPages;
    @JsonProperty("sort")
    private JsonNode sort;
    @JsonProperty("first")
    private boolean first;
    @JsonProperty("empty")
    private boolean empty;
    @JsonProperty("numberOfElements")
    private int numberOfElements;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public CachedPage(
            @JsonProperty("content") List<T> content,
            @JsonProperty("pageNumber") int pageNumber,
            @JsonProperty("pageSize") int pageSize,
            @JsonProperty("offset") long offset,
            @JsonProperty("totalElements") Long totalElements,
            @JsonProperty("last") boolean last,
            @JsonProperty("totalPages") int totalPages,
            @JsonProperty("sort") JsonNode sort,
            @JsonProperty("first") boolean first,
            @JsonProperty("empty") boolean empty,
            @JsonProperty("numberOfElements") int numberOfElements
    ) {
        this.content = content;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.offset = offset;
        this.totalElements = totalElements;
        this.last = last;
        this.totalPages = totalPages;
        this.sort = sort;
        this.first = first;
        this.empty = empty;
        this.numberOfElements = numberOfElements;
    }

    public CachedPage(Page<T> result, Pageable pageable) {
        this(result.getContent(), pageable.getPageNumber(), pageable.getPageSize(), pageable.getOffset(), result.getTotalElements(), result.isLast(),
                result.getTotalPages(), objectMapper.valueToTree(pageable.getSort()), result.isFirst(), result.isEmpty(), result.getNumberOfElements());
    }
}

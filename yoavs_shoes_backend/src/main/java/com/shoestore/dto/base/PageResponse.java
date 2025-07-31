package com.shoestore.dto.base;

import com.fasterxml.jackson.annotation.JsonView;
import com.shoestore.dto.view.Views;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Generic page response for paginated data
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {

    @JsonView(Views.Summary.class)
    private List<T> content;
    
    @JsonView(Views.Summary.class)
    private int page;
    
    @JsonView(Views.Summary.class)
    private int size;
    
    @JsonView(Views.Summary.class)
    private long totalElements;
    
    @JsonView(Views.Summary.class)
    private int totalPages;
    
    @JsonView(Views.Summary.class)
    private boolean first;
    
    @JsonView(Views.Summary.class)
    private boolean last;
    
    @JsonView(Views.Summary.class)
    private boolean empty;
    
    @JsonView(Views.Summary.class)
    private int numberOfElements;

    /**
     * Create PageResponse from Spring Page
     */
    public PageResponse(Page<T> page) {
        this.content = page.getContent();
        this.page = page.getNumber();
        this.size = page.getSize();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.first = page.isFirst();
        this.last = page.isLast();
        this.empty = page.isEmpty();
        this.numberOfElements = page.getNumberOfElements();
    }

    /**
     * Create PageResponse with mapped content
     */
    public static <T, R> PageResponse<R> of(Page<T> page, List<R> mappedContent) {
        return PageResponse.<R>builder()
                .content(mappedContent)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .empty(page.isEmpty())
                .numberOfElements(page.getNumberOfElements())
                .build();
    }
}

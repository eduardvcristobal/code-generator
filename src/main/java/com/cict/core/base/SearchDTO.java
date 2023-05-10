package com.cict.core.base;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

@Getter
@Setter
public class SearchDTO {
    private int start;
    private int length;
    private long recordsTotal;
    private long recordsFiltered;
    private int totalPages;
    private long totalElements;
    private int size;
    private int page;
    private  boolean lastPage;
    private Collection<Object> data = null;
    private Object objectx = null;
//    private List<Object> list = null;

    public static <T> SearchDTO of(
            int page,
            int size,
            Page<T> pages,
            Function<T, BaseDTO<? extends BaseModel>> supplier
    ) {
        SearchDTO response = new SearchDTO();
        response.setStart(page <= 0 ? 1 : page);
        response.setLength(size);
        response.setRecordsFiltered(pages.getTotalElements());
        response.setRecordsTotal(pages.getTotalElements());
        response.setData(pages.getContent().stream()
                .map(supplier)
                .collect(toList()));
        return response;
    }

    public static <T> SearchDTO of(
            int page,
            int size,
            Page<T> pages
    ) {
        SearchDTO response = new SearchDTO();
        response.setStart(page <= 0 ? 1 : page);
        response.setLength(size);
        response.setRecordsFiltered(pages.getTotalElements());
        response.setRecordsTotal(pages.getTotalElements());
        response.setData(new ArrayList<>(pages.getContent()));
        return response;
    }

    public static <T> SearchDTO of(
            Pageable p,
            Page<T> pages
    ) {
        SearchDTO response = new SearchDTO();
        response.setStart(p.getPageNumber() <= 0 ? 1 : p.getPageNumber());
        response.setLength(p.getPageSize());
        response.setRecordsFiltered(pages.getTotalElements());
        response.setRecordsTotal(pages.getTotalElements());
        response.setData(new ArrayList<>(pages.getContent()));
        return response;
    }

    public static <T> SearchDTO of(
            int page,
            int size,
            Collection<Object> data
    ) {
        SearchDTO response = new SearchDTO();
        response.setStart(page <= 0 ? 1 : page);
        response.setLength(size);
        response.setRecordsFiltered(data.size() );
        response.setRecordsTotal(  data.size() );
        response.setData( data );
        return response;
    }

    public static <T> SearchDTO of(
            Pageable p,
            Collection<Object> data
    ) {
        SearchDTO response = new SearchDTO();
        response.setStart(p.getPageNumber() <= 0 ? 1 : p.getPageNumber());
        response.setLength(p.getPageSize());
        response.setRecordsFiltered(data.size() );
        response.setRecordsTotal(  data.size() );
        response.setData( data );
        return response;
    }

    public static <R> SearchDTO of(int pageNo, int size, Collection<Object> data, long totalElements,
                                   int totalPages, int sizePages, int pageNumber, boolean lastPage) {
        SearchDTO response = new SearchDTO();
        response.setStart(pageNo <= 0 ? 1 : pageNo);
        response.setLength(size);
        response.setRecordsFiltered(data.size() );
        response.setRecordsTotal(  data.size() );
        response.setData( data );
        response.setTotalPages(totalPages);
        response.setTotalElements(totalElements);
        response.setSize(sizePages);
        response.setPage(pageNo);
        response.setLastPage(lastPage);
        return response;
    }

    public static <R> SearchDTO ofList( Collection<Object> data) {
        SearchDTO response = new SearchDTO();
        response.setData( data );
        response.setRecordsTotal(data.size());
        response.setRecordsFiltered(data.size());
        return response;
    }

    public static <R> SearchDTO ofObject( Collection<Object> data) {
        SearchDTO response = new SearchDTO();
        response.setData( data );
        return response;
    }
}
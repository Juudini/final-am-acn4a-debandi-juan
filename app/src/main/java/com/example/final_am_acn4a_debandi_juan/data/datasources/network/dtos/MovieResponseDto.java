package com.example.final_am_acn4a_debandi_juan.data.datasources.network.dtos;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public final class MovieResponseDto {
    private int page;
    private List<MovieDto> results;

    @SerializedName("total_pages")
    private int totalPages;

    @SerializedName("total_results")
    private int totalResults;

    public int getPage() { return page; }
    public List<MovieDto> getResults() { return results; }
    public int getTotalPages() { return totalPages; }
    public int getTotalResults() { return totalResults; }
}

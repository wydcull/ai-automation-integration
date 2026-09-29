package org.example.aisalesops.dto;

public class SourceCountResponse {

    private String source;
    private long count;

    public SourceCountResponse(String source, long count) {
        this.source = source;
        this.count = count;
    }

    public String getSource() { return source; }
    public long getCount() { return count; }
}
package org.example.aisalesops.dto;

public class ScoreBandCountResponse {

    private String scoreBand;
    private long count;

    public ScoreBandCountResponse(String scoreBand, long count) {
        this.scoreBand = scoreBand;
        this.count = count;
    }

    public String getScoreBand() { return scoreBand; }
    public void setScoreBand(String scoreBand) { this.scoreBand = scoreBand; }

    public long getCount() { return count; }
    public void setCount(long count) { this.count = count; }
}
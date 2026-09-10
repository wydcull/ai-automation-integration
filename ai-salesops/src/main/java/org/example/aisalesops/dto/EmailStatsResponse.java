package org.example.aisalesops.dto;

public class EmailStatsResponse {

    private long received;
    private long processing;
    private long processed;
    private long failed;

    public EmailStatsResponse(
            long received,
            long processing,
            long processed,
            long failed
    ) {
        this.received = received;
        this.processing = processing;
        this.processed = processed;
        this.failed = failed;
    }

    public long getReceived() {
        return received;
    }

    public void setReceived(long received) {
        this.received = received;
    }

    public long getProcessing() {
        return processing;
    }

    public void setProcessing(long processing) {
        this.processing = processing;
    }

    public long getProcessed() {
        return processed;
    }

    public void setProcessed(long processed) {
        this.processed = processed;
    }

    public long getFailed() {
        return failed;
    }

    public void setFailed(long failed) {
        this.failed = failed;
    }
}
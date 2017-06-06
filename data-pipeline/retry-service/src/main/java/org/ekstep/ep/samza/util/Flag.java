package org.ekstep.ep.samza.util;

public class Flag {
    private String prefix;
    private String processedCountFlag = "processed_count";
    private String lastProcessedAtFlag = "last_processed_at";
    private String lastSkippedAtFlag = "last_skipped_at";
    private String metadataFlag = "metadata";
    public Flag(String prefix){
        this.prefix = prefix;
    }

    public String processedCount() {
        return addPrefix(processedCountFlag);
    }

    public String lastProcessedAt() {
        return addPrefix(lastProcessedAtFlag);
    }

    public String lastSkippedAt() {
        return addPrefix(lastSkippedAtFlag);
    }



    private String addPrefix(String flag) {
        return String.format("%s_%s",prefix, flag);
    }


    public String metadata() {
        return metadataFlag;
    }
}

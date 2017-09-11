package com.softwareverde.tidyduck;

public class ReleaseItem {
    private String itemType;
    private Long itemId;
    private String itemName;
    private String itemVersion;
    private String predictedNextVersion;

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemVersion() {
        return itemVersion;
    }

    public void setItemVersion(String itemVersion) {
        this.itemVersion = itemVersion;
    }

    public String getPredictedNextVersion() {
        return predictedNextVersion;
    }

    public void setPredictedNextVersion(final String predictedNextVersion) {
        this.predictedNextVersion = predictedNextVersion;
    }
}

package com.softwareverde.tidyduck;

public class ReleaseItem {
    private String _itemType;
    private Long _itemId;
    private String _itemName;
    private String _itemVersion;
    private String _newVersion;
    private String _predictedNextVersion;

    public String getItemType() {
        return _itemType;
    }

    public void setItemType(String itemType) {
        this._itemType = itemType;
    }

    public Long getItemId() {
        return _itemId;
    }

    public void setItemId(Long itemId) {
        this._itemId = itemId;
    }

    public String getItemName() {
        return _itemName;
    }

    public void setItemName(String itemName) {
        this._itemName = itemName;
    }

    public String getItemVersion() {
        return _itemVersion;
    }

    public void setItemVersion(String itemVersion) {
        this._itemVersion = itemVersion;
    }

    public String getNewVersion() {
        return _newVersion;
    }

    public void setNewVersion(final String newVersion) {
        _newVersion = newVersion;
    }

    public String getPredictedNextVersion() {
        return _predictedNextVersion;
    }

    public void setPredictedNextVersion(final String predictedNextVersion) {
        this._predictedNextVersion = predictedNextVersion;
    }

    /**
     * <p>Returns true iff <code>obj</code> references the same item as <code>this</code>.</p>
     * @param obj
     * @return
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj instanceof ReleaseItem) {
            final ReleaseItem releaseItem = (ReleaseItem) obj;
            // if type and ID are the same then these reference the same item and should be considered equal
            return (this._itemType.equals(releaseItem._itemType) && this._itemId == releaseItem._itemId);
        }
        return false;
    }
}

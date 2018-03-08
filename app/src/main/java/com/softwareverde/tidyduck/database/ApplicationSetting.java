package com.softwareverde.tidyduck.database;

public enum ApplicationSetting {
    REVIEW_APPROVAL_MINIMUM_UPVOTES("REVIEW_APPROVAL_MINIMUM_UPVOTES"),
    ;

    private String _settingName;

    ApplicationSetting(final String settingName) {
        _settingName = settingName;
    }

    public String getSettingName() {
        return _settingName;
    }
}

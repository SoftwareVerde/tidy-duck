package com.softwareverde.tidyduck;

import com.softwareverde.json.Json;
import com.softwareverde.json.Jsonable;

public class Settings implements Jsonable {
    private String _theme;
    private String _defaultMode;

    public String getTheme() {
        return _theme;
    }

    public void setTheme(String _theme) {
        this._theme = _theme;
    }

    public String getDefaultMode() {
        return _defaultMode;
    }

    public void setDefaultMode(String _defaultMode) {
        this._defaultMode = _defaultMode;
    }

    @Override
    public Json toJson() {
        final Json json = new Json(false);

        json.put("theme", _theme);
        json.put("defaultMode", _defaultMode);

        return json;
    }
}

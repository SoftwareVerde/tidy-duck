class Settings {
    static fromJson(json) {
        if (json == null) {
            return null;
        }
        const settings = new Settings();

        settings.setTheme(json.theme);
        settings.setDefaultMode(json.defaultMode);

        return settings;
    }

    static toJson(settings) {
        return {
            theme:          this._theme,
            defaultMode:    this._defaultMode
        };
    }

    constructor() {
        this._theme = null;
        this._defaultMode = null;
    }

    setTheme(theme) {
        this._theme = theme;
    }

    getTheme() {
        return this._theme;
    }

    setDefaultMode(defaultMode) {
        this._defaultMode = defaultMode;
    }

    getDefaultMode() {
        return this._defaultMode;
    }
}
class Settings {
    static fromJson(json) {
        const settings = new Settings();

        settings.setTheme(json.theme);

        return settings;
    }

    static toJson(settings) {
        return {
            theme: this._theme
        };
    }

    constructor() {
        this._theme = null;
    }

    setTheme(theme) {
        this._theme = theme;
    }

    getTheme() {
        return this._theme;
    }
}
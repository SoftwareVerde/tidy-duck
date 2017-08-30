class NavigationItemConfig {
    constructor() {
        this._id = null;
        this._title = null;
        this._header = null;
        this._isReleased = null;
        this._onClickCallback = null;
        this._menuItemConfigs = [];
        this._iconName = "";

        this._form = null;
    }

    setId(id) {
        this._id = id;
    }

    getId() {
        return this._id;
    }

    setTitle(title) {
        this._title = title;
    }

    getTitle() {
        return this._title;
    }

    setHeader(header) {
        this._header = header;
    }

    getHeader() {
        return this._header;
    }

    setIsReleased(isReleased) {
        this._isReleased = isReleased;
    }

    isReleased() {
        return this._isReleased;
    }

    setOnClickCallback(callback) {
        this._onClickCallback = callback;
    }

    getOnClickCallback() {
        return this._onClickCallback;
    }

    addMenuItemConfig(menuItemConfig) {
        this._menuItemConfigs.push(menuItemConfig);
    }

    getMenuItemConfigs() {
        return this._menuItemConfigs;
    }

    setIconName(iconName) {
        this._iconName = iconName;
    }

    getIconName() {
        return this._iconName;
    }

    setForm(form) {
        this._form = form;
    }

    getForm() {
        return this._form;
    }
}

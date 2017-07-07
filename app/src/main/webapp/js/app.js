function redirectToLogin() {
    window.location.replace("/");
}

function setTheme(themeName) {
    document.getElementById('core-css').href =              '/css/themes/' + themeName + '/core.css';
    document.getElementById('app-css').href =               '/css/themes/' + themeName + '/app.css';
    document.getElementById('palette-css').href =           '/css/themes/' + themeName + '/palette.css';
    document.getElementById('react-input-field-css').href = '/css/themes/' + themeName + '/react/input-field.css';
    document.getElementById('react-toolbar-css').href =     '/css/themes/' + themeName + '/react/toolbar.css';
}

(function() {
    downloadAccount(function(data) {
        if (! data.wasSuccess) {
            redirectToLogin();
        }
    });
    document.getElementById('theme-selector').addEventListener("change", function () {
        setTheme(this.value);
    });
})();

(function() {
    const app = window.app;
    ReactDOM.render(<app.App />, document.getElementById("main"));
})();

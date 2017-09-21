function redirectToLogin() {
    window.location.replace("/");
}


(function() {
    checkAccount(function(data) {
        if (! data.wasSuccess) {
            redirectToLogin();
        }
    });
})();

(function() {
    const app = window.app;
    ReactDOM.render(<app.App />, document.getElementById("main"));
})();

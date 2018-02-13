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

if (typeof history.pushState === "function") {
    window.onpopstate = function(event) {
        let state = event.state;
        app.App._instance.handleRoleClick(state.roleName, state.subRoleName, false);
    };
}

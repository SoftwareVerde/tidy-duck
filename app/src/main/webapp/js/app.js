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
    history.pushState("tidy-duck-root", null, null);
    window.onpopstate = function(event) {
        let state = event.state;
        if (state == "tidy-duck-root") {
            // return to prior page
            history.go(-2); // skip over root state and original state
            return;
        }
        app.App._instance.handleRoleClick(state.roleName, state.subRoleName, false);
    };
}

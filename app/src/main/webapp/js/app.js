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

window.addEventListener("onload", function() {
    if (typeof history.pushState === "function") {
        history.pushState("tidy-duck-root", null, null);
        window.onpopstate = function(event) {
            console.log(event);
            //let state = event.state;
            //console.log("Popping: " + JSON.stringify(state));
            //App._instance.handleRoleClick(state.roleName, state.subRoleName, false);
        };
    }
});

(function() {
    const app = window.app;
    ReactDOM.render(<app.App />, document.getElementById("main"));
})();

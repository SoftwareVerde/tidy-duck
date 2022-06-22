function redirectToApp() {
    window.location.replace("/app/");
}

$(window).on("load", function() {
    $.get("/session/account", function(data) {
        if (data.wasSuccess) {
            redirectToApp();
        }
    });

    const submitFunction = function() {
        const username = $("#username").val();
        const password = $("#password").val();

        $.post(
            "/session/login",
            JSON.stringify({
                username: username,
                password: password
            }),
            function(data) {
                if (data.wasSuccess) {
                    redirectToApp();
                }
                else {
                    $("#authenticate-message").text(data.errorMessage);
                }
            },
            "json"
        );
    };

    $("#login-button").on("click", submitFunction);
    $("#username, #password").on("keypress", function(event) {
        if (event.which === 13) {
            submitFunction();
            return false;
        }
    });
});

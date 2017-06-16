function redirectToApp() {
    window.location.replace("/app/");
}

$(window).on("load", function() {
    $.get("/api/v1/account", function(data) {
        if (data.wasSuccess) {
            redirectToApp();
        }
    });

    $("#login-button").on("click", function() {
        const username = $("#username").val();
        const password = $("#password").val();

        $.post(
            "/api/v1/account/authenticate",
            {
                username: username,
                password: password
            },
            function(data) {
                if (data.wasSuccess) {
                    redirectToApp();
                }
            }
        );
    });
});

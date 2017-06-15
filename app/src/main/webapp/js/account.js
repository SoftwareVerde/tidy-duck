function redirectToApp() {
    window.location = "/app";
}


$(window).on("load", function() {
    $.get("/api/v1/account", function(data) {
        if (data.wasSuccess) {
            redirectToApp();
        }
    });

    $("#login-button").on("click", function() {
        $.post(
            "/api/v1/account/authenticate",
            {
                username: username,
                password: password
            },
            function(data) {
                console.log(data);

                if (data.wasSuccess) {
                    redirectToApp();
                }
            }
        );
    });
});

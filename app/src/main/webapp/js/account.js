$(window).on("load", function() {
    $("#login-button").on("click", function() {
        $.post(
            "/api/v1/account/authenticate",
            {
                username: username,
                password: password
            },
            function(data) {
                console.log(data);
            }
        );
    });
});

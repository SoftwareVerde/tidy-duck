
function downloadAccount(callback) {
    jsonFetch(
        new Request(
            API_PREFIX + "account",
            {
                method:         "GET",
                credentials:    "include"
            }
        ),
        function(data) {
            if (typeof callback == "function") {
                callback(data);
            }
        }
    );
}

function updateSettings(settings, callback) {
    const request = new Request(
        API_PREFIX + "settings",
        {
            method:         "POST",
            credentials:    "include",
            body:           JSON.stringify(settings)
        }
    )
    jsonFetch(request, function(data) {
            if (typeof callback == "function") {
                callback(data);
            }
        }
    );
}

function logout(callback) {
    jsonFetch(
        new Request(
            API_PREFIX + "account/logout",
            {
                method:         "GET",
                credentials:    "include"
            }
        ),
        function (data) {
            if (typeof callback == "function") {
                callback(data);
            }
        }
    );
}


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

function getAccount(accountId, callbackFunction) {
    jsonFetch(
        new Request(
            API_PREFIX + "accounts/" + accountId
        ),
        function (data) {
            let account = null;

            if (data.wasSuccess) {
                account = data.account;
            } else {
                console.log("Unable to get account: " + data.errorMessage);
            }

            if (typeof callbackFunction == "function") {
                callbackFunction(account);
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

function createNewAccount(account, callback) {
    const request = new Request(
        API_PREFIX + "accounts",
        {
            method: "POST",
            credentials: "include",
            body: JSON.stringify({
                "account":    account
            })
        }
    )
    jsonFetch(request, function(data) {
            if (typeof callback == "function") {
                callback(data);
            }
        }
    );
}

function changePassword(accountId, oldPassword, newPassword, callback) {
    const request = new Request(
        API_PREFIX + "accounts/" + accountId + "/change-password",
        {
            method: "POST",
            credentials: "include",
            body: JSON.stringify({
                "oldPassword":    oldPassword,
                "newPassword":    newPassword
            })
        }
    )
    jsonFetch(request, function(data) {
            if (typeof callback == "function") {
                callback(data);
            }
        }
    );
}

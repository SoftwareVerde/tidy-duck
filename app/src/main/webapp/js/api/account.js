
function checkAccount(callback) {
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

function getAccounts(callbackFunction) {
    jsonFetch(
        new Request(
            API_PREFIX + "accounts"
        ),
        function (data) {
            if (!data.wasSuccess) {
                console.log("Unable to get accounts: " + data.errorMessage);
            }

            if (typeof callbackFunction == "function") {
                callbackFunction(data);
            }
        }
    );
}

function getCompanies(callbackFunction) {
    const request = new Request(
        API_PREFIX + "companies",
        {
            method: "GET",
            credentials: "include"
        }
    );

    jsonFetch(request, function(data) {
        if (typeof callbackFunction == "function") {
            callbackFunction(data);
        }
    });
}

function createNewCompany(company, callback) {
    const request = new Request(
        API_PREFIX + "companies",
        {
            method: "POST",
            credentials: "include",
            body: JSON.stringify({
                "company":    company
            })
        }
    );
    jsonFetch(request, function(data) {
            if (typeof callback == "function") {
                callback(data);
            }
        }
    );
}

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
    );
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
    );
    jsonFetch(request, function(data) {
            if (typeof callback == "function") {
                callback(data);
            }
        }
    );
}

function resetPassword(accountId, callback) {
    const request = new Request(
        API_PREFIX + "accounts/" + accountId + "/reset-password",
        {
            method: "POST",
            credentials: "include"
        }
    );
    jsonFetch(request, function(data) {
            if (typeof callback == "function") {
                callback(data);
            }
        }
    );
}
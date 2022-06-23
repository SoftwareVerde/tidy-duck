
function checkAccount(callbackFunction, callbackWithResponse) {
    tidyFetch(
        new Request(
            API_PREFIX + "session/account",
            {
                method:         "GET",
                credentials:    "include"
            }
        ),
        function(data) {
            if (!data.wasSuccess) {
                console.log("Unable to get account: " + data.errorMessage);
            }

            if (typeof callbackFunction == "function") {
                if (callbackWithResponse) {
                    callbackFunction(data);
                    return;
                }

                let account = data.account;
                callbackFunction(account);
            }
        }
    );
}

function logout(callback) {
    let request = new Request(
        API_PREFIX + "session/logout",
        {
            method:         "GET",
            credentials:    "include"
        }
    );

    fetch(request, { credentials: "include" }).then(function(response) {
        if (response.ok) {
            if (typeof callback == "function") {
                callback();
            }
        }
    })
}

function getAccount(accountId, callbackFunction) {
    tidyFetch(
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
    tidyFetch(
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

function getActiveAccountsWithModifyPermission(callbackFunction) {
    tidyFetch(
        new Request(
            API_PREFIX + "accounts/filtered-accounts/active-modify-permission"
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

function updateAccountRoles(accountId, roleNames, callbackFunction) {
    tidyFetch(
        new Request(
            API_PREFIX + "accounts/" + accountId + "/roles",
            {
                method:         "POST",
                credentials:    "include",
                body: JSON.stringify({
                    roleNames: roleNames
                })
            }
        ),
        function (data) {
            if (!data.wasSuccess) {
                console.log("Unable to update roles: " + data.errorMessage);
            }

            if (typeof callbackFunction == "function") {
                callbackFunction(data);
            }
        }
    );
}

function getCompanies(callbackFunction) {
    const request = new Request(
        API_PREFIX + "accounts/companies",
        {
            method: "GET",
            credentials: "include"
        }
    );

    tidyFetch(request, function(data) {
        if (typeof callbackFunction == "function") {
            callbackFunction(data);
        }
    });
}

function createNewCompany(company, callback) {
    const request = new Request(
        API_PREFIX + "accounts/companies",
        {
            method: "POST",
            credentials: "include",
            body: JSON.stringify({
                "company":    company
            })
        }
    );
    tidyFetch(request, function(data) {
            if (typeof callback == "function") {
                callback(data);
            }
        }
    );
}

function downloadAccount(callback) {
    tidyFetch(
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
    tidyFetch(request, function(data) {
            if (typeof callback == "function") {
                callback(data);
            }
        }
    );
}

function updateAccountMetadata(account, callback) {
    const request = new Request(
        API_PREFIX + "accounts/" + account.id,
        {
            method:         "POST",
            credentials:    "include",
            body:           JSON.stringify({
                "account": account
            })
        }
    );
    tidyFetch(request, function(data) {
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
    tidyFetch(request, function(data) {
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
    tidyFetch(request, function(data) {
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
    tidyFetch(request, function(data) {
            if (typeof callback == "function") {
                callback(data);
            }
        }
    );
}

function markAccountAsDeleted(accountId, callback) {
    const request = new Request(
        API_PREFIX + "accounts/" + accountId + "/delete-account",
        {
            method: "POST",
            credentials: "include"
        }
    );
    tidyFetch(request, function(data) {
            if (typeof callback == "function") {
                callback(data);
            }
        }
    );
}

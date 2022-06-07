const ENDPOINT_PREFIX = "/";
const API_PREFIX = ENDPOINT_PREFIX + "api/v1/";

function exportFunctionCatalogToMost(functionCatalogId) {
    window.open(ENDPOINT_PREFIX + "v1/generate-most?function_catalog_id=" + functionCatalogId);
}

function jsonFetch(request, callbackFunction) {
    fetch(request, { credentials: "include" }).then(function(response) {
        return response.json();
    }).then(function(json) {
        if (typeof callbackFunction == "function") {
            callbackFunction(json);
        }
    });
}

function tidyFetch(request, callbackFunction) {
    jsonFetch(request, function(json) {
        if (json.errorMessage && json.errorMessage.includes("Not authenticated. Redirecting to login.")) {
            app.App.alert("Session Expired", "Your session has expired. You will be redirected to the login page.", function() {window.location.replace("/");});
        }
        else if (typeof callbackFunction == "function") {
            callbackFunction(json);
        }
    });
}

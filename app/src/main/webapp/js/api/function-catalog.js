
// calls callbackFunction with an array of function catalogs
function getFunctionCatalogs(callbackFunction) {
    const request = new Request(
        API_PREFIX + "function-catalog",
        {
            method: "GET",
            credentials: "include"
        }
    );

    jsonFetch(request, function(data) {
        let functionCatalogs = null;

        if (data.wasSuccess) {
            functionCatalogs = data.functionCatalogs;
        } else {
            console.log("Unable to get function catalogs: " + data.errorMessage);
        }

        if (typeof callbackFunction == "function") {
            callbackFunction(functionCatalogs);
        }
    });
}

// calls callbackFunction with new function catalog ID
function insertFunctionCatalog(functionCatalog, callbackFunction) {
    const request = new Request(
        API_PREFIX + "function-catalog",
        {
            method: "POST",
            credentials: "include",
            body: JSON.stringify({
                "functionCatalog": functionCatalog
            })
        }
    );

    jsonFetch(request, function(data) {
        let functionCatalogId = null;

        if (data.wasSuccess) {
            functionCatalogId = data.functionCatalogId;
        } else {
            console.log("Unable to insert function catalog: " + data.errorMessage);
        }

        if (typeof callbackFunction == "function") {
            callbackFunction(functionCatalogId);
        }
    });
}

//calls callbackFunction with modified function catalog ID
function updateFunctionCatalog(functionCatalogId, functionCatalog, callbackFunction) {
    const request = new Request(
        ENDPOINT_PREFIX + "api/v1/function-catalog/" + functionCatalogId,
        {
            method: "POST",
            credentials: "include",
            body: JSON.stringify({
                "functionCatalog": functionCatalog
            })
        }
    );

    jsonFetch(request, function(data) {
        const wasSuccess = data.wasSuccess;
        if (!wasSuccess) {
            console.log("Unable to modify function catalog " + functionCatalogId + ": " + data.errorMessage);
        }

        if (typeof callbackFunction == "function") {
            callbackFunction(wasSuccess);
        }
    });
}

function deleteFunctionCatalog(functionCatalogId, callbackFunction) {
    const request = new Request(
        ENDPOINT_PREFIX + "api/v1/function-catalog/" + functionCatalogId,
        {
            method: "DELETE",
            credentials: "include"
        }
    );

    jsonFetch(request, function (data) {
        const wasSuccess = data.wasSuccess;
        var errorMessage = "";
        if (!wasSuccess) {
            console.log("Unable to delete function catalog " + functionCatalogId + ": " + data.errorMessage);
            errorMessage = data.errorMessage;
        }

        if (typeof callbackFunction == "function") {
            callbackFunction(wasSuccess, errorMessage);
        }
    });
}

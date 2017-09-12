// calls callbackFunction with the function catalog
function getFunctionCatalog(functionCatalogId, callbackFunction) {
    const request = new Request(
        API_PREFIX + "function-catalogs/" + functionCatalogId,
        {
            method: "GET",
            credentials: "include"
        }
    );

    jsonFetch(request, function(data) {
        let functionCatalog = null;

        if (data.wasSuccess) {
            functionCatalog = data.functionCatalog;
        } else {
            console.error("Unable to get function catalog: " + data.errorMessage);
        }

        if (typeof callbackFunction == "function") {
            callbackFunction(functionCatalog);
        }
    });
}

// calls callbackFunction with an array of function catalogs
function getFunctionCatalogs(callbackFunction) {
    const request = new Request(
        API_PREFIX + "function-catalogs",
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
            console.error("Unable to get function catalogs: " + data.errorMessage);
        }

        if (typeof callbackFunction == "function") {
            callbackFunction(functionCatalogs);
        }
    });
}

// calls callbackFunction with new function catalog ID
function insertFunctionCatalog(functionCatalog, callbackFunction) {
    const request = new Request(
        API_PREFIX + "function-catalogs",
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
            console.error("Unable to insert function catalog: " + data.errorMessage);
        }

        if (typeof callbackFunction == "function") {
            callbackFunction(functionCatalogId);
        }
    });
}

//calls callbackFunction with modified function catalog ID
function updateFunctionCatalog(functionCatalogId, functionCatalog, shouldRelease, callbackFunction) {
    const request = new Request(
        ENDPOINT_PREFIX + "api/v1/function-catalogs/" + functionCatalogId,
        {
            method: "POST",
            credentials: "include",
            body: JSON.stringify({
                "functionCatalog": functionCatalog,
                "shouldRelease":   shouldRelease
            })
        }
    );

    jsonFetch(request, function(data) {
        const wasSuccess = data.wasSuccess;
        let functionCatalogId = null;

        if (wasSuccess) {
            functionCatalogId = data.functionCatalogId;
        }
        else {
            console.error("Unable to modify function catalog " + functionCatalogId + ": " + data.errorMessage);
        }

        if (typeof callbackFunction == "function") {
            callbackFunction(wasSuccess, functionCatalogId);
        }
    });
}

function deleteFunctionCatalog(functionCatalogId, callbackFunction) {
    const request = new Request(
        ENDPOINT_PREFIX + "api/v1/function-catalogs/" + functionCatalogId,
        {
            method: "DELETE",
            credentials: "include"
        }
    );

    jsonFetch(request, function (data) {
        const wasSuccess = data.wasSuccess;
        var errorMessage = "";
        if (!wasSuccess) {
            console.error("Unable to delete function catalog " + functionCatalogId + ": " + data.errorMessage);
            errorMessage = data.errorMessage;
        }

        if (typeof callbackFunction == "function") {
            callbackFunction(wasSuccess, errorMessage);
        }
    });
}

function submitFunctionCatalogForReview(functionCatalogId, callbackFunction) {
    const request = new Request(
        API_PREFIX + "function-catalogs/" + functionCatalogId + "/submit-for-review",
        {
            method: "POST",
            credentials: "include"
        }
    );

    jsonFetch(request, function(data) {
        const wasSuccess = data.wasSuccess;

        if (! wasSuccess) {
            console.error("Unable to submit function catalog for review: " + data.errorMessage);
        }

        if (typeof callbackFunction == "function") {
            callbackFunction(wasSuccess);
        }
    });
}

function getReleaseItemList(functionCatalogId, callbackFunction) {
    const request = new Request(
        API_PREFIX + "function-catalogs/" + functionCatalogId + "/release-item-list",
        {
            method: "GET",
            credentials: "include"
        }
    );

    jsonFetch(request, function(data) {
        const wasSuccess = data.wasSuccess;

        if (! wasSuccess) {
            console.error("Unable to get release items: " + data.errorMessage);
        }

        if (typeof callbackFunction == "function") {
            callbackFunction(data);
        }
    });
}

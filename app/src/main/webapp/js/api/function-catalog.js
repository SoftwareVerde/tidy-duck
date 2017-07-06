
// calls callbackFunction with an array of function catalogs
function getFunctionCatalogsForVersionId(versionId, callbackFunction) {
    const request = new Request(
        API_PREFIX + "function-catalog?version_id=" + versionId,
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
            console.log("Unable to get function catalogs for version " + versionId + ": " + data.errorMessage);
        }

        if (typeof callbackFunction == "function") {
            callbackFunction(functionCatalogs);
        }
    });
}

// calls callbackFunction with new function catalog ID
function insertFunctionCatalog(versionId, functionCatalog, callbackFunction) {
    const request = new Request(
        API_PREFIX + "function-catalog",
        {
            method: "POST",
            credentials: "include",
            body: JSON.stringify({
                "versionId": versionId,
                "functionCatalog": functionCatalog
            })
        }
    );

    jsonFetch(request, function(data) {
        let functionCatalogId = null;

        if (data.wasSuccess) {
            functionCatalogId = data.functionCatalogId;
        } else {
            console.log("Unable to insert function catalog for version " + versionId + ": " + data.errorMessage);
        }

        if (typeof callbackFunction == "function") {
            callbackFunction(functionCatalogId);
        }
    });
}

//calls callbackFunction with modified function catalog ID
function updateFunctionCatalog(versionId, functionCatalogId, functionCatalog, callbackFunction) {
    const request = new Request(
        ENDPOINT_PREFIX + "api/v1/function-catalog/" + functionCatalogId,
        {
            method: "POST",
            credentials: "include",
            body: JSON.stringify({
                "versionId": versionId,
                "functionCatalog": functionCatalog
            })
        }
    );

    jsonFetch(request, function(data) {
        const wasSuccess = data.wasSuccess;
        if (!wasSuccess) {
            console.log("Unable to modify function catalog " + functionCatalogId + " from version " + versionId + ": " + data.errorMessage);
        }

        if (typeof callbackFunction == "function") {
            callbackFunction(wasSuccess);
        }
    });
}

function deleteFunctionCatalog(versionId, functionCatalogId, callbackFunction) {
    const request = new Request(
        ENDPOINT_PREFIX + "api/v1/function-catalog/" + functionCatalogId + "?versionId=" + versionId,
        {
            method: "DELETE",
            credentials: "include"
        }
    );

    jsonFetch(request, function (data) {
        const wasSuccess = data.wasSuccess;
        var errorMessage = "";
        if (!wasSuccess) {
            console.log("Unable to delete function catalog " + functionCatalogId + " from version " + versionId + ": " + data.errorMessage);
            errorMessage = data.errorMessage;
        }

        if (typeof callbackFunction == "function") {
            callbackFunction(wasSuccess, errorMessage);
        }
    });
}
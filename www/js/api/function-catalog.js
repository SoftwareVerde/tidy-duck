// calls callbackFunction with the function catalog
function getFunctionCatalog(functionCatalogId, callbackFunction) {
    const request = new Request(
        API_PREFIX + "function-catalogs/" + functionCatalogId,
        {
            method: "GET",
            credentials: "include"
        }
    );

    tidyFetch(request, function(data) {
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

    tidyFetch(request, function(data) {
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

// calls callbackFunction with an array of function catalogs in trash
function getFunctionCatalogsMarkedAsDeleted(callbackFunction) {
    const request = new Request(
        API_PREFIX + "function-catalogs/trashed",
        {
            method: "GET",
            credentials: "include"
        }
    );

    tidyFetch(request, function(data) {
        let functionCatalogs = null;

        if (data.wasSuccess) {
            functionCatalogs = data.functionCatalogs;
        } else {
            console.error("Unable to get function catalogs marked as deleted: " + data.errorMessage);
        }

        if (typeof callbackFunction == "function") {
            callbackFunction(functionCatalogs);
        }
    });
}

///Calls callbackFunction with an array of Function Blocks filtered by search string.
function getFunctionCatalogsMatchingSearchString(searchString, includeDeleted, callbackFunction) {
    const request = new Request(
        API_PREFIX + "function-catalogs/search/" + searchString + (includeDeleted ? "" : "?includeDeleted=false"),
        {
            method: "GET",
            credentials: "include"
        }
    );

    tidyFetch(request, function(data) {
        let functionCatalogs = null;

        if (data.wasSuccess) {
            functionCatalogs = data.functionCatalogs;
        } else {
            console.error("Unable to get Function Catalogs for search string " + searchString + ": " + data.errorMessage);
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

    tidyFetch(request, function(data) {
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

function updateFunctionCatalog(functionCatalogId, functionCatalog, shouldRelease, callbackFunction) {
    const request = new Request(
        API_PREFIX + "function-catalogs/" + functionCatalogId,
        {
            method: "POST",
            credentials: "include",
            body: JSON.stringify({
                "functionCatalog": functionCatalog,
                "shouldRelease":   shouldRelease
            })
        }
    );

    tidyFetch(request, function(data) {
        const wasSuccess = data.wasSuccess;

        if (!wasSuccess) {
            console.error("Unable to modify function catalog " + functionCatalogId + ": " + data.errorMessage);
        }

        if (typeof callbackFunction == "function") {
            callbackFunction(data);
        }
    });
}

// calls callbackFunction with new function catalog ID
function forkFunctionCatalog(functionCatalogId, callbackFunction) {
    const request = new Request(
        API_PREFIX + "function-catalogs/" + functionCatalogId + "/fork",
        {
            method: "POST",
            credentials: "include"
        }
    );

    tidyFetch(request, function(data) {
        const wasSuccess = data.wasSuccess;
        let newFunctionCatalogId = null;

        if (wasSuccess) {
            newFunctionCatalogId = data.functionCatalogId;
        }
        else {
            console.error("Unable to fork function catalog " + functionCatalogId + ": " + data.errorMessage);
        }

        if (typeof callbackFunction == "function") {
            callbackFunction(data, newFunctionCatalogId);
        }
    });
}

function deleteFunctionCatalog(functionCatalogId, callbackFunction) {
    const request = new Request(
        API_PREFIX + "function-catalogs/" + functionCatalogId,
        {
            method: "DELETE",
            credentials: "include"
        }
    );

    tidyFetch(request, function (data) {
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

function markFunctionCatalogAsDeleted(functionCatalogId, callbackFunction) {
    const request = new Request(
        API_PREFIX + "function-catalogs/" + functionCatalogId + "/mark-as-deleted",
        {
            method: "POST",
            credentials: "include"
        }
    );

    tidyFetch(request, function (data) {
        const wasSuccess = data.wasSuccess;
        if (! wasSuccess) {
            console.error("Unable to mark function catalog " + functionCatalogId + " as deleted: " + data.errorMessage);
        }

        if (typeof callbackFunction == "function") {
            callbackFunction(data);
        }
    });
}

function restoreFunctionCatalogFromTrash(functionCatalogId, callbackFunction) {
    const request = new Request(
        API_PREFIX + "function-catalogs/" + functionCatalogId + "/restore-from-trash",
        {
            method: "POST",
            credentials: "include"
        }
    );

    tidyFetch(request, function (data) {
        const wasSuccess = data.wasSuccess;
        let errorMessage = "";
        if (! wasSuccess) {
            console.error("Unable to restore function catalog " + functionCatalogId + " from trash: " + data.errorMessage);
            errorMessage = data.errorMessage;
        }

        if (typeof callbackFunction == "function") {
            callbackFunction(data, errorMessage);
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

    tidyFetch(request, function(data) {
        const wasSuccess = data.wasSuccess;

        if (! wasSuccess) {
            console.error("Unable to submit function catalog for review: " + data.errorMessage);
        }

        if (typeof callbackFunction == "function") {
            callbackFunction(data);
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

    tidyFetch(request, function(data) {
        const wasSuccess = data.wasSuccess;

        if (! wasSuccess) {
            console.error("Unable to get release items: " + data.errorMessage);
        }

        if (typeof callbackFunction == "function") {
            callbackFunction(data);
        }
    });
}

function releaseFunctionCatalog(functionCatalogId, releaseItems, callbackFunction) {
    const request = new Request(
        API_PREFIX + "function-catalogs/" + functionCatalogId + "/release",
        {
            method: "POST",
            credentials: "include",
            body: JSON.stringify({
                releaseItems: releaseItems
            })
        }
    );

    tidyFetch(request, function(data) {
        const wasSuccess = data.wasSuccess;

        if (! wasSuccess) {
            console.error("Unable to release function catalog " + functionCatalogId + ": " + data.errorMessage);
        }

        if (typeof callbackFunction == "function") {
            callbackFunction(data);
        }
    });
}

function checkForDuplicateFunctionCatalog(functionCatalogName, functionCatalogVersionSeries, callbackFunction) {
    const request = new Request(
        API_PREFIX + "function-catalogs/duplicate-check",
        {
            method: "POST",
            credentials: "include",
            body: JSON.stringify({
                functionCatalogName: functionCatalogName,
                functionCatalogVersionSeries: functionCatalogVersionSeries
            })
        }
    );

    tidyFetch(request, function(data) {
        const wasSuccess = data.wasSuccess;

        if (! wasSuccess) {
            console.error("Unable to check for duplicate function catalog: " + data.errorMessage);
        }

        if (typeof callbackFunction == "function") {
            callbackFunction(data);
        }
    });
}

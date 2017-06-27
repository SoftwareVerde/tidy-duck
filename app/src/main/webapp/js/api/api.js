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

// FUNCTION CATALOGS

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
        if (!wasSuccess) {
            console.log("Unable to delete function catalog " + functionCatalogId + " from version " + versionId + ": " + data.errorMessage);
        }

        if (typeof callbackFunction == "function") {
            callbackFunction(wasSuccess);
        }
    });
}

// FUNCTION BLOCKS

// calls callbackFunction with an array of function blocks
function getFunctionBlocksForFunctionCatalogId(functionCatalogId, callbackFunction) {
    const request = new Request(
        ENDPOINT_PREFIX + "api/v1/function-block?function_catalog_id=" + functionCatalogId,
        {
            method: "GET",
            credentials: "include"
        }
    );

    jsonFetch(request, function(data) {
        let functionBlocks = null;

        if (data.wasSuccess) {
            functionBlocks = data.functionBlocks;
        } else {
            console.log("Unable to get function blocks for function catalog " + functionCatalogId + ": " + data.errorMessage);
        }

        if (typeof callbackFunction == "function") {
            callbackFunction(functionBlocks);
        }
    });
}

// calls callbackFunction with new function block ID
function insertFunctionBlock(functionCatalogId, functionBlock, callbackFunction) {
    const request = new Request(
        API_PREFIX + "function-block",
        {
            method: "POST",
            credentials: "include",
            body: JSON.stringify({
                "functionCatalogId":    functionCatalogId,
                "functionBlock":        functionBlock
            })
        }
    );

    jsonFetch(request, function(data) {
        let functionBlockId = null;

        if (data.wasSuccess) {
            functionBlockId = data.functionBlockId;
        } else {
            console.log("Unable to insert function block: " + data.errorMessage);
        }

        if (typeof callbackFunction == "function") {
            callbackFunction(functionBlockId);
        }
    });
}

// calls callbackFunction with modified function block ID
function updateFunctionBlock(functionCatalogId, functionBlockId, functionBlock, callbackFunction) {
    const request = new Request(
        ENDPOINT_PREFIX + "api/v1/function-block/" + functionBlockId,
        {
            method: "POST",
            credentials: "include",
            body: JSON.stringify({
                "functionCatalogId":    functionCatalogId,
                "functionBlock":    functionBlock
            })
        }
    );

    jsonFetch(request, function(data) {
        const wasSuccess = data.wasSuccess;
        if (! wasSuccess) {
            console.log("Unable to modify function block " + functionBlockId + " : " + data.errorMessage);
        }

        if (typeof callbackFunction == "function") {
            callbackFunction(wasSuccess);
        }
    });
}

function deleteFunctionBlock(functionCatalogId, functionBlockId, callbackFunction) {
    const request = new Request(
        ENDPOINT_PREFIX + "api/v1/function-block/" + functionBlockId + "?functionCatalogId=" + functionCatalogId,
        {
            method: "DELETE",
            credentials: "include"
        }
    );

    jsonFetch(request, function (data) {
        const wasSuccess = data.wasSuccess;
        if (!wasSuccess) {
            console.log("Unable to delete function block " + functionBlockId + " from function catalog " + functionCatalogId + ": " + data.errorMessage);
        }

        if (typeof callbackFunction == "function") {
            callbackFunction(wasSuccess);
        }
    });
}

// MOST INTERFACES

// calls callbackFunction with an array of MOST interfaces.
function getMostInterfacesForFunctionBlockId(functionBlockId, callbackFunction) {
    const request = new Request(
        ENDPOINT_PREFIX + "api/v1/most-interface?function_block_id=" + functionBlockId,
        {
            method: "GET",
            credentials: "include"
        }
    );

    jsonFetch(request, function(data) {
        let mostInterfaces = null;

        if (data.wasSuccess) {
            mostInterfaces = data.mostInterfaces;
        } else {
            console.log("Unable to get Interfaces for function block " + functionBlockId + ": " + data.errorMessage);
        }

        if (typeof callbackFunction == "function") {
            callbackFunction(mostInterfaces);
        }
    });
}

///Calls callbackFunction with an array of MOST interfaces filtered by search string.
function getMostInterfacesMatchingSearchString(searchString, callbackFunction) {
    const request = new Request(
        ENDPOINT_PREFIX + "api/v1/most-interface/search?name=" + searchString,
        {
            method: "GET",
            credentials: "include"
        }
    );

    jsonFetch(request, function(data) {
        let mostInterfaces = null;

        if (data.wasSuccess) {
            mostInterfaces = data.mostInterfaces;
        } else {
            console.log("Unable to get Interfaces for search string " + searchString + ": " + data.errorMessage);
        }

        if (typeof callbackFunction == "function") {
            callbackFunction(mostInterfaces);
        }
    });
}

// calls callbackFunction with new MOST interface ID
function insertMostInterface(functionBlockId, mostInterface, callbackFunction) {
    const request = new Request(
        API_PREFIX + "most-interface",
        {
            method: "POST",
            credentials: "include",
            body: JSON.stringify({
                "functionBlockId":      functionBlockId,
                "mostInterface":        mostInterface
            })
        }
    );

    jsonFetch(request, function(data) {
        let mostInterfaceId = null;

        if (data.wasSuccess) {
            mostInterfaceId = data.mostInterfaceId;
        } else {
            console.log("Unable to insert interface: " + data.errorMessage);
        }

        if (typeof callbackFunction == "function") {
            callbackFunction(mostInterfaceId);
        }
    });
}

// calls callbackFunction with modified MOST interface ID
function updateMostInterface(functionBlockId, mostInterfaceId, mostInterface, callbackFunction) {
    const request = new Request(
        ENDPOINT_PREFIX + "api/v1/most-interface/" + mostInterfaceId,
        {
            method: "POST",
            credentials: "include",
            body: JSON.stringify({
                "functionBlockId":    functionBlockId,
                "mostInterface":      mostInterface
            })
        }
    );

    jsonFetch(request, function(data) {
        const wasSuccess = data.wasSuccess;
        if (! wasSuccess) {
            console.log("Unable to modify interface " + mostInterfaceId + " : " + data.errorMessage);
        }

        if (typeof callbackFunction == "function") {
            callbackFunction(wasSuccess);
        }
    });
}

function deleteMostInterface(functionBlockId, mostInterfaceId, callbackFunction) {
    const request = new Request(
        ENDPOINT_PREFIX + "api/v1/most-interface/" + mostInterfaceId + "?functionBlockId=" + functionBlockId,
        {
            method: "DELETE",
            credentials: "include"
        }
    );

    jsonFetch(request, function (data) {
        const wasSuccess = data.wasSuccess;
        if (!wasSuccess) {
            console.log("Unable to delete interface " + mostInterfaceId + " from function block " + functionBlockId + ": " + data.errorMessage);
        }

        if (typeof callbackFunction == "function") {
            callbackFunction(wasSuccess);
        }
    });
}

// ACCOUNT

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



// calls callbackFunction with an array of function blocks
function getFunctionBlocksForFunctionCatalogId(functionCatalogId, callbackFunction) {
    let url = ENDPOINT_PREFIX + "api/v1/function-blocks";
    if (functionCatalogId) {
        url = url.concat("?function_catalog_id=" + functionCatalogId);
    }

    const request = new Request(
        url,
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
            console.error("Unable to get function blocks for function catalog " + functionCatalogId + ": " + data.errorMessage);
        }

        if (typeof callbackFunction == "function") {
            callbackFunction(functionBlocks);
        }
    });
}

///Calls callbackFunction with an array of Function Blocks filtered by search string.
function getFunctionBlocksMatchingSearchString(searchString, callbackFunction) {
    const request = new Request(
        ENDPOINT_PREFIX + "api/v1/function-blocks/search?name=" + searchString,
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
            console.error("Unable to get Function Blocks for search string " + searchString + ": " + data.errorMessage);
        }

        if (typeof callbackFunction == "function") {
            callbackFunction(functionBlocks);
        }
    });
}

// calls callbackFunction with list of function catalog IDs
function listFunctionCatalogsContainingFunctionBlock(functionBlockId, callbackFunction) {
    const request = new Request(
        API_PREFIX + "function-blocks/" + functionBlockId + "/function-catalogs",
        {
            method: "GET",
            credentials: "include"
        }
    );

    jsonFetch(request, function (data) {
        if (!data.wasSuccess) {
            console.error("Unable to get function catalogs associated with function block " + functionBlockId);
        }
        if (typeof callbackFunction == "function") {
            callbackFunction(data);
        }
    });
}

// calls callbackFunction with new function block ID
function insertFunctionBlock(functionCatalogId, functionBlock, callbackFunction) {
    const request = new Request(
        API_PREFIX + "function-blocks",
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
            console.error("Unable to insert function block: " + data.errorMessage);
        }

        if (typeof callbackFunction == "function") {
            callbackFunction(functionBlockId);
        }
    });
}

// calls callbackFunction with wasSuccess
function associateFunctionBlockWithFunctionCatalog(functionCatalogId, functionBlockId, callbackFunction) {
    const request = new Request(
        ENDPOINT_PREFIX + "api/v1/function-blocks/" + functionBlockId + "/function-catalogs",
        {
            method: "POST",
            credentials: "include",
            body: JSON.stringify({
                functionCatalogId: functionCatalogId
            })
        }
    );
    jsonFetch(request, function (data) {
        const wasSuccess = data.wasSuccess;
        var errorMessage = "";
        if (! wasSuccess) {
            console.error("Unable to associate function block " + functionBlockId + " with function catalog: " + data.errorMessage);
            errorMessage = data.errorMessage;
        }

        if (typeof callbackFunction == "function") {
            callbackFunction(wasSuccess, errorMessage);
        }
    });
}

// calls callbackFunction with modified function block ID
function updateFunctionBlock(functionCatalogId, functionBlockId, functionBlock, callbackFunction) {
    const request = new Request(
        ENDPOINT_PREFIX + "api/v1/function-blocks/" + functionBlockId,
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
        let functionBlockId = null;

        if (wasSuccess) {
            functionBlockId = data.functionBlockId;
        }
        else {
            console.error("Unable to modify function block " + functionBlockId + " : " + data.errorMessage);
        }

        if (typeof callbackFunction == "function") {
            callbackFunction(wasSuccess, functionBlockId);
        }
    });
}

function deleteFunctionBlock(functionCatalogId, functionBlockId, callbackFunction) {
    const request = new Request(
        ENDPOINT_PREFIX + "api/v1/function-blocks/" + functionBlockId + "?functionCatalogId=" + functionCatalogId,
        {
            method: "DELETE",
            credentials: "include"
        }
    );

    jsonFetch(request, function (data) {
        const wasSuccess = data.wasSuccess;
        var errorMessage = "";
        if (!wasSuccess) {
            console.error("Unable to delete function block " + functionBlockId + " from function catalog " + functionCatalogId + ": " + data.errorMessage);
            errorMessage = data.errorMessage;
        }

        if (typeof callbackFunction == "function") {
            callbackFunction(wasSuccess, errorMessage);
        }
    });
}

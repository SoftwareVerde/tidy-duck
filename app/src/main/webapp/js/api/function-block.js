
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


///Calls callbackFunction with an array of Function Blocks filtered by search string.
function getFunctionBlocksMatchingSearchString(versionId, searchString, callbackFunction) {
    const request = new Request(
        ENDPOINT_PREFIX + "api/v1/function-block/search?name=" + searchString + "&versionId=" + versionId,
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
            console.log("Unable to get Function Blocks for search string " + searchString + ": " + data.errorMessage);
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

// calls callbackFunction with wasSuccess
function associateFunctionBlockWithFunctionCatalog(functionCatalogId, functionBlockId, callbackFunction) {
    const request = new Request(
        ENDPOINT_PREFIX + "api/v1/function-block/" + functionBlockId + "/function-catalogs",
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
            console.log("Unable to associate function block " + functionBlockId + " with function catalog: " + data.errorMessage);
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
        var errorMessage = "";
        if (!wasSuccess) {
            console.log("Unable to delete function block " + functionBlockId + " from function catalog " + functionCatalogId + ": " + data.errorMessage);
            errorMessage = data.errorMessage;
        }

        if (typeof callbackFunction == "function") {
            callbackFunction(wasSuccess, errorMessage);
        }
    });
}


// calls callbackFunction with an array of MOST interfaces.
function getMostInterfacesForFunctionBlockId(functionBlockId, callbackFunction) {
    let url = ENDPOINT_PREFIX + "api/v1/most-interfaces";
    if (functionBlockId) {
        url = url.concat("?function_block_id=" + functionBlockId);
    }

    const request = new Request(
        url,
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
            console.error("Unable to get Interfaces for function block " + functionBlockId + ": " + data.errorMessage);
        }

        if (typeof callbackFunction == "function") {
            callbackFunction(mostInterfaces);
        }
    });
}

///Calls callbackFunction with an array of MOST interfaces filtered by search string.
function getMostInterfacesMatchingSearchString(searchString, callbackFunction) {
    const request = new Request(
        ENDPOINT_PREFIX + "api/v1/most-interfaces/search?name=" + searchString,
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
            console.error("Unable to get Interfaces for search string " + searchString + ": " + data.errorMessage);
        }

        if (typeof callbackFunction == "function") {
            callbackFunction(mostInterfaces);
        }
    });
}

// calls callbackFunction with list of function catalog IDs
function listFunctionBlocksContainingMostInterface(mostInterfaceId, callbackFunction) {
    const request = new Request(
        API_PREFIX + "most-interfaces/" + mostInterfaceId + "/function-blocks",
        {
            method: "GET",
            credentials: "include"
        }
    );

    jsonFetch(request, function (data) {
        if (!data.wasSuccess) {
            console.error("Unable to get function blocks associated with interface " + mostInterfaceId);
        }
        if (typeof callbackFunction == "function") {
            callbackFunction(data);
        }
    });
}

// calls callbackFunction with new MOST interface ID
function insertMostInterface(functionBlockId, mostInterface, callbackFunction) {
    const request = new Request(
        API_PREFIX + "most-interfaces",
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
            console.error("Unable to insert interface: " + data.errorMessage);
        }

        if (typeof callbackFunction == "function") {
            callbackFunction(mostInterfaceId);
        }
    });
}

// calls callbackFunction with wasSuccess
function associateMostInterfaceWithFunctionBlock(functionBlockId, mostInterfaceId, callbackFunction) {
    const request = new Request(
        ENDPOINT_PREFIX + "api/v1/most-interfaces/" + mostInterfaceId + "/function-blocks",
        {
            method: "POST",
            credentials: "include",
            body: JSON.stringify({
                functionBlockId: functionBlockId
            })
        }
    );
    jsonFetch(request, function (data) {
        const wasSuccess = data.wasSuccess;
        var errorMessage = "";
        if (! wasSuccess) {
            console.error("Unable to associate interface " + mostInterfaceId + " with function block: " + data.errorMessage);
            errorMessage = data.errorMessage;
        }

        if (typeof callbackFunction == "function") {
            callbackFunction(wasSuccess, errorMessage);
        }
    });
}

// calls callbackFunction with modified MOST interface ID
function updateMostInterface(functionBlockId, mostInterfaceId, mostInterface, callbackFunction) {
    const request = new Request(
        ENDPOINT_PREFIX + "api/v1/most-interfaces/" + mostInterfaceId,
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
            console.error("Unable to modify interface " + mostInterfaceId + " : " + data.errorMessage);
        }

        if (typeof callbackFunction == "function") {
            callbackFunction(wasSuccess);
        }
    });
}

function deleteMostInterface(functionBlockId, mostInterfaceId, callbackFunction) {
    const request = new Request(
        ENDPOINT_PREFIX + "api/v1/most-interfaces/" + mostInterfaceId + "?functionBlockId=" + functionBlockId,
        {
            method: "DELETE",
            credentials: "include"
        }
    );

    jsonFetch(request, function (data) {
        const wasSuccess = data.wasSuccess;
        var errorMessage = "";
        if (!wasSuccess) {
            console.error("Unable to delete interface " + mostInterfaceId + " from function block " + functionBlockId + ": " + data.errorMessage);
            errorMessage = data.errorMessage;
        }

        if (typeof callbackFunction == "function") {
            callbackFunction(wasSuccess, errorMessage);
        }
    });
}

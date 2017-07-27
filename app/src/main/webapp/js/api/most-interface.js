
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

// calls callbackFunction with list of function catalog IDs
function listFunctionBlocksContainingMostInterface(mostInterfaceId, callbackFunction) {
    const request = new Request(
        API_PREFIX + "most-interface/" + mostInterfaceId + "/function-blocks",
        {
            method: "GET",
            credentials: "include"
        }
    );

    jsonFetch(request, function (data) {
        if (!data.wasSuccess) {
            console.log("Unable to get function blocks associated with interface " + mostInterfaceId);
        }
        if (typeof callbackFunction == "function") {
            callbackFunction(data);
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

// calls callbackFunction with wasSuccess
function associateMostInterfaceWithFunctionBlock(functionBlockId, mostInterfaceId, callbackFunction) {
    const request = new Request(
        ENDPOINT_PREFIX + "api/v1/most-interface/" + mostInterfaceId + "/function-blocks",
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
            console.log("Unable to associate interface " + mostInterfaceId + " with function block: " + data.errorMessage);
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
        var errorMessage = "";
        if (!wasSuccess) {
            console.log("Unable to delete interface " + mostInterfaceId + " from function block " + functionBlockId + ": " + data.errorMessage);
            errorMessage = data.errorMessage;
        }

        if (typeof callbackFunction == "function") {
            callbackFunction(wasSuccess, errorMessage);
        }
    });
}

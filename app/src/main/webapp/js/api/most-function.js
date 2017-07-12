
// calls callbackFunction with an array of functions
function getMostFunctionsForMostInterfaceId(mostInterfaceId, callbackFunction) {
    const request = new Request(
        API_PREFIX + "most-function?most_interface_id=" + mostInterfaceId,
        {
            method: "GET",
            credentials: "include"
        }
    );

    jsonFetch(request, function(data) {
        let mostFunctions = null;

        if (data.wasSuccess) {
            mostFunctions = data.mostFunctions;
        } else {
            console.log("Unable to get functions for interface " + mostInterfaceId + ": " + data.errorMessage);
        }

        if (typeof callbackFunction == "function") {
            callbackFunction(mostFunctions);
        }
    });
}

// calls callbackFunction with list of function catalog IDs
function listMostInterfacesContainingMostFunction(mostFunctionId, versionId, callbackFunction) {
    const request = new Request(
        API_PREFIX + "most-function/" + mostFunctionId + "/interfaces?versionId=" + versionId,
        {
            method: "GET",
            credentials: "include"
        }
    );

    jsonFetch(request, function (data) {
        if (!data.wasSuccess) {
            console.log("Unable to get interfaces associated with function " + mostFunctionId);
        }
        if (typeof callbackFunction == "function") {
            callbackFunction(data);
        }
    });
}

// calls callbackFunction with new function ID
function insertMostFunction(mostInterfaceId, mostFunction, callbackFunction) {
    const request = new Request(
        API_PREFIX + "most-function",
        {
            method: "POST",
            credentials: "include",
            body: JSON.stringify({
                "mostInterfaceId": mostInterfaceId,
                "mostFunction": mostFunction
            })
        }
    );

    jsonFetch(request, function(data) {
        let mostFunctionId = null;

        if (data.wasSuccess) {
            mostFunctionId = data.mostFunctionId;
        } else {
            console.log("Unable to insert function for version " + mostInterfaceId + ": " + data.errorMessage);
        }

        if (typeof callbackFunction == "function") {
            callbackFunction(mostFunctionId);
        }
    });
}

//calls callbackFunction with modified function ID
function updateMostFunction(mostInterfaceId, mostFunctionId, mostFunction, callbackFunction) {
    const request = new Request(
        ENDPOINT_PREFIX + "api/v1/most-function/" + mostFunctionId,
        {
            method: "POST",
            credentials: "include",
            body: JSON.stringify({
                "mostInterfaceId": mostInterfaceId,
                "mostFunction": mostFunction
            })
        }
    );

    jsonFetch(request, function(data) {
        const wasSuccess = data.wasSuccess;
        if (!wasSuccess) {
            console.log("Unable to modify function " + mostFunctionId + " from interface " + mostInterfaceId + ": " + data.errorMessage);
        }

        if (typeof callbackFunction == "function") {
            callbackFunction(wasSuccess);
        }
    });
}

function deleteMostFunction(mostInterfaceId, mostFunctionId, callbackFunction) {
    const request = new Request(
        ENDPOINT_PREFIX + "api/v1/most-function/" + mostFunctionId + "?most_interface_id=" + mostInterfaceId,
        {
            method: "DELETE",
            credentials: "include"
        }
    );

    jsonFetch(request, function (data) {
        const wasSuccess = data.wasSuccess;
        var errorMessage = "";
        if (!wasSuccess) {
            console.log("Unable to delete function " + mostFunctionId + " from interface " + mostInterfaceId + ": " + data.errorMessage);
            errorMessage = data.errorMessage;
        }

        if (typeof callbackFunction == "function") {
            callbackFunction(wasSuccess, errorMessage);
        }
    });
}
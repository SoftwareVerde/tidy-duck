// calls callbackFunction with the most function
function getMostFunction(mostFunctionId, callbackFunction) {
    const request = new Request(
        API_PREFIX + "most-functions/" + mostFunctionId,
        {
            method: "GET",
            credentials: "include"
        }
    );

    tidyFetch(request, function(data) {
        let mostFunction = null;

        if (data.wasSuccess) {
            mostFunction = data.mostFunction;
        } else {
            console.error("Unable to get most function: " + data.errorMessage);
        }

        if (typeof callbackFunction == "function") {
            callbackFunction(mostFunction);
        }
    });
}

// calls callbackFunction with an array of functions
function getMostFunctionsForMostInterfaceId(mostInterfaceId, callbackFunction) {
    const request = new Request(
        API_PREFIX + "most-functions?most_interface_id=" + mostInterfaceId,
        {
            method: "GET",
            credentials: "include"
        }
    );

    tidyFetch(request, function(data) {
        let mostFunctions = null;

        if (data.wasSuccess) {
            mostFunctions = data.mostFunctions;
        } else {
            console.error("Unable to get functions for interface " + mostInterfaceId + ": " + data.errorMessage);
        }

        if (typeof callbackFunction == "function") {
            callbackFunction(mostFunctions);
        }
    });
}

// calls callbackFunction with an array of functions in trash
function getMostFunctionsMarkedAsDeletedForMostInterfaceId(mostInterfaceId, callbackFunction) {
    const request = new Request(
        API_PREFIX + "trashed-most-functions?most_interface_id=" + mostInterfaceId,
        {
            method: "GET",
            credentials: "include"
        }
    );

    tidyFetch(request, function(data) {
        let mostFunctions = null;

        if (data.wasSuccess) {
            mostFunctions = data.mostFunctions;
        } else {
            console.error("Unable to get functions marked as deleted: " + data.errorMessage);
        }

        if (typeof callbackFunction == "function") {
            callbackFunction(mostFunctions);
        }
    });
}

// calls callbackFunction with list of function catalog IDs
function listMostInterfacesContainingMostFunction(mostFunctionId, callbackFunction) {
    const request = new Request(
        API_PREFIX + "most-functions/" + mostFunctionId + "/interfaces",
        {
            method: "GET",
            credentials: "include"
        }
    );

    tidyFetch(request, function (data) {
        if (!data.wasSuccess) {
            console.error("Unable to get interfaces associated with function " + mostFunctionId);
        }
        if (typeof callbackFunction == "function") {
            callbackFunction(data);
        }
    });
}

// calls callbackFunction with new function ID
function insertMostFunction(mostInterfaceId, mostFunction, callbackFunction) {
    const request = new Request(
        API_PREFIX + "most-functions",
        {
            method: "POST",
            credentials: "include",
            body: JSON.stringify({
                "mostInterfaceId": mostInterfaceId,
                "mostFunction": mostFunction
            })
        }
    );

    tidyFetch(request, function(data) {
        if (!data.wasSuccess) {
            console.error("Unable to insert function for version " + mostInterfaceId + ": " + data.errorMessage);
        }

        if (typeof callbackFunction == "function") {
            callbackFunction(data);
        }
    });
}

//calls callbackFunction with modified function ID
function updateMostFunction(mostInterfaceId, mostFunctionId, mostFunction, callbackFunction) {
    const request = new Request(
        ENDPOINT_PREFIX + "api/v1/most-functions/" + mostFunctionId,
        {
            method: "POST",
            credentials: "include",
            body: JSON.stringify({
                "mostInterfaceId": mostInterfaceId,
                "mostFunction": mostFunction
            })
        }
    );

    tidyFetch(request, function(data) {
        const wasSuccess = data.wasSuccess;
        if (!wasSuccess) {
            console.error("Unable to modify function " + mostFunctionId + " from interface " + mostInterfaceId + ": " + data.errorMessage);
        }

        if (typeof callbackFunction == "function") {
            callbackFunction(data);
        }
    });
}

function deleteMostFunction(mostInterfaceId, mostFunctionId, callbackFunction) {
    const request = new Request(
        ENDPOINT_PREFIX + "api/v1/most-functions/" + mostFunctionId + "?most_interface_id=" + mostInterfaceId,
        {
            method: "DELETE",
            credentials: "include"
        }
    );

    tidyFetch(request, function (data) {
        const wasSuccess = data.wasSuccess;
        var errorMessage = "";
        if (!wasSuccess) {
            console.error("Unable to delete function " + mostFunctionId + " from interface " + mostInterfaceId + ": " + data.errorMessage);
            errorMessage = data.errorMessage;
        }

        if (typeof callbackFunction == "function") {
            callbackFunction(wasSuccess, errorMessage);
        }
    });
}

function markMostFunctionAsDeleted(mostFunctionId, callbackFunction) {
    const request = new Request(
        ENDPOINT_PREFIX + "api/v1/most-functions/" + mostFunctionId + "/mark-as-deleted",
        {
            method: "DELETE",
            credentials: "include"
        }
    );

    tidyFetch(request, function (data) {
        const wasSuccess = data.wasSuccess;
        if (! wasSuccess) {
            console.error("Unable to mark function " + mostFunctionId + " as deleted: " + data.errorMessage);
        }

        if (typeof callbackFunction == "function") {
            callbackFunction(data);
        }
    });
}

function restoreMostFunctionFromTrash(mostFunctionId, callbackFunction) {
    const request = new Request(
        ENDPOINT_PREFIX + "api/v1/most-functions/" + mostFunctionId + "/restore-from-trash",
        {
            method: "DELETE",
            credentials: "include"
        }
    );

    tidyFetch(request, function (data) {
        const wasSuccess = data.wasSuccess;
        let errorMessage = "";
        if (! wasSuccess) {
            console.error("Unable to restore function " + mostFunctionId + " from trash: " + data.errorMessage);
            errorMessage = data.errorMessage;
        }

        if (typeof callbackFunction == "function") {
            callbackFunction(data, errorMessage);
        }
    });
}

function submitMostFunctionForReview(mostFunctionId, callbackFunction) {
    const request = new Request(
        API_PREFIX + "most-functions/" + mostFunctionId + "/submit-for-review",
        {
            method: "POST",
            credentials: "include"
        }
    );

    tidyFetch(request, function(data) {
        const wasSuccess = data.wasSuccess;

        if (! wasSuccess) {
            console.error("Unable to submit most function for review: " + data.errorMessage);
        }

        if (typeof callbackFunction == "function") {
            callbackFunction(data);
        }
    });
}
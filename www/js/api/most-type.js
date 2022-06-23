// calls callbackFunction with an array of MOST types.
function getMostTypes(callbackFunction) {
    const request = new Request(
        API_PREFIX + "most-types",
        {
            method: "GET",
            credentials: "include"
        }
    );

    tidyFetch(request, function(data) {
        let mostTypes = null;

        if (data.wasSuccess) {
            mostTypes = data.mostTypes;
        } else {
            console.error("Unable to get types: " + data.errorMessage);
        }

        if (typeof callbackFunction == "function") {
            callbackFunction(mostTypes);
        }
    });
}

// calls callbackFunction with the new MOST Type ID
function insertMostType(mostType, callbackFunction) {
    const request = new Request(
        API_PREFIX + "most-types",
        {
            method: "POST",
            credentials: "include",
            body: JSON.stringify(mostType)
        }
    );

    tidyFetch(request, function(data) {
        if (!data.wasSuccess) {
            console.error("Unable to create MOST type: " + data.errorMessage);
        }

        if (typeof callbackFunction == "function") {
            callbackFunction(data);
        }
    });
}

//calls callbackFunction with modified function ID
function updateMostType(mostTypeId, mostType, callbackFunction) {
    const request = new Request(
        API_PREFIX + "most-types/" + mostTypeId,
        {
            method: "POST",
            credentials: "include",
            body: JSON.stringify({
                "mostType": mostType
            })
        }
    );

    tidyFetch(request, function (data) {
       const wasSuccess = data.wasSuccess;
       if (! wasSuccess) {
           console.error("Unable to modify Most Type " + mostTypeId + ": " + data.errorMessage);
       }

       if (typeof callbackFunction == "function") {
            callbackFunction(data);
       }
    });
}


// calls callbackFunction with an array of primitive types.
function getPrimitiveTypes(callbackFunction) {
    const request = new Request(
        API_PREFIX + "most-types/primitive-types",
        {
            method: "GET",
            credentials: "include"
        }
    );

    tidyFetch(request, function(data) {
        let primitiveTypes = null;

        if (data.wasSuccess) {
            primitiveTypes = data.primitiveTypes;
        } else {
            console.error("Unable to get primitive types: " + data.errorMessage);
        }

        if (typeof callbackFunction == "function") {
            callbackFunction(primitiveTypes);
        }
    });
}

// calls callbackFunction with an array of units
function getUnits(callbackFunction) {
    const request = new Request(
        API_PREFIX + "most-types/most-units",
        {
            method: "GET",
            credentials: "include"
        }
    );

    tidyFetch(request, function(data) {
        let units = null;

        if (data.wasSuccess) {
            units = data.units;
        } else {
            console.error("Unable to get units: " + data.errorMessage);
        }

        if (typeof callbackFunction == "function") {
            callbackFunction(units);
        }
    });
}

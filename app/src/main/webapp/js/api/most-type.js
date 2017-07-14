// calls callbackFunction with an array of MOST types.
function getMostTypes(callbackFunction) {
    const request = new Request(
        ENDPOINT_PREFIX + "api/v1/most-type",
        {
            method: "GET",
            credentials: "include"
        }
    );

    jsonFetch(request, function(data) {
        let mostTypes = null;

        if (data.wasSuccess) {
            mostTypes = data.mostTypes;
        } else {
            console.log("Unable to get types: " + data.errorMessage);
        }

        if (typeof callbackFunction == "function") {
            callbackFunction(mostTypes);
        }
    });
}
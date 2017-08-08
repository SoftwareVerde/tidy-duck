// calls callbackFunction with an array of MOST types.
function getMostTypes(callbackFunction) {
    const request = new Request(
        ENDPOINT_PREFIX + "api/v1/most-types",
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
            console.error("Unable to get types: " + data.errorMessage);
        }

        if (typeof callbackFunction == "function") {
            callbackFunction(mostTypes);
        }
    });
}
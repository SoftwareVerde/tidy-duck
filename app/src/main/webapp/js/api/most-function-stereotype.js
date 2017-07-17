// calls callbackFunction with an array of Function Stereotypes.
function getMostFunctionStereotypes(callbackFunction) {
    const request = new Request(
        ENDPOINT_PREFIX + "api/v1/most-function-stereotype",
        {
            method: "GET",
            credentials: "include"
        }
    );

    jsonFetch(request, function(data) {
       let mostFunctionStereotypes = null;

       if (data.wasSuccess) {
           mostFunctionStereotypes = data.mostFunctionStereotypes;
       } else {
           console.log("Unable to get types: " + data.errorMessage);
       }

        if (typeof callbackFunction == "function") {
            callbackFunction(mostFunctionStereotypes);
        }
    });
}
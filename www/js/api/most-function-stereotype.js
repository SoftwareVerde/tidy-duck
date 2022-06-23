// calls callbackFunction with an array of Function Stereotypes.
function getMostFunctionStereotypes(callbackFunction) {
    const request = new Request(
        API_PREFIX + "most-function-stereotypes",
        {
            method: "GET",
            credentials: "include"
        }
    );

    tidyFetch(request, function(data) {
       let mostFunctionStereotypes = null;

       if (data.wasSuccess) {
           mostFunctionStereotypes = data.mostFunctionStereotypes;
       } else {
           console.error("Unable to get types: " + data.errorMessage);
       }

        if (typeof callbackFunction == "function") {
            callbackFunction(mostFunctionStereotypes);
        }
    });
}

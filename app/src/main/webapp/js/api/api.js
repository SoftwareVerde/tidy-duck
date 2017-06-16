const ENDPOINT_PREFIX = '/';
const API_PREFIX = ENDPOINT_PREFIX + 'api/v1/';

function jsonFetch(request, callbackFunction) {
    fetch(request, { credentials: 'include' }).then(function(response) {
        return response.json();
    }).then(function(json) {
        if (typeof callbackFunction == "function") {
            callbackFunction(json);
        }
    });
}

// calls callbackFunction with an array of function catalogs
function getFunctionCatalogsForVersionId(versionId, callbackFunction) {
    const endpoint = API_PREFIX + 'function-catalog?version_id=' + versionId;

    jsonFetch(endpoint, function(data) {
        let functionCatalogs = null;

        if (data.wasSuccess) {
            functionCatalogs = data.functionCatalogs;
        } else {
            console.log('Unable to get function catalogs for version ' + versionId + ': ' + data.errorMessage);
        }

        if (typeof callbackFunction == "function") {
            callbackFunction(functionCatalogs);
        }
    });
}

// calls callbackFunction with new function catalog ID
function insertFunctionCatalog(versionId, functionCatalog, callbackFunction) {
    const request = new Request(
        API_PREFIX + 'function-catalog',
        {
            method: 'POST',
            body: JSON.stringify({
                'versionId': versionId,
                'functionCatalog': functionCatalog
            })
        }
    );

    jsonFetch(request, function(data) {
        let functionCatalogId = null;

        if (data.wasSuccess) {
            functionCatalogId = data.functionCatalogId;
        } else {
            console.log('Unable to insert function catalog for version ' + versionId + ': ' + data.errorMessage);
        }

        if (typeof callbackFunction == "function") {
            callbackFunction(functionCatalogId);
        }
    });
}

function exportFunctionCatalogToMost(functionCatalogId) {
    window.open(ENDPOINT_PREFIX + 'v1/generate-most?function_catalog_id=' + functionCatalogId);
}

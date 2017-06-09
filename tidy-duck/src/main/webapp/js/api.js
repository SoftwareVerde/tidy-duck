
var ENDPOINT_PREFIX = '/tidy-duck/';

function jsonFetch(request) {
    return fetch(request)
        .then(function (response) {
            return response.json();
        })
}

// calls callbackFunction with an array of function catalogs
function getFunctionCatalogsForVersionId(versionId, callbackFunction) {
    var endpoint = ENDPOINT_PREFIX + 'api/v1/function-catalog?version_id=' + versionId;
    jsonFetch(endpoint)
        .then(function (data) {
            if (data.wasSuccess) {
                callbackFunction(data.functionCatalogs);
            } else {
                console.log('Unable to get function catalogs for version ' + versionId + ': ' + data.error);
            }
        });
}

// calls callbackFunction with new function catalog ID
function insertFunctionCatalog(versionId, functionCatalog, callbackFunction) {
    var request = new Request(ENDPOINT_PREFIX + 'api/v1/function-catalog', {
        method: 'POST',
        body: functionCatalog
    })
    jsonFetch(request)
        .then(function (data) {
            if (data.wasSuccess) {
                callbackFunction(data.functionCatalogId);
            } else {
                console.log('Unable to insert function catalog for version ' + versionId + ': ' + data.error);
            }
        });
}

function exportFunctionCatalogToMost(functionCatalogId) {
    window.open(ENDPOINT_PREFIX + 'v1/generate-most?function_catalog_id=' + functionCatalogId);
}
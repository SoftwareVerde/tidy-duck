// Shortens a string to or below the specified maxLength.
// If the input string is longer than this, three periods will be appended.
// Defaults on using word boundaries for shortening.
// Returns null if an non-string is provided
function shortenString(string, maxLength, useWordBoundary){
    if (string == null || typeof string != "string") {
        return null;
    }
    if (string.length <= maxLength) {
        return string;
    }
    if (typeof useWordBoundary == "undefined") {
        useWordBoundary = true;
    }
    const subString = string.substr(0, maxLength-1);
    if (useWordBoundary) {
        return subString.substr(0, subString.lastIndexOf(' ')) + "...";
    } else {
        return subString + "...";
    }
}

// Performs a deep copy of a MOST object
// The first parameter, clazz, must have a toJson and fromJson function.
// It is expected that clazz will be a reference to the class itself while
//  object is a reference an actual instance of that class
function copyMostObject(clazz, object) {
    return clazz.fromJson(clazz.toJson(object));
}

// Converts the array data using convertFunction and stores the converted
// data into the json object at jsonKey
function addConvertedJsonArray(json, jsonKey, data, convertFunction) {
    const convertedArray = [];
    for (let i in data) {
        let convertedItem = convertFunction(data[i]);
        convertedArray.push(convertedItem);
    }
    if (convertedArray.length > 0) {
        json[jsonKey] = convertedArray;
    }
}

function toStandardDateTimeString(date) {
    let dateString = date.getFullYear() + "-" + ("0"+(date.getMonth()+1)).slice(-2) + "-" + ("0" + date.getDate()).slice(-2);
    let separator = " ";
    let timeString = ("0" + date.getHours()).slice(-2) + ":" + ("0" + date.getMinutes()).slice(-2) + ":" + ("0" + date.getSeconds()).slice(-2);

    return dateString + separator + timeString;
}

// Ensures a hex string has a lower-case 'x' and capital letters elsewhere (e.g. 0Xabc -> 0xABC).
function formatHex(hex) {
    return hex.toUpperCase().replace('X', 'x');
}
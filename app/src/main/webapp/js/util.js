// Shortens a string to or below the specified maxLength.
// If the input string is longer than this, three periods will be appended.
// Defaults on using word boundaries for shortening.
function shortenString(string, maxLength, useWordBoundary){
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
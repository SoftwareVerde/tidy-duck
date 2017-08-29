// calls callbackFunction with an array of reviews
function getReviews(excludeOpenReviews, excludeClosedReviews, callbackFunction) {
    let endpoint = "api/v1/reviews";
    if (excludeOpenReviews) {
        endpoint += "?excludeOpenReviews=true";
    }
    if (excludeClosedReviews) {
        const separator = (excludeOpenReviews ? '&' : '?');
        endpoint += separator + "excludeClosedReviews=true";
    }
    const request = new Request(
        ENDPOINT_PREFIX + endpoint,
        {
            method: "GET",
            credentials: "include"
        }
    );

    jsonFetch(request, function(data) {
       let reviews = null;

       if (data.wasSuccess) {
           reviews = data.reviews;
       } else {
           console.error("Unable to get types: " + data.errorMessage);
       }

        if (typeof callbackFunction == "function") {
            callbackFunction(reviews);
        }
    });
}

// calls callbackFunction with new review ID
function insertReview(review, callbackFunction) {
    const request = new Request(
        ENDPOINT_PREFIX + "api/v1/reviews",
        {
            method: "POST",
            credentials: "include",
            body: JSON.stringify({
                "review" : review
            })
        }
    );

    jsonFetch(request, function(data) {
        let reviewId = null;
        const wasSuccess = data.wasSuccess;

        if (wasSuccess) {
            reviewId = data.reviewId;
        } else {
            console.error("Unable to submit for review: " + data.errorMessage);
        }

        if (typeof callbackFunction == "function") {
            callbackFunction(wasSuccess, reviewId);
        }
    });
}
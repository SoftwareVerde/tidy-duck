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

function approveReview(reviewId, callbackFunction) {
    const request = new Request(
        ENDPOINT_PREFIX + "api/v1/reviews/" + reviewId + "/approve",
        {
            method: "POST",
            credentials: "include",
        }
    );

    jsonFetch(request, function(data) {
        const wasSuccess = data.wasSuccess;

        if (! wasSuccess) {
            console.error("Unable to approve review: " + data.errorMessage);
        }

        if (typeof callbackFunction == "function") {
            callbackFunction(data);
        }
    });
}


function insertReviewVote(reviewId, reviewVote, callbackFunction) {
    const request = new Request(
        ENDPOINT_PREFIX + "api/v1/reviews/" + reviewId + "/votes",
        {
            method: "POST",
            credentials: "include",
            body: JSON.stringify({
                "reviewVote" : reviewVote
            })
        }
    );

    jsonFetch(request, function(data) {
        let reviewVoteId = null;
        const wasSuccess = data.wasSuccess;

        if (wasSuccess) {
            reviewVoteId = data.reviewVoteId;
        } else {
            console.error("Unable to vote for approval: " + data.errorMessage);
        }

        if (typeof callbackFunction == "function") {
            callbackFunction(wasSuccess, reviewVoteId);
        }
    });
}

function updateReviewVote(reviewVoteId, reviewVote, callbackFunction) {
    const request = new Request(
        ENDPOINT_PREFIX + "api/v1/review-votes/" + reviewVoteId,
        {
            method: "POST",
            credentials: "include",
            body: JSON.stringify({
                "reviewVote" : reviewVote
            })
        }
    );

    jsonFetch(request, function(data) {
        const wasSuccess = data.wasSuccess;
        if (! wasSuccess) {
            console.error("Unable to update vote for approval: " + data.errorMessage);
        }

        if (typeof callbackFunction == "function") {
            callbackFunction(wasSuccess);
        }
    });
}

function deleteReviewVote(reviewVoteId, callbackFunction) {
    const request = new Request(
        ENDPOINT_PREFIX + "api/v1/review-votes/" + reviewVoteId,
        {
            method: "DELETE",
            credentials: "include",
        }
    );

    jsonFetch(request, function(data) {
        const wasSuccess = data.wasSuccess;
        if (! wasSuccess) {
            console.error("Unable to remove vote for approval: " + data.errorMessage);
        }

        if (typeof callbackFunction == "function") {
            callbackFunction(wasSuccess);
        }
    });
}
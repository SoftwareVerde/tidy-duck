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

    tidyFetch(request, function(data) {
       let reviews = null;

       if (data.wasSuccess) {
           reviews = data.reviews;
       } else {
           console.error("Unable to list reviews: " + data.errorMessage);
       }

        if (typeof callbackFunction == "function") {
            callbackFunction(reviews);
        }
    });
}

// calls callbackFunction with requested review
function getReview(reviewId, callbackFunction) {
    const request = new Request(
        ENDPOINT_PREFIX + "api/v1/reviews/" + reviewId,
        {
            method: "GET",
            credentials: "include",
        }
    );

    tidyFetch(request, function(data) {
        if (typeof callbackFunction == "function") {
            callbackFunction(data);
        }

        if (! data.wasSuccess) {
            console.error("Unable to get review: " + data.errorMessage);
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

    tidyFetch(request, function(data) {
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

// calls callbackFunction with success flag
function updateReview(review, callbackFunction) {
    const request = new Request(
        ENDPOINT_PREFIX + "api/v1/reviews/" + review.id,
        {
            method: "POST",
            credentials: "include",
            body: JSON.stringify({
                "review" : review
            })
        }
    );

    tidyFetch(request, function(data) {
        const wasSuccess = data.wasSuccess;

        if (!wasSuccess) {
            console.error("Unable to update review: " + data.errorMessage);
        }

        if (typeof callbackFunction == "function") {
            callbackFunction(wasSuccess);
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

    tidyFetch(request, function(data) {
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

    tidyFetch(request, function(data) {
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

    tidyFetch(request, function(data) {
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

    tidyFetch(request, function(data) {
        const wasSuccess = data.wasSuccess;
        if (! wasSuccess) {
            console.error("Unable to remove vote for approval: " + data.errorMessage);
        }

        if (typeof callbackFunction == "function") {
            callbackFunction(wasSuccess);
        }
    });
}

function insertReviewComment(reviewId, reviewComment, callbackFunction) {
    const request = new Request(
        ENDPOINT_PREFIX + "api/v1/reviews/" + reviewId + "/comments",
        {
            method: "POST",
            credentials: "include",
            body: JSON.stringify({
                "reviewComment" : reviewComment
            })
        }
    );

    tidyFetch(request, function(data) {
        let reviewCommentId = null;

        if (data.wasSuccess) {
            reviewCommentId = data.reviewCommentId;
        }

        if (typeof callbackFunction == "function") {
            callbackFunction(data, reviewCommentId);
        }
    });
}
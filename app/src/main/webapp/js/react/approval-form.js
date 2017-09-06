class ApprovalForm extends React.Component{
    constructor(props) {
        super(props);

        const reviewComment = new ReviewComment();
        reviewComment.setAccount(this.props.account);
        this.state = {
            shouldShowSaveCommentAnimation: false,
            reviewComment:                  reviewComment
        };

        this.onReviewCommentChanged = this.onReviewCommentChanged.bind(this);
        this.onSubmitComment = this.onSubmitComment.bind(this);
        this.onUpvoteClicked = this.onUpvoteClicked.bind(this);
        this.onDownvoteClicked = this.onDownvoteClicked.bind(this);
        this.renderUpvoteButton = this.renderUpvoteButton.bind(this);
        this.renderDownvoteButton = this.renderDownvoteButton.bind(this);
        this.renderComments = this.renderComments.bind(this);
    }

    componentWillReceiveProps(newProperties) {
        this.setState({
            shouldShowSaveCommentAnimation: false
        });
    }

    onReviewCommentChanged(value) {
        const reviewComment = this.state.reviewComment;

        reviewComment.setCommentText(value);

        this.setState({
            reviewComment: reviewComment
        })
    }

    onSubmitComment() {
        const reviewComment = this.state.reviewComment;
        if (!reviewComment.getCommentText()) {
            alert("Empty comments are not allowed.");
            return;
        }

        this.setState({
            shouldShowSaveCommentAnimation: true
        });

        reviewComment.setCreatedDate(toStandardDateTimeString(new Date()));

        const review = this.props.review;
        const thisForm = this;
        const reviewCommentJson = ReviewComment.toJson(reviewComment);
        insertReviewComment(review.getId(), reviewCommentJson, function (wasSuccess, reviewCommentId) {
            if (wasSuccess) {
                reviewComment.setId(reviewCommentId);
                review.addReviewComment(reviewComment);

                const newReviewComment = new ReviewComment();
                newReviewComment.setAccount(thisForm.props.account);
                thisForm.setState({
                    shouldShowSaveCommentAnimation: false,
                    reviewComment:                  newReviewComment
                });
                // scroll to bottom of comment area
                const commentArea = document.getElementsByClassName('comments-area')[0];
                commentArea.scrollTop = commentArea.scrollHeight;
            }
        });
    }

    onUpvoteClicked() {
        if (typeof this.props.onVoteClicked == "function") {
            this.props.onVoteClicked(true);
        }
    }

    onDownvoteClicked() {
        if (typeof this.props.onVoteClicked == "function") {
            this.props.onVoteClicked(false);
        }
    }

    renderUpvoteButton() {
        if (this.props.shouldShowVoteButtons) {
            const buttonTitle = "Upvote";
            let icon = "fa fa-4 fa-thumbs-o-up"
            if (this.props.selectedVote === "isUpvote") {
                icon = "fa fa-4 fa-thumbs-up";
            }

            return (
                <div className="toolbar-item upvote" onClick={this.onUpvoteClicked} title={buttonTitle}>
                    <i className={icon} />
                </div>
            );
        }
    }

    renderDownvoteButton() {
        if (this.props.shouldShowVoteButtons) {
            const buttonTitle = "Downvote";
            let icon = "fa fa-4 fa-thumbs-o-down"
            if (this.props.selectedVote === "isDownvote") {
                icon = "fa fa-4 fa-thumbs-down";
            }

            return (
                <div className="toolbar-item downvote" onClick={this.onDownvoteClicked} title={buttonTitle}>
                    <i className={icon} />
                </div>
            );
        }
    }

    renderComments() {
        const reactComponents = [];
        const review = this.props.review;
        const comments = review.getReviewComments();

        for (let i in comments) {
            const comment = comments[i];
            reactComponents.push(<app.ReviewComment key={i} reviewComment={comment} />);
        }

        return (<div className="comments-area">{reactComponents}</div>);
    }

    render() {
        let submitCommentButton = <button className="button submit-button" id="function-block-submit" onClick={this.onSubmitComment}>Submit Comment</button>;
        if (this.state.shouldShowSaveCommentAnimation) {
            submitCommentButton = <div className="button submit-button" id="function-block-submit"><i className="fa fa-refresh fa-spin"></i></div>;
        }

        let submitApprovalButton = <button className="button submit-button" id="function-block-submit" onClick={this.onSubmit}>Approve</button>;
        if (this.props.shouldShowSaveAnimation) {
            submitApprovalButton = <div className="button submit-button" id="function-block-submit"><i className="fa fa-refresh fa-spin"></i></div>;
        }

        return(
            <div key="approvalForm" className="approval-form">
                <div className="metadata-form">
                    <div className="metadata-form-title">Approval</div>
                    {this.renderComments()}
                    <div className="vote-area">
                        <div className="submit-comment-form">
                            <app.InputField name="comment" type="textarea" label={"Comment"} value={this.state.reviewComment.getCommentText()} readOnly={this.props.readOnly} onChange={this.onReviewCommentChanged} />
                            {submitCommentButton}
                        </div>
                        <div className="toolbar">
                            {this.renderUpvoteButton()}
                            {this.renderDownvoteButton()}
                        </div>
                        {submitApprovalButton}
                    </div>
                </div>
            </div>
        );
    }
}

registerClassWithGlobalScope("ApprovalForm", ApprovalForm);
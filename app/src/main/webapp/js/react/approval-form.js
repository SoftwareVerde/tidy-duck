class ApprovalForm extends React.Component{
    constructor(props) {
        super(props);

        const reviewComment = new ReviewComment();
        reviewComment.setAccount(this.props.account);
        this.state = {
            shouldShowSaveCommentAnimation: false,
            ticketUrlSaveButtonText:        'Save',
            reviewComment:                  reviewComment
        };

        this.onReviewCommentChanged = this.onReviewCommentChanged.bind(this);
        this.onSubmitComment = this.onSubmitComment.bind(this);
        this.onUpvoteClicked = this.onUpvoteClicked.bind(this);
        this.onDownvoteClicked = this.onDownvoteClicked.bind(this);
        this.onApproveButtonClicked = this.onApproveButtonClicked.bind(this);
        this.onTicketUrlChanged = this.onTicketUrlChanged.bind(this);
        this.onSaveTicketUrlClicked = this.onSaveTicketUrlClicked.bind(this);
        this.renderUpvoteButton = this.renderUpvoteButton.bind(this);
        this.renderDownvoteButton = this.renderDownvoteButton.bind(this);
        this.renderVoteList = this.renderVoteList.bind(this);
        this.renderComments = this.renderComments.bind(this);
        this.renderTicketUrlArea = this.renderTicketUrlArea.bind(this);
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
            let icon = "fa fa-thumbs-o-up fa-3x"
            if (this.props.selectedVote === "isUpvote") {
                icon = "fa fa-thumbs-up fa-3x";
            }

            // Find number of upvotes
            let upvoteCounter = 0;
            const reviewVotes = this.props.review.getReviewVotes();
            for (let i in reviewVotes) {
                upvoteCounter += reviewVotes[i].isUpvote();
            }

            return (
                <div className="toolbar-item upvote" onClick={this.onUpvoteClicked} title={buttonTitle}>
                    <i className={icon} />{upvoteCounter}
                </div>
            );
        }
    }

    onApproveButtonClicked() {
        if (typeof this.props.onApproveButtonClicked == "function") {
            this.props.onApproveButtonClicked();
        }
    }

    onTicketUrlChanged(value) {
        this.setState({
            ticketUrl: value
        });
    }

    onSaveTicketUrlClicked() {
        this.setState({
            ticketUrlSaveButtonText: <i className="fa fa-refresh fa-spin"/>
        });

        const thisForm = this;
        if (typeof this.props.onSaveTicketUrlClicked == "function") {
            this.props.onSaveTicketUrlClicked(this.state.ticketUrl, function(wasSuccess) {
                const newSaveButtonText = wasSuccess ? 'Saved' : 'Error';
                thisForm.setState({
                    ticketUrlSaveButtonText: newSaveButtonText
                });
                if (!wasSuccess) {
                    // switch back to 'Save' from 'Error' after 3 seconds
                    setTimeout(3000, function () {
                        thisForm.setState({
                            ticketUrlSaveButtonText: 'Save'
                        });
                    })
                }
            });
        }
    }

    renderDownvoteButton() {
        if (this.props.shouldShowVoteButtons) {
            const buttonTitle = "Downvote";
            let icon = "fa fa-4 fa-thumbs-o-down"
            if (this.props.selectedVote === "isDownvote") {
                icon = "fa fa-4 fa-thumbs-down";
            }

            // Find number of downvotes
            let downvoteCounter = 0;
            const reviewVotes = this.props.review.getReviewVotes();
            for (let i in reviewVotes) {
                downvoteCounter += ! reviewVotes[i].isUpvote();
            }

            return (
                <div className="toolbar-item downvote" onClick={this.onDownvoteClicked} title={buttonTitle}>
                    <i className={icon} />{downvoteCounter}
                </div>
            );
        }
    }

    renderVoteList() {
        const reactComponents = [];
        const review = this.props.review;
        const votes = review.getReviewVotes();

        for (let i in votes) {
            const vote = votes[i];
            const voteIcon = vote.isUpvote() ? "fa fa-thumbs-up" : "fa fa-thumbs-down";
            const voteName = vote.getAccount().getName();
            reactComponents.push(
                <div key={"vote" + i} className="vote-item primary-bg primary-contrast" >
                    {voteName}<i className={voteIcon} />
                </div>);
        }

        return (<div className="vote-list">{reactComponents}</div>);
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

    renderTicketUrlArea() {
        return (
            <div className="ticket-url-area">
                <app.InputField name="ticket-url" type="text" label="Ticket URL" value={this.state.ticketUrl} readOnly={this.props.readOnly} onChange={this.onTicketUrlChanged} />
                <button className="button" id="ticket-url-save-button" onClick={this.onSaveTicketUrlClicked}>{this.state.ticketUrlSaveButtonText}</button>
            </div>
        );
    }

    render() {
        let submitCommentButton = <button className="button submit-button" id="function-block-submit" onClick={this.onSubmitComment}>Submit Comment</button>;
        if (this.state.shouldShowSaveCommentAnimation) {
            submitCommentButton = <div className="button submit-button" id="function-block-submit"><i className="fa fa-refresh fa-spin"></i></div>;
        }

        let submitApprovalButton = <button className="button submit-button" id="function-block-submit" onClick={this.onApproveButtonClicked}>Approve</button>;
        if (this.props.shouldShowSaveAnimation) {
            submitApprovalButton = <div className="button submit-button" id="function-block-submit"><i className="fa fa-refresh fa-spin"></i></div>;
        }

        return(
            <div key="approvalForm" className="approval-form">
                <div>
                    <div>Comments</div>
                    {this.renderComments()}
                    <div className="vote-area">
                        {this.renderTicketUrlArea()}
                        <div className="submit-comment-form">
                            <app.InputField name="comment" type="textarea" label={"Comment"} value={this.state.reviewComment.getCommentText()} readOnly={this.props.readOnly} onChange={this.onReviewCommentChanged} />
                            {submitCommentButton}
                        </div>
                        <div className="toolbar">
                            {this.renderUpvoteButton()}
                            {this.renderDownvoteButton()}
                        </div>
                        {this.renderVoteList()}
                        {submitApprovalButton}
                    </div>
                </div>
            </div>
        );
    }
}

registerClassWithGlobalScope("ApprovalForm", ApprovalForm);
class ApprovalForm extends React.Component{
    constructor(props) {
        super(props);

        this.onReviewCommentChanged = this.onReviewCommentChanged.bind(this);
        this.onUpvoteClicked = this.onUpvoteClicked.bind(this);
        this.onDownvoteClicked = this.onDownvoteClicked.bind(this);
        this.renderUpvoteButton = this.renderUpvoteButton.bind(this);
        this.renderDownvoteButton = this.renderDownvoteButton.bind(this);
        this.renderComments = this.renderComments.bind(this);
    }

    componentWillReceiveProps(newProperties) {
    }

    onReviewCommentChanged(value) {
        if (typeof this.props.onUpdate == "function") {
            this.props.onUpdate(value);
        }
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
       const comments = this.props.reviewComments;

       for (let i in comments) {
           const key = "comment" + i;
           // TODO: insert name of commenter into label variable.
           const label = " said";
           reactComponents.push(<app.InputField key={key} id={key} name="comment" type="textarea" label={label} value={comments[i]} readOnly={true} />);
       }

       return (<div className="comments-area">{reactComponents}</div>);
    }

    render() {
        let submitCommentButton = <button className="button submit-button" id="function-block-submit" onClick={this.onSubmit}>Submit Comment</button>;
        if (this.props.shouldShowSaveAnimation) {
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
                            <app.InputField name="comment" type="textarea" label={"Comment"} value={this.props.reviewComment} readOnly={this.props.readOnly} onChange={this.onReviewCommentChanged} />
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
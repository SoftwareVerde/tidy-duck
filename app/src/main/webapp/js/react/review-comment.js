class ReviewComment extends React.Component {
    constructor(props) {
        super(props);
    }

    componentWillReceiveProps(newProperties) {

    }

    render() {
        const comment = this.props.reviewComment;

        const account = comment.getAccount();
        const title = account.getName() + " at " + comment.getCreatedDate();
        const content = comment.getCommentText();

        return (
            <div className="review-comment">
                <div key="header" className="review-comment-header secondary-bg accent">{title}</div>
                <div key="content" className="review-comment-content primary-bg primary-contrast">
                    <pre>{content}</pre>
                </div>
            </div>
        );
    }
}

registerClassWithGlobalScope("ReviewComment", ReviewComment);

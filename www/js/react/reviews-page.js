class ReviewsPage extends React.Component {
    constructor(props) {
        super(props);

        this.onReviewSelected = this.onReviewSelected.bind(this);
        this.renderReviews = this.renderReviews.bind(this);
        this.renderReviewContent = this.renderReviewContent.bind(this);
    }

    componentWillReceiveProps(newProps) {

    }

    onReviewSelected(review) {
        if (typeof this.props.onReviewSelected == "function") {
            this.props.onReviewSelected(review);
        }
    }

    renderReviews() {
        if (this.props.isLoadingReviews) {
            return (
                <div className="center">
                    <i id="loading-reviews-icon" className="fa fa-4x fa-refresh fa-spin"/>
                </div>
            );
        }

        if (this.props.reviews.length == 0) {
            return (
                < div className="center">
                    <h1>There are no reviews that require approval.</h1>
                    <i className="fa fa-4x fa-thumbs-up"/>
                </div>
            );
        }

        const reactComponents = [];
        for (let i in this.props.reviews) {
            const review = this.props.reviews[i];

            reactComponents.push(
                <div className="review-entry" key={i} onClick={() => this.onReviewSelected(review)}>
                    <div className="review-name">{review.getReviewName()}</div>
                    <div className="review-content">{this.renderReviewContent(review)}</div>
                </div>
            );
        }
        return reactComponents;
    }

    renderReviewContent(review) {
        const account = review.getAccount();
        let submitter = "";
        if (account != null) {
            submitter += account.getName();
            const company = account.getCompany();
            if (company != null) {
                submitter += " (" + company.getName() + ")";
            }
        }

        const reviewObject = review.getReviewObject();

        switch (reviewObject.constructor.name) {
            case 'FunctionCatalog': {
                const authorName = reviewObject.getAuthor() == null ? "" : reviewObject.getAuthor().getName();
                const companyName = reviewObject.getCompany() == null ? "" : reviewObject.getCompany().getName();
                return (
                    <table>
                        <tbody>
                            <tr>
                                <th>Review Type:</th>
                                <td>Function Catalog</td>
                            </tr>
                            <tr>
                                <th>Submitted By:</th>
                                <td>{submitter}</td>
                            </tr>
                            <tr>
                                <th>Release Version:</th>
                                <td>{reviewObject.getDisplayVersion()}</td>
                            </tr>
                            <tr>
                                <th>Author:</th>
                                <td>{authorName}</td>
                            </tr>
                            <tr>
                                <th>Company:</th>
                                <td>{companyName}</td>
                            </tr>
                        </tbody>
                    </table>
                );
            } break;
            case 'FunctionBlock': {
                const authorName = reviewObject.getAuthor() == null ? "" : reviewObject.getAuthor().getName();
                const companyName = reviewObject.getCompany() == null ? "" : reviewObject.getCompany().getName();
                return (
                    <table>
                        <tbody>
                            <tr>
                                <th>Review Type:</th>
                                <td>Function Block</td>
                            </tr>
                            <tr>
                                <th>Submitted By:</th>
                                <td>{submitter}</td>
                            </tr>
                            <tr>
                                <th>MOST ID:</th>
                                <td>{reviewObject.getMostId()}</td>
                            </tr>
                            <tr>
                                <th>Kind:</th>
                                <td>{reviewObject.getKind()}</td>
                            </tr>
                            <tr>
                                <th>Description:</th>
                                <td>{reviewObject.getDescription()}</td>
                            </tr>
                            <tr>
                                <th>Access:</th>
                                <td>{reviewObject.getAccess()}</td>
                            </tr>
                            <tr>
                                <th>Release Version:</th>
                                <td>{reviewObject.getDisplayVersion()}</td>
                            </tr>
                            <tr>
                                <th>Last Modified:</th>
                                <td>{reviewObject.getLastModifiedDate()}</td>
                            </tr>
                            <tr>
                                <th>Author:</th>
                                <td>{authorName}</td>
                            </tr>
                            <tr>
                                <th>Company:</th>
                                <td>{companyName}</td>
                            </tr>
                        </tbody>
                    </table>
                );
            } break;
            case 'MostInterface': {
                return (
                    <table>
                        <tbody>
                            <tr>
                                <th>Review Type:</th>
                                <td>Interface</td>
                            </tr>
                            <tr>
                                <th>Submitted By:</th>
                                <td>{submitter}</td>
                            </tr>
                            <tr>
                                <th>MOST ID:</th>
                                <td>{reviewObject.getMostId()}</td>
                            </tr>
                            <tr>
                                <th>Description:</th>
                                <td>{reviewObject.getDescription()}</td>
                            </tr>
                            <tr>
                                <th>Version:</th>
                                <td>{reviewObject.getDisplayVersion()}</td>
                            </tr>
                            <tr>
                                <th>Last Modified:</th>
                                <td>{reviewObject.getLastModifiedDate()}</td>
                            </tr>
                        </tbody>
                    </table>
                );
            } break;
            case 'MostFunction': {
                const authorName = reviewObject.getAuthor() == null ? "" : reviewObject.getAuthor().getName();
                const companyName = reviewObject.getCompany() == null ? "" : reviewObject.getCompany().getName();
                return (
                    <table>
                        <tbody>
                            <tr>
                                <th>Review Type:</th>
                                <td>Function</td>
                            </tr>
                            <tr>
                                <th>Submitted By:</th>
                                <td>{submitter}</td>
                            </tr>
                            <tr>
                                <th>MOST ID:</th>
                                <td>{reviewObject.getMostId()}</td>
                            </tr>
                            <tr>
                                <th>Description:</th>
                                <td>{reviewObject.getDescription()}</td>
                            </tr>
                            <tr>
                                <th>Function Type:</th>
                                <td>{reviewObject.getFunctionType()}</td>
                            </tr>
                            <tr>
                                <th>Stereotype:</th>
                                <td>{reviewObject.getStereotype().getName()}</td>
                            </tr>
                            <tr>
                                <th>Release Version:</th>
                                <td>{reviewObject.getDisplayVersion()}</td>
                            </tr>
                            <tr>
                                <th>Last Modified:</th>
                                <td>{reviewObject.getLastModifiedDate()}</td>
                            </tr>
                            <tr>
                                <th>Author:</th>
                                <td>{authorName}</td>
                            </tr>
                            <tr>
                                <th>Company:</th>
                                <td>{companyName}</td>
                            </tr>
                        </tbody>
                    </table>
                );
            } break;
            default: {
                return (
                    <div>Invalid review for {reviewObject.constructor.name}</div>
                );
            }
        }
    }

    render() {
        return (
            <div id="reviews-container">
                {this.renderReviews()}
            </div>
        );
    }
}

registerClassWithGlobalScope("ReviewsPage", ReviewsPage);

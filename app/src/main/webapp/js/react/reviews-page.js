class ReviewsPage extends React.Component {
    constructor(props) {
        super(props);

        this.options = ["View Review", "Create Review"];

        this.state = {
            selectedOption: this.options[0],
            saveButtonText: 'Save'
        };

        this.renderReviews = this.renderReviews.bind(this);
        this.onSave = this.onSave.bind(this);
    }

    componentWillReceiveProps(newProps) {
        this.state = {
            selectedOption: this.options[0],
            saveButtonText: 'Save'
        };
    }

    onSave() {
        const thisApp = this;
        this.setState({
            saveButtonText: <i className="fa fa-refresh fa-spin"></i>
        });

        setTimeout(function() {
            thisApp.setState({
                saveButtonText: "Saved"
            });
        }, 1000);
    }

    renderReviews() {
        if (this.props.isLoadingReviews) {
            return (
                <div className="center">
                    <i className="fa fa-3x fa-refresh fa-spin"/>
                </div>
            );
        }
        const reactComponents = [];
        for (let i in this.props.reviews) {
            const review = this.props.reviews[i];

            reactComponents.push(
                <div className="review-entry" key={i}>
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
                                <td>{reviewObject.getReleaseVersion()}</td>
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
                                <td>{reviewObject.getReleaseVersion()}</td>
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
                                <td>{reviewObject.getReleaseVersion()}</td>
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
                                <td>{reviewObject.getReleaseVersion()}</td>
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

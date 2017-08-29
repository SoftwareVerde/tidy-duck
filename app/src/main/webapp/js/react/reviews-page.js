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
                saveButtonText: saveButtonText
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
        const reviewObject = review.getReviewObject();
        switch (reviewObject.constructor.name) {
            case 'FunctionCatalog': {
                // TODO
                return (
                    <div>Function catalog review.</div>
                );
            } break;
            case 'FunctionBlock': {
                // TODO
                return (
                    <div>Function block review.</div>
                );
            } break;
            case 'MostInterface': {
                // TODO
                return (
                    <div>Most interface review.</div>
                );
            } break;
            case 'MostFunction': {
                // TODO
                return (
                    <div>Most function review.</div>
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

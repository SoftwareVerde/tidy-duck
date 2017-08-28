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
        for (let i in this.props.reviews) {

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

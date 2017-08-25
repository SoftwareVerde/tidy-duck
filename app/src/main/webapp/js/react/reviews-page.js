class ReviewsPage extends React.Component {
    constructor(props) {
        super(props);

        this.options = ["Create Review", "View Review"];

        this.state = {
            selectedOption: this.options[0],
            saveButtonText: 'Save'
        };

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

    render() {
        return (
            <div id="reviews-container">
                
            </div>
        );
    }
}

registerClassWithGlobalScope("ReviewsPage", ReviewsPage);

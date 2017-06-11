class FunctionCatalog extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            name:           this.props.name,
            releaseVersion: this.props.releaseVersion,
            date:           this.props.date,
            author:         this.props.author,
            company:        this.props.company
        };

        this.selectFunctionCatalog = this.selectFunctionCatalog.bind(this);

        window.app.navigation = this;
    }

    selectFunctionCatalog() {
        app.navigation.addNavigationItem(this.state.name);

        ReactDOM.render(
            <app.FunctionCatalogForm name={this.state.name} releaseVersion={this.state.releaseVersion} date={this.state.date} author={this.state.author} company={this.state.company} readOnly={true} />,
            document.getElementsByClassName("metadata-form")[0]
        );
    }

    render() {
        return (
            <div onClick={this.selectFunctionCatalog}>
                <div className="child-function-catalog-property">{this.state.name}</div>
                <div className="child-function-catalog-property">{this.state.releaseVersion}</div>
                <div className="child-function-catalog-property">{this.state.date}</div>
                <div className="child-function-catalog-property">{this.state.author}</div>
                <div className="child-function-catalog-property">{this.state.company}</div>
            </div>
        );
    }
}

(function (app) {
    app.FunctionCatalog = FunctionCatalog;
})(window.app || (window.app = { }))

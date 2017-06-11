class FunctionCatalog extends React.Component {
    constructor(props) {
        super(props);

        this.onClick = this.onClick.bind(this);

        window.app.navigation = this;
    }

    onClick() {
        if (typeof this.props.onClick == "function") {
            this.props.onClick(this.props.functionCatalog);
        }
    }

    render() {
        return (
            <div className="function-catalog" onClick={this.onClick}>
                <div className="child-function-catalog-property">{this.props.functionCatalog.getName()}</div>
                <div className="child-function-catalog-property">{this.props.functionCatalog.getReleaseVersion()}</div>
                <div className="child-function-catalog-property">{this.props.functionCatalog.getReleaseDate()}</div>
                <div className="child-function-catalog-property">{this.props.functionCatalog.getAuthor().getId()}</div>
                <div className="child-function-catalog-property">{this.props.functionCatalog.getCompany().getId()}</div>
            </div>
        );
    }
}

(function (app) {
    app.FunctionCatalog = FunctionCatalog;
})(window.app || (window.app = { }))

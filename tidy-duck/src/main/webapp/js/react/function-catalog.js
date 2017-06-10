class FunctionCatalog extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            name : document.getElementById("function-catalog-name").value,
            release : document.getElementById("function-catalog-release-version").value,
            author : document.getElementById("function-catalog-author").value,
            date : document.getElementById("function-catalog-date").value,
            company : document.getElementById("function-catalog-company").value,
        };

        this.clickOnFunctionCatalog = this.clickOnFunctionCatalog.bind(this);
    }

    clickOnFunctionCatalog() {
        var newNavigationEntry = document.createElement("div");
        newNavigationEntry.innerText = this.state.name;
        newNavigationEntry.className = "navigation-entry";

        var navigationColumn = document.getElementsByClassName("navigation-column")[0];
        navigationColumn.appendChild(newNavigationEntry);

        ReactDOM.render(
            <app.FunctionCatalogForm name={this.state.name} releaseVersion={this.state.release} date={this.state.date} author={this.state.author} company={this.state.company} readOnly={true} />,
            document.getElementsByClassName("metadata-form")[0]
        );
    }

    render() {
        return (
            <div onClick={this.clickOnFunctionCatalog}>
                <div className="child-function-catalog-property">{this.state.name}</div>
                <div className="child-function-catalog-property">{this.state.release}</div>
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

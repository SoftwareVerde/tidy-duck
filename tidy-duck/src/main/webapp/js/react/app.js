class App extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            functionCatalogs:   []
        };

        this.onFunctionCatalogSubmit = this.onFunctionCatalogSubmit.bind(this);
    }

    onFunctionCatalogSubmit(functionCatalog) {
        console.log("onFunctionCatalogSubmit");

        const versionId = 1; // TODO
        const functionCatalogJson = {
            name:           functionCatalog.getName(),
            release:        functionCatalog.getReleaseVersion(),
            releaseDate:    functionCatalog.getReleaseDate(),
            authorId:       functionCatalog.getAuthor().getId(),
            companyId:      functionCatalog.getCompany().getId()
        };

        insertFunctionCatalog(versionId, functionCatalogJson, new function(data) {
            console.log(data);
        });

        /*
            const newDisplayAreaChild = document.createElement("div");
            newDisplayAreaChild.className = "function-catalog";
            document.getElementById("child-display-area").appendChild(newDisplayAreaChild);

            // Render function catalog in new display area slot.
            ReactDOM.render(<app.FunctionCatalog name={this.state.name} releaseVersion={this.state.releaseVersion} date={this.state.date} author={this.state.author} company={this.state.company} />, newDisplayAreaChild);
        */
    }

    render() {
        return (
            <div className="container">
                <app.Navigation />
                <div className="display-area">
                    <div className="metadata-form">
                        <app.FunctionCatalogForm onSubmit={this.onFunctionCatalogSubmit} />
                    </div>
                    <div id="child-display-area">
                        <app.Navigation name="Function Catalogs" functionCatalogs={this.state.functionCatalogs} />
                    </div>
                </div>
            </div>
        );
    }
}

(function (app) {
    app.App = App;
})(window.app || (window.app = { }))

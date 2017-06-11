class App extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            navigationItems:    [],
            functionCatalogs:   []
        };

        this.renderFunctionCatalogs = this.renderFunctionCatalogs.bind(this);
        this.onFunctionCatalogSubmit = this.onFunctionCatalogSubmit.bind(this);
        this.onFunctionCatalogSelected = this.onFunctionCatalogSelected.bind(this);
        this.onRootNavigationItemClicked = this.onRootNavigationItemClicked.bind(this);
    }

    onFunctionCatalogSubmit(functionCatalog) {
        const author = (functionCatalog.getAuthor() || new Author());
        const company = (functionCatalog.getCompany() || new Company());

        const versionId = 1; // TODO
        const functionCatalogJson = {
            name:           functionCatalog.getName(),
            release:        functionCatalog.getReleaseVersion(),
            releaseDate:    functionCatalog.getReleaseDate(),
            authorId:       author.getId(),
            companyId:      company.getId()
        };

        insertFunctionCatalog(versionId, functionCatalogJson, new function(data) {
            console.log(data);
        });

        const functionCatalogs = this.state.functionCatalogs.concat(functionCatalog);
        this.setState({
            functionCatalogs: functionCatalogs
        });

        /*
            const newDisplayAreaChild = document.createElement("div");
            newDisplayAreaChild.className = "function-catalog";
            document.getElementById("child-display-area").appendChild(newDisplayAreaChild);

            // Render function catalog in new display area slot.
            ReactDOM.render(<app.FunctionCatalog name={this.state.name} releaseVersion={this.state.releaseVersion} date={this.state.date} author={this.state.author} company={this.state.company} />, newDisplayAreaChild);
        */
    }

    onRootNavigationItemClicked() {
        const navigationItems = [];

        this.setState({
            navigationItems: navigationItems
        });
    }

    onFunctionCatalogSelected(functionCatalog) {
        const navigationItems = [];
        navigationItems.push(functionCatalog.getName());

        const functionBlocks = functionCatalog.getFunctionBlocks();
        for (let i in functionBlocks) {
            const functionBlock = functionBlocks[i];
            navigationItems.push(functioBlock.getName());
        }
        // TODO: Traverse FB children... [, ...]

        this.setState({
            navigationItems: navigationItems
        });
    }

    renderFunctionCatalogs() {
        const reactComponents = [];
        for (let i in this.state.functionCatalogs) {
            const functionCatalog = this.state.functionCatalogs[i];
            reactComponents.push(<app.FunctionCatalog key={i} functionCatalog={functionCatalog} onClick={this.onFunctionCatalogSelected} />);
        }
        return reactComponents;
    }

    render() {
        return (
            <div className="container">
                <app.Navigation navigationItems={this.state.navigationItems} onRootItemClicked={this.onRootNavigationItemClicked} />
                <div className="display-area">
                    <app.FunctionCatalogForm onSubmit={this.onFunctionCatalogSubmit} />
                    <div id="child-display-area">
                        {this.renderFunctionCatalogs()}
                    </div>
                </div>
            </div>
        );
    }
}

(function (app) {
    app.App = App;
})(window.app || (window.app = { }))

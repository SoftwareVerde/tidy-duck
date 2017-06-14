class App extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            navigationItems:    [],
            functionCatalogs:   [],
            isFunctionCatalogSelected : false,
            currentFunctionCatalog : undefined,
        };

        this.deleteFunctionCatalog = this.deleteFunctionCatalog.bind(this);
        this.renderFunctionCatalogs = this.renderFunctionCatalogs.bind(this);
        this.onFunctionCatalogSubmit = this.onFunctionCatalogSubmit.bind(this);
        this.onFunctionCatalogSave = this.onFunctionCatalogSave.bind(this);
        this.onFunctionCatalogSelected = this.onFunctionCatalogSelected.bind(this);
        this.onRootNavigationItemClicked = this.onRootNavigationItemClicked.bind(this);

        const thisApp = this;
        const versionId = 1;
        getFunctionCatalogsForVersionId(versionId, function(functionCatalogsJson) {
            const functionCatalogs = [];
            for (let i in functionCatalogsJson) {
                const functionCatalogJson = functionCatalogsJson[i];
                const functionCatalog = FunctionCatalog.fromJson(functionCatalogJson);
                functionCatalogs.push(functionCatalog);
            }

            thisApp.setState({
                functionCatalogs: functionCatalogs
            });
        });
    }

    onFunctionCatalogSubmit(functionCatalog) {
        const thisApp = this;

        const versionId = 1; // TODO
        const functionCatalogJson = FunctionCatalog.toJson(functionCatalog);

        insertFunctionCatalog(versionId, functionCatalogJson, function(functionCatalogId) {
            functionCatalog.setId(functionCatalogId);
            const functionCatalogs = thisApp.state.functionCatalogs.concat(functionCatalog);

            thisApp.setState({
                functionCatalogs: functionCatalogs
            });

        });
    }

    onFunctionCatalogSave(functionCatalog) {
        const thisApp = this;

        const versionId = 1; // TODO
        const functionCatalogJson = FunctionCatalog.toJson(functionCatalog);

        modifyFunctionCatalog(versionId,functionCatalogJson, functionCatalog.getId(), function(wasSuccess) {
            if (wasSuccess) {
                const index = thisApp.state.functionCatalogs.indexOf(functionCatalog);
                var functionCatalogs = thisApp.state.functionCatalogs.splice(index, index+1);
                functionCatalogs = functionCatalogs.concat(functionCatalog);

                thisApp.setState({
                    functionCatalogs: functionCatalogs,
                    currentFunctionCatalog : functionCatalog
                });
            }
        });
    }

    onRootNavigationItemClicked() {
        const navigationItems = [];

        this.setState({
            navigationItems: navigationItems,
            isFunctionCatalogSelected : false
        });
    }

    onFunctionCatalogSelected(functionCatalog) {
        const navigationItems = [];
        navigationItems.push(functionCatalog);

        const functionBlocks = functionCatalog.getFunctionBlocks();
        for (let i in functionBlocks) {
            const functionBlock = functionBlocks[i];
            navigationItems.push(functionBlock);
        }
        // TODO: Traverse FB children... [, ...]

        this.setState({
            navigationItems: navigationItems,
            currentFunctionCatalog: functionCatalog,
            isFunctionCatalogSelected : true
        });
    }

    deleteFunctionCatalog(functionCatalog) {
        const thisApp = this;

        const versionId = 1; // TODO
        const functionCatalogId = functionCatalog.getId();

        deleteFunctionCatalog(versionId, functionCatalogId, function (success) {
            if (success) {
                const newFunctionCatalogs = [];
                const existingFunctionCatalogs = thisApp.state.functionCatalogs;
                for (let i in existingFunctionCatalogs) {
                    const existingFunctionCatalog = existingFunctionCatalogs[i];
                    if (existingFunctionCatalog.getId() != functionCatalog.getId()) {
                        newFunctionCatalogs.push(existingFunctionCatalog);
                    }
                }
                thisApp.setState({
                    functionCatalogs: newFunctionCatalogs
                });
            }
        });
    }

    renderFunctionCatalogs() {
        const reactComponents = [];
        for (let i in this.state.functionCatalogs) {
            const functionCatalog = this.state.functionCatalogs[i];
            reactComponents.push(<app.FunctionCatalog key={i} functionCatalog={functionCatalog} onClick={this.onFunctionCatalogSelected} onDelete={this.deleteFunctionCatalog} />);
        }
        return reactComponents;
    }

    render() {
        return (
            <div className="container">
                <app.Navigation navigationItems={this.state.navigationItems} onRootItemClicked={this.onRootNavigationItemClicked} />
                <div className="display-area">
                    <app.FunctionCatalogForm
                        onSubmit={this.state.isFunctionCatalogSelected ? this.onFunctionCatalogSave : this.onFunctionCatalogSubmit}
                        functionCatalog={this.state.currentFunctionCatalog}
                        isFunctionCatalogSelected={this.state.isFunctionCatalogSelected}
                    />
                    <div id="child-display-area" className="clearfix">
                        {this.renderFunctionCatalogs()}
                    </div>
                </div>
            </div>
        );
    }
}

registerClassWithGlobalScope("App", App);

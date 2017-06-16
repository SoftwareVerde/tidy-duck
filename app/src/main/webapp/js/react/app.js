class App extends React.Component {
    constructor(props) {
        super(props);

        this.navigationLevel = {
            versions:           "versions",
            functionCatalogs:   "functionCatalogs",
            functionBlocks:     "functionBlocks",
            interfaces:         "interfaces",
            functions:          "functions",
            operations:         "operations"
        };

        this.state = {
            navigationItems:    [],
            childItems:         [],
            functionCatalogs:   [],
            functionBlocks:     [],
            interfaces:         [],
            isChildItemSelected :   false,
            selectedChildItem :     null,
            currentNavigationLevel: this.navigationLevel.versions,
            showFunctionBlockForm: false
        };

        this.deleteFunctionCatalog = this.deleteFunctionCatalog.bind(this);
        this.renderChildItems = this.renderChildItems.bind(this);
        this.onFunctionCatalogSubmit = this.onFunctionCatalogSubmit.bind(this);
        this.onFunctionCatalogSave = this.onFunctionCatalogSave.bind(this);
        this.onFunctionCatalogSelected = this.onFunctionCatalogSelected.bind(this);
        this.onRootNavigationItemClicked = this.onRootNavigationItemClicked.bind(this);
        this.onPlusButtonClicked = this.onPlusButtonClicked.bind(this);
        this.onFunctionBlockSelected = this.onFunctionBlockSelected.bind(this);

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
                functionCatalogs: functionCatalogs,
                childItems: functionCatalogs
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
                functionCatalogs: functionCatalogs,
                childItems: functionCatalogs,
                selectedChildItem : functionCatalog
            });
        });
    }

    onFunctionCatalogSave(functionCatalog) {
        const thisApp = this;

        const versionId = 1; // TODO
        const functionCatalogJson = FunctionCatalog.toJson(functionCatalog);
        const functionCatalogId = functionCatalog.getId();

        modifyFunctionCatalog(versionId,functionCatalogJson, functionCatalogId, function(wasSuccess) {
            if (wasSuccess) {
                var functionCatalogs = thisApp.state.functionCatalogs.filter(function(value) {
                  return value.getId() != functionCatalogId;
                });
                functionCatalogs = functionCatalogs.push(functionCatalog);

                thisApp.setState({
                    functionCatalogs: functionCatalogs,
                    childItems: functionCatalogs,
                    selectedChildItem : functionCatalog
                });
            }
        });
    }

    onRootNavigationItemClicked() {
        const navigationItems = [];

        this.setState({
            navigationItems: navigationItems,
            isChildItemSelected : false,
            selectedChildItem: null,
            navigationLevel: this.navigationLevel.versions,
            showFunctionBlockForm: false
        });

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
                functionCatalogs: functionCatalogs,
                childItems: functionCatalogs,
            });
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
            selectedChildItem: functionCatalog,
            functionBlocks: functionBlocks,
            childItems: functionBlocks,
            isChildItemSelected : true,
            navigationLevel: this.navigationLevel.functionCatalogs
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
                    functionCatalogs: newFunctionCatalogs,
                    childItems: newFunctionCatalogs,
                    selectedChildItem: null
                });
            }
        });
    }

    onPlusButtonClicked() {
        this.setState({
            isChildItemSelected:    false,
            selectedChildItem:      null,
            showFunctionBlockForm:  true
        });
    }

    onFunctionBlockSelected(functionBlock) {
        const navigationItems = [];
        navigationItems.push(functionBlock);

        const interfaces = functionBlock.getInterfaces();
        for (let i in interfaces) {
            const blockInterface = interfaces[i];
            navigationItems.push(blockInterface);
        }
        // TODO: Traverse FB children... [, ...]
        this.setState({
            navigationItems: navigationItems,
            selectedChildItem: functionBlock,
            interfaces: interfaces,
            childItems: interfaces,
            isChildItemSelected : true,
            navigationLevel: this.navigationLevel.interfaces
        });
    }

    renderChildItems() {
        const reactComponents = [];
        const navigationLevel = this.navigationLevel;
        const currentNavigationLevel = this.state.currentNavigationLevel;

        for (let i in this.state.childItems) {
            const childItem = this.state.childItems[i];

            // TODO: having this switch statement in the loop is...most troubling.
            switch(currentNavigationLevel)
            {
                case navigationLevel.versions:
                    reactComponents.push(<app.FunctionCatalog key={i} functionCatalog={childItem} onClick={this.onFunctionCatalogSelected} onDelete={this.deleteFunctionCatalog} />);
                    break;
                case navigationLevel.functionCatalogs:
                    // TODO: Add necessary save/submit functions and change onSubmit props.
                    if(this.state.showFunctionBlockForm) { // Display Function Block Form AFTER Function Catalog form.
                        reactComponents.push(
                            <app.FunctionBlockForm
                                onSubmit={this.state.isChildItemSelected ? this.onFunctionCatalogSave : this.onFunctionCatalogSubmit}
                                functionCatalog={this.state.selectedChildItem}
                                isChildItemSelected={this.state.isChildItemSelected}
                            />);
                    } else {                               // Display + icon for adding a function block.
                        reactComponents.push(<i className="fa fa-plus" onClick={this.onPlusButtonClicked}/>);
                    }

                    // TODO: Add necessary delete function and change onDelete props.
                    reactComponents.push(<app.FunctionBlock key={i} functionBlock={childItem} onClick={this.onFunctionBlockSelected} onDelete={this.deleteFunctionCatalog} />);
                    break;
            }
        }
        return reactComponents;
    }

    render() {
        return (
            <div className="container">
                <app.Navigation navigationItems={this.state.navigationItems} onRootItemClicked={this.onRootNavigationItemClicked} />
                <div className="display-area">
                    <app.FunctionCatalogForm
                        onSubmit={this.state.isChildItemSelected ? this.onFunctionCatalogSave : this.onFunctionCatalogSubmit}
                        functionCatalog={this.state.selectedChildItem}
                        isChildItemSelected={this.state.isChildItemSelected}
                    />
                    <div id="child-display-area" className="clearfix">
                        {this.renderChildItems()}
                    </div>
                </div>
            </div>
        );
    }
}

registerClassWithGlobalScope("App", App);

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
            functionCatalogs:   [],
            functionBlocks:     [],
            interfaces:         [],
            isChildItemSelected:    false,
            selectedChildItem:      null,
            currentNavigationLevel: this.navigationLevel.versions,
            showFunctionBlockForm:  false
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
                functionCatalogs:       functionCatalogs,
                currentNavigationLevel: thisApp.navigationLevel.versions
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
                functionCatalogs:       functionCatalogs,
                selectedChildItem:      functionCatalog,
                currentNavigationLevel: thisApp.navigationLevel.versions
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
                    functionCatalogs:       functionCatalogs,
                    selectedChildItem:      functionCatalog,
                    currentNavigationLevel: thisApp.navigationLevel.functionCatalogs
                });
            }
        });
    }

    onRootNavigationItemClicked() {
        const thisApp = this;
        const navigationItems = [];

        this.setState({
            navigationItems:        navigationItems,
            isChildItemSelected:    false,
            selectedChildItem:      null,
            showFunctionBlockForm:  false,
            navigationLevel:        thisApp.navigationLevel.versions
        });

        const versionId = 1;
        getFunctionCatalogsForVersionId(versionId, function(functionCatalogsJson) {
            const functionCatalogs = [];
            for (let i in functionCatalogsJson) {
                const functionCatalogJson = functionCatalogsJson[i];
                const functionCatalog = FunctionCatalog.fromJson(functionCatalogJson);
                functionCatalogs.push(functionCatalog);
            }

            thisApp.setState({
                functionCatalogs:       functionCatalogs,
                currentNavigationLevel: thisApp.navigationLevel.versions
            });
        });
    }

    onFunctionCatalogSelected(functionCatalog) {
        const thisApp = this;
        const navigationItems = [];
        navigationItems.push(functionCatalog);

        getFunctionBlocksForFunctionCatalogId(functionCatalog.getId(), function(functionBlocksJson) {
            const functionBlocks = [];
            for (let i in functionBlocksJson) {
                const functionBlockJson = functionBlocksJson[i];
                const functionBlock = FunctionBlock.fromJson(functionBlockJson);
                functionBlocks.push(functionBlock);
            }
            thisApp.setState({
                navigationItems:        navigationItems,
                selectedChildItem:      functionCatalog,
                functionBlocks:         functionBlocks,
                isChildItemSelected:    true,
                currentNavigationLevel: thisApp.navigationLevel.functionCatalogs
            });
        })
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
                    functionCatalogs:       newFunctionCatalogs,
                    selectedChildItem:      null,
                    currentNavigationLevel: thisApp.navigationLevel.functionCatalogs
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
        const thisApp = this;

        const navigationItems = [];
        navigationItems.push(functionBlock);

        const interfaces = functionBlock.getInterfaces();
        for (let i in interfaces) {
            const blockInterface = interfaces[i];
            navigationItems.push(blockInterface);
        }
        // TODO: Traverse FB children... [, ...]
        this.setState({
            navigationItems:        navigationItems,
            selectedChildItem:      functionBlock,
            interfaces:             interfaces,
            isChildItemSelected:    true,
            currentNavigationLevel: thisApp.navigationLevel.interfaces
        });
    }

    renderChildItems() {
        const reactComponents = [];
        const navigationLevel = this.navigationLevel;
        const currentNavigationLevel = this.state.currentNavigationLevel;

        console.log(currentNavigationLevel);

        let childItems = [];
        switch (currentNavigationLevel) {
            case navigationLevel.versions:
                childItems = this.state.functionCatalogs;
                for (let i in childItems) {
                    const childItem = childItems[i];
                    const functionCatalogKey = "FunctionCatalog" + i;
                    reactComponents.push(<app.FunctionCatalog key={functionCatalogKey} functionCatalog={childItem} onClick={this.onFunctionCatalogSelected} onDelete={this.deleteFunctionCatalog} />);
                }
            break;

            case navigationLevel.functionCatalogs:
                const shouldShowFunctionBlockForm = this.state.showFunctionBlockForm;
                childItems = this.state.functionBlocks;
                for (let i in childItems) {
                    const childItem = childItems[i];
                    // TODO: Add necessary save/submit functions and change onSubmit props.
                    if (shouldShowFunctionBlockForm) { // Display Function Block Form AFTER Function Catalog form.
                        reactComponents.push(
                            <app.FunctionBlockForm key="FunctionBlockDisplayForm"
                                onSubmit={this.state.isChildItemSelected ? this.onFunctionCatalogSave : this.onFunctionCatalogSubmit}
                                functionCatalog={this.state.selectedChildItem}
                                isChildItemSelected={this.state.isChildItemSelected}
                            />
                        );
                    } else {                               // Display + icon for adding a function block.
                        reactComponents.push(<i key="FunctionBlockAddButton" className="fa fa-plus" onClick={this.onPlusButtonClicked}/>);
                    }

                    // TODO: Add necessary delete function and change onDelete props.
                    const functionBlockKey = "FunctionBlock" + i;
                    reactComponents.push(<app.FunctionBlock key={functionBlockKey} functionBlock={childItem} onClick={this.onFunctionBlockSelected} onDelete={this.deleteFunctionCatalog} />);
                }
            break;
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

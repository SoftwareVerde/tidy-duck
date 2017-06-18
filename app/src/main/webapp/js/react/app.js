class App extends React.Component {
    constructor(props) {
        super(props);

        this.NavigationLevel = {
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
            selectedItem:               null,
            currentNavigationLevel:     this.NavigationLevel.versions,
            shouldShowCreateChildForm:  true
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
                currentNavigationLevel: thisApp.NavigationLevel.versions
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
                selectedItem:           functionCatalog,
                currentNavigationLevel: thisApp.NavigationLevel.versions
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
                    selectedItem:           functionCatalog,
                    currentNavigationLevel: thisApp.NavigationLevel.functionCatalogs
                });
            }
        });
    }

    onFunctionBlockSubmit(functionBlock) {
        const thisApp = this;

        const versionId = 1; // TODO
        const functionBlockJson = FunctionBlock.toJson(functionBlock);

        insertFunctionBlock(versionId, functionBlockJson, function(functionBlockId) {
            functionBlock.setId(functionBlockId);
            const functionBlocks = thisApp.state.functionBlocks.concat(functionBlock);

            thisApp.setState({
                functionBlocks:       functionBlocks,
                selectedItem:           functionBlock,
                currentNavigationLevel: thisApp.NavigationLevel.versions
            });
        });
    }

    onFunctionBlockSave(functionBlock) {
        const thisApp = this;

        const versionId = 1; // TODO
        const functionBlockJson = FunctionBlock.toJson(functionBlock);
        const functionBlockId = functionBlock.getId();

        modifyFunctionBlock(versionId,functionBlockJson, functionBlockId, function(wasSuccess) {
            if (wasSuccess) {
                var functionBlocks = thisApp.state.functionBlocks.filter(function(value) {
                  return value.getId() != functionBlockId;
                });
                functionBlocks = functionBlocks.push(functionBlock);

                thisApp.setState({
                    functionBlocks:       functionBlocks,
                    selectedItem:           functionBlock,
                    currentNavigationLevel: thisApp.NavigationLevel.functionBlocks
                });
            }
        });
    }

    onRootNavigationItemClicked() {
        const thisApp = this;
        const navigationItems = [];

        this.setState({
            navigationItems:            navigationItems,
            selectedItem:               null,
            shouldShowCreateChildForm:  true,
            navigationLevel:            thisApp.NavigationLevel.versions
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
                currentNavigationLevel: thisApp.NavigationLevel.versions
            });
        });
    }

    onFunctionCatalogSelected(functionCatalog) {
        const thisApp = this;
        const navigationItems = [];

        const navigationItemConfig = new NavigationItemConfig();
        navigationItemConfig.setTitle(functionCatalog.getName());
        navigationItemConfig.setIconName("fa-bars");

        const navigationMenuItemConfig = new NavigationItemConfig();
        navigationMenuItemConfig.setTitle("Download MOST XML");
        navigationMenuItemConfig.setIconName("fa-download");
        navigationMenuItemConfig.setOnClickCallback(function() {
            const functionCatalogId = functionCatalog.getId();
            exportFunctionCatalogToMost(functionCatalogId);
        });
        navigationItemConfig.addMenuItemConfig(navigationMenuItemConfig);
        navigationItemConfig.setOnClickCallback(function() {
            thisApp.onFunctionCatalogSelected(functionCatalog);
        });

        navigationItems.push(navigationItemConfig);

        getFunctionBlocksForFunctionCatalogId(functionCatalog.getId(), function(functionBlocksJson) {
            const functionBlocks = [];
            for (let i in functionBlocksJson) {
                const functionBlockJson = functionBlocksJson[i];
                const functionBlock = FunctionBlock.fromJson(functionBlockJson);
                functionBlocks.push(functionBlock);
            }
            thisApp.setState({
                navigationItems:        navigationItems,
                selectedItem:           functionCatalog,
                functionBlocks:         functionBlocks,
                currentNavigationLevel: thisApp.NavigationLevel.functionCatalogs
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
                    selectedItem:           null,
                    currentNavigationLevel: thisApp.NavigationLevel.functionCatalogs
                });
            }
        });
    }

    onPlusButtonClicked() {
        this.setState({
            selectedItem:               null,
            shouldShowCreateChildForm:  true
        });
    }

    onFunctionBlockSelected(functionBlock) {
        const thisApp = this;

        const navigationItems = [];
        for (let i in this.state.navigationItems) {
            const navigationItem = this.state.navigationItems[i];
            navigationItems.push(navigationItem);
            break; // Take only for the first one...
        }

        const navigationItemConfig = new NavigationItemConfig();
        navigationItemConfig.setTitle(functionBlock.getName());
        navigationItemConfig.setOnClickCallback(function() {
            thisApp.onFunctionBlockSelected(functionBlock);
        });
        navigationItems.push(navigationItemConfig);

        const interfaces = functionBlock.getInterfaces();

        // TODO: Traverse FB children... [, ...]
        this.setState({
            navigationItems:        navigationItems,
            selectedItem:           functionBlock,
            interfaces:             interfaces,
            currentNavigationLevel: thisApp.NavigationLevel.functionBlocks
        });
    }

    renderChildItems() {
        const reactComponents = [];
        const NavigationLevel = this.NavigationLevel;
        const currentNavigationLevel = this.state.currentNavigationLevel;

        console.log(currentNavigationLevel);

        let childItems = [];
        switch (currentNavigationLevel) {
            case NavigationLevel.versions:
                childItems = this.state.functionCatalogs;
                for (let i in childItems) {
                    const childItem = childItems[i];
                    const functionCatalogKey = "FunctionCatalog" + i;
                    reactComponents.push(<app.FunctionCatalog key={functionCatalogKey} functionCatalog={childItem} onClick={this.onFunctionCatalogSelected} onDelete={this.deleteFunctionCatalog} />);
                }
            break;

            case NavigationLevel.functionCatalogs:
                childItems = this.state.functionBlocks;
                for (let i in childItems) {
                    const childItem = childItems[i];
                    // TODO: Add necessary save/submit functions and change onSubmit props.
                    // reactComponents.push(<i key="FunctionBlockAddButton" className="fa fa-plus" onClick={this.onPlusButtonClicked}/>);

                    // TODO: Add necessary delete function and change onDelete props.
                    const functionBlockKey = "FunctionBlock" + i;
                    reactComponents.push(<app.FunctionBlock key={functionBlockKey} functionBlock={childItem} onClick={this.onFunctionBlockSelected} onDelete={this.deleteFunctionCatalog} />);
                }
            break;

            case NavigationLevel.functionBlocks:
                childItems = this.state.interfaces;
                for (let i in childItems) {
                    const childItem = childItems[i];
                    reactComponents.push(<div className="function-catalog" />);
                }
            break;

            default:
                console.log("renderChildItems: Unimplemented Navigation Level: "+ currentNavigationLevel);
            break;
        }

        return reactComponents;
    }

    renderForm() {
        const NavigationLevel = this.NavigationLevel;
        const currentNavigationLevel = this.state.currentNavigationLevel;

        const isEditingExistingObject = (this.state.selectedItem != null);
        const shouldShowCreateChildForm = this.state.shouldShowCreateChildForm;

        const reactComponents = [];

        switch (currentNavigationLevel) {
            case NavigationLevel.versions:
                if (shouldShowCreateChildForm) {
                    reactComponents.push(
                        <app.FunctionCatalogForm key="FunctionCatalogForm"
                            onSubmit={isEditingExistingObject ? this.onFunctionCatalogSave : this.onFunctionCatalogSubmit}
                            isItemSelected={this.state.isItemSelected}
                            functionCatalog={this.state.selectedItem}
                        />
                    );
                }
            break;

            case NavigationLevel.functionCatalogs:
                reactComponents.push(
                    <app.FunctionCatalogForm key="FunctionCatalogForm"
                        onSubmit={isEditingExistingObject ? this.onFunctionCatalogSave : this.onFunctionCatalogSubmit}
                        functionCatalog={this.state.selectedItem}
                        isItemSelected={true}
                    />
                );
                if (shouldShowCreateChildForm) {
                    reactComponents.push(
                        <app.FunctionBlockForm key="FunctionBlockForm"
                            onSubmit={this.onFunctionBlockSubmit}
                            isItemSelected={false}
                        />
                    );
                }
            break;

            case NavigationLevel.functionBlocks:
                reactComponents.push(
                    <app.FunctionBlockForm key="FunctionBlockForm"
                        onSubmit={this.onFunctionBlockSave}
                        functionBlock={this.state.selectedItem}
                        isItemSelected={true}
                    />
                );
            break;

            default:
                console.log("renderForm: Unimplemented Navigation Level: "+ currentNavigationLevel);
            break;
        }

        return reactComponents;
    }

    render() {
        return (
            <div className="container">
                <app.Navigation navigationItems={this.state.navigationItems} onRootItemClicked={this.onRootNavigationItemClicked} />
                <div className="display-area">
                    {this.renderForm()}
                    <div id="child-display-area" className="clearfix">
                        {this.renderChildItems()}
                    </div>
                </div>
            </div>
        );
    }
}

registerClassWithGlobalScope("App", App);

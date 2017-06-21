class App extends React.Component {
    constructor(props) {
        super(props);

        this.NavigationLevel = {
            versions:           "versions",
            functionCatalogs:   "functionCatalogs",
            functionBlocks:     "functionBlocks",
            mostInterfaces:     "mostInterfaces",
            functions:          "functions",
            operations:         "operations"
        };

        this.state = {
            navigationItems:            [],
            functionCatalogs:           [],
            functionBlocks:             [],
            mostInterfaces:             [],
            functions:                  [],
            selectedItem:               null,
            parentItem:                 null,
            currentNavigationLevel:     this.NavigationLevel.versions,
            shouldShowToolbar:          true,
            shouldShowCreateChildForm:  false
        };

        this.onRootNavigationItemClicked = this.onRootNavigationItemClicked.bind(this);
        this.renderChildItems = this.renderChildItems.bind(this);

        this.onFunctionCatalogSelected = this.onFunctionCatalogSelected.bind(this);
        this.onCreateFunctionCatalog = this.onCreateFunctionCatalog.bind(this);
        this.onUpdateFunctionCatalog = this.onUpdateFunctionCatalog.bind(this);
        this.onDeleteFunctionCatalog = this.onDeleteFunctionCatalog.bind(this);

        this.onFunctionBlockSelected = this.onFunctionBlockSelected.bind(this);
        this.onCreateFunctionBlock = this.onCreateFunctionBlock.bind(this);
        this.onUpdateFunctionBlock = this.onUpdateFunctionBlock.bind(this);
        this.onDeleteFunctionBlock = this.onDeleteFunctionBlock.bind(this);

        this.onMostInterfaceSelected = this.onMostInterfaceSelected.bind(this);
        this.onCreateMostInterface = this.onCreateMostInterface.bind(this);
        this.onUpdateMostInterface = this.onUpdateMostInterface.bind(this);

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

    onCreateFunctionCatalog(functionCatalog) {
        const thisApp = this;

        const versionId = 1; // TODO
        const functionCatalogJson = FunctionCatalog.toJson(functionCatalog);

        insertFunctionCatalog(versionId, functionCatalogJson, function(functionCatalogId) {
            functionCatalog.setId(functionCatalogId);
            const functionCatalogs = thisApp.state.functionCatalogs.concat(functionCatalog);

            thisApp.setState({
                functionCatalogs:       functionCatalogs,
                currentNavigationLevel: thisApp.NavigationLevel.versions
            });
        });
    }

    onUpdateFunctionCatalog(functionCatalog) {
        const thisApp = this;

        const versionId = 1; // TODO
        const functionCatalogJson = FunctionCatalog.toJson(functionCatalog);
        const functionCatalogId = functionCatalog.getId();

        updateFunctionCatalog(versionId, functionCatalogId, functionCatalogJson, function(wasSuccess) {
            if (wasSuccess) {
                var functionCatalogs = thisApp.state.functionCatalogs.filter(function(value) {
                    return value.getId() != functionCatalogId;
                });
                functionCatalogs.push(functionCatalog);

                //Update final navigation item to reflect any name changes.
                var navigationItems = [];
                navigationItems = navigationItems.concat(thisApp.state.navigationItems);
                var navigationItem = navigationItems.pop();
                navigationItem.setTitle(functionCatalog.getName());
                navigationItems.push(navigationItem);

                thisApp.setState({
                    functionCatalogs:       functionCatalogs,
                    selectedItem:           functionCatalog,
                    navigationItems:        navigationItems,
                    currentNavigationLevel: thisApp.NavigationLevel.functionCatalogs
                });
            }
        });
    }

    onCreateFunctionBlock(functionBlock) {
        const thisApp = this;

        const functionCatalog = this.state.selectedItem;

        const functionCatalogId = functionCatalog.getId();
        const functionBlockJson = FunctionBlock.toJson(functionBlock);

        insertFunctionBlock(functionCatalogId, functionBlockJson, function(functionBlockId) {
            if (! (functionBlockId > 0)) {
                console.log("Unable to create function block.");
                return;
            }

            functionBlock.setId(functionBlockId);
            const functionBlocks = thisApp.state.functionBlocks.concat(functionBlock);

            thisApp.setState({
                functionBlocks:         functionBlocks,
                currentNavigationLevel: thisApp.NavigationLevel.functionCatalogs
            });
        });
    }

    onUpdateFunctionBlock(functionBlock) {
        const thisApp = this;

        const functionCatalogId = this.state.parentItem.getId();
        const functionBlockJson = FunctionBlock.toJson(functionBlock);
        const functionBlockId = functionBlock.getId();

        updateFunctionBlock(functionCatalogId, functionBlockId, functionBlockJson, function(wasSuccess) {
            if (wasSuccess) {
                var functionBlocks = thisApp.state.functionBlocks.filter(function(value) {
                    return value.getId() != functionBlockId;
                });
                functionBlocks.push(functionBlock);

                //Update final navigation item to reflect any name changes.
                var navigationItems = [];
                navigationItems = navigationItems.concat(thisApp.state.navigationItems);
                var navigationItem = navigationItems.pop();
                navigationItem.setTitle(functionBlock.getName());
                navigationItems.push(navigationItem);

                thisApp.setState({
                    functionBlocks:         functionBlocks,
                    selectedItem:           functionBlock,
                    navigationItems:        navigationItems,
                    currentNavigationLevel: thisApp.NavigationLevel.functionBlocks
                });
            }
        });
    }

    onCreateMostInterface(mostInterface) {
        const thisApp = this;

        const functionBlock = this.state.selectedItem;

        const functionBlockId = functionBlock.getId();
        const mostInterfaceJson = MostInterface.toJson(mostInterface);

        insertMostInterface(functionBlockId, mostInterfaceJson, function(mostInterfaceId) {
            if (! (mostInterfaceId > 0)) {
                console.log("Unable to create interface.");
                return;
            }

            mostInterface.setId(mostInterfaceId);
            const mostInterfaces = thisApp.state.mostInterfaces.concat(mostInterface);

            thisApp.setState({
                mostInterfaces:         mostInterfaces,
                currentNavigationLevel: thisApp.NavigationLevel.mostInterfaces
            });
        });
    }

    onUpdateMostInterface(mostInterface) {
        const thisApp = this;

        const functionBlockId = this.state.parentItem.getId();
        const mostInterfaceJson = MostInterface.toJson(mostInterface);
        const mostInterfaceId = mostInterface.getId();

        updateMostInterface(functionBlockId, mostInterfaceId, mostInterfaceJson, function(wasSuccess) {
            if (wasSuccess) {
                var mostInterfaces = thisApp.state.mostInterfaces.filter(function(value) {
                    return value.getId() != mostInterfaceId;
                });
                mostInterfaces.push(mostInterface);

                //Update final navigation item to reflect any name changes.
                var navigationItems = [];
                navigationItems = navigationItems.concat(thisApp.state.navigationItems);
                var navigationItem = navigationItems.pop();
                navigationItem.setTitle(mostInterface.getName());
                navigationItems.push(navigationItem);

                thisApp.setState({
                    mostInterfaces:         mostInterfaces,
                    selectedItem:           mostInterface,
                    navigationItems:        navigationItems,
                    currentNavigationLevel: thisApp.NavigationLevel.mostInterfaces
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
            parentItem:                 null,
            shouldShowToolbar:          true,
            shouldShowCreateChildForm:  false,
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

        navigationItemConfig.setForm(
            <app.FunctionCatalogForm 
                showTitle={false}
                onSubmit={this.onUpdateFunctionCatalog}
                functionCatalog={functionCatalog}
                buttonTitle="Save"
            />
        );

        navigationItems.push(navigationItemConfig);

        getFunctionBlocksForFunctionCatalogId(functionCatalog.getId(), function(functionBlocksJson) {
            const functionBlocks = [];
            for (let i in functionBlocksJson) {
                const functionBlockJson = functionBlocksJson[i];
                const functionBlock = FunctionBlock.fromJson(functionBlockJson);
                functionBlocks.push(functionBlock);
            }
            thisApp.setState({
                navigationItems:            navigationItems,
                selectedItem:               functionCatalog,
                parentItem:                 null,
                functionBlocks:             functionBlocks,
                shouldShowCreateChildForm:  false,
                currentNavigationLevel:     thisApp.NavigationLevel.functionCatalogs
            });
        })
    }

    onDeleteFunctionCatalog(functionCatalog) {
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
                    currentNavigationLevel: thisApp.NavigationLevel.functionCatalogs
                });
            }
        });
    }

    onFunctionBlockSelected(functionBlock) {
        const thisApp = this;

        const navigationItems = [];
        for (let i in this.state.navigationItems) {
            const navigationItem = this.state.navigationItems[i];
            navigationItem.setForm(null);
            navigationItems.push(navigationItem);
            break; // Take only for the first one...
        }

        const navigationItemConfig = new NavigationItemConfig();
        navigationItemConfig.setTitle(functionBlock.getName());
        navigationItemConfig.setOnClickCallback(function() {
            thisApp.onFunctionBlockSelected(functionBlock);
        });
        navigationItemConfig.setForm(
            <app.FunctionBlockForm key="FunctionBlockForm"
                showTitle={false}
                onSubmit={this.onUpdateFunctionBlock}
                functionBlock={functionBlock}
                buttonTitle="Save"
            />
        );
        navigationItems.push(navigationItemConfig);

        getMostInterfacesForFunctionBlockId(functionBlock.getId(), function(mostInterfacesJson) {
            const parentItem = thisApp.state.selectedItem; //Preserve reference to previously selected item.

            const mostInterfaces = [];
            for (let i in mostInterfacesJson) {
                const mostInterfaceJson = mostInterfacesJson[i];
                const mostInterface = MostInterface.fromJson(mostInterfaceJson);
                mostInterfaces.push(mostInterface);
            }

            thisApp.setState({
                navigationItems:            navigationItems,
                selectedItem:               functionBlock,
                parentItem:                 parentItem,
                mostInterfaces:             mostInterfaces,
                shouldShowCreateChildForm:  false,
                currentNavigationLevel:     thisApp.NavigationLevel.functionBlocks
            });
        });
        
    }

    onDeleteFunctionBlock(functionBlock) {
        const thisApp = this;

        const functionCatalogId = this.state.selectedItem.getId();
        const functionBlockId = functionBlock.getId();

        deleteFunctionBlock(functionCatalogId, functionBlockId, function (success) {
            if (success) {
                const newFunctionBlocks = [];
                const existingFunctionBlocks = thisApp.state.functionBlocks;
                for (let i in existingFunctionBlocks) {
                    const existingFunctionBlock = existingFunctionBlocks[i];
                    if (existingFunctionBlock.getId() != functionBlock.getId()) {
                        newFunctionBlocks.push(existingFunctionBlock);
                    }
                }
                thisApp.setState({
                    functionBlocks:       newFunctionBlocks,
                    currentNavigationLevel: thisApp.NavigationLevel.functionCatalogs
                });
            }
        });
    }

    onMostInterfaceSelected(mostInterface) {
        const thisApp = this;

        const navigationItems = [];
        for (let i in this.state.navigationItems) {
            const navigationItem = this.state.navigationItems[i];
            navigationItem.setForm(null);
            navigationItems.push(navigationItem);
            break; // Take only for the first one...
        }

        const navigationItemConfig = new NavigationItemConfig();
        navigationItemConfig.setTitle(mostInterface.getName());
        navigationItemConfig.setOnClickCallback(function() {
            thisApp.onFunctionBlockSelected(mostInterface);
        });
        navigationItemConfig.setForm(
            <app.MostInterfaceForm key="MostInterfaceForm"
                                   showTitle={false}
                                   onSubmit={this.onUpdateMostInterface}
                                   mostInterface={mostInterface}
                                   buttonTitle="Save"
            />
        );
        navigationItems.push(navigationItemConfig);

        // TODO: getFunctionsForMostInterfaceId should use the following as a callback function.

        const parentItem = this.state.selectedItem; //Preserve reference to previously selected item.
        const mostFunctions = this.state.functions; //Placeholder.

        // TODO: might want to consider renaming "functions" array to "mostFunctions".
        /*
        const mostFunctions = [];
        for (let i in mostFunctions) {
            const mostFunctionJson = mostFunctions[i];
            const mostFunction = MostFunction.fromJson(mostFunctionJson);
            mostFunctions.push(mostFunction);
        }
        */

        this.setState({
            navigationItems:            navigationItems,
            selectedItem:               mostInterface,
            parentItem:                 parentItem,
            functions:                  mostFunctions,
            shouldShowCreateChildForm:  false,
            currentNavigationLevel:     thisApp.NavigationLevel.mostInterfaces
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
                    reactComponents.push(<app.FunctionCatalog key={functionCatalogKey} functionCatalog={childItem} onClick={this.onFunctionCatalogSelected} onDelete={this.onDeleteFunctionCatalog} />);
                }
            break;

            case NavigationLevel.functionCatalogs:
                childItems = this.state.functionBlocks;
                for (let i in childItems) {
                    const childItem = childItems[i];
                    const functionBlockKey = "FunctionBlock" + i;
                    reactComponents.push(<app.FunctionBlock key={functionBlockKey} functionBlock={childItem} onClick={this.onFunctionBlockSelected} onDelete={this.onDeleteFunctionBlock} />);
                }
            break;

            case NavigationLevel.functionBlocks:
                childItems = this.state.mostInterfaces;
                for (let i in childItems) {
                    const childItem = childItems[i];
                    const interfaceKey = "Interface" + i;
                    // TODO: implement Delete Most Interface function for onDelete.
                    reactComponents.push(<app.MostInterface key={interfaceKey} mostInterface={childItem} onClick={this.onMostInterfaceSelected} onDelete={this.onDeleteFunctionBlock} />);
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
        const shouldShowToolbar = this.state.shouldShowToolbar;
        const shouldShowCreateChildForm = this.state.shouldShowCreateChildForm;

        const reactComponents = [];

        if (shouldShowToolbar) {
            reactComponents.push(
                <app.Toolbar key="Toolbar"
                    onCreateClicked={() => this.setState({ shouldShowCreateChildForm: true })}
                    onCancel={() => this.setState({ shouldShowCreateChildForm: false })}
                />
            );
        }


        switch (currentNavigationLevel) {
            case NavigationLevel.versions:
                if (shouldShowCreateChildForm) {
                    reactComponents.push(
                        <app.FunctionCatalogForm key="FunctionCatalogForm"
                            showTitle={true}
                            onSubmit={this.onCreateFunctionCatalog}
                        />
                    );
                }
            break;

            case NavigationLevel.functionCatalogs:
                if (shouldShowCreateChildForm) {
                    reactComponents.push(
                        <app.FunctionBlockForm key="FunctionBlockForm"
                            showTitle={true}
                            onSubmit={this.onCreateFunctionBlock}
                        />
                    );
                }
            break;

            case NavigationLevel.functionBlocks:
                if (shouldShowCreateChildForm) {
                    reactComponents.push(
                        <app.MostInterfaceForm key="MostInterfaceForm"
                            showTitle={true}
                            onSubmit={this.onCreateMostInterface}
                        />
                    );
                }
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

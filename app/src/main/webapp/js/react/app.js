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
            navigationItems:            [],
            functionCatalogs:           [],
            functionBlocks:             [],
            interfaces:                 [],
            selectedItem:               null,
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
                functionCatalogs = functionCatalogs.push(functionCatalog);

                thisApp.setState({
                    functionCatalogs:       functionCatalogs,
                    selectedItem:           functionCatalog,
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

        const versionId = 1; // TODO
        const functionBlockJson = FunctionBlock.toJson(functionBlock);
        const functionBlockId = functionBlock.getId();

        updateFunctionBlock(functionBlockId, functionBlockJson, function(wasSuccess) {
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
                    selectedItem:           null,
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

        const interfaces = functionBlock.getInterfaces();

        this.setState({
            navigationItems:            navigationItems,
            selectedItem:               functionBlock,
            interfaces:                 interfaces,
            shouldShowCreateChildForm:  false,
            currentNavigationLevel:     thisApp.NavigationLevel.functionBlocks
        });
    }

    onDeleteFunctionBlock(functionBlock) {
        // TODO
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
                    reactComponents.push(<app.FunctionBlock key={functionBlockKey} functionBlock={childItem} onClick={this.onFunctionBlockSelected} onDelete={this.onDeleteFunctionCatalog} />);
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
                        <div key="InterfaceForm" className="metadata-form interfaces-placeholder">
                            Create-Interface Placeholder
                        </div>
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

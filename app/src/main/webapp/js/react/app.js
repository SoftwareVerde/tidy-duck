class App extends React.Component {
    constructor(props) {
        super(props);

        this.NavigationLevel = {
            versions:           "versions",
            functionCatalogs:   "functionCatalogs",
            functionBlocks:     "functionBlocks",
            mostInterfaces:     "mostInterfaces",
            mostFunctions:      "mostFunctions",
            operations:         "operations"
        };

        this.CreateButtonState = {
            normal:     "normal",
            animate:    "animate",
            success:    "success"
        };

        this.state = {
            account:                    null,
            navigationItems:            [],
            functionCatalogs:           [],
            functionBlocks:             [],
            mostInterfaces:             [],
            mostFunctions:              [],
            selectedItem:               null,
            parentItem:                 null,
            currentNavigationLevel:     this.NavigationLevel.versions,
            shouldShowToolbar:          true,
            shouldShowCreateChildForm:  false,
            createButtonState:          this.CreateButtonState.normal,
            isLoadingChildren:          true,
            theme:                      ""
        };

        this.onRootNavigationItemClicked = this.onRootNavigationItemClicked.bind(this);
        this.renderChildItems = this.renderChildItems.bind(this);

        this.getCurrentAccountAuthor = this.getCurrentAccountAuthor.bind(this);
        this.getCurrentAccountCompany = this.getCurrentAccountCompany.bind(this);
        this.getFunctionCatalogsForCurrentVersion = this.getFunctionCatalogsForCurrentVersion.bind(this);

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
        this.onDeleteMostInterface = this.onDeleteMostInterface.bind(this);

        this.handleSettingsClick = this.handleSettingsClick.bind(this);
        this.setTheme = this.setTheme.bind(this);

        this.logout = this.logout.bind(this);

        const thisApp = this;

        const account = downloadAccount(function (data) {
            if (data.wasSuccess) {
                thisApp.setState({
                    account: data.account
                });
            }
        });

        this.getFunctionCatalogsForCurrentVersion(function (functionCatalogs) {
            thisApp.setState({
                functionCatalogs:       functionCatalogs,
                currentNavigationLevel: thisApp.NavigationLevel.versions,
                isLoadingChildren:      false
            });
        });
    }

    onCreateFunctionCatalog(functionCatalog) {
        const thisApp = this;

        const versionId = 1; // TODO
        const functionCatalogJson = FunctionCatalog.toJson(functionCatalog);

        this.setState({
            createButtonState:  this.CreateButtonState.animate
        });

        insertFunctionCatalog(versionId, functionCatalogJson, function(functionCatalogId) {
            if (! (functionCatalogId > 0)) {
                console.log("Unable to create function catalog.");
                this.setState({
                    createButtonState: thisApp.CreateButtonState.normal
                });
                return;
            }

            functionCatalog.setId(functionCatalogId);
            functionCatalog.setAuthor(thisApp.getCurrentAccountAuthor());
            functionCatalog.setCompany(thisApp.getCurrentAccountCompany());

            const functionCatalogs = thisApp.state.functionCatalogs.concat(functionCatalog);

            thisApp.setState({
                createButtonState:      thisApp.CreateButtonState.success,
                functionCatalogs:       functionCatalogs,
                currentNavigationLevel: thisApp.NavigationLevel.versions
            });
        });
    }

    getCurrentAccountAuthor() {
        const author = new Author();
        author.setId(this.state.account.id);
        author.setName(this.state.account.name);
        return author;
    }

    getCurrentAccountCompany() {
        const company = new Company();
        company.setId(this.state.account.companyId);
        company.setName(this.state.account.companyName);
        return company;
    }

    onUpdateFunctionCatalog(functionCatalog) {
        const thisApp = this;

        const versionId = 1; // TODO
        const functionCatalogJson = FunctionCatalog.toJson(functionCatalog);
        const functionCatalogId = functionCatalog.getId();

        //Update function catalog form to display saving animation.
        var navigationItems = [];
        navigationItems = navigationItems.concat(thisApp.state.navigationItems);
        var navigationItem = navigationItems.pop();
        navigationItem.setForm(
            <app.FunctionCatalogForm
                showTitle={false}
                shouldShowSaveAnimation={true}
                onSubmit={this.onUpdateFunctionCatalog}
                functionCatalog={functionCatalog}
                buttonTitle="Save"
            />
        );
        navigationItems.push(navigationItem);

        this.setState({
           navigationItems: navigationItems
        });

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

                //Update form to show changes were saved.
                navigationItem.setForm(
                    <app.FunctionCatalogForm
                        showTitle={false}
                        shouldShowSaveAnimation={false}
                        onSubmit={thisApp.onUpdateFunctionCatalog}
                        functionCatalog={functionCatalog}
                        buttonTitle="Changes Saved"
                    />
                );
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

        this.setState({
            createButtonState:  this.CreateButtonState.animate
        });

        insertFunctionBlock(functionCatalogId, functionBlockJson, function(functionBlockId) {
            if (! (functionBlockId > 0)) {
                console.log("Unable to create function block.");
                this.setState({
                    createButtonState:  thisApp.CreateButtonState.normal
                });
                return;
            }

            functionBlock.setId(functionBlockId);
            functionBlock.setAuthor(thisApp.getCurrentAccountAuthor());
            functionBlock.setCompany(thisApp.getCurrentAccountCompany());

            const functionBlocks = thisApp.state.functionBlocks.concat(functionBlock);

            thisApp.setState({
                createButtonState:      thisApp.CreateButtonState.success,
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

        //Update function block form to display saving animation.
        var navigationItems = [];
        navigationItems = navigationItems.concat(thisApp.state.navigationItems);
        var navigationItem = navigationItems.pop();
        navigationItem.setForm(
            <app.FunctionBlockForm
                showTitle={false}
                shouldShowSaveAnimation={true}
                onSubmit={this.onUpdateFunctionBlock}
                functionBlock={functionBlock}
                buttonTitle="Save"
            />
        );
        navigationItems.push(navigationItem);

        this.setState({
           navigationItems: navigationItems
        });

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

                //Update form to show changes were saved.
                navigationItem.setForm(
                    <app.FunctionBlockForm
                        showTitle={false}
                        shouldShowSaveAnimation={false}
                        onSubmit={thisApp.onUpdateFunctionBlock}
                        functionBlock={functionBlock}
                        buttonTitle="Changes Saved"
                    />
                );
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

        this.setState({
            createButtonState:  this.CreateButtonState.animate
        });

        insertMostInterface(functionBlockId, mostInterfaceJson, function(mostInterfaceId) {
            if (! (mostInterfaceId > 0)) {
                console.log("Unable to create interface.");
                this.setState({
                    createButtonState:  thisApp.CreateButtonState.normal
                });
                return;
            }

            mostInterface.setId(mostInterfaceId);
            const mostInterfaces = thisApp.state.mostInterfaces.concat(mostInterface);

            thisApp.setState({
                createButtonState:      thisApp.CreateButtonState.success,
                mostInterfaces:         mostInterfaces,
                currentNavigationLevel: thisApp.NavigationLevel.functionBlocks
            });
        });
    }

    onUpdateMostInterface(mostInterface) {
        const thisApp = this;

        const functionBlockId = this.state.parentItem.getId();
        const mostInterfaceJson = MostInterface.toJson(mostInterface);
        const mostInterfaceId = mostInterface.getId();

        //Update function block form to display saving animation.
        var navigationItems = [];
        navigationItems = navigationItems.concat(thisApp.state.navigationItems);
        var navigationItem = navigationItems.pop();
        navigationItem.setForm(
            <app.MostInterfaceForm
                showTitle={false}
                shouldShowSaveAnimation={true}
                onSubmit={this.onUpdateMostInterface}
                mostInterface={mostInterface}
                buttonTitle="Save"
            />
        );
        navigationItems.push(navigationItem);

        this.setState({
            navigationItems: navigationItems
        });

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

                //Update form to show changes were saved.
                navigationItem.setForm(
                    <app.MostInterfaceForm
                        showTitle={false}
                        shouldShowSaveAnimation={false}
                        onSubmit={thisApp.onUpdateMostInterface}
                        mostInterface={mostInterface}
                        buttonTitle="Changes Saved"
                    />
                );
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
            createButtonState:          thisApp.CreateButtonState.normal,
            currentNavigationLevel:     thisApp.NavigationLevel.versions,
            isLoadingChildren:          false // can default on what we already have
        });

        this.getFunctionCatalogsForCurrentVersion(function (functionCatalogs) {
            if (thisApp.state.currentNavigationLevel == thisApp.NavigationLevel.versions) {
                // didn't navigate away while downloading children
                thisApp.setState({
                    functionCatalogs:       functionCatalogs,
                    isLoadingChildren:      false
                });
            }
        });
    }

    getFunctionCatalogsForCurrentVersion(callbackFunction) {
        const versionId = 1; // TODO
        getFunctionCatalogsForVersionId(versionId, function(functionCatalogsJson) {
            const functionCatalogs = [];

            for (let i in functionCatalogsJson) {
                const functionCatalogJson = functionCatalogsJson[i];
                const functionCatalog = FunctionCatalog.fromJson(functionCatalogJson);
                functionCatalogs.push(functionCatalog);
            }

            callbackFunction(functionCatalogs);
        });
    }

    onFunctionCatalogSelected(functionCatalog, canUseCachedChildren) {
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
            thisApp.onFunctionCatalogSelected(functionCatalog, true);
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

        thisApp.setState({
            navigationItems:            navigationItems,
            selectedItem:               functionCatalog,
            shouldShowCreateChildForm:  false,
            createButtonState:          thisApp.CreateButtonState.normal,
            currentNavigationLevel:     thisApp.NavigationLevel.functionCatalogs,
            isLoadingChildren:          !canUseCachedChildren
        });

        getFunctionBlocksForFunctionCatalogId(functionCatalog.getId(), function(functionBlocksJson) {
            if (thisApp.state.currentNavigationLevel == thisApp.NavigationLevel.functionCatalogs) {
                // didn't navigate away while downloading children
                const functionBlocks = [];
                for (let i in functionBlocksJson) {
                    const functionBlockJson = functionBlocksJson[i];
                    const functionBlock = FunctionBlock.fromJson(functionBlockJson);
                    functionBlocks.push(functionBlock);
                }
                thisApp.setState({
                    functionBlocks:     functionBlocks,
                    isLoadingChildren:  false
                });
            }
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
                    currentNavigationLevel: thisApp.NavigationLevel.versions
                });
            }
        });
    }

    onFunctionBlockSelected(functionBlock, canUseCachedChildren) {
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
            thisApp.onFunctionBlockSelected(functionBlock, true);
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

        const parentItem = thisApp.state.selectedItem; //Preserve reference to previously selected item.

        thisApp.setState({
            navigationItems:            navigationItems,
            selectedItem:               functionBlock,
            parentItem:                 parentItem,
            mostInterfaces:             [],
            shouldShowCreateChildForm:  false,
            createButtonState:          thisApp.CreateButtonState.normal,
            currentNavigationLevel:     thisApp.NavigationLevel.functionBlocks,
            isLoadingChildren:          !canUseCachedChildren
        });

        getMostInterfacesForFunctionBlockId(functionBlock.getId(), function(mostInterfacesJson) {
            if (thisApp.state.currentNavigationLevel == thisApp.NavigationLevel.functionBlocks) {
                // didn't navigate away while downloading children
                const mostInterfaces = [];
                for (let i in mostInterfacesJson) {
                    const mostInterfaceJson = mostInterfacesJson[i];
                    const mostInterface = MostInterface.fromJson(mostInterfaceJson);
                    mostInterfaces.push(mostInterface);
                }

                thisApp.setState({
                    mostInterfaces:     mostInterfaces,
                    isLoadingChildren:  false
                });
            }

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

    onMostInterfaceSelected(mostInterface, canUseCachedChildren) {
        const thisApp = this;

        const navigationItems = [];
        for (let i in this.state.navigationItems) {
            const navigationItem = this.state.navigationItems[i];
            navigationItem.setForm(null);
            navigationItems.push(navigationItem);
        }

        const navigationItemConfig = new NavigationItemConfig();
        navigationItemConfig.setTitle(mostInterface.getName());
        navigationItemConfig.setOnClickCallback(function() {
            thisApp.onFunctionBlockSelected(mostInterface, true);
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

        const parentItem = this.state.selectedItem; //Preserve reference to previously selected item.
        thisApp.setState({
            navigationItems:            navigationItems,
            selectedItem:               mostInterface,
            parentItem:                 parentItem,
            shouldShowCreateChildForm:  false,
            createButtonState:          thisApp.CreateButtonState.normal,
            currentNavigationLevel:     thisApp.NavigationLevel.mostInterfaces,
            isLoadingChildren:          !canUseCachedChildren
        });

        // TODO: getFunctionsForMostInterfaceId should use the following as a callback function.
        if (thisApp.state.currentNavigationLevel == thisApp.NavigationLevel.mostInterfaces) {
            // didn't navigate away while downloading children
            const mostFunctions = [];
            /*
            for (let i in mostFunctions) {
                const mostFunctionJson = mostFunctions[i];
                const mostFunction = MostFunction.fromJson(mostFunctionJson);
                mostFunctions.push(mostFunction);
            }
            */

            thisApp.setState({
                mostFunctions:      mostFunctions,
                isLoadingChildren:  false
            })
        }
    }

    onDeleteMostInterface(mostInterface) {
        const thisApp = this;

        const functionBlockId = this.state.selectedItem.getId();
        const mostInterfaceId = mostInterface.getId();

        deleteMostInterface(functionBlockId, mostInterfaceId, function (success) {
            if (success) {
                const newMostInterfaces = [];
                const existingMostInterfaces = thisApp.state.mostInterfaces;
                for (let i in existingMostInterfaces) {
                    const existingMostInterface = existingMostInterfaces[i];
                    if (existingMostInterface.getId() != mostInterface.getId()) {
                        newMostInterfaces.push(existingMostInterface);
                    }
                }
                thisApp.setState({
                    mostInterfaces:         newMostInterfaces,
                    currentNavigationLevel: thisApp.NavigationLevel.functionBlocks
                });
            }
        });
    }

    handleSettingsClick() {
        this.setState({
            showSettingsPage: !this.state.showSettingsPage
        });
    }

    setTheme(themeName) {
        const themeCssDirectory = themeName.toLowerCase();
        document.getElementById('core-css').href =              '/css/themes/' + themeCssDirectory + '/core.css';
        document.getElementById('app-css').href =               '/css/themes/' + themeCssDirectory + '/app.css';
        document.getElementById('palette-css').href =           '/css/themes/' + themeCssDirectory + '/palette.css';
        document.getElementById('react-input-field-css').href = '/css/themes/' + themeCssDirectory + '/react/input-field.css';
        document.getElementById('react-toolbar-css').href =     '/css/themes/' + themeCssDirectory + '/react/toolbar.css';

        this.setState({
            theme: themeName
        });
    }

    renderChildItems() {
        const reactComponents = [];
        const NavigationLevel = this.NavigationLevel;
        const currentNavigationLevel = this.state.currentNavigationLevel;

        if (this.state.isLoadingChildren) {
            // return loading icon
            return (
                <i id="loading-children-icon" className="fa fa-3x fa-refresh fa-spin"></i>
            );
        }

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
                    reactComponents.push(<app.MostInterface key={interfaceKey} mostInterface={childItem} onClick={this.onMostInterfaceSelected} onDelete={this.onDeleteMostInterface} />);
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

        const buttonTitle = (this.state.createButtonState == this.CreateButtonState.success) ? "Added" : "Submit";
        const shouldAnimateCreateButton = (this.state.createButtonState == this.CreateButtonState.animate);

        switch (currentNavigationLevel) {
            case NavigationLevel.versions:
                if (shouldShowCreateChildForm) {
                    reactComponents.push(
                        <app.FunctionCatalogForm key="FunctionCatalogForm"
                            shouldShowSaveAnimation={shouldAnimateCreateButton}
                            buttonTitle={buttonTitle}
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
                            shouldShowSaveAnimation={shouldAnimateCreateButton}
                            buttonTitle={buttonTitle}
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
                            shouldShowSaveAnimation={shouldAnimateCreateButton}
                            buttonTitle={buttonTitle}
                            showTitle={true}
                            onSubmit={this.onCreateMostInterface}
                        />
                    );
                }
            break;

            case NavigationLevel.mostInterfaces:
                if (shouldShowCreateChildForm) {
                    // TODO: implement metadata form for creating/saving Functions.
                }
                break;

            default:
                console.log("renderForm: Unimplemented Navigation Level: "+ currentNavigationLevel);
            break;
        }

        return reactComponents;
    }

    renderMainContent() {
        if (this.state.showSettingsPage) {
            return (
                <div id="main-content" className="container">
                    <app.SettingsPage onThemeChange={this.setTheme} currentTheme={this.state.theme}/>
                </div>
            );
        } else {
            return (
                <div id="main-content" className="container">
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

    logout() {
        logout(function (data) {
            if (data.wasSuccess) {
                window.location.replace("/");
            }
        });
    }

    render() {
        const accountName = this.state.account ? this.state.account.name : "";
        return (
            <div>
                <div id="header" className="secondary-bg accent title-font">
                    Tidy Duck
                    <div id="account-area">
                        {accountName}
                        <a id="logout" href="#" onClick={this.logout}>logout</a>
                        <i id="settings-icon" className="fa fa-cog" onClick={this.handleSettingsClick}/>
                    </div>
                </div>
                {this.renderMainContent()}
            </div>
        );
    }
}

registerClassWithGlobalScope("App", App);

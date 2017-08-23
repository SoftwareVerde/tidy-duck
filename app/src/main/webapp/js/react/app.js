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

        this.roles = {
            release:          "Release",
            development:      "Development",
            types:            "Types"
        };

        this.developmentRoles = {
            mostInterface:    "Interface",
            functionBlock:    "Function Block"
        };

        this.headers = {
          functionCatalog:  "FCAT",
          functionBlock:    "FBLOCK",
          mostInterface:    "INTERFACE",
          mostFunction:     "FUNCTION"
        };

        this.FunctionStereotypes = {
            event:                      "Event",
            readOnlyProperty:           "ReadOnlyProperty",
            readOnlyPropertyWithEvent:  "ReadOnlyPropertyWithEvent",
            propertyWithEvent:          "PropertyWithEvent",
            commandWithAck:             "CommandWithAck",
            requestResponse:            "Request/Response"
        };

        this.state = {
            account:                    null,
            navigationItems:            [],
            parentHistory:              [],
            searchResults:              [],
            lastSearchResultTimestamp:  0,
            functionCatalogs:           [],
            functionBlocks:             [],
            mostInterfaces:             [],
            mostFunctions:              [],
            mostTypes:                  [],
            primitiveTypes:             [],
            mostUnits:                  [],
            mostFunctionStereotypes:    [],
            activeRole:                 this.roles.release,
            activeSubRole:              null,
            selectedItem:               null,
            parentItem:                 null,
            proposedItem:               null,
            currentNavigationLevel:     this.NavigationLevel.versions,
            shouldShowToolbar:          true,
            shouldShowCreateChildForm:  false,
            createButtonState:          this.CreateButtonState.normal,
            selectedFunctionStereotype: null,
            shouldShowSearchChildForm:  false,
            isLoadingChildren:          true,
            isLoadingSearchResults:     false,
            isLoadingMostTypes:         true,
            isLoadingPrimitiveTypes:    true,
            isLoadingUnits:             true,
            filterString:               null,
            shouldShowFilteredResults:  false,
            shouldShowEditForm:         false
        };

        this.onRootNavigationItemClicked = this.onRootNavigationItemClicked.bind(this);
        this.renderChildItems = this.renderChildItems.bind(this);
        this.renderMainContent = this.renderMainContent.bind(this);
        this.renderRoleToggle = this.renderRoleToggle.bind(this);
        this.renderSubRoleToggle = this.renderSubRoleToggle.bind(this);
        this.renderFilterBar = this.renderFilterBar.bind(this);

        this.getCurrentAccountAuthor = this.getCurrentAccountAuthor.bind(this);
        this.getCurrentAccountCompany = this.getCurrentAccountCompany.bind(this);
        this.getFunctionCatalogsForCurrentVersion = this.getFunctionCatalogsForCurrentVersion.bind(this);

        this.onFunctionCatalogSelected = this.onFunctionCatalogSelected.bind(this);
        this.onCreateFunctionCatalog = this.onCreateFunctionCatalog.bind(this);
        this.onUpdateFunctionCatalog = this.onUpdateFunctionCatalog.bind(this);
        this.onDeleteFunctionCatalog = this.onDeleteFunctionCatalog.bind(this);
        this.onReleaseFunctionCatalog = this.onReleaseFunctionCatalog.bind(this);

        this.onFunctionBlockSelected = this.onFunctionBlockSelected.bind(this);
        this.onCreateFunctionBlock = this.onCreateFunctionBlock.bind(this);
        this.onUpdateFunctionBlock = this.onUpdateFunctionBlock.bind(this);
        this.onSearchFunctionBlocks = this.onSearchFunctionBlocks.bind(this);
        this.onFilterFunctionBlocks = this.onFilterFunctionBlocks.bind(this);
        this.onAssociateFunctionBlockWithFunctionCatalog = this.onAssociateFunctionBlockWithFunctionCatalog.bind(this);
        this.onDeleteFunctionBlock = this.onDeleteFunctionBlock.bind(this);
        this.disassociateFunctionBlockFromFunctionCatalog = this.disassociateFunctionBlockFromFunctionCatalog.bind(this);
        this.disassociateFunctionBlockFromAllFunctionCatalogs = this.disassociateFunctionBlockFromAllFunctionCatalogs.bind(this);
        this.deleteFunctionBlockFromDatabase = this.deleteFunctionBlockFromDatabase.bind(this);

        this.onMostInterfaceSelected = this.onMostInterfaceSelected.bind(this);
        this.onCreateMostInterface = this.onCreateMostInterface.bind(this);
        this.onUpdateMostInterface = this.onUpdateMostInterface.bind(this);
        this.onSearchMostInterfaces = this.onSearchMostInterfaces.bind(this);
        this.onFilterMostInterfaces = this.onFilterMostInterfaces.bind(this);
        this.onAssociateMostInterfaceWithFunctionBlock = this.onAssociateMostInterfaceWithFunctionBlock.bind(this);
        this.onDeleteMostInterface = this.onDeleteMostInterface.bind(this);
        this.disassociateMostInterfaceFromFunctionBlock = this.disassociateMostInterfaceFromFunctionBlock.bind(this);
        this.disassociateMostInterfaceFromAllFunctionBlocks = this.disassociateMostInterfaceFromAllFunctionBlocks.bind(this);
        this.deleteMostInterfaceFromDatabase = this.deleteMostInterfaceFromDatabase.bind(this);

        this.onMostFunctionSelected = this.onMostFunctionSelected.bind(this);
        this.onCreateMostFunction = this.onCreateMostFunction.bind(this);
        this.onUpdateMostFunction = this.onUpdateMostFunction.bind(this);
        this.onDeleteMostFunction = this.onDeleteMostFunction.bind(this);

        this.getChildItemsFromVersions = this.getChildItemsFromVersions.bind(this);
        this.onChildItemVersionChanged = this.onChildItemVersionChanged.bind(this);
        this.updateMostTypes = this.updateMostTypes.bind(this);
        this.onTypeCreated = this.onTypeCreated.bind(this);
        this.updateMostFunctionStereotypes = this.updateMostFunctionStereotypes.bind(this);

        this.handleFunctionStereotypeClick = this.handleFunctionStereotypeClick.bind(this);
        this.handleSettingsClick = this.handleSettingsClick.bind(this);
        this.handleRoleClick = this.handleRoleClick.bind(this);
        this.onThemeChange = this.onThemeChange.bind(this);
        this.setTheme = this.setTheme.bind(this);

        this.logout = this.logout.bind(this);

        const thisApp = this;

        const account = downloadAccount(function (data) {
            if (data.wasSuccess) {
                thisApp.setTheme(data.account.theme);
                thisApp.setState({
                    account:    data.account
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

        const functionCatalogJson = FunctionCatalog.toJson(functionCatalog);

        this.setState({
            createButtonState:  this.CreateButtonState.animate,
            proposedItem:       functionCatalog
        });

        insertFunctionCatalog(functionCatalogJson, function(functionCatalogId) {
            if (! (functionCatalogId > 0)) {
                console.error("Unable to create function catalog.");
                thisApp.setState({
                    createButtonState: thisApp.CreateButtonState.normal
                });
                return;
            }

            functionCatalog.setId(functionCatalogId);
            functionCatalog.setAuthor(thisApp.getCurrentAccountAuthor());
            functionCatalog.setCompany(thisApp.getCurrentAccountCompany());

            const versions = [ FunctionCatalog.toJson(functionCatalog) ];
            functionCatalog.setVersionsJson(versions);

            const functionCatalogs = thisApp.state.functionCatalogs.concat(functionCatalog);

            thisApp.setState({
                createButtonState:          thisApp.CreateButtonState.success,
                functionCatalogs:           functionCatalogs,
                currentNavigationLevel:     thisApp.NavigationLevel.versions,
                proposedItem:               null,
                shouldShowCreateChildForm:  false
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

        const functionCatalogJson = FunctionCatalog.toJson(functionCatalog);
        const functionCatalogId = functionCatalog.getId();

        //Update function catalog form to display saving animation.
        let navigationItems = [];
        navigationItems = navigationItems.concat(thisApp.state.navigationItems);
        let navigationItem = navigationItems.pop();
        navigationItem.setForm(
            <app.FunctionCatalogForm
                showTitle={false}
                shouldShowSaveAnimation={true}
                onSubmit={this.onUpdateFunctionCatalog}
                functionCatalog={functionCatalog}
                buttonTitle="Save"
                defaultButtonTitle="Save"
            />
        );
        navigationItems.push(navigationItem);

        this.setState({
           navigationItems: navigationItems
        });

        updateFunctionCatalog(functionCatalogId, functionCatalogJson, false, function(wasSuccess, newFunctionCatalogId) {
            if (wasSuccess) {
                let functionCatalogs = thisApp.state.functionCatalogs.filter(function(value) {
                    return value.getId() != functionCatalogId;
                });

                // If returned ID is different, a new unreleased version was created.
                if (newFunctionCatalogId != functionCatalogId) {
                    functionCatalog.setIsReleased(false);
                }

                functionCatalog.setId(newFunctionCatalogId);
                functionCatalogs.push(functionCatalog);

                //Update final navigation item to reflect any name changes.
                let navigationItems = [];
                navigationItems = navigationItems.concat(thisApp.state.navigationItems);
                let navigationItem = navigationItems.pop();
                navigationItem.setTitle(functionCatalog.getName());
                navigationItem.setIsReleased(functionCatalog.isReleased());
                navigationItem.setHeader(thisApp.headers.functionCatalog);
                navigationItem.setOnClickCallback(function() {
                    thisApp.onFunctionCatalogSelected(functionCatalog, true, false);
                });

                //Update form to show changes were saved.
                navigationItem.setForm(
                    <app.FunctionCatalogForm
                        showTitle={false}
                        shouldShowSaveAnimation={false}
                        onSubmit={thisApp.onUpdateFunctionCatalog}
                        functionCatalog={functionCatalog}
                        buttonTitle="Changes Saved"
                        defaultButtonTitle="Save"
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

    onReleaseFunctionCatalog(functionCatalog) {
        const thisApp = this;
        const functionBlocks = this.state.functionBlocks;
        const functionCatalogJson = FunctionCatalog.toJson(functionCatalog);
        const functionCatalogId = functionCatalog.getId();

        if (confirm("Are you sure you want to release this function catalog?")) {
            updateFunctionCatalog(functionCatalogId, functionCatalogJson, true, function(wasSuccess, newFunctionCatalogId) {
                if (wasSuccess) {
                    // Set currently selected function catalog to released.
                    functionCatalog.setIsReleased(true);

                    // Set currently selected function catalog's function blocks to released.
                    for (let i in functionBlocks) {
                        functionBlocks[i].setIsReleased(true);
                    }

                    alert("Function Catalog " + functionCatalogId + " successfully released!")

                    thisApp.setState({
                        functionBlocks:       functionBlocks,
                        selectedItem:           functionCatalog,
                        currentNavigationLevel: thisApp.NavigationLevel.functionCatalogs
                    });
                }
            });
        }
    }

    onCreateFunctionBlock(functionBlock) {
        const thisApp = this;

        const functionCatalog = this.state.selectedItem;

        const functionCatalogId = functionCatalog ? functionCatalog.getId() : null;
        const functionBlockJson = FunctionBlock.toJson(functionBlock);

        this.setState({
            createButtonState:  this.CreateButtonState.animate,
            proposedItem:       functionBlock,
        });

        insertFunctionBlock(functionCatalogId, functionBlockJson, function(functionBlockId) {
            if (! (functionBlockId > 0)) {
                console.error("Unable to create function block.");
                thisApp.setState({
                    createButtonState:  thisApp.CreateButtonState.normal
                });
                return;
            }

            functionBlock.setId(functionBlockId);
            functionBlock.setAuthor(thisApp.getCurrentAccountAuthor());
            functionBlock.setCompany(thisApp.getCurrentAccountCompany());

            const versions = [ FunctionBlock.toJson(functionBlock) ];
            functionBlock.setVersionsJson(versions);

            const functionBlocks = thisApp.state.functionBlocks.concat(functionBlock);

            thisApp.setState({
                createButtonState:          thisApp.CreateButtonState.normal,
                functionBlocks:             functionBlocks,
                currentNavigationLevel:     thisApp.NavigationLevel.functionCatalogs,
                shouldShowCreateChildForm:  false,
                proposedItem:               null
            });
        });
    }

    onUpdateFunctionBlock(functionBlock) {
        const thisApp = this;

        let functionCatalogId = this.state.parentItem ? this.state.parentItem.getId() : null;
        // Need to disregard parentItem id if in development mode and navigation level corresponds with development role.
        if (functionCatalogId) {
            if (this.state.activeSubRole == this.developmentRoles.functionBlock) {
                functionCatalogId = null;
            }
        }
        const functionBlockJson = FunctionBlock.toJson(functionBlock);
        const functionBlockId = functionBlock.getId();

        //Update function block form to display saving animation.
        let navigationItems = [];
        navigationItems = navigationItems.concat(thisApp.state.navigationItems);
        let navigationItem = navigationItems.pop();
        navigationItem.setForm(
            <app.FunctionBlockForm
                showTitle={false}
                shouldShowSaveAnimation={true}
                onSubmit={this.onUpdateFunctionBlock}
                functionBlock={functionBlock}
                buttonTitle="Save"
                defaultButtonTitle="Save"
            />
        );
        navigationItems.push(navigationItem);

        // If not in release mode, show save animation on metadata form.
        const createButtonState = this.state.activeRole !== this.roles.release ?
            this.CreateButtonState.animate : this.CreateButtonState.normal;

        this.setState({
            navigationItems: navigationItems,
            selectedItem:   functionBlock,
            createButtonState: createButtonState
        });

        updateFunctionBlock(functionCatalogId, functionBlockId, functionBlockJson, function(wasSuccess, newFunctionBlockId) {
            if (wasSuccess) {
                let functionBlocks = thisApp.state.functionBlocks.filter(function(value) {
                    return value.getId() != functionBlockId;
                });

                // If returned ID is different, a new unreleased version was created.
                if (newFunctionBlockId != functionBlockId) {
                    functionBlock.setIsReleased(false);
                }

                functionBlock.setId(newFunctionBlockId);
                functionBlocks.push(functionBlock);

                //Update final navigation item to reflect any name changes.
                let navigationItems = [];
                navigationItems = navigationItems.concat(thisApp.state.navigationItems);
                let navigationItem = navigationItems.pop();
                navigationItem.setTitle(functionBlock.getName());
                navigationItem.setIsReleased(functionBlock.isReleased());
                navigationItem.setHeader(thisApp.headers.functionBlock);
                navigationItem.setOnClickCallback(function() {
                    thisApp.onFunctionBlockSelected(functionBlock, true, false);
                });

                //Update form to show changes were saved.
                navigationItem.setForm(
                    <app.FunctionBlockForm
                        showTitle={false}
                        shouldShowSaveAnimation={false}
                        onSubmit={thisApp.onUpdateFunctionBlock}
                        functionBlock={functionBlock}
                        buttonTitle="Changes Saved"
                        defaultButtonTitle="Save"
                    />
                );
                navigationItems.push(navigationItem);

                thisApp.setState({
                    functionBlocks:             functionBlocks,
                    selectedItem:               functionBlock,
                    navigationItems:            navigationItems,
                    currentNavigationLevel:     thisApp.NavigationLevel.functionBlocks,
                    createButtonState:          thisApp.CreateButtonState.success

                });
            }
        });
    }

    onCreateMostInterface(mostInterface) {
        const thisApp = this;

        const functionBlock = this.state.selectedItem;

        const functionBlockId = functionBlock ? functionBlock.getId() : null;
        const mostInterfaceJson = MostInterface.toJson(mostInterface);

        this.setState({
            createButtonState:  this.CreateButtonState.animate,
            proposedItem:       mostInterface
        });

        insertMostInterface(functionBlockId, mostInterfaceJson, function(mostInterfaceId) {
            if (! (mostInterfaceId > 0)) {
                console.error("Unable to create interface.");
                thisApp.setState({
                    createButtonState:  thisApp.CreateButtonState.normal,
                });
                return;
            }

            mostInterface.setId(mostInterfaceId);
            const mostInterfaces = thisApp.state.mostInterfaces.concat(mostInterface);

            const versions = [ MostInterface.toJson(mostInterface) ];
            mostInterface.setVersionsJson(versions);

            thisApp.setState({
                createButtonState:          thisApp.CreateButtonState.success,
                mostInterfaces:             mostInterfaces,
                currentNavigationLevel:     thisApp.NavigationLevel.functionBlocks,
                proposedItem:               null,
                shouldShowCreateChildForm:  false
            });
        });
    }

    onUpdateMostInterface(mostInterface, getNewFunctions) {
        const thisApp = this;

        let functionBlockId = this.state.parentItem ? this.state.parentItem.getId() : null;
        // Need to disregard parentItem id if in development mode and navigation level corresponds with development role.
        if (functionBlockId) {
            if (this.state.activeSubRole == this.developmentRoles.mostInterface) {
                functionBlockId = null;
            }
        }
        const mostInterfaceJson = MostInterface.toJson(mostInterface);
        const mostInterfaceId = mostInterface.getId();

        //Update function block form to display saving animation.
        const navigationItems = thisApp.state.navigationItems;
        const navigationItem = navigationItems.pop();
        navigationItem.setForm(
            <app.MostInterfaceForm
                showTitle={false}
                shouldShowSaveAnimation={true}
                onSubmit={this.onUpdateMostInterface}
                mostInterface={mostInterface}
                buttonTitle="Save"
                defaultButtonTitle="Save"
            />
        );
        navigationItems.push(navigationItem);

        // If not in release mode, show save animation on metadata form.
        const createButtonState = this.state.activeRole !== this.roles.release ?
            this.CreateButtonState.animate : this.CreateButtonState.normal;

        this.setState({
            navigationItems: navigationItems,
            selectedItem:   mostInterface,
            createButtonState: createButtonState
        });
        updateMostInterface(functionBlockId, mostInterfaceId, mostInterfaceJson, function(wasSuccess, newMostInterfaceId) {
            if (wasSuccess) {
                var mostInterfaces = thisApp.state.mostInterfaces.filter(function(value) {
                    return value.getId() != mostInterfaceId;
                });

                // If returned ID is different, a new unreleased version was created.
                if (newMostInterfaceId != mostInterfaceId) {
                    mostInterface.setIsReleased(false);
                }

                mostInterface.setId(newMostInterfaceId);
                mostInterfaces.push(mostInterface);

                //Update final navigation item to reflect any name changes.
                var navigationItems = [];
                navigationItems = navigationItems.concat(thisApp.state.navigationItems);
                var navigationItem = navigationItems.pop();
                navigationItem.setTitle(mostInterface.getName());
                navigationItem.setIsReleased(mostInterface.isReleased());
                navigationItem.setHeader(thisApp.headers.mostInterface);
                navigationItem.setOnClickCallback(function() {
                    thisApp.onMostInterfaceSelected(mostInterface, true, false);
                });

                //Update form to show changes were saved.
                navigationItem.setForm(
                    <app.MostInterfaceForm
                        showTitle={false}
                        shouldShowSaveAnimation={false}
                        onSubmit={thisApp.onUpdateMostInterface}
                        mostInterface={mostInterface}
                        buttonTitle="Changes Saved"
                        defaultButtonTitle="Save"
                    />
                );
                navigationItems.push(navigationItem);

                thisApp.setState({
                    mostInterfaces:         mostInterfaces,
                    selectedItem:           mostInterface,
                    navigationItems:        navigationItems,
                    currentNavigationLevel: thisApp.NavigationLevel.mostInterfaces,
                    createButtonState:      thisApp.CreateButtonState.success
                });
                // Need to get new functions if forking a released interface.
                if (getNewFunctions) {
                    thisApp.onMostInterfaceSelected(mostInterface);
                }
            }
        });
    }

    onCreateMostFunction(mostFunction) {
        const thisApp = this;

        const mostInterface = this.state.selectedItem;

        const mostInterfaceId = mostInterface.getId();
        const mostFunctionJson = MostFunction.toJson(mostFunction);

        this.setState({
            createButtonState:  this.CreateButtonState.animate,
            proposedItem:       mostFunction
        });

        insertMostFunction(mostInterfaceId, mostFunctionJson, function(mostFunctionId) {
            if (! (mostFunctionId > 0)) {
                console.error("Unable to create function.");
                thisApp.setState({
                    createButtonState:  thisApp.CreateButtonState.normal
                });
                return;
            }

            mostFunction.setId(mostFunctionId);
            mostFunction.setAuthor(thisApp.getCurrentAccountAuthor());
            mostFunction.setCompany(thisApp.getCurrentAccountCompany());

            const mostFunctions = thisApp.state.mostFunctions.concat(mostFunction);

            thisApp.setState({
                createButtonState:          thisApp.CreateButtonState.success,
                mostFunctions:              mostFunctions,
                currentNavigationLevel:     thisApp.NavigationLevel.mostInterfaces,
                proposedItem:               null,
                shouldShowCreateChildForm:  false
            });
        });
    }

    onUpdateMostFunction(mostFunction) {
        const thisApp = this;

        const mostInterfaceId = this.state.parentItem.getId();
        const mostFunctionJson = MostFunction.toJson(mostFunction);
        const mostFunctionId = mostFunction.getId();

        // TODO: Update function metadata form to display saving animation.

        thisApp.setState({
            createButtonState:  this.CreateButtonState.animate,
            selectedItem:       mostFunction
        });

        updateMostFunction(mostInterfaceId, mostFunctionId, mostFunctionJson, function(wasSuccess) {
            if (wasSuccess) {
                const mostFunctions = thisApp.state.mostFunctions.filter(function(value) {
                    return value.getId() != mostFunctionId;
                });
                mostFunctions.push(mostFunction);

                //Update final navigation item to reflect any name changes.
                const navigationItems = thisApp.state.navigationItems;
                const navigationItem = navigationItems.pop();
                navigationItem.setTitle(mostFunction.getName());
                navigationItem.setIsReleased(thisApp.state.parentItem.isReleased());
                navigationItem.setHeader(thisApp.headers.mostFunction);

                //Update form to show changes were saved.
                navigationItem.setForm(null);
                navigationItem.setOnClickCallback(function() {
                    thisApp.onMostFunctionSelected(mostFunction, true);
                });
                navigationItems.push(navigationItem);

                thisApp.setState({
                    mostFunctions:          mostFunctions,
                    selectedItem:           mostFunction,
                    navigationItems:        navigationItems,
                    currentNavigationLevel: thisApp.NavigationLevel.mostFunctions,
                    createButtonState:      thisApp.CreateButtonState.success
                });
            } else {
                console.error("Unable to update Function.");
                thisApp.setState({
                    createButtonState:  thisApp.CreateButtonState.normal,
                });
                return;
            }
        });
    }

    onRootNavigationItemClicked() {
        const thisApp = this;
        const navigationItems = [];

        this.setState({
            navigationItems:            navigationItems,
            searchResults:              [],
            selectedItem:               null,
            parentItem:                 null,
            shouldShowToolbar:          true,
            shouldShowCreateChildForm:  false,
            shouldShowSearchChildForm:  false,
            createButtonState:          thisApp.CreateButtonState.normal,
            currentNavigationLevel:     thisApp.NavigationLevel.versions,
            isLoadingChildren:          false // can default on what we already have
        });

        this.getFunctionCatalogsForCurrentVersion(function (functionCatalogs) {
            if (thisApp.state.currentNavigationLevel == thisApp.NavigationLevel.versions) {
                // didn't navigate away while downloading children
                thisApp.setState({
                    functionCatalogs:       functionCatalogs,
                    functionBlocks:         [],
                    mostInterfaces:         [],
                    mostFunctions:          [],
                    isLoadingChildren:      false
                });
            }
        });
    }

    getFunctionCatalogsForCurrentVersion(callbackFunction) {
        const thisApp = this;
        getFunctionCatalogs(function(functionCatalogsJson) {
            const functionCatalogs = thisApp.getChildItemsFromVersions(functionCatalogsJson, FunctionCatalog.fromJson);
            callbackFunction(functionCatalogs);
        });
    }

    onFunctionCatalogSelected(functionCatalog, canUseCachedChildren) {
        const thisApp = this;
        const navigationItems = [];
        const parentHistory = [];

        const navigationItemConfig = new NavigationItemConfig();
        navigationItemConfig.setTitle(functionCatalog.getName());
        navigationItemConfig.setIsReleased(functionCatalog.isReleased());
        navigationItemConfig.setHeader(thisApp.headers.functionCatalog);
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
            thisApp.onFunctionCatalogSelected(functionCatalog, true, false);
        });

        navigationItemConfig.setForm(
            <app.FunctionCatalogForm 
                showTitle={false}
                onSubmit={this.onUpdateFunctionCatalog}
                functionCatalog={functionCatalog}
                buttonTitle="Save"
                defaultButtonTitle="Save"
            />
        );
        navigationItems.push(navigationItemConfig);

        // Preserve history of parent items.
        const parentHistoryItem = {
              id: "functionCatalog" + functionCatalog.getId(),
              item: functionCatalog,
        };
        parentHistory.push(parentHistoryItem);

        console.log(parentHistory);

        thisApp.setState({
            navigationItems:            navigationItems,
            parentHistory:              parentHistory,
            searchResults:              [],
            selectedItem:               functionCatalog,
            parentItem:                 null,
            proposedItem:               null,
            functionBlocks:             canUseCachedChildren ? this.state.functionBlocks : [],
            shouldShowCreateChildForm:  false,
            shouldShowSearchChildForm:  false,
            shouldShowEditForm:         false,
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
                    mostInterfaces:     [],
                    isLoadingChildren:  false
                });
            }
        })
    }

    onDeleteFunctionCatalog(functionCatalog, callbackFunction) {
        if (functionCatalog.isReleased()) {
            alert("Unable to delete Function Catalog. Released Function Catalogs cannot be deleted.");
            callbackFunction();
        }
        else {
            const thisApp = this;
            const functionCatalogId = functionCatalog.getId();

            deleteFunctionCatalog(functionCatalogId, function (success, errorMessage) {
                if (success) {
                    const newFunctionCatalogs = [];
                    const existingFunctionCatalogs = thisApp.state.functionCatalogs;
                    for (let i in existingFunctionCatalogs) {
                        const existingFunctionCatalog = existingFunctionCatalogs[i];
                        const existingFunctionCatalogId = existingFunctionCatalog.getId();
                        if (existingFunctionCatalogId != functionCatalog.getId()) {
                            newFunctionCatalogs.push(existingFunctionCatalog);
                        }
                        else {
                            // Remove deleted version from child item. Don't push to new array if no versions remain.
                            const existingVersionsJson = existingFunctionCatalog.getVersionsJson();
                            if (existingVersionsJson.length > 1) {
                                // Find newest released version to be displayed on screen.
                                let displayedVersionId = existingVersionsJson[0].id;
                                let displayedVersionJson = existingVersionsJson[0];

                                for (let j in existingVersionsJson) {
                                    const existingVersionJson = existingVersionsJson[j];
                                    if (existingFunctionCatalogId == existingVersionJson.id) {
                                        delete existingVersionsJson[j];
                                    }
                                    else {
                                        if (existingVersionJson.isReleased) {
                                            if (existingVersionJson.id > displayedVersionId) {
                                                displayedVersionId = existingVersionJson.id;
                                                displayedVersionJson = existingVersionJson;
                                            }
                                        }
                                    }
                                }
                                const newFunctionCatalog = FunctionCatalog.fromJson(displayedVersionJson);
                                newFunctionCatalog.setVersionsJson(existingVersionsJson);
                                newFunctionCatalogs.push(newFunctionCatalog);
                            }
                        }
                    }
                    thisApp.setState({
                        functionCatalogs:       newFunctionCatalogs,
                        currentNavigationLevel: thisApp.NavigationLevel.versions
                    });
                } else {
                    alert("Request to delete Function Catalog failed: " + errorMessage);
                    callbackFunction();
                }
            });
        }
    }

    onDeleteFunctionCatalogWithConfirmPrompt(functionCatalog, callbackFunction) {
        //Check if this function catalog contains any function blocks that are not referenced elsewhere.
        const thisApp = this;
        const functionCatalogId = functionCatalog.getId();
        const orphanedFunctionBlocks = [];

        getFunctionBlocksForFunctionCatalogId(functionCatalogId, function(functionBlocksJson) {
            if (functionBlocksJson.length > 0) {
                let functionBlockCounter = 0;
                for (let i in functionBlocksJson) {
                    const functionBlock = functionBlocksJson[i];
                    listFunctionCatalogsContainingFunctionBlock(functionBlock.id, function(data) {
                        // TODO: add else statement if data request failed. User should attempt the delete again to be safe.
                        if (data.wasSuccess) {
                            const functionCatalogIds = data.functionCatalogIds;

                            if (functionCatalogIds.length < 2) {
                                orphanedFunctionBlocks.push(functionBlock);
                            }
                            functionBlockCounter++;

                            if (functionBlockCounter == functionBlocksJson.length) {
                                let confirmPromptText = "This action will disassociate the included function blocks from this function catalog. The included function blocks are referenced elsewhere and will not be deleted. Are you sure you want to delete this function catalog?"

                                if (orphanedFunctionBlocks.length > 0) {
                                    confirmPromptText = "This action will delete the following function blocks, because they are not referenced in any other function catalog:\n"

                                    for (let i in orphanedFunctionBlocks) {
                                        confirmPromptText = confirmPromptText.concat("\n* " + orphanedFunctionBlocks[i].name);
                                    }

                                    confirmPromptText = confirmPromptText.concat("\n\nAre you sure you want to delete this function catalog?");
                                }

                                if (confirm(confirmPromptText)) {
                                    deleteFunctionCatalog(functionCatalogId, function (success, errorMessage) {
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
                                        } else {alert("Request to delete Function Catalog failed: " + errorMessage);}
                                    });
                                }
                            }
                        }
                        //Let function catalog menu component know that action is completed.
                        callbackFunction();
                    });
                }
            } else {
                deleteFunctionCatalog(functionCatalogId, function (success, errorMessage) {
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
                    } else {alert("Request to delete Function Catalog failed: " + errorMessage);}
                });
                //Let function catalog menu component know that action is completed.
                callbackFunction();
            }
        });
    }

    onFunctionBlockSelected(functionBlock, canUseCachedChildren) {
        const thisApp = this;

        const navigationItems = [];
        const parentHistory = this.state.parentHistory;

        if (this.state.activeRole === this.roles.release) {
            const navigationItem = this.state.navigationItems[0];
            navigationItem.setForm(null);
            navigationItems.push(navigationItem);
        }

        const navigationItemConfig = new NavigationItemConfig();
        navigationItemConfig.setTitle(functionBlock.getName());
        navigationItemConfig.setIsReleased(functionBlock.isReleased());
        navigationItemConfig.setHeader(thisApp.headers.functionBlock);
        navigationItemConfig.setOnClickCallback(function() {
            thisApp.onFunctionBlockSelected(functionBlock, true);
        });
        navigationItemConfig.setForm(
            <app.FunctionBlockForm key="FunctionBlockForm"
                showTitle={false}
                onSubmit={this.onUpdateFunctionBlock}
                functionBlock={functionBlock}
                buttonTitle="Save"
                defaultButtonTitle="Save"
            />
        );
        navigationItems.push(navigationItemConfig);

        // Preserve reference to parent items.
        const parentHistoryItem = {
            id: "functionBlock" + functionBlock.getId(),
            item: functionBlock,
        };

        let parentItem = null;
        let newParentHistory = [];
        for (let i in parentHistory) {
            const existingSelectionHistoryItem = parentHistory[i];
            if (existingSelectionHistoryItem["id"] === parentHistoryItem["id"]) {
                newParentHistory = parentHistory.slice(0, Number(i));
                break;
            }
            newParentHistory.push(existingSelectionHistoryItem);
            parentItem = existingSelectionHistoryItem["item"];
        }
        newParentHistory.push(parentHistoryItem);

        thisApp.setState({
            navigationItems:            navigationItems,
            parentHistory:              newParentHistory,
            searchResults:              [],
            selectedItem:               functionBlock,
            parentItem:                 parentItem,
            proposedItem:               null,
            mostInterfaces:             canUseCachedChildren ? this.state.mostInterfaces : [],
            shouldShowCreateChildForm:  false,
            shouldShowSearchChildForm:  false,
            shouldShowEditForm:         false,
            createButtonState:          thisApp.CreateButtonState.normal,
            currentNavigationLevel:     thisApp.NavigationLevel.functionBlocks,
            isLoadingChildren:          !canUseCachedChildren,
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
                    mostInterfaces:             mostInterfaces,
                    mostFunctions:              [],
                    isLoadingChildren:          false,
                    shouldShowFilteredResults:  false,
                    filterString:               ""
                });
            }
        });
    }

    onSearchFunctionBlocks(searchString) {
        const requestTime = (new Date()).getTime();

        if (searchString.length > 0) {
            const thisApp = this;
            this.setState({isLoadingSearchResults: true});

            getFunctionBlocksMatchingSearchString(searchString, function (functionBlocksJson) {
                if (thisApp.state.currentNavigationLevel == thisApp.NavigationLevel.functionCatalogs) {
                    if (thisApp.state.lastSearchResultTimestamp > requestTime) {
                        // old results, discard
                        return;
                    }
                    const proposedFunctionBlocks = thisApp.getChildItemsFromVersions(functionBlocksJson, FunctionBlock.fromJson);
                    const functionBlocks = [];

                    const existingFunctionBlocks = thisApp.state.functionBlocks;
                    for (let i in proposedFunctionBlocks) {
                        const functionBlock = proposedFunctionBlocks[i];

                        // Filter any existing child elements that appear in the search results.
                        let pushToSearchResults = true;
                        for (let m in existingFunctionBlocks) {
                            if (existingFunctionBlocks[m].getBaseVersionId() === functionBlock.getBaseVersionId()) {
                                pushToSearchResults = false;
                                break;
                            }
                        }
                        if (pushToSearchResults) {
                            functionBlocks.push(functionBlock);
                        }
                    }

                    thisApp.setState({
                        searchResults: functionBlocks,
                        lastSearchResultTimestamp: requestTime,
                        isLoadingSearchResults: false
                    });
                }
            });
        } else {
            this.setState({
                searchResults: [],
                lastSearchResultTimestamp: requestTime,
                isLoadingSearchResults: false
            });
        }
    }

    onFilterFunctionBlocks(filterString) {
        const requestTime = (new Date()).getTime();

        if (filterString.length > 0) {
            const thisApp = this;
            this.setState({
                isLoadingChildren: true,
                filterString:      filterString
            });

            getFunctionBlocksMatchingSearchString(filterString, function(functionBlocksJson) {
                if (thisApp.state.currentNavigationLevel == thisApp.NavigationLevel.functionCatalogs) {
                    if (thisApp.state.lastSearchResultTimestamp > requestTime) {
                        // old results, discard
                        return;
                    }

                    const functionBlocks = thisApp.getChildItemsFromVersions(functionBlocksJson, FunctionBlock.fromJson);

                    thisApp.setState({
                        searchResults:              functionBlocks,
                        shouldShowFilteredResults:  true,
                        lastSearchResultTimestamp:  requestTime,
                        isLoadingChildren:          false,
                        isLoadingSearchResults:     false
                    });
                }
            });
        } else {
            // TODO: remove isLoadingChildren change and indicate that no results were found in child display area.
            this.setState({
                searchResults:                  [],
                lastSearchResultTimestamp:      requestTime,
                isLoadingChildren:              false,
                isLoadingSearchResults:         false,
                shouldShowFilteredResults:      false,
                filterString:                   ""
            });
        }

    }

    onAssociateFunctionBlockWithFunctionCatalog(functionBlock, functionCatalog) {
        const thisApp = this;
        associateFunctionBlockWithFunctionCatalog(functionCatalog.getId(), functionBlock.getId(), function (success, errorMessage) {
            if (success) {
                // remove most function block from search results
                let searchResults = thisApp.state.searchResults;
                const newSearchResults = [];
                for (let index in searchResults) {
                    const searchResult = searchResults[index];
                    if (searchResult.getId() != functionBlock.getId()) {
                        newSearchResults.push(searchResult);
                    }
                }

                // add function block to children
                const functionBlocks = thisApp.state.functionBlocks.concat(functionBlock);

                thisApp.setState({
                    searchResults: newSearchResults,
                    functionBlocks: functionBlocks
                });
            } else {alert("Request to associate Function Block failed: " + errorMessage);}
        });
    }

    onDeleteFunctionBlock(functionBlock, callbackFunction) {
        const thisApp = this;
        const selectedItem = this.state.selectedItem;

        // If this item has a containing parent, simply disassociate it.
        if (selectedItem) {
            if (selectedItem.isReleased()) {
                alert("Unable to delete Function Block. Currently selected Function Catalog is released.");
                callbackFunction();
            }
            else {thisApp.disassociateFunctionBlockFromFunctionCatalog(functionBlock, callbackFunction);}
        }
        else {
            listFunctionCatalogsContainingFunctionBlock(functionBlock.getId(), function (data) {
               if (data.wasSuccess) {
                   if (data.functionCatalogIds.length > 0) {
                       thisApp.disassociateFunctionBlockFromAllFunctionCatalogs(functionBlock, callbackFunction);
                   }
                   else {
                       thisApp.deleteFunctionBlockFromDatabase(functionBlock, callbackFunction);
                   }
               }
            });
        }
    }

    disassociateFunctionBlockFromFunctionCatalog(functionBlock, callbackFunction) {
        const thisApp = this;

        const functionCatalogId = this.state.selectedItem.getId();
        const functionBlockId = functionBlock.getId();

        deleteFunctionBlock(functionCatalogId, functionBlockId, function (success, errorMessage) {
            if (success) {
                const newFunctionBlocks = [];
                const existingFunctionBlocks = thisApp.state.functionBlocks;
                for (let i in existingFunctionBlocks) {
                    const existingFunctionBlock = existingFunctionBlocks[i];
                    if (existingFunctionBlock.getId() != functionBlockId) {
                        newFunctionBlocks.push(existingFunctionBlock);
                    }
                }
                thisApp.setState({
                    functionBlocks:         newFunctionBlocks,
                    currentNavigationLevel: thisApp.NavigationLevel.functionCatalogs
                });
            } else {
                alert("Request to disassociate Function Block failed: " + errorMessage);
            }
        });

        // let component know action is complete
        callbackFunction();
    }

    disassociateFunctionBlockFromAllFunctionCatalogs(functionBlock, callbackFunction) {
        const thisApp = this;

        const functionCatalogId = "";
        const functionBlockId = functionBlock.getId();
        let executeCallback = true;

        const shouldDisassociate = confirm("Are you sure you want to disassociate this function block version from all unreleased function catalogs?");

        if (shouldDisassociate) {
            deleteFunctionBlock(functionCatalogId, functionBlockId, function (success, errorMessage) {
                if (success) {
                    // TODO: some indication that disassociation completed. Maybe an icon on the child element?
                    if (!functionBlock.isReleased()) {
                        const shouldDelete = confirm("Would you like to delete this function block version from the database?");
                        if (shouldDelete) {
                            executeCallback = false;
                            thisApp.deleteFunctionBlockFromDatabase(functionBlock, callbackFunction, true);
                        }
                    }
                } else {
                    alert("Request to disassociate Function Block failed: " + errorMessage);
                }
                // let component know action is complete
                if (executeCallback) {callbackFunction();}
            });
        }
        else {
            callbackFunction();
        }
    }

    deleteFunctionBlockFromDatabase(functionBlock, callbackFunction, shouldSkipConfirmation) {
        if (functionBlock.isReleased()) {
            alert("The currently selected Function Block version is released. Released function blocks cannot be deleted.")
        }
        else {
            const thisApp = this;

            const functionCatalogId = "";
            const functionBlockId = functionBlock.getId();

            let shouldDelete = false;
            if (shouldSkipConfirmation) {
                shouldDelete = true;
            }
            else {
                shouldDelete = confirm("This action will delete the last reference to this function block version.  Are you sure you want to delete it?");
            }

            if (shouldDelete) {
                deleteFunctionBlock(functionCatalogId, functionBlockId, function (success, errorMessage) {
                    if (success) {
                        const newFunctionBlocks = [];
                        const existingFunctionBlocks = thisApp.state.functionBlocks;
                        for (let i in existingFunctionBlocks) {
                            const existingFunctionBlock = existingFunctionBlocks[i];
                            const existingFunctionBlockId = existingFunctionBlock.getId();
                            if (existingFunctionBlockId != functionBlockId) {
                                newFunctionBlocks.push(existingFunctionBlock);
                            }
                            else {
                                // Remove deleted version from child item. Don't push to new array if no versions remain.
                                const existingVersionsJson = existingFunctionBlock.getVersionsJson();
                                if (existingVersionsJson.length > 1) {
                                    // Find newest released version to be displayed on screen.
                                    let displayedVersionId = existingVersionsJson[0].id;
                                    let displayedVersionJson = existingVersionsJson[0];

                                    for (let j in existingVersionsJson) {
                                        const existingVersionJson = existingVersionsJson[j];
                                        if (existingFunctionBlockId == existingVersionJson.id) {
                                            delete existingVersionsJson[j];
                                        }
                                        else {
                                            if (existingVersionJson.isReleased) {
                                                if (existingVersionJson.id > displayedVersionId) {
                                                    displayedVersionId = existingVersionJson.id;
                                                    displayedVersionJson = existingVersionJson;
                                                }
                                            }
                                        }
                                    }
                                    const newFunctionBlock = FunctionBlock.fromJson(displayedVersionJson);
                                    newFunctionBlock.setVersionsJson(existingVersionsJson);
                                    newFunctionBlocks.push(newFunctionBlock);
                                }
                            }
                        }

                        thisApp.setState({
                            functionBlocks:         newFunctionBlocks,
                            currentNavigationLevel: thisApp.NavigationLevel.functionCatalogs
                        });
                    }
                    else {
                        alert("Request to delete Function Block failed: " + errorMessage);
                    }
                });
            }
        }

        // let component know action is complete
        callbackFunction();
    }

    onMostInterfaceSelected(mostInterface, canUseCachedChildren) {
        const thisApp = this;

        const navigationItems = [];
        const parentHistory = this.state.parentHistory;
        if(this.state.activeRole == this.roles.release) {
            for (let i in this.state.navigationItems) {
                const navigationItem = this.state.navigationItems[i];
                navigationItem.setForm(null);
                navigationItems.push(navigationItem);
                if (i >= 1) {break;}
            }
        }
        else if (this.state.activeSubRole != this.developmentRoles.mostInterface) {
            const navigationItem = this.state.navigationItems[0];
            navigationItems.push(navigationItem);
        }

        const navigationItemConfig = new NavigationItemConfig();
        navigationItemConfig.setTitle(mostInterface.getName());
        navigationItemConfig.setIsReleased(mostInterface.isReleased());
        navigationItemConfig.setHeader(thisApp.headers.mostInterface);
        navigationItemConfig.setOnClickCallback(function() {
            thisApp.onMostInterfaceSelected(mostInterface, true);
        });
        navigationItemConfig.setForm(
            <app.MostInterfaceForm key="MostInterfaceForm"
               showTitle={false}
               onSubmit={this.onUpdateMostInterface}
               mostInterface={mostInterface}
               buttonTitle="Save"
               defaultButtonTitle="Save"
            />
        );
        navigationItems.push(navigationItemConfig);

        //Preserve reference to previously selected item.
        const parentHistoryItem = {
            id: "mostInterface" + mostInterface.getId(),
            item: mostInterface,
        };

        // Preserve reference to parent item.
        let parentItem = null;
        let newParentHistory = [];
        for (let i in parentHistory) {
            const existingSelectionHistoryItem = parentHistory[i];
            if (existingSelectionHistoryItem["id"] === parentHistoryItem["id"]) {
                newParentHistory = parentHistory.slice(0, Number(i));
                break;
            }
            newParentHistory.push(existingSelectionHistoryItem);
            parentItem = existingSelectionHistoryItem["item"];
        }
        newParentHistory.push(parentHistoryItem);

        thisApp.setState({
            navigationItems:            navigationItems,
            parentHistory:              newParentHistory,
            searchResults:              [],
            selectedItem:               mostInterface,
            parentItem:                 parentItem,
            proposedItem:               null,
            shouldShowCreateChildForm:  false,
            shouldShowSearchChildForm:  false,
            shouldShowEditForm:         false,
            mostFunctions:              canUseCachedChildren ? this.state.mostFunctions : [],
            createButtonState:          thisApp.CreateButtonState.normal,
            currentNavigationLevel:     thisApp.NavigationLevel.mostInterfaces,
            isLoadingChildren:          !canUseCachedChildren
        });

        this.updateMostTypes();
        this.updateMostFunctionStereotypes();

        getMostFunctionsForMostInterfaceId(mostInterface.getId(), function(mostFunctionsJson) {
            if (thisApp.state.currentNavigationLevel == thisApp.NavigationLevel.mostInterfaces) {
                // didn't navigate away while downloading children
                const mostFunctions = [];
                for (let i in mostFunctionsJson) {
                    const mostFunctionJson = mostFunctionsJson[i];
                    const mostFunction = MostFunction.fromJson(mostFunctionJson);
                    mostFunctions.push(mostFunction);
                }

                // TODO: if Functions have child elements that can be displayed, clear their array in setState.
                thisApp.setState({
                    mostFunctions:                  mostFunctions,
                    isLoadingChildren:              false,
                    shouldShowFilteredResults:      false,
                    filterString:                   ""
                });
            }
        });
    }

    onSearchMostInterfaces(searchString) {
        const requestTime = (new Date()).getTime();

        if (searchString.length > 0) {
            const thisApp = this;
            this.setState({isLoadingSearchResults: true});

            getMostInterfacesMatchingSearchString(searchString, function (mostInterfacesJson) {
                if (thisApp.state.currentNavigationLevel == thisApp.NavigationLevel.functionBlocks) {
                    if (thisApp.state.lastSearchResultTimestamp > requestTime) {
                        // old results, discard
                        return;
                    }

                    const proposedMostInterfaces = thisApp.getChildItemsFromVersions(mostInterfacesJson, MostInterface.fromJson);
                    const mostInterfaces = [];
                    const existingMostInterfaces = thisApp.state.mostInterfaces;

                    for (let i in proposedMostInterfaces) {
                        const mostInterface = proposedMostInterfaces[i];

                        //Filter any existing child elements or versions that appear in the search results.
                        let pushToSearchResults = true;
                        for(let m in existingMostInterfaces) {
                            if (existingMostInterfaces[m].getBaseVersionId() === mostInterface.getBaseVersionId()) {
                                pushToSearchResults = false;
                                break;
                            }
                        }
                        // Add to search results if no duplicates are found.
                        if (pushToSearchResults) {
                            mostInterfaces.push(mostInterface);
                        }
                    }

                    thisApp.setState({
                        searchResults: mostInterfaces,
                        lastSearchResultTimestamp: requestTime,
                        isLoadingSearchResults: false
                    });
                }
            });
        } else {
            this.setState({
                searchResults: [],
                lastSearchResultTimestamp: requestTime,
                isLoadingSearchResults: false
            });
        }
    }

    onFilterMostInterfaces(filterString) {
        const requestTime = (new Date()).getTime();

        if (filterString.length > 0) {
            const thisApp = this;
            this.setState({
                isLoadingChildren: true,
                filterString:      filterString
            });

            getMostInterfacesMatchingSearchString(filterString, function(mostInterfacesJson) {
                if (thisApp.state.currentNavigationLevel == thisApp.NavigationLevel.functionBlocks) {
                    if (thisApp.state.lastSearchResultTimestamp > requestTime) {
                        // old results, discard
                        return;
                    }

                    const mostInterfaces = thisApp.getChildItemsFromVersions(mostInterfacesJson, MostInterface.fromJson);

                    thisApp.setState({
                        searchResults:              mostInterfaces,
                        shouldShowFilteredResults:  true,
                        lastSearchResultTimestamp:  requestTime,
                        isLoadingChildren:          false,
                        isLoadingSearchResults:     false
                    });
                }
            });
        } else {
            // TODO: remove isLoadingChildren change and indicate that no results were found in child display area.
            this.setState({
                searchResults:                  [],
                lastSearchResultTimestamp:      requestTime,
                isLoadingChildren:              false,
                isLoadingSearchResults:         false,
                shouldShowFilteredResults:      false,
                filterString:                   ""
            });
        }

    }

    onAssociateMostInterfaceWithFunctionBlock(mostInterface, functionBlock) {
        const thisApp = this;
        associateMostInterfaceWithFunctionBlock(functionBlock.getId(), mostInterface.getId(), function (success, errorMessage) {
            if (success) {
                // remove most interface from search results
                let searchResults = thisApp.state.searchResults;
                const newSearchResults = [];
                for (let index in searchResults) {
                    const searchResult = searchResults[index];
                    if (searchResult.getId() != mostInterface.getId()) {
                        newSearchResults.push(searchResult);
                    }
                }

                // add most interface to children
                const mostInterfaces = thisApp.state.mostInterfaces.concat(mostInterface);

                thisApp.setState({
                    searchResults: newSearchResults,
                    mostInterfaces: mostInterfaces
                });
            } else {alert("Request to associate Interface failed: " + errorMessage);}
        });
    }

    onDeleteMostInterface(mostInterface, callbackFunction) {
        const thisApp = this;
        const selectedItem = this.state.selectedItem;

        // If this item has a containing parent, simply disassociate it.
        if (selectedItem) {
            if (selectedItem.isReleased()) {
                alert("Unable to delete Interface. Currently selected Function Block is released.");
                callbackFunction();
            }
            else {thisApp.disassociateMostInterfaceFromFunctionBlock(mostInterface, callbackFunction);}
        }
        else {
            listFunctionBlocksContainingMostInterface(mostInterface.getId(), function (data) {
                if (data.wasSuccess) {
                    if (data.functionBlockIds.length > 0) {
                        thisApp.disassociateMostInterfaceFromAllFunctionBlocks(mostInterface, callbackFunction);
                    }
                    else {
                        thisApp.deleteMostInterfaceFromDatabase(mostInterface, callbackFunction);
                    }
                }
            });
        }
    }

    disassociateMostInterfaceFromFunctionBlock(mostInterface, callbackFunction) {
        const thisApp = this;

        const functionBlockId = this.state.selectedItem.getId();
        const mostInterfaceId = mostInterface.getId();

        deleteMostInterface(functionBlockId, mostInterfaceId, function (success, errorMessage) {
            if (success) {
                const newMostInterfaces = [];
                const existingMostInterfaces = thisApp.state.mostInterfaces;
                for (let i in existingMostInterfaces) {
                    const existingMostInterface = existingMostInterfaces[i];
                    if (existingMostInterface.getId() != mostInterfaceId) {
                        newMostInterfaces.push(existingMostInterface);
                    }
                }
                thisApp.setState({
                    mostInterfaces:         newMostInterfaces,
                    currentNavigationLevel: thisApp.NavigationLevel.functionBlocks
                });
            } else {
                alert("Request to disassociate Interface failed: " + errorMessage);
            }
        });

        // let component know action is complete
        callbackFunction();
    }

    disassociateMostInterfaceFromAllFunctionBlocks(mostInterface, callbackFunction) {
        const thisApp = this;

        const functionBlockId = "";
        const mostInterfaceId = mostInterface.getId();
        let executeCallback = true;

        const shouldDisassociate = confirm("Are you sure you want to disassociate this interface version from all unreleased function blocks?");

        if (shouldDisassociate) {
            deleteMostInterface(functionBlockId, mostInterfaceId, function (success, errorMessage) {
                if (success) {
                    // TODO: some indication that disassociation completed. Maybe an icon on the child element?
                    if (!mostInterface.isReleased()) {
                        const shouldDelete = confirm("Would you like to delete this function block version from the database?");
                        if (shouldDelete) {
                            executeCallback = false;
                            thisApp.deleteMostInterfaceFromDatabase(mostInterface, callbackFunction, true);
                        }
                    }
                } else {
                    alert("Request to disassociate Interface failed: " + errorMessage);
                }
                // let component know action is complete
                if (executeCallback) {callbackFunction();}
            });
        }
        else {
            callbackFunction();
        }
    }

    deleteMostInterfaceFromDatabase(mostInterface, callbackFunction, shouldSkipConfirmation) {
        if (mostInterface.isReleased()) {
            alert("The currently selected interface version is released. Released interfaces cannot be deleted.")
        }
        else {
            const thisApp = this;

            const functionBlockId = "";
            const mostInterfaceId = mostInterface.getId();

            let shouldDelete = false;
            if (shouldSkipConfirmation) {
                shouldDelete = true;
            }
            else {
                shouldDelete = confirm("This action will delete the last reference to this Interface version.  Are you sure you want to delete it?");
            }

            if (shouldDelete) {
                deleteMostInterface(functionBlockId, mostInterfaceId, function (success, errorMessage) {
                    if (success) {
                        const newMostInterfaces = [];
                        const existingMostInterfaces = thisApp.state.mostInterfaces;
                        for (let i in existingMostInterfaces) {
                            const existingMostInterface = existingMostInterfaces[i];
                            const existingMostInterfaceId = existingMostInterface.getId();
                            if (existingMostInterfaceId != mostInterfaceId) {
                                newMostInterfaces.push(existingMostInterface);
                            }
                            else {
                                // Remove deleted version from child item. Don't push to new array if no versions remain.
                                const existingVersionsJson = existingMostInterface.getVersionsJson();
                                if (existingVersionsJson.length > 1) {
                                    // Find newest released version to be displayed on screen.
                                    let displayedVersionId = existingVersionsJson[0].id;
                                    let displayedVersionJson = existingVersionsJson[0];

                                    for (let j in existingVersionsJson) {
                                        const existingVersionJson = existingVersionsJson[j];
                                        if (existingMostInterfaceId == existingVersionJson.id) {
                                            delete existingVersionsJson[j];
                                        }
                                        else {
                                            if (existingVersionJson.isReleased) {
                                                if (existingVersionJson.id > displayedVersionId) {
                                                    displayedVersionId = existingVersionJson.id;
                                                    displayedVersionJson = existingVersionJson;
                                                }
                                            }
                                        }
                                    }
                                    const newMostInterface = MostInterface.fromJson(displayedVersionJson);
                                    newMostInterface.setVersionsJson(existingVersionsJson);
                                    newMostInterfaces.push(newMostInterface);
                                }
                            }
                        }

                        thisApp.setState({
                            mostInterfaces:         newMostInterfaces,
                            currentNavigationLevel: thisApp.NavigationLevel.functionBlocks
                        });
                    }
                    else {
                        alert("Request to delete Interface failed: " + errorMessage);
                    }
                });
            }
        }
        // let component know action is complete
        callbackFunction();
    }

    onMostFunctionSelected(mostFunction) {
        const thisApp = this;
        // Set all navigation forms to null, since editing functions occurs in metadata form.
        const navigationItems = [];
        for (let i in this.state.navigationItems) {
            const navigationItem = this.state.navigationItems[i];
            navigationItem.setForm(null);
            navigationItems.push(navigationItem);

            if (this.state.activeRole == this.roles.development) {
                if (this.state.activeSubRole == this.developmentRoles.mostInterface) {break;}
                else if (this.state.activeSubRole == this.developmentRoles.functionBlock) {
                    if (i >= 1) {break;}
                }
            }
            else if (i >= 2) {break;}
        }

        // Functions must have a parent interface.
        // Retrieve reference to parent interface, which is always the last entry in parentHistory.
        const parentHistory = this.state.parentHistory;
        const parentItem = parentHistory[parentHistory.length-1]["item"];

        const navigationItemConfig = new NavigationItemConfig();
        navigationItemConfig.setTitle(mostFunction.getName());
        navigationItemConfig.setHeader(thisApp.headers.mostFunction);
        navigationItemConfig.setIsReleased(parentItem.isReleased());
        navigationItemConfig.setOnClickCallback(function() {
            thisApp.onMostFunctionSelected(mostFunction, true);
        });
        navigationItemConfig.setForm(null);
        navigationItems.push(navigationItemConfig);

        thisApp.setState({
            navigationItems:            navigationItems,
            searchResults:              [],
            selectedItem:               mostFunction,
            parentItem:                 parentItem,
            proposedItem:               null,
            createButtonState:          thisApp.CreateButtonState.normal,
            currentNavigationLevel:     thisApp.NavigationLevel.mostFunctions,
            shouldShowCreateChildForm:  false,
            shouldShowFilteredResults:  false,
            shouldShowEditForm:         false,
        });

        // this.updateMostTypes();
    }

    onDeleteMostFunction(mostFunction, callbackFunction) {
        const thisApp = this;
        const selectedItem = this.state.selectedItem;

        if (selectedItem.isReleased()) {
            alert("Unable to delete Function. Currently selected Interface is released.");
            callbackFunction();
        }
        else {
            const mostInterfaceId = selectedItem.getId();
            const mostFunctionId = mostFunction.getId();

            const shouldDelete = confirm("This action will delete the only reference to this function. Are you sure you want to delete it?");
            if (shouldDelete) {
                deleteMostFunction(mostInterfaceId, mostFunctionId, function (success, errorMessage) {
                    if (success) {
                        const newMostFunctions = [];
                        const existingMostFunctions = thisApp.state.mostFunctions;
                        for (let i in existingMostFunctions) {
                            const existingMostFunction = existingMostFunctions[i];
                            if (existingMostFunction.getId() !== mostFunction.getId()) {
                                newMostFunctions.push(existingMostFunction);
                            }
                        }
                        thisApp.setState({
                            mostFunctions: newMostFunctions,
                            currentNavigationLevel: thisApp.NavigationLevel.mostInterfaces
                        });
                    } else {
                        alert("Request to delete function failed: " + errorMessage);
                        // let component know delete was unsuccessful
                        callbackFunction();
                    }
                });
            } else {
                // let component know delete was canceled
                callbackFunction();
            }
        }
    }

    getChildItemsFromVersions(childItemsJson, fromJsonFunction) {
        const childItems = [];

        for (let i in childItemsJson) {
            const versionSeriesJson = childItemsJson[i];
            const versions = versionSeriesJson.versions;

            // Set default version to be displayed, in case no versions have been released.
            let displayedVersionId = versions[0].id;
            let displayedVersionJson = versions[0];

            // Get highest version object that is released, using IDs.
            for (let j in versions) {
                const childItemJson = versions[j];
                if (childItemJson.isReleased) {
                    if (childItemJson.id > displayedVersionId) {
                        displayedVersionId = childItemJson.id;
                        displayedVersionJson = childItemJson;
                    }
                }
            }
            const childItem = fromJsonFunction(displayedVersionJson);
            childItem.setVersionsJson(versions);
            childItems.push(childItem);
        }

        return childItems;
    }

    onChildItemVersionChanged(oldChildItem, newChildItemJson, versionsJson) {
        const currentNavigationLevel = this.state.currentNavigationLevel;
        let fromJsonFunction = null;

        switch (currentNavigationLevel) {
            case this.NavigationLevel.versions:
                const functionCatalogs = this.state.functionCatalogs;
                fromJsonFunction = FunctionCatalog.fromJson;
                for (let i in functionCatalogs) {
                    if (functionCatalogs[i].getId() === oldChildItem.getId()) {
                        const newChildItem = FunctionCatalog.fromJson(newChildItemJson);
                        newChildItem.setVersionsJson(versionsJson);
                        functionCatalogs[i] = newChildItem;
                        this.setState({functionCatalogs: functionCatalogs});
                        break;
                    }
                }
            break;
            case this.NavigationLevel.functionCatalogs:
                const functionBlocks = this.state.functionBlocks;
                fromJsonFunction = FunctionBlock.fromJson;
                for (let i in functionBlocks) {
                    if (functionBlocks[i].getId() === oldChildItem.getId()) {
                        const newChildItem = FunctionBlock.fromJson(newChildItemJson);
                        newChildItem.setVersionsJson(versionsJson);
                        functionBlocks[i] = newChildItem;
                        this.setState({functionBlocks: functionBlocks});
                        break;
                    }
                }
            break;
            case this.NavigationLevel.functionBlocks:
                const mostInterfaces = this.state.mostInterfaces;
                fromJsonFunction = MostInterface.fromJson;
                for (let i in mostInterfaces) {
                    if (mostInterfaces[i].getId() === oldChildItem.getId()) {
                        const newChildItem = MostInterface.fromJson(newChildItemJson);
                        newChildItem.setVersionsJson(versionsJson);
                        mostInterfaces[i] = newChildItem;
                        this.setState({mostInterfaces: mostInterfaces});
                        break;
                    }
                }
            break;
        }

        // Need to update search results as well.
        const searchResults = this.state.searchResults;
        for (let i in searchResults) {
            if (searchResults[i].getId() === oldChildItem.getId()) {
                const searchResult = fromJsonFunction(newChildItemJson);
                searchResult.setVersionsJson(versionsJson);
                searchResults[i] = searchResult;
                this.setState({searchResults: searchResults});
                break;
            }
        }
    }

    handleFunctionStereotypeClick(selectedFunctionStereotype) {
        const shouldShowCreateChildForm = this.state.selectedFunctionStereotype == selectedFunctionStereotype ? !this.state.shouldShowCreateChildForm : true;
        this.setState({
            shouldShowCreateChildForm:  shouldShowCreateChildForm,
            selectedFunctionStereotype: selectedFunctionStereotype,
            shouldShowEditForm:         false
        });
    }

    updateMostTypes() {
        const thisApp = this;
        // get most types (used cached ones for now but set the new ones in the callback)
        getMostTypes(function (mostTypesJson) {
            if (!mostTypesJson) {
                thisApp.setState({
                    isLoadingMostTypes: false
                });
                return;
            }
            const mostTypes = [];
            for (let i in mostTypesJson) {
                const jsonType = mostTypesJson[i];

                const mostType = MostType.fromJson(jsonType);
                mostTypes.push(mostType);
            }
            thisApp.setState({
                mostTypes:          mostTypes,
                isLoadingMostTypes: false
            });
        });
        getPrimitiveTypes(function (primitiveTypesJson) {
            if (!primitiveTypesJson) {
                thisApp.setState({
                    isLoadingPrimitiveTypes: false
                });
                return;
            }
            const primitiveTypes = [];
            for (let i in primitiveTypesJson) {
                const jsonType = primitiveTypesJson[i];

                const primitiveType = PrimitiveType.fromJson(jsonType);
                primitiveTypes.push(primitiveType);
            }
            thisApp.setState({
                primitiveTypes:             primitiveTypes,
                isLoadingPrimitiveTypes:    false
            });
        });
        getUnits(function (unitsJson) {
            if (!unitsJson) {
                thisApp.setState({
                    isLoadingUnits: false
                });
                return;
            }

            const units = [];
            for (let i in unitsJson) {
                const jsonUnit = unitsJson[i];

                const unit = MostUnit.fromJson(jsonUnit);
                units.push(unit);
            }
            thisApp.setState({
                mostUnits: units,
                isLoadingUnits: false
            });
        });
    }

    onTypeCreated(newType) {
        const mostTypes = this.state.mostTypes;

        mostTypes.push(newType);

        this.setState({
            mostTypes: mostTypes
        });
    }

    updateMostFunctionStereotypes() {
        const thisApp = this;
        // get most types (used cached ones for now but set the new ones in the callback)
        getMostFunctionStereotypes(function (mostFunctionStereotypesJson) {
            if (!mostFunctionStereotypesJson) {
                console.error("Invalid stereotype JSON data.");
                return;
            }
            const mostFunctionStereotypes = [];
            for (let i in mostFunctionStereotypesJson) {
                const mostFunctionStereotype = MostFunctionStereotype.fromJson(mostFunctionStereotypesJson[i]);
                mostFunctionStereotypes.push(mostFunctionStereotype);
            }
            thisApp.setState({
                mostFunctionStereotypes: mostFunctionStereotypes
            });
        });
    }
    
    handleRoleClick(roleName, subRoleName, canUseCachedChildren) {
        const thisApp = this;

        switch (roleName) {
            case this.roles.release: {
                // Release Mode
                // TODO: could try saving a user's position (ie Function Catalog 1's Function Blocks) and reverting to it at this step.
                this.setState({
                    currentNavigationLevel:         thisApp.NavigationLevel.versions,
                    parentHistory:               [],
                    activeRole:                     roleName,
                    activeSubRole:                  null,
                    selectedItem:                   null,
                    parentItem:                     null,
                    proposedItem:               null,
                    shouldShowToolbar:              true,
                    shouldShowCreateChildForm:      false,
                    shouldShowSearchChildForm:      false,
                    shouldShowEditForm:             false,
                    createButtonState:              this.CreateButtonState.normal,
                    selectedFunctionStereotype:     null,
                    isLoadingChildren:              true,
                    isLoadingSearchResults:         false,
                    shouldShowFilteredResults:      false,
                    searchResults:                  [],
                    functionBlocks:                 [],
                    mostInterfaces:                 [],
                    navigationItems:                [],
                    showSettingsPage:               false
                });

                this.getFunctionCatalogsForCurrentVersion(function (functionCatalogs) {
                    thisApp.setState({
                        functionCatalogs:       functionCatalogs,
                        isLoadingChildren:      false
                    });
                });
            } break;
            case this.roles.development: {
                // Development Mode
                // set navigation level similar to onItemSelected() methods. If the rolename isn't mostInterface and the activeSubRole is null, default to displaying functionBlocks.
                const newActiveSubRole = (subRoleName || this.developmentRoles.functionBlock);
                const newNavigationLevel = newActiveSubRole === this.developmentRoles.mostInterface ? this.NavigationLevel.functionBlocks : this.NavigationLevel.functionCatalogs;

                this.setState({
                    navigationItems:            [],
                    parentHistory:           [],
                    searchResults:              [],
                    functionCatalogs:           [],
                    selectedItem:               null,
                    parentItem:                 null,
                    proposedItem:               null,
                    shouldShowCreateChildForm:  false,
                    shouldShowSearchChildForm:  false,
                    shouldShowEditForm:         false,
                    shouldShowToolbar:          true,
                    shouldShowFilteredResults:  false,
                    createButtonState:          thisApp.CreateButtonState.normal,
                    isLoadingChildren:          !canUseCachedChildren,
                    currentNavigationLevel:     newNavigationLevel,
                    activeRole:                 roleName,
                    activeSubRole:              newActiveSubRole,
                    showSettingsPage:           false
                });

                if (newActiveSubRole === this.developmentRoles.functionBlock) {
                    getFunctionBlocksForFunctionCatalogId(null, function(functionBlocksJson) {
                        if (thisApp.state.currentNavigationLevel == newNavigationLevel) {
                            // didn't navigate away while downloading children
                            const functionBlocks = thisApp.getChildItemsFromVersions(functionBlocksJson, FunctionBlock.fromJson);
                            thisApp.setState({
                                functionBlocks:     functionBlocks,
                                isLoadingChildren:  false
                            });
                        }
                    });
                } else {
                    getMostInterfacesForFunctionBlockId(null, function(mostInterfacesJson) {
                        if (thisApp.state.currentNavigationLevel == newNavigationLevel) {
                            // didn't navigate away while downloading children
                            const mostInterfaces = thisApp.getChildItemsFromVersions(mostInterfacesJson, MostInterface.fromJson);
                            thisApp.setState({
                                mostFunctions:      [],
                                mostInterfaces:     mostInterfaces,
                                isLoadingChildren:  false
                            });
                        }
                    });
                }
            } break;
            case this.roles.types: {
                this.setState({
                    navigationItems:            [],
                    searchResults:              [],
                    functionCatalogs:           [],
                    selectedItem:               null,
                    parentItem:                 null,
                    proposedItem:               null,
                    shouldShowCreateChildForm:  false,
                    shouldShowSearchChildForm:  false,
                    shouldShowEditForm:         false,
                    shouldShowToolbar:          false,
                    shouldShowFilteredResults:  false,
                    isLoadingMostTypes:         true,
                    isLoadingPrimitiveTypes:    true,
                    isLoadingUnits:             true,
                    createButtonState:          thisApp.CreateButtonState.normal,
                    currentNavigationLevel:     null,
                    activeRole:                 roleName,
                    activeSubRole:              null,
                    showSettingsPage:           false
                });
                thisApp.updateMostTypes();
            } break;
            default: {
                console.error("Invalid role " + roleName + " selected.");
            }
        }
    }

    handleSettingsClick() {
        this.setState({
            showSettingsPage: !this.state.showSettingsPage
        });
    }

    onThemeChange(themeName) {
        this.setTheme(themeName);
        const account = this.state.account;
        account.theme = themeName;
        this.setState({
            account: account
        });
    }

    setTheme(themeName) {
        const themeCssDirectory = themeName.toLowerCase();
        document.getElementById('core-css').href =              '/css/themes/' + themeCssDirectory + '/core.css';
        document.getElementById('app-css').href =               '/css/themes/' + themeCssDirectory + '/app.css';
        document.getElementById('palette-css').href =           '/css/themes/' + themeCssDirectory + '/palette.css';
        document.getElementById('react-input-field-css').href = '/css/themes/' + themeCssDirectory + '/react/input-field.css';
        document.getElementById('react-toolbar-css').href =     '/css/themes/' + themeCssDirectory + '/react/toolbar.css';
    }

    renderChildItems() {
        const reactComponents = [];
        const NavigationLevel = this.NavigationLevel;
        const currentNavigationLevel = this.state.currentNavigationLevel;

        if (this.state.isLoadingChildren) {
            // return loading icon
            return (
                <div className="form-loading"><i id="loading-children-icon" className="fa fa-3x fa-refresh fa-spin"></i></div>
            );
        }

        let childItems = [];
        switch (currentNavigationLevel) {
            case NavigationLevel.versions:
                childItems = this.state.functionCatalogs;
                for (let i in childItems) {
                    const childItem = childItems[i];
                    const functionCatalogKey = "FunctionCatalog" + i;
                    reactComponents.push(<app.FunctionCatalog key={functionCatalogKey} functionCatalog={childItem} onClick={this.onFunctionCatalogSelected} onDelete={this.onDeleteFunctionCatalog} onVersionChanged={this.onChildItemVersionChanged}/>);
                }
            break;

            case NavigationLevel.functionCatalogs:
                childItems = this.state.shouldShowFilteredResults ? this.state.searchResults : this.state.functionBlocks;
                for (let i in childItems) {
                    const childItem = childItems[i];
                    const functionBlockKey = "FunctionBlock" + i;
                    reactComponents.push(<app.FunctionBlock key={functionBlockKey} functionBlock={childItem} onClick={this.onFunctionBlockSelected} displayVersionsList={this.state.selectedItem} onDelete={this.onDeleteFunctionBlock} onVersionChanged={this.onChildItemVersionChanged} />);
                }
            break;

            case NavigationLevel.functionBlocks:
                childItems = this.state.shouldShowFilteredResults ? this.state.searchResults : this.state.mostInterfaces;
                for (let i in childItems) {
                    const childItem = childItems[i];
                    const interfaceKey = "Interface" + i;
                    reactComponents.push(<app.MostInterface key={interfaceKey} mostInterface={childItem} onClick={this.onMostInterfaceSelected} displayVersionsList={this.state.selectedItem} onDelete={this.onDeleteMostInterface} onVersionChanged={this.onChildItemVersionChanged} />);
                }
            break;

            case NavigationLevel.mostInterfaces:
                childItems = this.state.mostFunctions;
                for (let i in childItems) {
                    const childItem = childItems[i];
                    const mostFunctionKey = "mostFunction" + i;
                    reactComponents.push(<app.MostFunction key={mostFunctionKey} mostFunction={childItem} onClick={this.onMostFunctionSelected} onDelete={this.onDeleteMostFunction} isInterfaceReleased={this.state.selectedItem.isReleased()} />);
                }
            break;

            case NavigationLevel.mostFunctions:
                // add a form for the selected MOST function
                const shouldAnimateCreateButton = (this.state.createButtonState == this.CreateButtonState.animate);
                const buttonTitle = (this.state.createButtonState == this.CreateButtonState.success) ? "Changes Saved" : "Save";
                reactComponents.push(<app.MostFunctionForm key="MostFunctionForm"
                    readOnly={this.state.parentItem.isReleased()}
                    showTitle={true}
                    onSubmit={this.onUpdateMostFunction}
                    buttonTitle={buttonTitle}
                    defaultButtonTitle="Save"
                    mostFunctionStereotypes={this.state.mostFunctionStereotypes}
                    mostTypes={this.state.mostTypes}
                    mostFunction={this.state.selectedItem}
                    shouldShowSaveAnimation={shouldAnimateCreateButton}
                    />);
                break;

            default:
                console.error("renderChildItems: Unimplemented Navigation Level: " + currentNavigationLevel);
            break;
        }

        return reactComponents;
    }

    renderForm() {
        const NavigationLevel = this.NavigationLevel;
        const currentNavigationLevel = this.state.currentNavigationLevel;
        const activeRole = this.state.activeRole;
        const navigationItems = this.state.navigationItems;

        const shouldShowToolbar = this.state.shouldShowToolbar;
        const shouldShowCreateChildForm = this.state.shouldShowCreateChildForm;
        const shouldShowSearchChildForm = this.state.shouldShowSearchChildForm;
        const shouldShowEditForm = this.state.shouldShowEditForm;
        // Show the filter bar for development mode only when viewing orphaned items
        const selectedItem = this.state.selectedItem;
        const shouldShowFilterBar = (this.state.activeRole === this.roles.development) && !selectedItem;

        const reactComponents = [];
        const thisApp = this;

        if (shouldShowToolbar) {
            // Determine correct behavior for back button in development mode
            let backFunction = null;

            let shouldShowForkButton = false;
            let shouldShowBackButton = false;
            let shouldShowEditButton = false;
            let shouldShowSearchButton = false;
            let shouldShowCreateButton = true;
            let shouldShowReleaseButton = false;
            let shouldShowNavigationItems = false;
            let forkFunction = null;

            // Determine what buttons should be displayed.
            if (selectedItem) {
                const isReleased = currentNavigationLevel != NavigationLevel.mostFunctions ? selectedItem.isReleased() : this.state.parentItem.isReleased();
                shouldShowCreateButton = ! isReleased;
                shouldShowBackButton = true;
                shouldShowSearchButton = ! isReleased && ! shouldShowFilterBar;

                if (currentNavigationLevel == NavigationLevel.functionCatalogs) {
                    shouldShowReleaseButton = ! isReleased;
                    shouldShowForkButton = isReleased;
                }

                if (activeRole === this.roles.development) {
                    const activeSubRole = this.state.activeSubRole;
                    shouldShowEditButton = true;
                    shouldShowNavigationItems = true;

                    // Determine if fork button should be shown.
                    if (isReleased) {
                        shouldShowForkButton = (currentNavigationLevel == NavigationLevel.functionBlocks && activeSubRole == this.developmentRoles.functionBlock) ||
                            (currentNavigationLevel == NavigationLevel.mostInterfaces && activeSubRole == this.developmentRoles.mostInterface);
                        // shouldShowForkButton = currentNavigationLevel != NavigationLevel.mostFunctions ? selectedItem.isReleased() : false;

                        // Determine fork button functionality
                        if (shouldShowForkButton) {
                            switch (currentNavigationLevel) {
                                case this.NavigationLevel.functionCatalogs:
                                    forkFunction = this.onUpdateFunctionCatalog;
                                    break;
                                case this.NavigationLevel.functionBlocks:
                                    forkFunction = this.onUpdateFunctionBlock;
                                    break;
                                case this.NavigationLevel.mostInterfaces:
                                    forkFunction = this.onUpdateMostInterface;
                                    break;
                            }
                        }
                    }

                    // TODO: Adjust switch statements if a function catalog layer is needed in development mode.
                    switch (currentNavigationLevel) {
                        case this.NavigationLevel.functionBlocks:
                            backFunction = function() {thisApp.handleRoleClick(thisApp.state.activeRole, thisApp.state.activeSubRole, true)};
                            break;
                        case this.NavigationLevel.mostInterfaces:
                            if (activeSubRole === thisApp.developmentRoles.functionBlock) {
                                backFunction = navigationItems[navigationItems.length-2].getOnClickCallback();
                            }
                            else {
                                backFunction = function() {thisApp.handleRoleClick(thisApp.state.activeRole, thisApp.state.activeSubRole, true);};
                            }
                            break;
                        case this.NavigationLevel.mostFunctions:
                            backFunction = navigationItems[navigationItems.length-2].getOnClickCallback();
                            break;
                    }
                }
                else {
                    if (currentNavigationLevel == this.NavigationLevel.functionCatalogs) {
                        backFunction = this.onRootNavigationItemClicked;
                    }
                    else {
                        backFunction = navigationItems[navigationItems.length-2].getOnClickCallback();
                    }
                }
            }

            reactComponents.push(
                <app.Toolbar key="Toolbar"
                    onCreateClicked={() => this.setState({ shouldShowCreateChildForm: !shouldShowCreateChildForm, shouldShowSearchChildForm: false, shouldShowEditForm: false })}
                    onCancel={() => this.setState({ shouldShowCreateChildForm: false, shouldShowSearchChildForm: false, shouldShowEditForm: false })}
                    onSearchClicked={() => this.setState({shouldShowSearchChildForm: !shouldShowSearchChildForm, shouldShowCreateChildForm: false, shouldShowEditForm: false })}
                    onEditClicked={() => this.setState({shouldShowEditForm: !shouldShowEditForm, shouldShowCreateChildForm: false, shouldShowSearchChildForm: false })}
                    onForkClicked={() => forkFunction(selectedItem, true)}
                    onReleaseClicked={() => this.onReleaseFunctionCatalog(selectedItem)}
                    navigationLevel={this.NavigationLevel}
                    currentNavigationLevel={this.state.currentNavigationLevel}
                    navigationItems={navigationItems}
                    functionStereotypes={this.FunctionStereotypes}
                    handleFunctionStereotypeClick={this.handleFunctionStereotypeClick}
                    shouldShowForkButton={shouldShowForkButton}
                    shouldShowCreateButton={shouldShowCreateButton}
                    shouldShowSearchIcon={shouldShowSearchButton}
                    shouldShowBackButton={shouldShowBackButton}
                    shouldShowEditButton={shouldShowEditButton}
                    shouldShowReleaseButton={shouldShowReleaseButton}
                    shouldShowNavigationItems={shouldShowNavigationItems}
                    onBackButtonClicked={backFunction}
                />
            );
        }

        const buttonTitle = (this.state.createButtonState == this.CreateButtonState.success) ? "Added" : "Submit";
        const developmentButtonTitle = (this.state.createButtonState == this.CreateButtonState.success) ? "Changes Saved" : "Save";
        const shouldAnimateCreateButton = (this.state.createButtonState == this.CreateButtonState.animate);

        switch (currentNavigationLevel) {
            case NavigationLevel.versions:
                if (shouldShowCreateChildForm) {
                    reactComponents.push(
                        <app.FunctionCatalogForm key="FunctionCatalogForm"
                            shouldShowSaveAnimation={shouldAnimateCreateButton}
                            functionCatalog={this.state.proposedItem}
                            buttonTitle={buttonTitle}
                            showTitle={true}
                            onSubmit={this.onCreateFunctionCatalog}
                            defaultButtonTitle="Submit"
                        />
                    );
                }
            break;

            case NavigationLevel.functionCatalogs:
                if (shouldShowCreateChildForm) {
                    reactComponents.push(
                        <app.FunctionBlockForm key="FunctionBlockForm"
                            shouldShowSaveAnimation={shouldAnimateCreateButton}
                            functionBlock={this.state.proposedItem}
                            buttonTitle={buttonTitle}
                            showTitle={true}
                            onSubmit={this.onCreateFunctionBlock}
                            defaultButtonTitle="Submit"
                        />
                    );
                }
                else if (shouldShowSearchChildForm) {
                    reactComponents.push(
                        <app.SearchForm key="SearchForm"
                            navigationLevel={NavigationLevel}
                            currentNavigationLevel={currentNavigationLevel}
                            showTitle={true}
                            formTitle={"Find and Associate Function Blocks"}
                            onUpdate={this.onSearchFunctionBlocks}
                            onVersionChanged={this.onChildItemVersionChanged}
                            onPlusButtonClick={this.onAssociateFunctionBlockWithFunctionCatalog}
                            selectedItem={this.state.selectedItem}
                            searchResults={this.state.searchResults}
                            isLoadingSearchResults={this.state.isLoadingSearchResults}
                        />
                    );
                }
                break;

            case NavigationLevel.functionBlocks:
                if (shouldShowEditForm) {
                    reactComponents.push(
                        <app.FunctionBlockForm key="FunctionBlockForm"
                           shouldShowSaveAnimation={shouldAnimateCreateButton}
                           showTitle={true}
                           showCustomTitle={true}
                           formTitle={selectedItem.getName()}
                           onSubmit={this.onUpdateFunctionBlock}
                           functionBlock={selectedItem}
                           buttonTitle={developmentButtonTitle}
                           defaultButtonTitle="Save"
                        />
                    );
                }
                if (shouldShowCreateChildForm) {
                    reactComponents.push(
                        <app.MostInterfaceForm key="MostInterfaceForm"
                            shouldShowSaveAnimation={shouldAnimateCreateButton}
                            mostInterface={this.state.proposedItem}
                            buttonTitle={buttonTitle}
                            showTitle={true}
                            onSubmit={this.onCreateMostInterface}
                            defaultButtonTitle="Submit"
                        />
                    );
                }
                else if (shouldShowSearchChildForm) {
                    reactComponents.push(
                        <app.SearchForm key="SearchForm"
                            navigationLevel={NavigationLevel}
                            currentNavigationLevel={currentNavigationLevel}
                            showTitle={true}
                            formTitle={"Find and Associate Interfaces"}
                            onUpdate={this.onSearchMostInterfaces}
                            onVersionChanged={this.onChildItemVersionChanged}
                            onPlusButtonClick={this.onAssociateMostInterfaceWithFunctionBlock}
                            selectedItem={this.state.selectedItem}
                            searchResults={this.state.searchResults}
                            isLoadingSearchResults={this.state.isLoadingSearchResults}
                        />
                    );
                }
            break;

            case NavigationLevel.mostInterfaces:
                if (shouldShowEditForm) {
                    reactComponents.push(
                        <app.MostInterfaceForm key="MostInterfaceForm"
                           shouldShowSaveAnimation={shouldAnimateCreateButton}
                           showTitle={true}
                           showCustomTitle={true}
                           formTitle={selectedItem.getName()}
                           onSubmit={this.onUpdateMostInterface}
                           mostInterface={selectedItem}
                           buttonTitle={developmentButtonTitle}
                           defaultButtonTitle="Save"
                        />
                    );
                }
                if (shouldShowCreateChildForm) {
                    reactComponents.push(
                        <app.MostFunctionForm key="MostFunctionForm"
                           shouldShowSaveAnimation={shouldAnimateCreateButton}
                           mostFunction={this.state.proposedItem}
                           buttonTitle={buttonTitle}
                           showTitle={true}
                           onSubmit={this.onCreateMostFunction}
                           defaultButtonTitle="Submit"
                           mostFunctionStereotypes={this.state.mostFunctionStereotypes}
                           selectedFunctionStereotype={this.state.selectedFunctionStereotype}
                           mostTypes={this.state.mostTypes}
                        />
                    );
                }
                break;

            case NavigationLevel.mostFunctions:
                // add nothing, no child elements
                break;

            default:
                console.error("renderForm: Unimplemented Navigation Level: "+ currentNavigationLevel);
            break;
        }

        if (shouldShowFilterBar) {reactComponents.push(this.renderFilterBar());}
        return reactComponents;
    }

    renderMainContent() {
        if (this.state.showSettingsPage) {
            const theme = this.state.account ? this.state.account.theme : "Tidy";
            return (
                <div id="main-content" className="container">
                    <app.SettingsPage theme={theme} onThemeChange={this.onThemeChange}/>
                </div>
            );
        }
        else {
            switch (this.state.activeRole) {
                case this.roles.types: {
                    // types role
                    return (
                        <div id="main-content" className="container">
                            <app.TypesPage onTypeCreated={this.onTypeCreated} mostTypes={this.state.mostTypes} primitiveTypes={this.state.primitiveTypes} mostUnits={this.state.mostUnits}
                                           isLoadingTypesPage={this.state.isLoadingMostTypes || this.state.isLoadingPrimitiveTypes || this.state.isLoadingUnits} />
                        </div>
                    );
                } break;
                default: {
                    // other roles
                    let navigationItems = "";
                    if (this.state.activeRole === this.roles.release) {
                        navigationItems = <app.Navigation navigationItems={this.state.navigationItems} onRootItemClicked={this.onRootNavigationItemClicked} />;
                    }
                    return (
                        <div id="main-content" className="container">
                            {navigationItems}
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
        }
    }

    renderRoleToggle() {
        const roleItems = [];
        roleItems.push(this.roles.release);
        roleItems.push(this.roles.development);
        roleItems.push(this.roles.types);

        return (
            <app.RoleToggle roleItems={roleItems} handleClick={(role, canUseCachedChildren) => this.handleRoleClick(role, null, canUseCachedChildren)} activeRole={this.state.activeRole} />
        );
    }

    renderSubRoleToggle() {
        if (this.state.activeRole === this.roles.development) {
            const roleItems = [];
            roleItems.push(this.developmentRoles.functionBlock);
            roleItems.push(this.developmentRoles.mostInterface);

            return (
                <app.RoleToggle roleItems={roleItems} handleClick={(subRole, canUseCachedChildren) => this.handleRoleClick(this.roles.development, subRole, canUseCachedChildren)} activeRole={this.state.activeSubRole} />
            );
        }
    }

    renderFilterBar() {
        const currentNavigationLevel = this.state.currentNavigationLevel;
        const filterFunction = this.state.currentNavigationLevel === this.NavigationLevel.functionCatalogs ? this.onFilterFunctionBlocks : this.onFilterMostInterfaces;
        const defaultText = this.state.currentNavigationLevel === this.NavigationLevel.functionCatalogs ? "Filter Function Blocks" : "Filter Interfaces";

        if (currentNavigationLevel === this.NavigationLevel.functionBlocks && this.state.selectedItem) {
            // Don't show filter bar when viewing interfaces in a selected Function Block.
            return;
        }

        if(currentNavigationLevel === this.NavigationLevel.mostInterfaces) {
            // Don't show filter bar when viewing most functions in a selected interface.
            return;
        }

        return (
            <div className="filter-form" key="filtered-search-form">
                <app.SearchBar id="search-bar" name="search" type="text" label="Search" value={this.state.filterString} defaultValue={defaultText} readOnly={false} onChange={filterFunction}/>
            </div>
        );

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
            <div id="app-root">
                <div id="header" className="secondary-bg accent title-font">
                    Tidy Duck
                    {this.renderRoleToggle()}
                    {this.renderSubRoleToggle()}
                    <div id="account-area">
                        {accountName}
                        <a id="logout" href="#" onClick={this.logout}>logout</a>
                        <i id="settings-icon" className="fa fa-cog fa-lg" onClick={this.handleSettingsClick}/>
                    </div>
                </div>
                {this.renderMainContent()}
            </div>
        );
    }
}

registerClassWithGlobalScope("App", App);

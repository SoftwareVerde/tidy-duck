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
            searchResults:              [],
            lastSearchResultTimestamp:  0,
            currentVersionId:           1, // TODO
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
            shouldShowSearchChildForm:  false,
            isLoadingChildren:          true,
            isLoadingSearchResults:     false
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
        this.onSearchFunctionBlocks = this.onSearchFunctionBlocks.bind(this);
        this.onAssociateFunctionBlockWithFunctionCatalog = this.onAssociateFunctionBlockWithFunctionCatalog.bind(this);
        this.onDeleteFunctionBlock = this.onDeleteFunctionBlock.bind(this);

        this.onMostInterfaceSelected = this.onMostInterfaceSelected.bind(this);
        this.onCreateMostInterface = this.onCreateMostInterface.bind(this);
        this.onUpdateMostInterface = this.onUpdateMostInterface.bind(this);
        this.onSearchMostInterfaces = this.onSearchMostInterfaces.bind(this);
        this.onAssociateMostInterfaceWithFunctionBlock = this.onAssociateMostInterfaceWithFunctionBlock.bind(this);
        this.onDeleteMostInterface = this.onDeleteMostInterface.bind(this);

        this.handleSettingsClick = this.handleSettingsClick.bind(this);
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
            createButtonState:  this.CreateButtonState.animate
        });

        insertFunctionCatalog(this.state.currentVersionId, functionCatalogJson, function(functionCatalogId) {
            if (! (functionCatalogId > 0)) {
                console.log("Unable to create function catalog.");
                thisApp.setState({
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
                defaultButtonTitle="Save"
            />
        );
        navigationItems.push(navigationItem);

        this.setState({
           navigationItems: navigationItems
        });

        updateFunctionCatalog(this.state.currentVersionId, functionCatalogId, functionCatalogJson, function(wasSuccess) {
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
                thisApp.setState({
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
                defaultButtonTitle="Save"
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
                        defaultButtonTitle="Save"
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
                thisApp.setState({
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
                defaultButtonTitle="Save"
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
                        defaultButtonTitle="Save"
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
        getFunctionCatalogsForVersionId(this.state.currentVersionId, function(functionCatalogsJson) {
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
                defaultButtonTitle="Save"
            />
        );

        navigationItems.push(navigationItemConfig);

        thisApp.setState({
            navigationItems:            navigationItems,
            searchResults:              [],
            selectedItem:               functionCatalog,
            shouldShowCreateChildForm:  false,
            shouldShowSearchChildForm:  false,
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

    onDeleteFunctionCatalog(functionCatalog) {
        const thisApp = this;

        const functionCatalogId = functionCatalog.getId();

        deleteFunctionCatalog(this.state.currentVersionId, functionCatalogId, function (success, errorMessage) {
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
                defaultButtonTitle="Save"
            />
        );
        navigationItems.push(navigationItemConfig);

        const parentItem = thisApp.state.selectedItem; //Preserve reference to previously selected item.

        thisApp.setState({
            navigationItems:            navigationItems,
            searchResults:              [],
            selectedItem:               functionBlock,
            parentItem:                 parentItem,
            mostInterfaces:             [],
            shouldShowCreateChildForm:  false,
            shouldShowSearchChildForm:  false,
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
                    mostFunctions:      [],
                    isLoadingChildren:  false
                });
            }

        });
    }

    onSearchFunctionBlocks(searchString) {
        const requestTime = (new Date()).getTime();

        if (searchString.length > 0) {
            const thisApp = this;
            this.setState({isLoadingSearchResults: true});

            getFunctionBlocksMatchingSearchString(this.state.currentVersionId, searchString, function (functionBlocksJson) {
                if (thisApp.state.currentNavigationLevel == thisApp.NavigationLevel.functionCatalogs) {
                    if (thisApp.state.lastSearchResultTimestamp > requestTime) {
                        // old results, discard
                        return;
                    }
                    const functionBlocks = [];
                    const existingFunctionBlocks = thisApp.state.functionBlocks;
                    for (let i in functionBlocksJson) {
                        const functionBlockJson = functionBlocksJson[i];

                        //Filter any existing child elements that appear in the search results.
                        var pushToSearchResults = true;
                        for(let m in existingFunctionBlocks) {
                            if (existingFunctionBlocks[m].getId() == functionBlockJson.id) {
                                pushToSearchResults = false;
                                break;
                            }
                        }
                        if (pushToSearchResults) {
                            const functionBlock = FunctionBlock.fromJson(functionBlockJson);
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

        const functionCatalogId = this.state.selectedItem.getId();
        const functionBlockId = functionBlock.getId();

        listFunctionCatalogsContainingFunctionBlock(functionBlockId, this.state.currentVersionId, function (data) {
            if (data.wasSuccess) {
                let shouldDelete = false;
                const functionCatalogIds = data.functionCatalogIds;
                if (functionCatalogIds.length > 1) {
                    shouldDelete = true;
                } else {
                    shouldDelete = confirm("This action will delete the last reference to this function block.  Are you sure you want to delete it?");
                }
                if (shouldDelete) {
                    deleteFunctionBlock(functionCatalogId, functionBlockId, function (success, errorMessage) {
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
                                functionBlocks:         newFunctionBlocks,
                                currentNavigationLevel: thisApp.NavigationLevel.functionCatalogs
                            });
                        } else {
                            alert("Request to delete Function Block failed: " + errorMessage);
                        }
                    });
                }
            }
            // let component know action is complete
            callbackFunction();
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

        const parentItem = this.state.selectedItem; //Preserve reference to previously selected item.
        thisApp.setState({
            navigationItems:            navigationItems,
            searchResults:              [],
            selectedItem:               mostInterface,
            parentItem:                 parentItem,
            shouldShowCreateChildForm:  false,
            shouldShowSearchChildForm:  false,
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

            // TODO: if Functions have child elements that can be displayed, clear their array in setState.
            thisApp.setState({
                mostFunctions:      mostFunctions,
                isLoadingChildren:  false
            })
        }
    }

    onSearchMostInterfaces(searchString) {
        const requestTime = (new Date()).getTime();

        if (searchString.length > 0) {
            const thisApp = this;
            this.setState({isLoadingSearchResults: true});

            getMostInterfacesMatchingSearchString(this.state.currentVersionId, searchString, function (mostInterfacesJson) {
                if (thisApp.state.currentNavigationLevel == thisApp.NavigationLevel.functionBlocks) {
                    if (thisApp.state.lastSearchResultTimestamp > requestTime) {
                        // old results, discard
                        return;
                    }
                    const mostInterfaces = [];
                    const existingMostInterfaces = thisApp.state.mostInterfaces;
                    for (let i in mostInterfacesJson) {
                        const mostInterfaceJson = mostInterfacesJson[i];

                        //Filter any existing child elements that appear in the search results.
                        var pushToSearchResults = true;
                        for(let m in existingMostInterfaces) {
                            if (existingMostInterfaces[m].getId() == mostInterfaceJson.id) {
                                pushToSearchResults = false;
                                break;
                            }
                        }
                        if (pushToSearchResults) {
                            const mostInterface = MostInterface.fromJson(mostInterfaceJson);
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

        const functionBlockId = this.state.selectedItem.getId();
        const mostInterfaceId = mostInterface.getId();

        listFunctionBlocksContainingMostInterface(mostInterfaceId, this.state.currentVersionId, function (data) {
            if (data.wasSuccess) {
                let shouldDelete = false;
                const functionBlockIds = data.functionBlockIds;
                if (functionBlockIds.length > 1) {
                    shouldDelete = true;
                } else {
                    shouldDelete = confirm("This action will delete the last reference to this interface.  Are you sure you want to delete it?");
                }

                if (shouldDelete) {
                    deleteMostInterface(functionBlockId, mostInterfaceId, function (success, errorMessage) {
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
                        } else {
                            alert("Request to delete Interface failed: " + errorMessage);
                        }
                    });
                }
            }
            // let component know action is complete
            callbackFunction();
        });
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
        const shouldShowSearchChildForm = this.state.shouldShowSearchChildForm;

        const reactComponents = [];

        if (shouldShowToolbar) {
            reactComponents.push(
                <app.Toolbar key="Toolbar"
                    onCreateClicked={() => this.setState({ shouldShowCreateChildForm: !shouldShowCreateChildForm, shouldShowSearchChildForm: false })}
                    onCancel={() => this.setState({ shouldShowCreateChildForm: false, shouldShowSearchChildForm: false })}
                    onSearchClicked={() => this.setState({shouldShowSearchChildForm: !shouldShowSearchChildForm, shouldShowCreateChildForm: false })}
                    navigationLevel={this.NavigationLevel}
                    currentNavigationLevel={this.state.currentNavigationLevel}
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
                            buttonTitle={buttonTitle}
                            showTitle={true}
                            onSubmit={this.onCreateFunctionBlock}
                            defaultButtonTitle="Submit"
                        />
                    );
                } else if (shouldShowSearchChildForm) {
                    reactComponents.push(
                        <app.SearchForm key="SearchForm"
                            navigationLevel={NavigationLevel}
                            currentNavigationLevel={currentNavigationLevel}
                            showTitle={true}
                            formTitle={"Search Function Blocks"}
                            onUpdate={this.onSearchFunctionBlocks}
                            onPlusButtonClick={this.onAssociateFunctionBlockWithFunctionCatalog}
                            selectedItem={this.state.selectedItem}
                            searchResults={this.state.searchResults}
                            isLoadingSearchResults={this.state.isLoadingSearchResults}
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
                            defaultButtonTitle="Submit"
                        />
                    );
                } else if (shouldShowSearchChildForm) {
                    reactComponents.push(
                        <app.SearchForm key="SearchForm"
                            navigationLevel={NavigationLevel}
                            currentNavigationLevel={currentNavigationLevel}
                            showTitle={true}
                            formTitle={"Search Interfaces"}
                            onUpdate={this.onSearchMostInterfaces}
                            onPlusButtonClick={this.onAssociateMostInterfaceWithFunctionBlock}
                            selectedItem={this.state.selectedItem}
                            searchResults={this.state.searchResults}
                            isLoadingSearchResults={this.state.isLoadingSearchResults}
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
            const theme = this.state.account ? this.state.account.theme : "Tidy";
            return (
                <div id="main-content" className="container">
                    <app.SettingsPage theme={theme} onThemeChange={this.onThemeChange}/>
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
                        <i id="settings-icon" className="fa fa-cog fa-lg" onClick={this.handleSettingsClick}/>
                    </div>
                </div>
                {this.renderMainContent()}
            </div>
        );
    }
}

registerClassWithGlobalScope("App", App);

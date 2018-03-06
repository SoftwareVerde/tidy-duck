class App extends React.Component {
    static alert(title, content, onConfirm) {
        const alertQueue = App._instance.state.alertQueue;

        // add to queue
        alertQueue.push({
            title: title,
            content: content,
            onConfirm: onConfirm,
            isConfirmAlert: false
        });
        App._instance.setState({
            alertQueue: alertQueue
        });

        if (App._instance.state.alert.shouldShow) {
            // alert is current being displayed, bail out
            return;
        }

        const alert = alertQueue.shift();
        App._instance.displayAlert(alert);
    }

    static confirm(title, content, onConfirm, onCancel) {
        const alertQueue = App._instance.state.alertQueue;
        // add to queue
        alertQueue.push({
            title: title,
            content: content,
            onConfirm: onConfirm,
            onCancel: onCancel,
            isConfirmAlert: true
        });
        App._instance.setState({
            alertQueue: alertQueue
        });

        if (App._instance.state.alert.shouldShow) {
            // alert is current being displayed, bail out
            return;
        }

        const alert = alertQueue.shift();
        App._instance.displayAlert(alert);
    }

    constructor(props) {
        super(props);

        App._instance = this;

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
            development:    "Development",
            types:          "Types",
            reviews:        "Reviews",
            accounts:       "Accounts",
            trash:          "Trash"
        };

        this.developmentRoles = {
            functionCatalog:  "Function Catalog",
            functionBlock:    "Function Block",
            mostInterface:    "Interface"
        };

        this.typesRoles = {
            createType: "Create Type",
            editType:   "Edit Type"
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
            account:                        null,
            accountsForEditForm:            [],
            companies:                      [],
            navigationItems:                [],
            parentHistory:                  [],
            searchResults:                  [],
            lastSearchResultTimestamp:      0,
            functionCatalogs:               [],
            functionBlocks:                 [],
            mostInterfaces:                 [],
            mostFunctions:                  [],
            mostTypes:                      [],
            primitiveTypes:                 [],
            mostUnits:                      [],
            mostFunctionStereotypes:        [],
            reviews:                        [],
            currentReview:                  null,
            activeRole:                     null,
            activeSubRole:                  null,
            selectedItem:                   null,
            parentItem:                     null,
            proposedItem:                   null,
            currentNavigationLevel:         this.NavigationLevel.versions,
            shouldShowToolbar:              true,
            shouldShowCreateChildForm:      false,
            createButtonState:              this.CreateButtonState.normal,
            selectedFunctionStereotype:     null,
            shouldShowSearchChildForm:      false,
            shouldShowDeletedChildItems:    false,
            shouldShowLoadingIcon:          false,
            loadingTimeout:                 null,
            isLoadingChildren:              true,
            isLoadingSearchResults:         false,
            isLoadingMostTypes:             true,
            isLoadingPrimitiveTypes:        true,
            isLoadingUnits:                 true,
            isLoadingReviews:               true,
            filterString:                   null,
            reviewCommentString:            null,
            shouldShowFilteredResults:      false,
            shouldShowEditForm:             false,
            releasingFunctionCatalog:       null,
            alert: {
                shouldShow:     false,
                title:          "",
                content:        "",
                onConfirm:      null,
                onCancel:       null,
                isConfirmAlert: false
            },
            alertQueue:                 []
        };

        this.renderChildItems = this.renderChildItems.bind(this);
        this.renderMainContent = this.renderMainContent.bind(this);
        this.renderRoleToggle = this.renderRoleToggle.bind(this);
        this.renderSubRoleToggle = this.renderSubRoleToggle.bind(this);
        this.renderFilterBar = this.renderFilterBar.bind(this);

        this.getCurrentAccountAuthor = this.getCurrentAccountAuthor.bind(this);
        this.getCurrentAccountCompany = this.getCurrentAccountCompany.bind(this);
        this.onResetPassword = this.onResetPassword.bind(this);
        this.getAllCompanies = this.getAllCompanies.bind(this);
        this.getAccountsForEditForm = this.getAccountsForEditForm.bind(this);
        this.onCreateCompany = this.onCreateCompany.bind(this);
        this.getFunctionCatalogsForCurrentVersion = this.getFunctionCatalogsForCurrentVersion.bind(this);
        this.getValidRoleItems = this.getValidRoleItems.bind(this);

        this.addFunctionCatalogNavigationItem = this.addFunctionCatalogNavigationItem.bind(this);
        this.onFunctionCatalogSelected = this.onFunctionCatalogSelected.bind(this);
        this.onFilterFunctionCatalogs = this.onFilterFunctionCatalogs.bind(this);
        this.onCreateFunctionCatalog = this.onCreateFunctionCatalog.bind(this);
        this.onUpdateFunctionCatalog = this.onUpdateFunctionCatalog.bind(this);
        this.onForkFunctionCatalog = this.onForkFunctionCatalog.bind(this);
        this.onDeleteFunctionCatalog = this.onDeleteFunctionCatalog.bind(this);
        this.onMarkFunctionCatalogAsDeleted = this.onMarkFunctionCatalogAsDeleted.bind(this);
        this.onRestoreFunctionCatalogFromTrash = this.onRestoreFunctionCatalogFromTrash.bind(this);
        this.onReleaseFunctionCatalog = this.onReleaseFunctionCatalog.bind(this);
        this.onFunctionCatalogReleased = this.onFunctionCatalogReleased.bind(this);

        this.addFunctionBlockNavigationItem = this.addFunctionBlockNavigationItem.bind(this);
        this.onFunctionBlockSelected = this.onFunctionBlockSelected.bind(this);
        this.onCreateFunctionBlock = this.onCreateFunctionBlock.bind(this);
        this.onUpdateFunctionBlock = this.onUpdateFunctionBlock.bind(this);
        this.onForkFunctionBlock = this.onForkFunctionBlock.bind(this);
        this.onSearchFunctionBlocks = this.onSearchFunctionBlocks.bind(this);
        this.onFilterFunctionBlocks = this.onFilterFunctionBlocks.bind(this);
        this.onAssociateFunctionBlockWithFunctionCatalog = this.onAssociateFunctionBlockWithFunctionCatalog.bind(this);
        this.onDeleteFunctionBlock = this.onDeleteFunctionBlock.bind(this);
        this.onMarkFunctionBlockAsDeleted = this.onMarkFunctionBlockAsDeleted.bind(this);
        this.onRestoreFunctionBlockFromTrash = this.onRestoreFunctionBlockFromTrash.bind(this);
        this.disassociateFunctionBlockFromFunctionCatalog = this.disassociateFunctionBlockFromFunctionCatalog.bind(this);
        this.deleteFunctionBlockFromDatabase = this.deleteFunctionBlockFromDatabase.bind(this);

        this.addMostInterfaceNavigationItem = this.addMostInterfaceNavigationItem.bind(this);
        this.onMostInterfaceSelected = this.onMostInterfaceSelected.bind(this);
        this.onCreateMostInterface = this.onCreateMostInterface.bind(this);
        this.onUpdateMostInterface = this.onUpdateMostInterface.bind(this);
        this.onForkMostInterface = this.onForkMostInterface.bind(this);
        this.onSearchMostInterfaces = this.onSearchMostInterfaces.bind(this);
        this.onFilterMostInterfaces = this.onFilterMostInterfaces.bind(this);
        this.onAssociateMostInterfaceWithFunctionBlock = this.onAssociateMostInterfaceWithFunctionBlock.bind(this);
        this.onDeleteMostInterface = this.onDeleteMostInterface.bind(this);
        this.onMarkMostInterfaceAsDeleted = this.onMarkMostInterfaceAsDeleted.bind(this);
        this.onRestoreMostInterfaceFromTrash = this.onRestoreMostInterfaceFromTrash.bind(this);
        this.disassociateMostInterfaceFromFunctionBlock = this.disassociateMostInterfaceFromFunctionBlock.bind(this);
        this.deleteMostInterfaceFromDatabase = this.deleteMostInterfaceFromDatabase.bind(this);

        this.addMostFunctionNavigationItem = this.addMostFunctionNavigationItem.bind(this);
        this.onMostFunctionSelected = this.onMostFunctionSelected.bind(this);
        this.onCreateMostFunction = this.onCreateMostFunction.bind(this);
        this.onUpdateMostFunction = this.onUpdateMostFunction.bind(this);
        this.onDeleteMostFunction = this.onDeleteMostFunction.bind(this);
        this.onMarkMostFunctionAsDeleted = this.onMarkMostFunctionAsDeleted.bind(this);
        this.onRestoreMostFunctionFromTrash = this.onRestoreMostFunctionFromTrash.bind(this);

        this.updateNavigationItems = this.updateNavigationItems.bind(this);
        this.updateParentHistory = this.updateParentHistory.bind(this);
        this.updateChildItemDisplayInformation = this.updateChildItemDisplayInformation.bind(this);
        this.getChildItemsFromVersions = this.getChildItemsFromVersions.bind(this);
        this.onChildItemVersionChanged = this.onChildItemVersionChanged.bind(this);
        this.updateMostTypes = this.updateMostTypes.bind(this);
        this.onTypeCreated = this.onTypeCreated.bind(this);
        this.onTypeChanged = this.onTypeChanged.bind(this);
        this.updateMostFunctionStereotypes = this.updateMostFunctionStereotypes.bind(this);
        this.updateReviews = this.updateReviews.bind(this);

        this.onApprovalReviewClicked = this.onApprovalReviewClicked.bind(this);
        this.onReviewSelected = this.onReviewSelected.bind(this);
        this.onReviewVoteClicked = this.onReviewVoteClicked.bind(this);
        this.onSaveTicketUrlClicked = this.onSaveTicketUrlClicked.bind(this);
        this.onApproveButtonClicked = this.onApproveButtonClicked.bind(this);

        this.onTrashItemSelected = this.onTrashItemSelected.bind(this);

        this.handleFunctionStereotypeClick = this.handleFunctionStereotypeClick.bind(this);
        this.handleSettingsClick = this.handleSettingsClick.bind(this);
        this.handleRoleClick = this.handleRoleClick.bind(this);
        this.getLoadingIconTimeoutStateChange = this.getLoadingIconTimeoutStateChange.bind(this);
        this.handleTrashButtonClick = this.handleTrashButtonClick.bind(this);
        this.onThemeChange = this.onThemeChange.bind(this);
        this.onDefaultModeChanged = this.onDefaultModeChanged.bind(this);
        this.setTheme = this.setTheme.bind(this);

        this.goToHistoryState = this.goToHistoryState.bind(this);

        this.logout = this.logout.bind(this);

        this.onDuckClick = this.onDuckClick.bind(this);
        this.showDuckAlert = this.showDuckAlert.bind(this);

        this.displayAlert = this.displayAlert.bind(this);

        const thisApp = this;

        checkAccount(function (checkData) {
            if (checkData.wasSuccess) {
                getAccount(checkData.accountId, function(accountData) {
                    const account = Account.fromJson(accountData);
                    thisApp.setTheme(account.getSettings().getTheme());

                    thisApp.setState({
                        account: account
                    });
                    
                    const validRoles = thisApp.getValidRoleItems(account);
                    const defaultMode = account.getSettings().getDefaultMode();
                    const defaultValidRole = validRoles[0];
                    if (validRoles.includes(defaultMode)) {
                        thisApp.handleRoleClick(defaultMode, null, false);
                    }
                    else {
                        thisApp.handleRoleClick(defaultValidRole, null, false);
                    }
                });
            }
        });

        this.getAllCompanies();
        this.getAccountsForEditForm();
        this.updateMostTypes();
        this.updateMostFunctionStereotypes();
    }

    displayAlert(alert) {
        const thisApp = this;
        const alertQueue = this.state.alertQueue;

        this.setState({
            alert: {
                shouldShow: true,
                title:      alert.title,
                content:    alert.content,
                onConfirm:  function () {
                    if (typeof alert.onConfirm == "function") {
                        alert.onConfirm();
                    }

                    if (alertQueue.length == 0) {
                        thisApp.setState({
                            alert: {
                                shouldShow:     false,
                                title:          "",
                                content:        "",
                                onConfirm:      null,
                                onCancel:       null,
                                isConfirmAlert: false
                            }
                        });
                    }
                    else {
                        const nextAlert = alertQueue.shift();
                        thisApp.displayAlert(nextAlert);
                    }
                },
                isConfirmAlert: alert.isConfirmAlert,
                onCancel: function() {
                    if (typeof alert.onCancel == "function") {
                        alert.onCancel();
                    }

                    if (alertQueue.length == 0) {
                        thisApp.setState({
                            alert: {
                                shouldShow:     false,
                                title:          "",
                                content:        "",
                                onConfirm:      null,
                                onCancel:       null,
                                isConfirmAlert: false
                            }
                        });
                    }
                    else {
                        const nextAlert = alertQueue.shift();
                        thisApp.displayAlert(nextAlert);
                    }
                }
            }
        });
    }

    onDuckClick() {
        const accountDefaultMode = this.state.account.getSettings().getDefaultMode();
        this.handleRoleClick(accountDefaultMode, null, false);
    }

    showDuckAlert() {
        const account = this.state.account;
        const settings = account.getSettings();

        const theme = settings.getTheme();
        let message = "";
        switch (theme) {
            case "Darkwing": {
                message = "I am the terror that flaps in the night!";
            } break;
            default: {
                message = "Quack quack!";
            }
        }

        app.App.alert(theme, message);
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
            functionCatalog.setBaseVersionId(functionCatalogId);

            const functionCatalogs = thisApp.state.functionCatalogs.concat(functionCatalog);
            functionCatalogs.sort(function(a, b) {
                return (a.getName().concat("_" + a.getId())).localeCompare((b.getName().concat("_" + b.getId())), undefined, {numeric : true, sensitivity: 'base'});
            });

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
        return this.state.account.toAuthor();
    }

    getCurrentAccountCompany() {
        return this.state.account.getCompany();
    }

    onResetPassword(account) {
        const accountId = account.getId();
        const accountName = account.getName();

        const resetPasswordFunction = function() {
            resetPassword(accountId, function(data) {
                if (! data.wasSuccess) {
                    app.App.alert("Unable to reset password", data.errorMessage);
                }
                else {
                    app.App.alert("Password reset", accountName + "'s password has been reset to: " + data.newPassword);
                }
            });
        };

        app.App.confirm("Reset Password", "Are you sure you want to reset the password for " + accountName + "?", resetPasswordFunction);
    }

    getAllCompanies() {
        const thisApp = this;

        getCompanies(function(data) {
            if (data.wasSuccess) {
                const companiesJson = data.companies;
                const companies = [];

                for (let i in companiesJson) {
                    const company = Company.fromJson(companiesJson[i]);
                    companies.push(company);
                }

                thisApp.setState({
                    companies: companies
                });
            }
            else {
                console.error("Unable to get companies:" + data.errorMessage);
            }
        });
    }

    getAccountsForEditForm() {
        const thisApp = this;

        getActiveAccountsWithModifyPermission(function (data) {
            if (data.wasSuccess) {
                const accountsJson = data.accounts;
                const accounts = accountsJson.map(Account.fromJson);
                const sortedAccounts = accounts.sort(function(a, b) {
                    return a.getName().localeCompare(b.getName(), undefined, {numeric : true, sensitivity: 'base'});
                });

                thisApp.setState({
                    accountsForEditForm: sortedAccounts
                });
            }
        });
    }

    onCreateCompany(newCompany, callbackFunction) {
        const newCompanyJson = Company.toJson(newCompany);
        const thisApp = this;
        const companies = this.state.companies;

        createNewCompany(newCompanyJson, function(data) {
           if (! data.wasSuccess) {
               app.App.alert("Unable to create company", data.errorMessage);
           }
           else {
               newCompany.setId(data.companyId);
               companies.push(newCompany);

               thisApp.setState({
                   companies: companies
               });
           }
           callbackFunction(data.wasSuccess);
        });
    }

    onUpdateFunctionCatalog(functionCatalog) {
        const thisApp = this;
        const functionCatalogJson = FunctionCatalog.toJson(functionCatalog);
        const functionCatalogId = functionCatalog.getId();
        const createButtonState = this.CreateButtonState.animate;

        this.setState({
            selectedItem:   functionCatalog,
            createButtonState: createButtonState
        });

        updateFunctionCatalog(functionCatalogId, functionCatalogJson, false, function(data) {
            if (! data.wasSuccess) {
                app.App.alert("Unable to update Function Catalog", data.errorMessage, function() {
                    // Update form to show changes were not saved.
                    thisApp.setState({
                        createButtonState:  thisApp.CreateButtonState.normal,
                    });
                });
            }
            else {
                //Update final navigation item to reflect any name changes.
                const navigationItems = thisApp.state.navigationItems;
                const navigationItem = navigationItems.pop();
                navigationItem.setId("functionCatalog" + functionCatalogId);
                navigationItem.setTitle(functionCatalog.getName());
                navigationItem.setIsReleased(functionCatalog.isReleased());
                navigationItem.setIsApproved(functionCatalog.isApproved());
                navigationItem.setHeader(thisApp.headers.functionCatalog);
                navigationItem.setOnClickCallback(function() {
                    thisApp.onFunctionCatalogSelected(functionCatalog, true, false);
                });

                navigationItems.push(navigationItem);

                const functionCatalogs = thisApp.updateChildItemDisplayInformation(FunctionCatalog, functionCatalog, thisApp.state.functionCatalogs);

                thisApp.setState({
                    selectedItem:           functionCatalog,
                    functionCatalogs:       functionCatalogs,
                    navigationItems:        navigationItems,
                    currentNavigationLevel: thisApp.NavigationLevel.functionCatalogs,
                    createButtonState:      thisApp.CreateButtonState.success
                });
            }
        });
    }

    onForkFunctionCatalog(functionCatalog) {
        const thisApp = this;

        forkFunctionCatalog(functionCatalog.getId(), function(data, newFunctionCatalogId) {
            if (! data.wasSuccess) {
                app.App.alert("Unable to fork Function Catalog", data.errorMessage, function() {
                    // nothing to do
                });
            }
            else {
                const functionCatalogs = thisApp.state.functionCatalogs;
                const functionCatalogVersionsJson = functionCatalog.getVersionsJson();
                const newFunctionCatalog = copyMostObject(FunctionCatalog, functionCatalog);
                newFunctionCatalog.setId(newFunctionCatalogId);

                // new catalog is not released or approved and was created by the current user
                newFunctionCatalog.setIsReleased(false);
                newFunctionCatalog.setIsApproved(false);
                newFunctionCatalog.setCreatorAccountId(thisApp.state.account.getId());
                functionCatalogVersionsJson.push(FunctionCatalog.toJson(newFunctionCatalog));
                functionCatalog.setVersionsJson(functionCatalogVersionsJson);

                //Update final navigation item to reflect any changes.
                const navigationItems = thisApp.state.navigationItems;
                const navigationItem = navigationItems.pop();
                navigationItem.setId("functionCatalog" + newFunctionCatalogId);
                navigationItem.setTitle(newFunctionCatalog.getName());
                navigationItem.setIsReleased(newFunctionCatalog.isReleased());
                navigationItem.setIsApproved(newFunctionCatalog.isApproved());
                navigationItem.setHeader(thisApp.headers.functionCatalog);
                navigationItem.setOnClickCallback(function() {
                    thisApp.onFunctionCatalogSelected(newFunctionCatalog, true, false);
                });

                navigationItems.push(navigationItem);

                thisApp.setState({
                    functionCatalogs:       functionCatalogs,
                    selectedItem:           newFunctionCatalog,
                    navigationItems:        navigationItems,
                    currentNavigationLevel: thisApp.NavigationLevel.functionCatalogs
                });
            }
        });
    }

    onReleaseFunctionCatalog(functionCatalog) {
        this.setState({
            releasingFunctionCatalog: functionCatalog
        });
    }

    onFunctionCatalogReleased() {
        // return to main release page
        this.handleRoleClick(this.roles.development, this.developmentRoles.functionCatalog, false);
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
            functionBlock.setBaseVersionId(functionBlockId);

            const functionBlocks = thisApp.state.functionBlocks.concat(functionBlock);
            functionBlocks.sort(function(a, b) {
                return (a.getName().concat("_" + a.getId())).localeCompare((b.getName().concat("_" + b.getId())), undefined, {numeric : true, sensitivity: 'base'});
            });

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
        const functionBlockJson = FunctionBlock.toJson(functionBlock);
        const functionBlockId = functionBlock.getId();
        const createButtonState = this.CreateButtonState.animate;

        // Need to disregard parentItem id if in development mode and navigation level corresponds with development role.
        let functionCatalogId = this.state.parentItem ? this.state.parentItem.getId() : null;
        if (functionCatalogId) {
            if (this.state.activeSubRole == this.developmentRoles.functionBlock) {
                functionCatalogId = null;
            }
        }

        this.setState({
            selectedItem:   functionBlock,
            createButtonState: createButtonState
        });

        updateFunctionBlock(functionBlockId, functionBlockJson, function(data) {
            if (! data.wasSuccess) {
                app.App.alert("Unable to update Function Block", data.errorMessage, function() {
                    thisApp.setState({
                        createButtonState:  thisApp.CreateButtonState.normal,
                    });
                });
            }
            else {
                //Update final navigation item to reflect any name changes.
                const navigationItems = thisApp.state.navigationItems;
                const navigationItem = navigationItems.pop();
                navigationItem.setId("functionBlock" + functionBlockId);
                navigationItem.setTitle(functionBlock.getName());
                navigationItem.setIsReleased(functionBlock.isReleased());
                navigationItem.setIsApproved(functionBlock.isApproved());
                navigationItem.setHeader(thisApp.headers.functionBlock);
                navigationItem.setOnClickCallback(function() {
                    thisApp.onFunctionBlockSelected(functionBlock, true, false);
                });

                navigationItems.push(navigationItem);

                const functionBlocks = thisApp.updateChildItemDisplayInformation(FunctionBlock, functionBlock, thisApp.state.functionBlocks);

                thisApp.setState({
                    selectedItem:               functionBlock,
                    functionBlocks:             functionBlocks,
                    navigationItems:            navigationItems,
                    currentNavigationLevel:     thisApp.NavigationLevel.functionBlocks,
                    createButtonState:          thisApp.CreateButtonState.success,
                });
            }
        });
    }

    onForkFunctionBlock(functionBlock) {
            const thisApp = this;

            // Need to disregard parentItem id if in development mode and navigation level corresponds with development role.
            let functionCatalogId = this.state.parentItem ? this.state.parentItem.getId() : null;
            if (functionCatalogId) {
                if (this.state.activeSubRole == this.developmentRoles.functionBlock) {
                    functionCatalogId = null;
                }
            }

            const functionBlockId = functionBlock.getId();

            forkFunctionBlock(functionCatalogId, functionBlockId, function(data, newFunctionBlockId) {
                if (! data.wasSuccess) {
                    app.App.alert("Unable to fork Function Block", data.errorMessage, function() {
                        // nothing to do
                    });
                }
                else {
                    const functionBlocks = thisApp.state.functionBlocks;
                    const functionBlockVersionsJson = functionBlock.getVersionsJson();
                    const newFunctionBlock = copyMostObject(FunctionBlock, functionBlock);
                    newFunctionBlock.setId(newFunctionBlockId);
                    newFunctionBlock.setIsReleased(false);
                    newFunctionBlock.setIsApproved(false);
                    newFunctionBlock.setCreatorAccountId(thisApp.state.account.getId());
                    // Update versions json, if it exists.
                    if (functionBlockVersionsJson) {
                        functionBlockVersionsJson.push(FunctionBlock.toJson(newFunctionBlock));
                        newFunctionBlock.setVersionsJson(functionBlockVersionsJson);
                    }

                    //Update final navigation item to reflect any name changes.
                    const navigationItems = thisApp.state.navigationItems;
                    const navigationItem = navigationItems.pop();
                    navigationItem.setId("functionBlock" + newFunctionBlockId);
                    navigationItem.setTitle(newFunctionBlock.getName());
                    navigationItem.setIsReleased(newFunctionBlock.isReleased());
                    navigationItem.setIsApproved(newFunctionBlock.isApproved());
                    navigationItem.setHeader(thisApp.headers.functionBlock);
                    navigationItem.setOnClickCallback(function() {
                        thisApp.onFunctionBlockSelected(newFunctionBlock, true, false);
                    });

                    navigationItems.push(navigationItem);

                    thisApp.setState({
                        functionBlocks:             functionBlocks,
                        selectedItem:               newFunctionBlock,
                        navigationItems:            navigationItems,
                        currentNavigationLevel:     thisApp.NavigationLevel.functionBlocks
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

        insertMostInterface(functionBlockId, mostInterfaceJson, function(data, mostInterfaceId) {
            if (! (mostInterfaceId > 0)) {
                app.App.alert("Unable to Create Function", data.errorMessage, function() {
                    thisApp.setState({
                        createButtonState:  thisApp.CreateButtonState.normal
                    });
                });
                return;
            }

            mostInterface.setId(mostInterfaceId);
            const mostInterfaces = thisApp.state.mostInterfaces.concat(mostInterface);
            mostInterfaces.sort(function(a, b) {
                return (a.getName().concat("_" + a.getId())).localeCompare((b.getName().concat("_" + b.getId())), undefined, {numeric : true, sensitivity: 'base'});
            });

            const versions = [ MostInterface.toJson(mostInterface) ];
            mostInterface.setVersionsJson(versions);
            mostInterface.setBaseVersionId(mostInterfaceId);

            thisApp.setState({
                createButtonState:          thisApp.CreateButtonState.success,
                mostInterfaces:             mostInterfaces,
                currentNavigationLevel:     thisApp.NavigationLevel.functionBlocks,
                proposedItem:               null,
                shouldShowCreateChildForm:  false
            });
        });
    }

    onUpdateMostInterface(mostInterface) {
        const thisApp = this;
        const mostInterfaceJson = MostInterface.toJson(mostInterface);
        const mostInterfaceId = mostInterface.getId();
        const createButtonState = this.CreateButtonState.animate;

        // Need to disregard parentItem id if in development mode and navigation level corresponds with development role.
        let functionBlockId = this.state.parentItem ? this.state.parentItem.getId() : null;
        if (functionBlockId) {
            if (this.state.activeSubRole == this.developmentRoles.mostInterface) {
                functionBlockId = null;
            }
        }

        this.setState({
            selectedItem:   mostInterface,
            createButtonState: createButtonState
        });

        updateMostInterface(mostInterfaceId, mostInterfaceJson, function(data) {
            if (! data.wasSuccess) {
                app.App.alert("Unable to update interface", data.errorMessage, function() {
                    // Update form to show changes were not saved.
                    thisApp.setState({
                        createButtonState:  thisApp.CreateButtonState.normal,
                    });
                });
            }
            else {
                //Update final navigation item to reflect any name changes.
                const navigationItems = thisApp.state.navigationItems;
                const navigationItem = navigationItems.pop();
                navigationItem.setId("mostInterface" + mostInterfaceId);
                navigationItem.setTitle(mostInterface.getName());
                navigationItem.setIsReleased(mostInterface.isReleased());
                navigationItem.setIsApproved(mostInterface.isApproved());
                navigationItem.setHeader(thisApp.headers.mostInterface);
                navigationItem.setOnClickCallback(function() {
                    thisApp.onMostInterfaceSelected(mostInterface, true, false);
                });

                navigationItems.push(navigationItem);

                const mostInterfaces = thisApp.updateChildItemDisplayInformation(MostInterface, mostInterface, thisApp.state.mostInterfaces);

                thisApp.setState({
                    selectedItem:           mostInterface,
                    mostInterfaces:         mostInterfaces,
                    navigationItems:        navigationItems,
                    currentNavigationLevel: thisApp.NavigationLevel.mostInterfaces,
                    createButtonState:      thisApp.CreateButtonState.success
                });
            }
        });
    }

    onForkMostInterface(mostInterface) {
        const thisApp = this;

        // Need to disregard parentItem id if in development mode and navigation level corresponds with development role.
        let functionBlockId = this.state.parentItem ? this.state.parentItem.getId() : null;
        if (functionBlockId) {
            if (this.state.activeSubRole == this.developmentRoles.mostInterface) {
                functionBlockId = null;
            }
        }
        const mostInterfaceId = mostInterface.getId();

        forkMostInterface(functionBlockId, mostInterfaceId, function(data, newMostInterfaceId) {
            if (! data.wasSuccess) {
                app.App.alert("Unable to fork interface", data.errorMessage, function() {
                    // nothing to do
                });
            }
            else {
                const mostInterfaces = thisApp.state.mostInterfaces;
                const mostInterfaceVersionsJson = mostInterface.getVersionsJson();
                const newMostInterface = copyMostObject(MostInterface, mostInterface);
                newMostInterface.setId(newMostInterfaceId);
                newMostInterface.setIsReleased(false);
                newMostInterface.setIsApproved(false);
                newMostInterface.setCreatorAccountId(thisApp.state.account.getId());
                // Update versions json, if it exists.
                if (mostInterfaceVersionsJson) {
                    mostInterfaceVersionsJson.push(MostInterface.toJson(newMostInterface));
                    newMostInterface.setVersionsJson(mostInterfaceVersionsJson);
                }

                //Update final navigation item to reflect any name changes.
                const navigationItems = thisApp.state.navigationItems;
                const navigationItem = navigationItems.pop();
                navigationItem.setId("mostInterface" + newMostInterfaceId);
                navigationItem.setTitle(newMostInterface.getName());
                navigationItem.setIsReleased(newMostInterface.isReleased());
                navigationItem.setIsApproved(newMostInterface.isApproved());
                navigationItem.setHeader(thisApp.headers.mostInterface);
                navigationItem.setOnClickCallback(function() {
                    thisApp.onMostInterfaceSelected(newMostInterface, true, false);
                });

                navigationItems.push(navigationItem);

                thisApp.setState({
                    mostInterfaces:         mostInterfaces,
                    selectedItem:           newMostInterface,
                    navigationItems:        navigationItems,
                    currentNavigationLevel: thisApp.NavigationLevel.mostInterfaces
                });
                // Need to get new functions if forking an interface.
                thisApp.onMostInterfaceSelected(newMostInterface);
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

        insertMostFunction(mostInterfaceId, mostFunctionJson, function(data) {
            if (data.wasSuccess) {
                const mostFunctionId = data.mostFunctionId;

                mostFunction.setId(mostFunctionId);
                mostFunction.setAuthor(thisApp.getCurrentAccountAuthor());
                mostFunction.setCompany(thisApp.getCurrentAccountCompany());

                const mostFunctions = thisApp.state.mostFunctions.concat(mostFunction);
                mostFunctions.sort(function(a, b) {
                    return (a.getName().concat("_" + a.getId())).localeCompare((b.getName().concat("_" + b.getId())), undefined, {numeric : true, sensitivity: 'base'});
                });

                thisApp.setState({
                    createButtonState:          thisApp.CreateButtonState.success,
                    mostFunctions:              mostFunctions,
                    currentNavigationLevel:     thisApp.NavigationLevel.mostInterfaces,
                    proposedItem:               null,
                    shouldShowCreateChildForm:  false
                });
            } else {
                app.App.alert("Unable to Create Function", data.errorMessage, function() {
                    thisApp.setState({
                        createButtonState:  thisApp.CreateButtonState.normal
                    });
                });
            }
        });
    }

    onUpdateMostFunction(mostFunction) {
        const thisApp = this;
        const mostInterfaceId = this.state.parentItem.getId();
        const mostFunctionJson = MostFunction.toJson(mostFunction);
        const mostFunctionId = mostFunction.getId();

        thisApp.setState({
            createButtonState:  this.CreateButtonState.animate,
            selectedItem:       mostFunction
        });

        updateMostFunction(mostInterfaceId, mostFunctionId, mostFunctionJson, function(data) {
            if (data.wasSuccess) {
                const mostFunctions = thisApp.state.mostFunctions.filter(function(value) {
                    return value.getId() != mostFunctionId;
                });
                mostFunctions.push(mostFunction);

                // Reset isApproved to false because it was updated.
                mostFunction.setIsApproved(false);

                //Update final navigation item to reflect any name changes.
                const navigationItems = thisApp.state.navigationItems;
                const navigationItem = navigationItems.pop();
                navigationItem.setId("mostFunction" + mostFunction.getId());
                navigationItem.setTitle(mostFunction.getName());
                navigationItem.setIsReleased(mostFunction.isReleased());
                navigationItem.setIsApproved(mostFunction.isApproved());
                navigationItem.setHeader(thisApp.headers.mostFunction);
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
                app.App.alert("Unable to Update Function", data.errorMessage, function() {
                    thisApp.setState({
                        createButtonState:  thisApp.CreateButtonState.normal
                    });
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

    addFunctionCatalogNavigationItem(functionCatalog) {
        const thisApp = this;
        const navigationItems = [];
        const parentHistory = [];

        const navigationItemConfig = new NavigationItemConfig();
        navigationItemConfig.setId("functionCatalog" + functionCatalog.getId());
        navigationItemConfig.setTitle(functionCatalog.getName());
        navigationItemConfig.setIsReleased(functionCatalog.isReleased());
        navigationItemConfig.setIsApproved(functionCatalog.isApproved());
        navigationItemConfig.setIsDeleted(functionCatalog.isDeleted());
        navigationItemConfig.setHeader(thisApp.headers.functionCatalog);

        navigationItemConfig.setOnClickCallback(function() {
            thisApp.onFunctionCatalogSelected(functionCatalog, true, false);
        });

        navigationItems.push(navigationItemConfig);

        // Preserve this selected function catalog as a parent.
        const parentHistoryItem = {
              id: "functionCatalog" + functionCatalog.getId(),
              item: functionCatalog,
        };
        parentHistory.push(parentHistoryItem);

        this.setState({
            navigationItems:                navigationItems,
            parentHistory:                  parentHistory,
            parentItem:                     null
        });
    }

    onFunctionCatalogSelected(functionCatalog, canUseCachedChildren) {
        this.addFunctionCatalogNavigationItem(functionCatalog);

        this.setState({
            searchResults:                  [],
            reviewCommentsString:           null,
            selectedItem:                   functionCatalog,
            proposedItem:                   null,
            functionBlocks:                 canUseCachedChildren ? this.state.functionBlocks : [],
            shouldShowCreateChildForm:      false,
            shouldShowSearchChildForm:      false,
            shouldShowEditForm:             false,
            shouldShowSubmitForReviewForm:  false,
            createButtonState:              this.CreateButtonState.normal,
            currentNavigationLevel:         this.NavigationLevel.functionCatalogs,
            isLoadingChildren:              !canUseCachedChildren
        });

        const thisApp = this;
        getFunctionBlocksForFunctionCatalogId(functionCatalog.getId(), function(functionBlocksJson) {
            if (thisApp.state.currentNavigationLevel == thisApp.NavigationLevel.functionCatalogs) {
                // didn't navigate away while downloading children
                const functionBlocks = [];
                for (let i in functionBlocksJson) {
                    const functionBlockJson = functionBlocksJson[i];
                    const functionBlock = FunctionBlock.fromJson(functionBlockJson);
                    functionBlocks.push(functionBlock);
                }

                functionBlocks.sort(function(a, b) {
                    return (a.getName().concat("_" + a.getId())).localeCompare((b.getName().concat("_" + b.getId())), undefined, {numeric : true, sensitivity: 'base'});
                });

                thisApp.setState({
                    functionBlocks:     functionBlocks,
                    mostInterfaces:     [],
                    isLoadingChildren:  false,
                    shouldShowFilteredResults:  false,
                    filterString:       "",
                });
            }
        })
    }

    onFilterFunctionCatalogs(filterString) {
        const requestTime = (new Date()).getTime();

        if (filterString.length > 1) {
            const thisApp = this;

            clearTimeout(this.state.loadingTimeout);
            let loadingTimeout = this.getLoadingIconTimeoutStateChange(750, false, true);

            this.setState({
                filterString:               filterString,
                shouldShowLoadingIcon:      true,
                isLoadingChildren:          false,
                loadingTimeout:             loadingTimeout
            });

            getFunctionCatalogsMatchingSearchString(filterString, true, function(functionCatalogsJson) {
                clearTimeout(thisApp.state.loadingTimeout);
                if (thisApp.state.currentNavigationLevel == thisApp.NavigationLevel.versions) {
                    if (thisApp.state.lastSearchResultTimestamp > requestTime) {
                        // old results, discard
                        return;
                    }

                    const functionCatalogs = thisApp.getChildItemsFromVersions(functionCatalogsJson, FunctionCatalog.fromJson);

                    thisApp.setState({
                        searchResults:              functionCatalogs,
                        shouldShowFilteredResults:  true,
                        lastSearchResultTimestamp:  requestTime,
                        isLoadingChildren:          false,
                        shouldShowLoadingIcon:      false
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
                shouldShowLoadingIcon:          false,
                shouldShowFilteredResults:      false,
                filterString:                   filterString
            });
        }

    }

    onDeleteFunctionCatalog(functionCatalog, callbackFunction) {
        if (functionCatalog.isReleased()) {
            app.App.alert("Delete Function Catalog", "This Function Catalog released and cannot be deleted.", callbackFunction);
            return;
        }

        const thisApp = this;
        const deleteFunction = function() {
            const functionCatalogId = functionCatalog.getId();
            deleteFunctionCatalog(functionCatalogId, function (success, errorMessage) {
                if (! success) {
                    app.App.alert("Delete Function Catalog", "Request to delete Function Catalog failed: " + errorMessage, callbackFunction);
                    return;
                }

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
                    functionCatalogs: newFunctionCatalogs,
                    currentNavigationLevel: thisApp.NavigationLevel.versions
                });
                if (typeof callbackFunction == "function") {
                    callbackFunction(true);
                }
            });
        };

        app.App.confirm("Delete Function Catalog", "This action will delete the last reference to this function catalog version. Are you sure you want to delete it?", deleteFunction, callbackFunction);
    }

    onMarkFunctionCatalogAsDeleted(functionCatalog, callbackFunction) {
        if (functionCatalog.isReleased()) {
            app.App.alert("Move Function Catalog to Trash Bin", "This Function Catalog released and cannot be moved to the trash bin.", callbackFunction);
            return;
        }

        const moveToTrashFunction = function() {
            const functionCatalogId = functionCatalog.getId();
            markFunctionCatalogAsDeleted(functionCatalogId, function(data) {
                if (! data.wasSuccess) {
                    app.App.alert("Move Function Catalog to Trash Bin", "Request to move Function Catalog to trash bin failed: " + data.errorMessage, callbackFunction);
                    return;
                }

                functionCatalog.setIsDeleted(true);
                // TODO: set deletedDate. Need to get that from API or rely on object being re-retrieved from API.
                functionCatalog.setDeletedDate("Just now");
                const versionsJson = functionCatalog.getVersionsJson();

                for (let i in versionsJson) {
                    const versionJson = versionsJson[i];
                    if (versionJson.id == functionCatalogId) {
                        versionJson.isDeleted = true;
                        // TODO: set deletedDate. Need to get that from API.
                        versionJson.deletedDate = "Just now";
                        break;
                    }
                }

                functionCatalog.setVersionsJson(versionsJson);
                callbackFunction();
            });
        };

        app.App.confirm("Move Function Catalog to Trash", "Are you sure you want to move this Function Catalog to the trash?", moveToTrashFunction, callbackFunction);
    }

    onRestoreFunctionCatalogFromTrash(functionCatalog, callbackFunction) {
        const restoreFunction = function() {
            const functionCatalogId = functionCatalog.getId();
            restoreFunctionCatalogFromTrash(functionCatalogId, function(data) {
                if (! data.wasSuccess) {
                    app.App.alert("Restore Function Catalog from Trash Bin", "Request to restore Function Catalog from trash bin failed: " + data.errorMessage, callbackFunction);
                    return;
                }

                functionCatalog.setIsDeleted(false);
                const versionsJson = functionCatalog.getVersionsJson();

                for (let i in versionsJson) {
                    const versionJson = versionsJson[i];
                    if (versionJson.id == functionCatalogId) {
                        versionJson.isDeleted = false;
                        break;
                    }
                }

                functionCatalog.setVersionsJson(versionsJson);
                callbackFunction(true);
            });
        };

        app.App.confirm("Restore Function Catalog from Trash", "Are you sure you want to restore this Function Catalog?", restoreFunction, callbackFunction);
    }

    addFunctionBlockNavigationItem(functionBlock) {
        const thisApp = this;
        const itemId = "functionBlock" + functionBlock.getId();
        let newParentHistory = [];
        const parentItem = this.updateParentHistory(itemId, functionBlock, newParentHistory);
        let newNavigationItems = [];

        const navigationItemConfig = new NavigationItemConfig();
        navigationItemConfig.setId(itemId);
        navigationItemConfig.setTitle(functionBlock.getName());
        navigationItemConfig.setIsReleased(functionBlock.isReleased());
        navigationItemConfig.setIsApproved(functionBlock.isApproved());
        navigationItemConfig.setIsDeleted(functionBlock.isDeleted());
        navigationItemConfig.setHeader(thisApp.headers.functionBlock);
        navigationItemConfig.setOnClickCallback(function() {
            thisApp.onFunctionBlockSelected(functionBlock, true);
        });

        this.updateNavigationItems(itemId, navigationItemConfig, newNavigationItems);

        this.setState({
            navigationItems:                newNavigationItems,
            parentHistory:                  newParentHistory,
            parentItem:                     parentItem
        });
    }

    onFunctionBlockSelected(functionBlock, canUseCachedChildren) {
        this.addFunctionBlockNavigationItem(functionBlock);

        this.setState({
            searchResults:                  [],
            selectedItem:                   functionBlock,
            proposedItem:                   null,
            mostInterfaces:                 canUseCachedChildren ? this.state.mostInterfaces : [],
            shouldShowCreateChildForm:      false,
            shouldShowSearchChildForm:      false,
            shouldShowEditForm:             false,
            shouldShowSubmitForReviewForm:  false,
            createButtonState:              this.CreateButtonState.normal,
            currentNavigationLevel:         this.NavigationLevel.functionBlocks,
            isLoadingChildren:              !canUseCachedChildren,
        });

        const thisApp = this;
        getMostInterfacesForFunctionBlockId(functionBlock.getId(), function(mostInterfacesJson) {
            if (thisApp.state.currentNavigationLevel == thisApp.NavigationLevel.functionBlocks) {
                // didn't navigate away while downloading children
                const mostInterfaces = [];
                for (let i in mostInterfacesJson) {
                    const mostInterfaceJson = mostInterfacesJson[i];
                    const mostInterface = MostInterface.fromJson(mostInterfaceJson);
                    mostInterfaces.push(mostInterface);
                }

                mostInterfaces.sort(function(a, b) {
                    return (a.getName().concat("_" + a.getId())).localeCompare((b.getName().concat("_" + b.getId())), undefined, {numeric : true, sensitivity: 'base'});
                });

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

        if (searchString.length > 1) {
            const thisApp = this;

            clearTimeout(this.state.loadingTimeout);
            let loadingTimeout = this.getLoadingIconTimeoutStateChange(750, true);

            this.setState({
                shouldShowLoadingIcon:  true,
                isLoadingSearchResults: false,
                loadingTimeout:         loadingTimeout
            });

            getFunctionBlocksMatchingSearchString(searchString, false, function (functionBlocksJson) {
                clearTimeout(thisApp.state.loadingTimeout);
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
                        isLoadingSearchResults: false,
                        shouldShowLoadingIcon: false
                    });
                }
            });
        } else {
            this.setState({
                searchResults: [],
                lastSearchResultTimestamp:  requestTime,
                shouldShowLoadingIcon:      false,
                isLoadingSearchResults:     false
            });
        }
    }

    onFilterFunctionBlocks(filterString) {
        const requestTime = (new Date()).getTime();

        if (filterString.length > 1) {
            const thisApp = this;

            clearTimeout(this.state.loadingTimeout);
            let loadingTimeout = this.getLoadingIconTimeoutStateChange(750, false, true);

            this.setState({
                filterString:               filterString,
                shouldShowLoadingIcon:      true,
                isLoadingChildren:          false,
                loadingTimeout:             loadingTimeout
            });

            getFunctionBlocksMatchingSearchString(filterString, true, function(functionBlocksJson) {
                clearTimeout(thisApp.state.loadingTimeout);
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
                        shouldShowLoadingIcon:      false
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
                shouldShowLoadingIcon:          false,
                shouldShowFilteredResults:      false,
                filterString:                   filterString
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
                functionBlocks.sort(function(a, b) {
                    return (a.getName().concat("_" + a.getId())).localeCompare((b.getName().concat("_" + b.getId())), undefined, {numeric : true, sensitivity: 'base'});
                });

                thisApp.setState({
                    searchResults: newSearchResults,
                    functionBlocks: functionBlocks
                });
            }
            else {
                app.App.alert("Associate Function Block", "Request to associate function block failed: " + errorMessage);
            }
        });
    }

    onDeleteFunctionBlock(functionBlock, callbackFunction) {
        const selectedItem = this.state.selectedItem;

        if (selectedItem && selectedItem.isReleased()) {
            app.App.alert("Delete Function Block", "Unable to delete function block. Currently selected Function Catalog is released.", callbackFunction);
            return;
        }

        if (!functionBlock.isDeleted()) {
            app.App.alert("Delete Function Block", "Unable to delete function block.  Function blocks must be moved to trash before it can be deleted.", callbackFunction);
            return;
        }

        this.deleteFunctionBlockFromDatabase(functionBlock, callbackFunction);
    }

    onMarkFunctionBlockAsDeleted(functionBlock, callbackFunction) {
        const selectedItem = this.state.selectedItem;

        if (functionBlock.isReleased()) {
            app.App.alert("Delete Function Block", "Unable to delete function block. Currently selected Function Catalog is released.", callbackFunction);
            return;
        }

        // If this item has a containing parent, simply disassociate it.
        if (selectedItem) {
            if (selectedItem.isApproved()) {
                app.App.alert("Delete Function Block", "Unable to delete function block. Currently selected Function Catalog is approved for release.", callbackFunction);
                return;
            }
        }

        const moveToTrashFunction = function() {
            const functionBlockId = functionBlock.getId();
            markFunctionBlockAsDeleted(functionBlockId, function(data) {
                if (! data.wasSuccess) {
                    app.App.alert("Move Function Block to Trash Bin", "Request to move function block to trash bin failed: " + data.errorMessage, callbackFunction);
                    return;
                }

                functionBlock.setIsDeleted(true);
                // TODO: set deletedDate. Need to get that from API or rely on object being re-retrieved from API.
                functionBlock.setDeletedDate("Just now");
                const versionsJson = functionBlock.getVersionsJson();

                for (let i in versionsJson) {
                    const versionJson = versionsJson[i];
                    if (versionJson.id === functionBlockId) {
                        versionJson.isDeleted = true;
                        // TODO: set deletedDate. Need to get that from API.
                        versionJson.deletedDate = "Just now";
                        break;
                    }
                }

                functionBlock.setVersionsJson(versionsJson);
                callbackFunction();
            });
        };

        app.App.confirm("Move Function Block to Trash", "Are you sure you want to move this function block to the trash?", moveToTrashFunction, callbackFunction);
    }

    onRestoreFunctionBlockFromTrash(functionBlock, callbackFunction) {
        const restoreFunction = function() {
            const functionBlockId = functionBlock.getId();
            restoreFunctionBlockFromTrash(functionBlockId, function(data) {
                if (! data.wasSuccess) {
                    app.App.alert("Restore Function Block from Trash Bin", "Request to restore function block from trash bin failed: " + data.errorMessage, callbackFunction);
                    return;
                }

                functionBlock.setIsDeleted(false);
                const versionsJson = functionBlock.getVersionsJson();

                for (let i in versionsJson) {
                    const versionJson = versionsJson[i];
                    if (versionJson.id == functionBlockId) {
                        versionJson.isDeleted = false;
                        break;
                    }
                }

                functionBlock.setVersionsJson(versionsJson);
                callbackFunction(true);
            });
        };

        app.App.confirm("Restore Function Block from Trash", "Are you sure you want to restore this function block?", restoreFunction, callbackFunction);
    }

    disassociateFunctionBlockFromFunctionCatalog(functionBlock, callbackFunction) {
        const thisApp = this;
        const functionCatalogId = this.state.selectedItem.getId();
        const functionBlockId = functionBlock.getId();

        const deleteFunction = function() {
            disassociateFunctionBlockFromFunctionCatalog(functionCatalogId, functionBlockId, function (success, errorMessage) {
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

                    if (typeof callbackFunction == "function") {
                        callbackFunction();
                    }
                }
                else {
                    app.App.alert("Disassociate Function Block", "Request to disassociate function block failed: " + errorMessage, callbackFunction);
                }
            });
        };
        app.App.confirm("Remove Function Block", "Are you sure want to remove the association between this function block and the currently selected Function Catalog?", deleteFunction, callbackFunction);
    }

    deleteFunctionBlockFromDatabase(functionBlock, callbackFunction, shouldSkipConfirmation) {
        const thisApp = this;
        const deleteFunction = function() {
            const functionBlockId = functionBlock.getId();

            deleteFunctionBlock(functionBlockId, function (success, errorMessage) {
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

                    if (typeof callbackFunction == "function") {
                        callbackFunction(true);
                    }
                }
                else {
                    app.App.alert("Delete Function Block", "Request to delete function block failed: " + errorMessage, callbackFunction);
                }
            });
        };

        if (! shouldSkipConfirmation) {
            app.App.confirm("Delete Function Block", "This action will permanently delete this function block.  Are you sure you want to delete it?", deleteFunction, callbackFunction);
            return;
        }

        deleteFunction();
    }

    addMostInterfaceNavigationItem(mostInterface) {
        const thisApp = this;
        const itemId = "mostInterface" + mostInterface.getId();
        let newParentHistory = [];
        const parentItem = this.updateParentHistory(itemId, mostInterface, newParentHistory);
        let newNavigationItems = [];

        const navigationItemConfig = new NavigationItemConfig();
        navigationItemConfig.setId(itemId);
        navigationItemConfig.setTitle(mostInterface.getName());
        navigationItemConfig.setIsReleased(mostInterface.isReleased());
        navigationItemConfig.setIsApproved(mostInterface.isApproved());
        navigationItemConfig.setIsDeleted(mostInterface.isDeleted());
        navigationItemConfig.setHeader(thisApp.headers.mostInterface);
        navigationItemConfig.setOnClickCallback(function() {
            thisApp.onMostInterfaceSelected(mostInterface, true);
        });

        this.updateNavigationItems(itemId, navigationItemConfig, newNavigationItems);

        this.setState({
            navigationItems:                newNavigationItems,
            parentHistory:                  newParentHistory,
            parentItem:                     parentItem
        });
    }

    onMostInterfaceSelected(mostInterface, canUseCachedChildren) {
        this.addMostInterfaceNavigationItem(mostInterface);

        this.setState({
            searchResults:                  [],
            selectedItem:                   mostInterface,
            proposedItem:                   null,
            shouldShowCreateChildForm:      false,
            shouldShowSearchChildForm:      false,
            shouldShowEditForm:             false,
            shouldShowSubmitForReviewForm:  false,
            mostFunctions:                  canUseCachedChildren ? this.state.mostFunctions : [],
            createButtonState:              this.CreateButtonState.normal,
            currentNavigationLevel:         this.NavigationLevel.mostInterfaces,
            isLoadingChildren:              !canUseCachedChildren
        });

        this.updateMostTypes();
        this.updateMostFunctionStereotypes();

        const thisApp = this;
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

        if (searchString.length > 1) {
            const thisApp = this;

            clearTimeout(this.state.loadingTimeout);
            let loadingTimeout = this.getLoadingIconTimeoutStateChange(750, true);

            this.setState({
                shouldShowLoadingIcon:  true,
                isLoadingSearchResults: false,
                loadingTimeout:         loadingTimeout
            });

            getMostInterfacesMatchingSearchString(searchString, false, function (mostInterfacesJson) {
                clearTimeout(thisApp.state.loadingTimeout);
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
                        isLoadingSearchResults: false,
                        shouldShowLoadingIcon: false
                    });
                }
            });
        } else {
            this.setState({
                searchResults: [],
                lastSearchResultTimestamp: requestTime,
                shouldShowLoadingIcon:  false,
                isLoadingSearchResults: false
            });
        }
    }

    onFilterMostInterfaces(filterString) {
        const requestTime = (new Date()).getTime();

        clearTimeout(this.state.loadingTimeout);
        let loadingTimeout = this.getLoadingIconTimeoutStateChange(750, false, true);

        if (filterString.length > 1) {
            const thisApp = this;

            this.setState({
                shouldShowLoadingIcon : true,
                filterString:           filterString,
                isLoadingChildren:      false,
                loadingTimeout:         loadingTimeout
            });

            getMostInterfacesMatchingSearchString(filterString, true, function(mostInterfacesJson) {
                clearTimeout(thisApp.state.loadingTimeout);
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
                        shouldShowLoadingIcon:     false
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
                shouldShowLoadingIcon:          false,
                shouldShowFilteredResults:      false,
                filterString:                   filterString
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
                mostInterfaces.sort(function(a, b) {
                    return (a.getName().concat("_" + a.getId())).localeCompare((b.getName().concat("_" + b.getId())), undefined, {numeric : true, sensitivity: 'base'});
                });

                thisApp.setState({
                    searchResults: newSearchResults,
                    mostInterfaces: mostInterfaces
                });
            }
            else {
                app.App.alert("Associate Interface", "Request to associate interface failed: " + errorMessage);
            }
        });
    }

    onDeleteMostInterface(mostInterface, callbackFunction) {
        const selectedItem = this.state.selectedItem;

        if (selectedItem && selectedItem.isReleased()) {
            app.App.alert("Delete Interface", "Unable to delete interface. Currently selected function block is approved for release.", callbackFunction);
        }

        if (!mostInterface.isDeleted()) {
            app.App.alert("Delete Most Interface", "Unable to delete interface.  Interfaces must be moved to trash before it can be deleted.", callbackFunction);
            return;
        }

        if (selectedItem) {
            if (selectedItem.isApproved()) {
                app.App.alert("Delete Interface", "Unable to delete interface.  The parent function block is approved for release and cannot be modified.", callbackFunction);
                return;
            }
        }

        this.deleteMostInterfaceFromDatabase(mostInterface, callbackFunction);
    }

    onMarkMostInterfaceAsDeleted(mostInterface, callbackFunction) {
        const selectedItem = this.state.selectedItem;

        if (mostInterface.isReleased()) {
            app.App.alert("Delete Interface", "Unable to delete interface. This interface is released and cannot be deleted.", callbackFunction);
        }

        if (selectedItem) {
            if (selectedItem.isApproved()) {
                app.App.alert("Delete Interface", "Unable to delete interface.  The parent function block is approved for release and cannot be modified.", callbackFunction);
                return;
            }
        }

        const moveToTrashFunction = function() {
            const mostInterfaceId = mostInterface.getId();
            markMostInterfaceAsDeleted(mostInterfaceId, function(data) {
                if (! data.wasSuccess) {
                    app.App.alert("Move Interface to Trash Bin", "Request to move interface to trash bin failed: " + data.errorMessage, callbackFunction);
                    return;
                }

                mostInterface.setIsDeleted(true);
                // TODO: set deletedDate. Need to get that from API or rely on object being re-retrieved from API.
                mostInterface.setDeletedDate("Just now");
                const versionsJson = mostInterface.getVersionsJson();

                for (let i in versionsJson) {
                    const versionJson = versionsJson[i];
                    if (versionJson.id == mostInterfaceId) {
                        versionJson.isDeleted = true;
                        // TODO: set deletedDate. Need to get that from API.
                        versionJson.deletedDate = "Just now";
                        break;
                    }
                }

                mostInterface.setVersionsJson(versionsJson);
                callbackFunction();
            });
        };

        app.App.confirm("Move Interface to Trash", "Are you sure you want to move this interface to the trash?", moveToTrashFunction, callbackFunction);
    }

    onRestoreMostInterfaceFromTrash(mostInterface, callbackFunction) {
        const restoreFunction = function() {
            const mostInterfaceId = mostInterface.getId();
            restoreMostInterfaceFromTrash(mostInterfaceId, function(data) {
                if (! data.wasSuccess) {
                    app.App.alert("Restore Interface from Trash Bin", "Request to restore interface from trash bin failed: " + data.errorMessage, callbackFunction);
                    return;
                }

                mostInterface.setIsDeleted(false);
                const versionsJson = mostInterface.getVersionsJson();

                for (let i in versionsJson) {
                    const versionJson = versionsJson[i];
                    if (versionJson.id == mostInterfaceId) {
                        versionJson.isDeleted = false;
                        break;
                    }
                }

                mostInterface.setVersionsJson(versionsJson);
                callbackFunction(true);
            });
        };

        app.App.confirm("Restore Interface from Trash", "Are you sure you want to restore this interface?", restoreFunction, callbackFunction);
    }

    disassociateMostInterfaceFromFunctionBlock(mostInterface, callbackFunction) {
        const thisApp = this;
        const functionBlockId = this.state.selectedItem.getId();
        const mostInterfaceId = mostInterface.getId();

        const deleteFunction = function() {
            disassociateMostInterfaceFromFunctionBlock(functionBlockId, mostInterfaceId, function (success, errorMessage) {
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

                    if (typeof callbackFunction == "function") {
                        callbackFunction();
                    }
                }
                else {
                    app.App.alert("Disassociate Interface", "Request to disassociate interface failed: " + errorMessage, callbackFunction);
                }
            });
        };
        app.App.confirm("Remove Function Block", "Are you sure want to remove the association between this interface and the currently selected function block?", deleteFunction, callbackFunction);
    }

    deleteMostInterfaceFromDatabase(mostInterface, callbackFunction, shouldSkipConfirmation) {
        const thisApp = this;
        const deleteFunction = function() {
            const mostInterfaceId = mostInterface.getId();

            deleteMostInterface(mostInterfaceId, function (success, errorMessage) {
                if (! success) {
                    app.App.alert("Delete Interface", "Request to delete interface failed: " + errorMessage, callbackFunction);
                    return;
                }

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

                if (typeof callbackFunction == "function") {
                    callbackFunction(true);
                }
            });
        };

        if (! shouldSkipConfirmation) {
            app.App.confirm("Delete Interface", "Are you sure you want to delete this interface?", deleteFunction, callbackFunction);
            return;
        }

        deleteFunction();
    }

    addMostFunctionNavigationItem(mostFunction) {
        const thisApp = this;
        const itemId = "mostFunction" + mostFunction.getId();
        let newParentHistory = [];
        const parentItem = this.updateParentHistory(itemId, mostFunction, newParentHistory);
        let newNavigationItems = [];

        const navigationItemConfig = new NavigationItemConfig();
        navigationItemConfig.setId(itemId);
        navigationItemConfig.setTitle(mostFunction.getName());
        navigationItemConfig.setHeader(thisApp.headers.mostFunction);
        navigationItemConfig.setIsReleased(mostFunction.isReleased());
        navigationItemConfig.setIsApproved(mostFunction.isApproved());
        navigationItemConfig.setIsDeleted(mostFunction.isDeleted());
        navigationItemConfig.setOnClickCallback(function() {
            thisApp.onMostFunctionSelected(mostFunction, true);
        });

        this.updateNavigationItems(itemId, navigationItemConfig, newNavigationItems);

        this.setState({
            navigationItems:                newNavigationItems,
            parentItem:                     parentItem
        });
    }

    onMostFunctionSelected(mostFunction) {
        this.addMostFunctionNavigationItem(mostFunction);

        this.setState({
            searchResults:                  [],
            selectedItem:                   mostFunction,
            proposedItem:                   null,
            createButtonState:              this.CreateButtonState.normal,
            currentNavigationLevel:         this.NavigationLevel.mostFunctions,
            shouldShowCreateChildForm:      false,
            shouldShowFilteredResults:      false,
            shouldShowEditForm:             false,
            shouldShowSubmitForReviewForm:  false
        });

        // this.updateMostTypes();
    }

    onDeleteMostFunction(mostFunction, callbackFunction) {
        const thisApp = this;
        const selectedItem = this.state.selectedItem;

        if (selectedItem && selectedItem.isApproved()) {
            app.App.alert("Delete Function", "Unable to delete Function. Currently selected interface is approved for release.", callbackFunction);
            return;
        }

        if (!mostFunction.isDeleted()) {
            app.App.alert("Delete Function", "Unable to delete function.  Functions must be moved to trash before it can be deleted.", callbackFunction);
            return;
        }

        const deleteFunction = function() {
            const mostInterfaceId = selectedItem.getId();
            const mostFunctionId = mostFunction.getId();

            deleteMostFunction(mostInterfaceId, mostFunctionId, function (success, errorMessage) {
                if (! success) {
                    app.App.alert("Delete Function", "Request to delete function failed: " + errorMessage);
                    return;
                }

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

                if (typeof callbackFunction == "function") {
                    callbackFunction(true);
                }
            });
        };

        app.App.confirm("Delete Function", "This action will permanently delete this function. Are you sure you want to delete it?", deleteFunction, callbackFunction);
    }

    onMarkMostFunctionAsDeleted(mostFunction, callbackFunction) {
        const selectedItem = this.state.selectedItem;

        const moveToTrashFunction = function() {
            if (selectedItem.isApproved()) {
                app.App.alert("Move Function to Trash Bin", "This currently selected interface is approved for release and cannot be modified.", callbackFunction);
                return;
            }

            const mostFunctionId = mostFunction.getId();
            const mostInterfaceId = selectedItem.getId();

            markMostFunctionAsDeleted(mostInterfaceId, mostFunctionId, function(data) {
                if (! data.wasSuccess) {
                    app.App.alert("Move Function to Trash Bin", "Request to move Function to trash bin failed: " + data.errorMessage, callbackFunction);
                    return;
                }

                mostFunction.setIsDeleted(true);
                // TODO: set deletedDate. Need to get that from API or rely on object being re-retrieved from API.
                mostFunction.setDeletedDate("Just now");

                callbackFunction();
            });
        };

        app.App.confirm("Move Function to Trash", "Are you sure you want to move this Function to the trash?", moveToTrashFunction, callbackFunction);
    }

    onRestoreMostFunctionFromTrash(mostFunction, callbackFunction) {
        const mostInterfaceId = this.state.selectedItem.getId();

        const restoreFunction = function() {
            const mostFunctionId = mostFunction.getId();
            restoreMostFunctionFromTrash(mostInterfaceId, mostFunctionId, function(data) {
                if (! data.wasSuccess) {
                    app.App.alert("Restore Function from Trash Bin", "Request to restore Function from trash bin failed: " + data.errorMessage, callbackFunction);
                    return;
                }

                mostFunction.setIsDeleted(false);
                callbackFunction(true);
            });
        };

        app.App.confirm("Restore Function from Trash", "Are you sure you want to restore this Function?", restoreFunction, callbackFunction);
    }

    updateNavigationItems(itemId, navigationItemConfig, newNavigationItems) {
        const navigationItems = this.state.navigationItems;
        for (let i in navigationItems) {
            const existingNavigationItem = navigationItems[i];
            if (existingNavigationItem.getId() === itemId) {
                break;
            }
            newNavigationItems.push(existingNavigationItem);
        }
        newNavigationItems.push(navigationItemConfig);
    }

    updateParentHistory(itemId, newItem, newParentHistory) {
        const parentHistory = this.state.parentHistory;
        let parentItem = null;
        const parentHistoryItem = {
            id: itemId,
            item: newItem
        };

        for (let i in parentHistory) {
            const existingSelectionHistoryItem = parentHistory[i];
            if (existingSelectionHistoryItem["id"] === itemId) { break; }
            newParentHistory.push(existingSelectionHistoryItem);
            // Preserve reference to parent item.
            parentItem = existingSelectionHistoryItem["item"];
        }
        newParentHistory.push(parentHistoryItem);

        return parentItem;
    }

    updateChildItemDisplayInformation(clazz, mostObject, childItems) {
        const mostObjectId = mostObject.getId();
        for (let i in childItems) {
            const childItem = childItems[i];
            if (childItem.getId() == mostObjectId) {
                const childItemVersionsJson = childItem.getVersionsJson();
                for (let i in childItemVersionsJson) {
                    if (childItemVersionsJson[i].id == mostObjectId) {
                        childItemVersionsJson[i] = clazz.toJson(mostObject);
                    }
                }

                mostObject.setVersionsJson(childItemVersionsJson);
                if (typeof childItem.getAuthor == "function") {
                    mostObject.setAuthor(childItem.getAuthor());
                }
                if (typeof childItem.getCompany == "function") {
                    mostObject.setCompany(childItem.getCompany());
                }
                childItems[i] = mostObject;
                return childItems;
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
                if (displayedVersionJson.isDeleted) {
                    if (! childItemJson.isDeleted) {
                        displayedVersionId = childItemJson.id;
                        displayedVersionJson = childItemJson;
                    }
                }
                else if (childItemJson.isReleased) {
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

        childItems.sort(function(a, b) {
            return (a.getName().concat("_" + a.getId())).localeCompare((b.getName().concat("_" + b.getId())), undefined, {numeric : true, sensitivity: 'base'});
        });

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
            shouldShowCreateChildForm:      shouldShowCreateChildForm,
            selectedFunctionStereotype:     selectedFunctionStereotype,
            shouldShowEditForm:             false,
            shouldShowSubmitForReviewForm:  false
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

    onTypeChanged(changedType) {
        this.updateMostTypes();
    }

    onReviewSubmitted(selectedItem) {
        const thisApp = this;

        const submitReviewFunction = function() {
            const currentNavigationLevel = thisApp.state.currentNavigationLevel;
            let submitFunction = submitFunctionCatalogForReview;

            switch (currentNavigationLevel) {
                case thisApp.NavigationLevel.functionBlocks:
                    submitFunction = submitFunctionBlockForReview;
                    break;
                case thisApp.NavigationLevel.mostInterfaces:
                    submitFunction = submitMostInterfaceforReview;
                    break;
                case thisApp.NavigationLevel.mostFunctions:
                    submitFunction = submitMostFunctionForReview;
                    break;
            }

            submitFunction(selectedItem.getId(), function(data) {
                if (data.wasSuccess) {
                    app.App.alert("Request Review", "Request to review " + selectedItem.getName() + " was successfully submitted.");
                }
                else {
                    app.App.alert("Request Review", "Unable to submit for review: " + data.errorMessage);
                }
            });
        };

        app.App.confirm("Submit for Review", "Submit " + selectedItem.getName() + " for review and approval?", submitReviewFunction);
    }

    updateReviews() {
        const thisApp = this;
        getReviews(false, true, function(reviewsJson) {
            const reviews = [];

            for (let i in reviewsJson) {
                const reviewJson = reviewsJson[i];
                const review = Review.fromJson(reviewJson);
                reviews.push(review);
            }

            for (let i in reviews) {
                const review = reviews[i];

                const updateReviewsState = function() {
                    thisApp.setState({
                        reviews: reviews
                    });
                }

                if (review.getFunctionCatalog()) {
                    getFunctionCatalog(review.getFunctionCatalog().getId(), function (functionCatalogJson) {
                        const functionCatalog = FunctionCatalog.fromJson(functionCatalogJson);
                        review.setFunctionCatalog(functionCatalog);
                        updateReviewsState();
                    });
                }
                if (review.getFunctionBlock()) {
                    getFunctionBlock(review.getFunctionBlock().getId(), function (functionBlockJson) {
                        const functionBlock = FunctionBlock.fromJson(functionBlockJson);
                        review.setFunctionBlock(functionBlock);
                        updateReviewsState();
                    });
                }
                if (review.getMostInterface()) {
                    getMostInterface(review.getMostInterface().getId(), function (mostInterfaceJson) {
                        const mostInterface = MostInterface.fromJson(mostInterfaceJson);
                        review.setMostInterface(mostInterface);
                        updateReviewsState();
                    });
                }
                if (review.getMostFunction()) {
                    getMostFunction(review.getMostFunction().getId(), function (mostFunctionJson) {
                        const mostFunction = MostFunction.fromJson(mostFunctionJson);
                        review.setMostFunction(mostFunction);
                        updateReviewsState();
                    });
                }
                getAccount(review.getAccount().getId(), function (accountJson) {
                    const account = Account.fromJson(accountJson);
                    review.setAccount(account);
                    updateReviewsState();
                });
                const reviewVotes = review.getReviewVotes();
                for (let i in reviewVotes) {
                    const reviewVote = reviewVotes[i];
                    getAccount(reviewVote.getAccount().getId(), function (accountJson) {
                        const account = Account.fromJson(accountJson);
                        reviewVote.setAccount(account);
                        updateReviewsState();
                    });
                }
                const reviewComments = review.getReviewComments();
                for (let i in reviewComments) {
                    const reviewComment = reviewComments[i];
                    getAccount(reviewComment.getAccount().getId(), function (accountJson) {
                        const account = Account.fromJson(accountJson);
                        reviewComment.setAccount(account);
                        updateReviewsState();
                    });
                }
            }

            // all reviews are loaded
            thisApp.setState({
                reviews: reviews,
                isLoadingReviews: false
            });
        });
    }

    onReviewSelected(review) {
        this.setState({
            currentReview:          review,
            shouldShowToolbar:      true
        });
        const reviewObject = review.getReviewObject();
        const reviewObjectClassName = reviewObject.constructor.name;
        switch (reviewObjectClassName) {
            case 'FunctionCatalog': {
                this.onFunctionCatalogSelected(reviewObject);
            } break;
            case 'FunctionBlock': {
                this.onFunctionBlockSelected(reviewObject);
            } break;
            case 'MostInterface': {
                this.onMostInterfaceSelected(reviewObject);
            } break;
            case 'MostFunction': {
                this.onMostFunctionSelected(reviewObject);
            }
        }
    }

    onApprovalReviewClicked(childItem) {
        const thisApp = this;
        const approvalReviewId = childItem.getApprovalReviewId();

        if (approvalReviewId < 1) {
            app.App.alert("Approval Review", "A valid review does not exist for this object. The review ID for an object must be greater than 0.");
            return;
        }

        getReview(approvalReviewId, function(data) {
            if (! data.wasSuccess) {
                app.App.alert("Approval Review", "Unable to get approval review: " + data.errorMessage);
                return;
            }

            thisApp.handleRoleClick(thisApp.roles.reviews, null, false);

            const review = Review.fromJson(data.review);
            const NavigationLevel = thisApp.NavigationLevel;

            const reviewSelectedFunction = function(currentNavigationLevel) {
                thisApp.setState({
                    currentNavigationLevel: currentNavigationLevel
                });

                thisApp.onReviewSelected(review);
            };

            if (review.getFunctionCatalog()) {
                getFunctionCatalog(review.getFunctionCatalog().getId(), function (functionCatalogJson) {
                    const functionCatalog = FunctionCatalog.fromJson(functionCatalogJson);
                    review.setFunctionCatalog(functionCatalog);
                    reviewSelectedFunction(NavigationLevel.versions);
                });
            }
            if (review.getFunctionBlock()) {
                getFunctionBlock(review.getFunctionBlock().getId(), function (functionBlockJson) {
                    const functionBlock = FunctionBlock.fromJson(functionBlockJson);
                    review.setFunctionBlock(functionBlock);
                    reviewSelectedFunction(NavigationLevel.functionCatalogs);
                });
            }
            if (review.getMostInterface()) {
                getMostInterface(review.getMostInterface().getId(), function (mostInterfaceJson) {
                    const mostInterface = MostInterface.fromJson(mostInterfaceJson);
                    review.setMostInterface(mostInterface);
                    reviewSelectedFunction(NavigationLevel.functionBlocks);
                });
            }
            if (review.getMostFunction()) {

                getMostFunction(review.getMostFunction().getId(), function (mostFunctionJson) {
                    const mostFunction = MostFunction.fromJson(mostFunctionJson);
                    review.setMostFunction(mostFunction);
                    reviewSelectedFunction(NavigationLevel.mostInterfaces);
                });
            }

            const reviewVotes = review.getReviewVotes();
            for (let i in reviewVotes) {
                const reviewVote = reviewVotes[i];
                getAccount(reviewVote.getAccount().getId(), function (accountJson) {
                    const account = Account.fromJson(accountJson);
                    reviewVote.setAccount(account);
                });
            }
            const reviewComments = review.getReviewComments();
            for (let i in reviewComments) {
                const reviewComment = reviewComments[i];
                getAccount(reviewComment.getAccount().getId(), function (accountJson) {
                    const account = Account.fromJson(accountJson);
                    reviewComment.setAccount(account);
                });
            }
        });
    }

    onReviewVoteClicked(isUpvote) {
        const thisApp = this;
        const currentReview = this.state.currentReview;
        const currentReviewVotes = currentReview.getReviewVotes();
        const account = this.state.account;
        const reviewId = currentReview.getId();

        // Check existing votes to see if this account has any
        for (let i in currentReviewVotes) {
            const currentReviewVote = currentReviewVotes[i]
            if (currentReviewVote.getAccount().getId() == account.getId()) {
                const currentReviewVoteId = currentReviewVote.getId();
                const currentReviewVoteIsUpvote = currentReviewVote.isUpvote();

                if (currentReviewVoteIsUpvote == isUpvote) {
                    deleteReviewVote(currentReviewVoteId, function(wasSuccess) {
                        if (! wasSuccess) {
                            app.App.alert("Review Vote", "Unable to remove vote for approval.");
                        }
                        else {
                            currentReviewVotes.splice(i, 1);
                            currentReview.setReviewVotes(currentReviewVotes);
                            thisApp.setState({ currentReview: currentReview });
                        }
                    });
                }
                else {
                    const currentReviewVoteJson = {
                        reviewId: currentReviewVoteId,
                        isUpvote: isUpvote
                    };
                    updateReviewVote(currentReviewVoteId, currentReviewVoteJson, function(wasSuccess) {
                        if (! wasSuccess) {
                            app.App.alert("Review Vote", "Unable to update vote for approval.");
                        }
                        else {
                            currentReviewVote.setIsUpvote(isUpvote);
                            thisApp.setState({ currentReview: currentReview });
                        }
                    });
                }
                return;
            }
        }

        const reviewVote = new ReviewVote();
        reviewVote.setAccount(account);
        reviewVote.setIsUpvote(isUpvote);

        const reviewVoteJson = ReviewVote.toJson(reviewVote);

        insertReviewVote(reviewId, reviewVoteJson, function(wasSuccess, reviewVoteId) {
            if (wasSuccess) {
                reviewVote.setId(reviewVoteId);
                currentReviewVotes.push(reviewVote);
                currentReview.setReviewVotes(currentReviewVotes);

                thisApp.setState({currentReview: currentReview});
            }
            else {
                app.App.alert("Review Vote", "Unable to submit vote for approval.");
            }
        });
    }

    isReviewVoteSelected() {
        const currentReview = this.state.currentReview;
        const currentReviewVotes = currentReview.getReviewVotes();
        const accountId = this.state.account.getId();

        // Check existing votes to see if this account has any
        for (let i in currentReviewVotes) {
            const currentReviewVote = currentReviewVotes[i]
            if (currentReviewVote.getAccount().getId() == accountId) {
                if (currentReviewVote.isUpvote()) {
                    return "isUpvote";
                }
                else {
                    return "isDownvote";
                }
            }
        }

        return false;
    }

    onSaveTicketUrlClicked(value, callbackFunction) {
        const review = this.state.currentReview;

        review.setTicketUrl(value);

        const reviewJson = Review.toJson(review);
        updateReview(reviewJson, function(wasSuccess) {
            if (typeof callbackFunction == "function") {
                callbackFunction(wasSuccess);
            }
        });

        this.setState({
            currentReview: review
        })
    }

    onApproveButtonClicked() {
        const thisApp = this;

        const approveReviewFunction = function() {
            const reviewId = thisApp.state.currentReview.getId();

            thisApp.setState({
                createButtonState: thisApp.CreateButtonState.animate
            });
            approveReview(reviewId, function (data) {
                if (data.wasSuccess) {
                    app.App.alert("Review Approval", "Review has been successfully approved.");
                    thisApp.handleRoleClick(thisApp.roles.reviews, null, false);
                }
                else {
                    app.App.alert("Review Approval", "Unable to approve review: " + data.errorMessage);
                    thisApp.setState({
                        createButtonState: thisApp.CreateButtonState.normal
                    });
                }
            });
        };

        app.App.confirm("Approve Review", "Are you sure you would like to approve this review?", approveReviewFunction);
    }

    onTrashItemSelected(trashItem) {
        this.setState({
            shouldShowToolbar:      true,
            isTrashItemSelected:    true
        });
        const className = trashItem.constructor.name;
        switch (className) {
            case 'FunctionCatalog': {
                this.onFunctionCatalogSelected(trashItem);
            } break;
            case 'FunctionBlock': {
                this.onFunctionBlockSelected(trashItem);
            } break;
            case 'MostInterface': {
                this.onMostInterfaceSelected(trashItem);
            } break;
            case 'MostFunction': {
                this.onMostFunctionSelected(trashItem);
            }
        }
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

    goToHistoryState(historyState) {
        this.handleRoleClick(historyState.roleName, historyState.subRoleName, false);
    }

    handleRoleClick(roleName, subRoleName, canUseCachedChildren) {
        const thisApp = this;

        if (history.state && (roleName != history.state.roleName || subRoleName != history.state.subRoleName)) {
            let historyState = {
                roleName: roleName,
                subRoleName: subRoleName
            };
            history.pushState(historyState, null, null);
        }

        switch (roleName) {
            case this.roles.development: {
                // Development Mode
                // set navigation level similar to onItemSelected() methods. Default to displaying functionBlocks if subRoleName is null.
                const newActiveSubRole = (subRoleName || this.developmentRoles.functionCatalog);

                let newNavigationLevel = this.NavigationLevel.versions;
                if (newActiveSubRole === this.developmentRoles.functionBlock) {
                    newNavigationLevel = this.NavigationLevel.functionCatalogs;
                }
                else if (newActiveSubRole === this.developmentRoles.mostInterface) {
                    newNavigationLevel = this.NavigationLevel.functionBlocks;
                }

                this.setState({
                    navigationItems:                [],
                    parentHistory:                  [],
                    searchResults:                  [],
                    selectedItem:                   null,
                    parentItem:                     null,
                    proposedItem:                   null,
                    releasingFunctionCatalog:       null,
                    shouldShowCreateChildForm:      false,
                    shouldShowSearchChildForm:      false,
                    shouldShowSubmitForReviewForm:  false,
                    shouldShowEditForm:             false,
                    shouldShowToolbar:              true,
                    shouldShowFilteredResults:      false,
                    filterString:                   "",
                    createButtonState:              thisApp.CreateButtonState.normal,
                    loadingTimeout:                 null,
                    isLoadingChildren:              !canUseCachedChildren,
                    isLoadingReviews:               false,
                    isLoadingAccounts:              false,
                    currentNavigationLevel:         newNavigationLevel,
                    activeRole:                     roleName,
                    activeSubRole:                  newActiveSubRole,
                    showSettingsPage:               false,
                    currentReview:                  null
                });

                if (newActiveSubRole === this.developmentRoles.functionCatalog) {
                    getFunctionCatalogs(function(functionCatalogsJson) {
                        if (thisApp.state.currentNavigationLevel == newNavigationLevel) {
                            // didn't navigate away while downloading children
                            const functionCatalogs = thisApp.getChildItemsFromVersions(functionCatalogsJson, FunctionCatalog.fromJson);
                            thisApp.setState({
                                functionCatalogs:   functionCatalogs,
                                isLoadingChildren:  false
                            });
                        }
                    });
                }
                else if (newActiveSubRole === this.developmentRoles.functionBlock) {
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
                const newActiveSubRole = (subRoleName || this.typesRoles.createType);

                this.setState({
                    navigationItems:                [],
                    searchResults:                  [],
                    functionCatalogs:               [],
                    selectedItem:                   null,
                    parentItem:                     null,
                    proposedItem:                   null,
                    shouldShowCreateChildForm:      false,
                    shouldShowSearchChildForm:      false,
                    shouldShowSubmitForReviewForm:  false,
                    shouldShowEditForm:             false,
                    shouldShowToolbar:              false,
                    shouldShowFilteredResults:      false,
                    isLoadingMostTypes:             true,
                    isLoadingPrimitiveTypes:        true,
                    isLoadingUnits:                 true,
                    isLoadingReviews:               false,
                    isLoadingAccounts:              false,
                    createButtonState:              thisApp.CreateButtonState.normal,
                    currentNavigationLevel:         null,
                    activeRole:                     roleName,
                    activeSubRole:                  newActiveSubRole,
                    showSettingsPage:               false,
                    currentReview:                  null
                });
                thisApp.updateMostTypes();
            } break;
            case this.roles.reviews: {
                this.setState({
                    navigationItems:            [],
                    parentHistory:              [],
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
                    shouldShowDeletedChildItems:true,
                    isLoadingMostTypes:         false,
                    isLoadingPrimitiveTypes:    false,
                    isLoadingUnits:             false,
                    isLoadingReviews:           true,
                    isLoadingAccounts:          false,
                    createButtonState:          thisApp.CreateButtonState.normal,
                    currentNavigationLevel:     null,
                    activeRole:                 roleName,
                    activeSubRole:              null,
                    showSettingsPage:           false,
                    currentReview:              null
                });
                thisApp.updateReviews();
            } break;
            case this.roles.accounts: {
                this.setState({
                    navigationItems:            [],
                    parentHistory:              [],
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
                    isLoadingMostTypes:         false,
                    isLoadingPrimitiveTypes:    false,
                    isLoadingUnits:             false,
                    isLoadingReviews:           false,
                    isLoadingAccounts:          true,
                    createButtonState:          thisApp.CreateButtonState.normal,
                    currentNavigationLevel:     null,
                    activeRole:                 roleName,
                    activeSubRole:              null,
                    showSettingsPage:           false,
                    currentReview:              null
                });
                this.getAllCompanies();
                this.getAccountsForEditForm();
            } break;
            case this.roles.trash: {
                this.setState({
                    navigationItems:            [],
                    parentHistory:              [],
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
                    isLoadingMostTypes:         false,
                    isLoadingPrimitiveTypes:    false,
                    isLoadingUnits:             false,
                    isLoadingReviews:           false,
                    isLoadingAccounts:          false,
                    createButtonState:          thisApp.CreateButtonState.normal,
                    currentNavigationLevel:     null,
                    activeRole:                 roleName,
                    activeSubRole:              null,
                    showSettingsPage:           false,
                    currentReview:              null,
                    isTrashItemSelected:        false
                });
            } break;
            default: {
                console.error("Invalid role " + roleName + " selected.");
                return;
            }
        }
    }

    handleSettingsClick() {
        this.setState({
            showSettingsPage: !this.state.showSettingsPage
        });
    }

    handleTrashButtonClick() {
        const shouldShowDeletedChildItems = this.state.shouldShowDeletedChildItems;
        const currentNavigationLevel = this.state.currentNavigationLevel;

        this.setState({
            shouldShowDeletedChildItems: !shouldShowDeletedChildItems,
            currentNavigationLevel: currentNavigationLevel
        });

    }

    onThemeChange(themeName) {
        this.setTheme(themeName);

        const account = this.state.account;
        account.getSettings().setTheme(themeName);

        this.setState({
            account: account
        });
    }

    onDefaultModeChanged(roleName) {
        const account = this.state.account;
        account.getSettings().setDefaultMode(roleName);
    }

    getLoadingIconTimeoutStateChange(timeoutDelay, displayIconForSearch, displayIconForFilter) {
        let thisApp = this;
        let loadingTimeout = setTimeout(() => {
            if (thisApp.state.shouldShowLoadingIcon) {
                thisApp.setState({
                    isLoadingSearchResults: displayIconForSearch,
                    isLoadingChildren: displayIconForFilter,
                    shouldShowLoadingIcon: false
                });
            }
        }, timeoutDelay);

        return loadingTimeout;
    }

    setTheme(themeName) {
        const themeCssDirectory = themeName.toLowerCase();
        document.getElementById('core-css').href =              '/css/themes/' + themeCssDirectory + '/core.css';
        document.getElementById('app-css').href =               '/css/themes/' + themeCssDirectory + '/app.css';
        document.getElementById('palette-css').href =           '/css/themes/' + themeCssDirectory + '/palette.css';
        document.getElementById('release-css').href =           '/css/themes/' + themeCssDirectory + '/release.css';
        document.getElementById('reviews-css').href =           '/css/themes/' + themeCssDirectory + '/reviews.css';
        document.getElementById('react-input-field-css').href = '/css/themes/' + themeCssDirectory + '/react/input-field.css';
        document.getElementById('react-toolbar-css').href =     '/css/themes/' + themeCssDirectory + '/react/toolbar.css';
    }

    renderChildItems() {
        const reactComponents = [];
        const NavigationLevel = this.NavigationLevel;
        const currentNavigationLevel = this.state.currentNavigationLevel;
        const canModify = this.state.account ? this.state.account.hasRole("Modify") : false;
        const shouldShowDeletedChildItems = this.state.shouldShowDeletedChildItems;

        if (this.state.isLoadingChildren) {
            // return loading icon
            return (
                <div className="form-loading"><i id="loading-children-icon" className="fa fa-3x fa-refresh fa-spin"></i></div>
            );
        }

        let childItems = [];
        switch (currentNavigationLevel) {
            case NavigationLevel.versions:
                if (this.state.shouldShowFilteredResults) {
                    childItems = this.state.searchResults;
                    childItems.sort(function(a, b) {
                        return (a.getName().concat("_" + a.getId())).localeCompare((b.getName().concat("_" + b.getId())), undefined, {numeric : true, sensitivity: 'base'});
                    });
                }
                else {
                    childItems = this.state.functionCatalogs;
                }

                for (let i in childItems) {
                    const childItem = childItems[i];
                    const functionCatalogKey = "FunctionCatalog" + i;
                    reactComponents.push(<app.FunctionCatalog
                        key={functionCatalogKey}
                        functionCatalog={childItem}
                        onClick={this.onFunctionCatalogSelected}
                        displayVersionsList={true}
                        onDelete={this.onDeleteFunctionCatalog}
                        onVersionChanged={this.onChildItemVersionChanged}
                        onExportFunctionCatalog={exportFunctionCatalogToMost}
                        onMarkAsDeleted={this.onMarkFunctionCatalogAsDeleted}
                        onRestoreFromTrash={this.onRestoreFunctionCatalogFromTrash}
                        onApprovalReviewClicked={this.onApprovalReviewClicked}
                        showDeletedVersions={shouldShowDeletedChildItems}
                        />
                    );
                }
            break;

            case NavigationLevel.functionCatalogs:
                if (this.state.shouldShowFilteredResults) {
                    childItems = this.state.searchResults;
                    childItems.sort(function(a, b) {
                        return (a.getName().concat("_" + a.getId())).localeCompare((b.getName().concat("_" + b.getId())), undefined, {numeric : true, sensitivity: 'base'});
                    });
                }
                else {
                    childItems = this.state.functionBlocks;
                }

                for (let i in childItems) {
                    const childItem = childItems[i];
                    const functionBlockKey = "FunctionBlock" + i;
                    reactComponents.push(<app.FunctionBlock
                        key={functionBlockKey}
                        functionBlock={childItem}
                        onClick={this.onFunctionBlockSelected}
                        displayVersionsList={!this.state.selectedItem}
                        parent={this.state.selectedItem}
                        onDisassociate={this.disassociateFunctionBlockFromFunctionCatalog}
                        onDelete={this.onDeleteFunctionBlock}
                        onVersionChanged={this.onChildItemVersionChanged}
                        onMarkAsDeleted={this.onMarkFunctionBlockAsDeleted}
                        onRestoreFromTrash={this.onRestoreFunctionBlockFromTrash}
                        onApprovalReviewClicked={this.onApprovalReviewClicked}
                        showDeletedVersions={shouldShowDeletedChildItems}
                        />
                    );
                }
            break;

            case NavigationLevel.functionBlocks:
                if (this.state.shouldShowFilteredResults) {
                    childItems = this.state.searchResults;
                    childItems.sort(function(a, b) {
                        return (a.getName().concat("_" + a.getId())).localeCompare((b.getName().concat("_" + b.getId())), undefined, {numeric : true, sensitivity: 'base'});
                    });
                }
                else {
                    childItems = this.state.mostInterfaces;
                }

                for (let i in childItems) {
                    const childItem = childItems[i];
                    const interfaceKey = "Interface" + i;
                    reactComponents.push(<app.MostInterface
                        key={interfaceKey}
                        mostInterface={childItem}
                        onClick={this.onMostInterfaceSelected}
                        displayVersionsList={!this.state.selectedItem}
                        parent={this.state.selectedItem}
                        onDisassociate={this.disassociateMostInterfaceFromFunctionBlock}
                        onDelete={this.onDeleteMostInterface}
                        onVersionChanged={this.onChildItemVersionChanged}
                        onMarkAsDeleted={this.onMarkMostInterfaceAsDeleted}
                        onRestoreFromTrash={this.onRestoreMostInterfaceFromTrash}
                        onApprovalReviewClicked={this.onApprovalReviewClicked}
                        showDeletedVersions={shouldShowDeletedChildItems}
                        />
                    );
                }
            break;

            case NavigationLevel.mostInterfaces:
                childItems = this.state.mostFunctions;
                childItems.sort(function(a, b) {
                    return (a.getName().concat("_" + a.getId())).localeCompare((b.getName().concat("_" + b.getId())), undefined, {numeric : true, sensitivity: 'base'});
                });

                for (let i in childItems) {
                    const childItem = childItems[i];
                    const mostFunctionKey = "mostFunction" + i;
                    reactComponents.push(<app.MostFunction
                        key={mostFunctionKey}
                        mostFunction={childItem}
                        onClick={this.onMostFunctionSelected}
                        onDelete={this.onDeleteMostFunction}
                        isInterfaceApproved={this.state.selectedItem.isApproved()}
                        isInterfaceReleased={this.state.selectedItem.isReleased()}
                        onMarkAsDeleted={this.onMarkMostFunctionAsDeleted}
                        onRestoreFromTrash={this.onRestoreMostFunctionFromTrash}
                        onApprovalReviewClicked={this.onApprovalReviewClicked}
                        showDeletedMostFunctions={shouldShowDeletedChildItems}
                        />
                    );
                }
            break;

            case NavigationLevel.mostFunctions:
                // add a form for the selected MOST function
                const shouldAnimateCreateButton = (this.state.createButtonState == this.CreateButtonState.animate);
                const buttonTitle = (this.state.createButtonState == this.CreateButtonState.success) ? "Changes Saved" : "Save";
                reactComponents.push(<app.MostFunctionForm key="MostFunctionForm"
                    readOnly={this.state.selectedItem.isApproved() || ! canModify}
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
        const shouldShowDeletedChildItems = this.state.shouldShowDeletedChildItems;
        const isReview = this.state.currentReview != null;
        const selectedItem = this.state.selectedItem;
        const parentItem = this.state.parentItem;
        const account = this.state.account;
        const canModify = (account && !isReview) ? account.hasRole("Modify") : false;
        const canRelease = (account && !isReview) ? account.hasRole("Release") : false;

        const shouldShowFilterBar = (this.state.activeRole === this.roles.development) && !selectedItem;
        const shouldShowApprovalForm = (this.state.activeRole === this.roles.reviews) && selectedItem;
        let selectedVote = null;

        const reactComponents = [];
        const thisApp = this;

        if (shouldShowToolbar) {
            let shouldShowCreateButton = true;

            let isApproved = false;
            let shouldShowForkButton = false;
            let shouldShowBackButton = false;
            let shouldShowEditButton = false;
            let shouldShowSearchButton = false;
            let shouldShowSubmitForReviewButton = false;
            let shouldShowReleaseButton = false;
            let shouldShowToggleItemsInTrashButton = true;
            let shouldShowNavigationItems = false;
            let backFunction = null;
            let forkFunction = null;

            // Determine what buttons should be displayed in toolbar.
            if (selectedItem) {
                const isReleased = selectedItem.isReleased();
                isApproved = selectedItem.isApproved();
                const isTrashItem = this.state.isTrashItemSelected;
                shouldShowBackButton = true;
                shouldShowToggleItemsInTrashButton = (currentNavigationLevel != NavigationLevel.mostFunctions) && (activeRole != thisApp.roles.reviews);

                if (! isReleased && ! isApproved && !isTrashItem) {
                    shouldShowSubmitForReviewButton = currentNavigationLevel != NavigationLevel.mostFunctions;
                    shouldShowSearchButton = ! shouldShowFilterBar;
                }
                else {
                    shouldShowCreateButton = false;
                }

                if (activeRole === this.roles.development) {
                    if (currentNavigationLevel == NavigationLevel.functionCatalogs) {
                        shouldShowReleaseButton = ! isReleased && isApproved;
                    }

                    const activeSubRole = this.state.activeSubRole;
                    shouldShowEditButton = true;
                    shouldShowNavigationItems = true;

                    // Determine if fork button should be shown.
                    if (isApproved) {
                        shouldShowForkButton = (currentNavigationLevel != NavigationLevel.mostFunctions);

                        if (parentItem) {
                            if (parentItem.isApproved()) {
                                shouldShowForkButton = false;
                            }
                        }

                        // Determine fork button functionality
                        if (shouldShowForkButton) {
                            switch (currentNavigationLevel) {
                                case NavigationLevel.functionCatalogs:
                                    forkFunction = this.onForkFunctionCatalog;
                                    break;
                                case this.NavigationLevel.functionBlocks:
                                    forkFunction = this.onForkFunctionBlock;
                                    break;
                                case this.NavigationLevel.mostInterfaces:
                                    forkFunction = this.onForkMostInterface;
                                    break;
                            }
                        }
                    }

                    // Determine back button functionality.
                    switch (currentNavigationLevel) {
                        case this.NavigationLevel.functionCatalogs:
                            backFunction = function() { thisApp.handleRoleClick(thisApp.state.activeRole, thisApp.state.activeSubRole, true); };
                            break;
                        case this.NavigationLevel.functionBlocks:
                            if (activeSubRole === thisApp.developmentRoles.functionCatalog) {
                                backFunction = navigationItems[navigationItems.length-2].getOnClickCallback();
                            }
                            else {
                                backFunction = function() { thisApp.handleRoleClick(thisApp.state.activeRole, thisApp.state.activeSubRole, true); };
                            }
                            break;
                        case this.NavigationLevel.mostInterfaces:
                            if ((activeSubRole === thisApp.developmentRoles.functionCatalog) || (activeSubRole === thisApp.developmentRoles.functionBlock)) {
                                backFunction = navigationItems[navigationItems.length-2].getOnClickCallback();
                            }
                            else {
                                backFunction = function() { thisApp.handleRoleClick(thisApp.state.activeRole, thisApp.state.activeSubRole, true); };
                            }
                            break;
                        case this.NavigationLevel.mostFunctions:
                            backFunction = navigationItems[navigationItems.length-2].getOnClickCallback();
                            break;
                    }
                }
                else {
                    const inReview = activeRole == thisApp.roles.reviews;
                    const inTrash = activeRole == thisApp.roles.trash;
                    if (inReview || inTrash) {
                        shouldShowNavigationItems = true;
                        shouldShowBackButton = true;
                        shouldShowEditButton = true; // either edit or view
                        shouldShowForkButton = false;
                        shouldShowSearchButton = false;
                        shouldShowCreateButton = false;
                        shouldShowSubmitForReviewButton = false;
                        shouldShowReleaseButton = false;

                        if (inReview) {
                            selectedVote = this.isReviewVoteSelected();
                        }

                        if (navigationItems.length > 1) {
                            backFunction = navigationItems[navigationItems.length-2].getOnClickCallback();
                        }
                        else {
                            backFunction = function() {
                                thisApp.handleRoleClick(activeRole, null, false);
                            };
                        }
                    }
                    else {
                        backFunction = navigationItems[navigationItems.length-2].getOnClickCallback();
                    }
                }
            }

            reactComponents.push(
                <app.Toolbar key="Toolbar"
                    onCreateClicked={() => this.setState({ shouldShowCreateChildForm: !shouldShowCreateChildForm, shouldShowSearchChildForm: false, shouldShowEditForm: false, shouldShowSubmitForReviewForm: false })}
                    onCancel={() => this.setState({ shouldShowCreateChildForm: false, shouldShowSearchChildForm: false, shouldShowEditForm: false, shouldShowSubmitForReviewForm: false })}
                    onSearchClicked={() => this.setState({shouldShowSearchChildForm: !shouldShowSearchChildForm, shouldShowCreateChildForm: false, shouldShowEditForm: false, shouldShowSubmitForReviewForm: false })}
                    onEditClicked={() => this.setState({shouldShowEditForm: !shouldShowEditForm, shouldShowCreateChildForm: false, shouldShowSearchChildForm: false, shouldShowSubmitForReviewForm: false })}
                    onSubmitForReviewClicked={() => this.onReviewSubmitted(selectedItem)}
                    onForkClicked={() => forkFunction(selectedItem, true)}
                    onReleaseClicked={() => this.onReleaseFunctionCatalog(selectedItem)}
                    onToggleItemsInTrashClicked={this.handleTrashButtonClick}
                    shouldShowDeletedChildItems={shouldShowDeletedChildItems}
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
                    shouldShowViewInfoButton={(isApproved || ! canModify)}
                    shouldShowSubmitForReviewButton={shouldShowSubmitForReviewButton}
                    shouldShowReleaseButton={shouldShowReleaseButton}
                    shouldShowToggleItemsInTrashButton={shouldShowToggleItemsInTrashButton}
                    shouldShowNavigationItems={shouldShowNavigationItems}
                    onBackButtonClicked={backFunction}
                    canModify={canModify}
                    canRelease={canRelease}
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
                            account={thisApp.state.account}
                            accountsForEditForm={thisApp.state.accountsForEditForm}
                        />
                    );
                }
            break;

            case NavigationLevel.functionCatalogs:
                if (shouldShowEditForm) {
                    reactComponents.push(
                        <app.FunctionCatalogForm key="FunctionCatalogForm"
                           shouldShowSaveAnimation={shouldAnimateCreateButton}
                           showTitle={true}
                           showCustomTitle={true}
                           formTitle={selectedItem.getName()}
                           onSubmit={this.onUpdateFunctionCatalog}
                           functionCatalog={selectedItem}
                           buttonTitle={developmentButtonTitle}
                           defaultButtonTitle="Save"
                           readOnly={! canModify}
                           account={thisApp.state.account}
                           accountsForEditForm={thisApp.state.accountsForEditForm}
                        />
                    );
                }
                if (shouldShowCreateChildForm) {
                    reactComponents.push(
                        <app.FunctionBlockForm key="FunctionBlockForm"
                            shouldShowSaveAnimation={shouldAnimateCreateButton}
                            functionBlock={this.state.proposedItem}
                            buttonTitle={buttonTitle}
                            showTitle={true}
                            onSubmit={this.onCreateFunctionBlock}
                            defaultButtonTitle="Submit"
                            account={thisApp.state.account}
                            accountsForEditForm={thisApp.state.accountsForEditForm}
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
                           readOnly={! canModify}
                           account={thisApp.state.account}
                           accountsForEditForm={thisApp.state.accountsForEditForm}
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
                            account={thisApp.state.account}
                            accountsForEditForm={thisApp.state.accountsForEditForm}
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
                           readOnly={! canModify}
                           account={thisApp.state.account}
                           accountsForEditForm={thisApp.state.accountsForEditForm}
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

        if (shouldShowFilterBar) {
            reactComponents.push(this.renderFilterBar());
        }

        if (shouldShowApprovalForm) {
            reactComponents.push(
                <app.ApprovalForm key="approvalForm"
                                  account={this.state.account}
                                  review={this.state.currentReview}
                                  shouldShowVoteButtons={true}
                                  shouldShowSaveAnimation={shouldAnimateCreateButton}
                                  onVoteClicked={this.onReviewVoteClicked}
                                  selectedVote={selectedVote}
                                  onSaveTicketUrlClicked={this.onSaveTicketUrlClicked}
                                  onApproveButtonClicked={this.onApproveButtonClicked}
                                  readOnly={selectedItem.isApproved()}
                />
            );
        }

        return reactComponents;
    }

    renderMainContent() {
        if (this.state.showSettingsPage) {
            const account = this.state.account;
            const theme = account ? account.getSettings().getTheme() : "Tidy";
            const defaultMode = account ? account.getSettings().getDefaultMode() : this.roles.development;
            const accountId = account.getId();
            const validRoles = this.getValidRoleItems(account);

            return (
                <div id="main-content" className="container">
                    <app.SettingsPage theme={theme} defaultMode={defaultMode} accountId={accountId} roles={validRoles} onThemeChange={this.onThemeChange} onDefaultModeChanged={this.onDefaultModeChanged} handleSettingsClick={this.handleSettingsClick}/>
                </div>
            );
        }
        else {
            let childDisplayAreaStyle = "child-display-area clearfix";
            switch (this.state.activeRole) {
                case this.roles.types: {
                    // types role
                    return (
                        <div id="main-content" className="container">
                            <app.TypesPage mode={this.state.activeSubRole} onTypeCreated={this.onTypeCreated} onTypeChanged={this.onTypeChanged} mostTypes={this.state.mostTypes} primitiveTypes={this.state.primitiveTypes} mostUnits={this.state.mostUnits}
                                           isLoadingTypesPage={this.state.isLoadingMostTypes || this.state.isLoadingPrimitiveTypes || this.state.isLoadingUnits} />
                        </div>
                    );
                } break;
                case this.roles.accounts: {
                    // accounts role
                    return (
                        <div id="main-content" className="container">
                            <app.AccountsPage companies={this.state.companies} onCreateCompany={this.onCreateCompany} onResetPassword={this.onResetPassword} thisAccount={this.state.account} />
                        </div>
                    );
                } break;
                case this.roles.trash: {
                    // trash role
                    if (!this.state.isTrashItemSelected) {
                        return (
                            <div id="main-content" className="container">
                                <app.TrashPage
                                    onItemSelected={this.onTrashItemSelected}
                                    onRestoreFunctionCatalog={this.onRestoreFunctionCatalogFromTrash}
                                    onRestoreFunctionBlock={this.onRestoreFunctionBlockFromTrash}
                                    onRestoreMostInterface={this.onRestoreMostInterfaceFromTrash}
                                    onRestoreMostFunction={this.onRestoreMostFunctionFromTrash}
                                    onDeleteFunctionCatalog={this.onDeleteFunctionCatalog}
                                    onDeleteFunctionBlock={this.onDeleteFunctionBlock}
                                    onDeleteMostInterface={this.onDeleteMostInterface}
                                    onDeleteMostFunction={this.onDeleteMostFunction}
                                />
                            </div>
                        );
                    }
                } break; // continue if an item has been selected
                case this.roles.reviews: {
                    // reviews role
                    const currentReview = this.state.currentReview;
                    childDisplayAreaStyle = "selected-review-display-area clearfix";
                    if (!currentReview) {
                        return (
                            <div id="main-content" className="container">
                                <app.ReviewsPage reviews={this.state.reviews} isLoadingReviews={this.state.isLoadingReviews} onReviewSelected={this.onReviewSelected}/>
                            </div>
                        );
                    }
                } break; // continue if a review is selected
            }
            // other roles
            if (this.state.activeSubRole === this.developmentRoles.functionCatalog) {
                const releasingFunctionCatalog = this.state.releasingFunctionCatalog;
                if (releasingFunctionCatalog != null) {
                    // don't display anything else, go to release page
                    return (
                        <div id="main-content" className="container">
                            <app.ReleasePage functionCatalog={releasingFunctionCatalog} onRelease={this.onFunctionCatalogReleased} onCancel={() => this.setState({releasingFunctionCatalog: null})} />
                        </div>
                    );
                }
            }
            return (
                <div id="main-content" className="container">
                    <div className="display-area">
                        {this.renderForm()}
                        <div id="child-display-area" className={childDisplayAreaStyle}>
                            {this.renderChildItems()}
                        </div>
                    </div>
                </div>
            );
        }
    }

    getValidRoleItems(account) {
        const roleItems = [];

        if (account) {
            if (account.hasPermission("MOST_COMPONENTS_VIEW")) {
                roleItems.push(this.roles.development);
            }
            if (account.hasPermission("TYPES_CREATE") || account.hasPermission("TYPES_MODIFY")) {
                roleItems.push(this.roles.types);
            }
            if (account.hasPermission("REVIEWS_VIEW")) {
                roleItems.push(this.roles.reviews);
            }
            if (account.hasPermission("ADMIN_MODIFY_USERS")) {
                roleItems.push(this.roles.accounts);
            }
            // should be last
            if (account.hasPermission("MOST_COMPONENTS_MODIFY")) {
                roleItems.push(this.roles.trash);
            }
        }

        return roleItems;
    }

    renderRoleToggle() {
        const roleItems = this.getValidRoleItems(this.state.account);

        let displayMappings = {
            "Trash": <span>&nbsp;<i className="fa fa-trash"/>&nbsp;</span>
        }

        return (
            <app.RoleToggle roleItems={roleItems} handleClick={(role, canUseCachedChildren) => this.handleRoleClick(role, null, canUseCachedChildren)} activeRole={this.state.activeRole} displayMappings={displayMappings} />
        );
    }

    renderSubRoleToggle() {
        let thisApp = this;
        function buildSubRolesToggle(activeRole, subRoles) {
            const roleItems = Object.values(subRoles);

            return (
                <app.RoleToggle roleItems={roleItems} handleClick={(subRole, canUseCachedChildren) => thisApp.handleRoleClick(activeRole, subRole, canUseCachedChildren)} activeRole={thisApp.state.activeSubRole} />
            );
        }

        if (this.state.activeRole === this.roles.development) {
            return buildSubRolesToggle(this.roles.development, this.developmentRoles);
        }
        if (this.state.activeRole === this.roles.types) {
            return buildSubRolesToggle(this.roles.types, this.typesRoles);
        }
    }

    renderFilterBar() {
        if (this.state.selectedItem) {
            // Don't show filter bar when viewing a selected item.
            return;
        }

        const currentNavigationLevel = this.state.currentNavigationLevel;

        // Don't show filter bar when viewing most functions in a selected interface.
        if(currentNavigationLevel === this.NavigationLevel.mostInterfaces) {
            return;
        }

        let filterFunction = this.onFilterFunctionCatalogs;
        let defaultText = "Filter Function Catalogs";
        if (currentNavigationLevel === this.NavigationLevel.functionCatalogs) {
            filterFunction = this.onFilterFunctionBlocks;
            defaultText = "Filter Function Blocks";
        }
        else if (currentNavigationLevel === this.NavigationLevel.functionBlocks) {
            filterFunction = this.onFilterMostInterfaces;
            defaultText = "Filter Interfaces";
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
        const accountName = this.state.account ? this.state.account.getName() : "";
        return (
            <div id="app-root">
                <div id="header" className="secondary-bg accent title-font">
                    <img onClick={this.onDuckClick} onDoubleClick={this.showDuckAlert} className="tidy-logo" src='/img/tidy-logo.svg' /> Tidy Duck
                    {this.renderRoleToggle()}
                    {this.renderSubRoleToggle()}
                    <div id="account-area">
                        <div id="account-name">{accountName}</div>
                        <a id="logout" href="#" onClick={this.logout}>logout</a>
                        <i id="settings-icon" className="fa fa-cog fa-lg" onClick={this.handleSettingsClick}/>
                    </div>
                </div>
                {this.renderMainContent()}

                <app.Alert shouldShow={this.state.alert.shouldShow} title={this.state.alert.title} content={this.state.alert.content} isConfirmAlert={this.state.alert.isConfirmAlert} onConfirm={this.state.alert.onConfirm} onCancel={this.state.alert.onCancel}/>
            </div>
        );
    }
}

registerClassWithGlobalScope("App", App);

class AccountsPage extends React.Component {
    constructor(props) {
        super(props);

        this.SaveButtonState = {
            submit: 'submit',
            save:   'save',
            saving: 'saving',
            saved:  'saved'
        };

        const account = new Account();
        const accountCompany = this.props.companies[0];
        account.setCompany(accountCompany);

        const loginRole = new Role();
        loginRole.setName("Login");
        account.addRole(loginRole);

        const newCompany = new Company();

        this.state = {
            newAccount: account,
            newCompany: newCompany,
            createAccountButtonState: this.SaveButtonState.submit,
            editedAccountButtonState: this.SaveButtonState.save,
            createCompanyButtonState: this.SaveButtonState.submit,
            isLoadingAccounts: true,
            editedAccount: null,
            accounts: []
        };

        const thisPage = this;
        getAccounts(function (data) {
            if (data.wasSuccess) {
                const accountsJson = data.accounts;
                const accounts = accountsJson.map(Account.fromJson);

                thisPage.setState({
                    accounts: accounts,
                    isLoadingAccounts: false
                });
            }
        });

        this.handleKeyPress = this.handleKeyPress.bind(this);
        this.onAccountClicked = this.onAccountClicked.bind(this);
        this.onNewAccountUsernameChanged = this.onNewAccountUsernameChanged.bind(this);
        this.onNewAccountNameChanged = this.onNewAccountNameChanged.bind(this);
        this.onNewAccountCompanyChanged = this.onNewAccountCompanyChanged.bind(this);
        this.onNewCompanyNameChanged = this.onNewCompanyNameChanged.bind(this);
        this.onSubmitNewAccount = this.onSubmitNewAccount.bind(this);
        this.onSubmitNewCompany = this.onSubmitNewCompany.bind(this);
        this.onEditedAccountCompanyChanged = this.onEditedAccountCompanyChanged.bind(this);
        this.onEditedAccountNameChanged = this.onEditedAccountNameChanged.bind(this);
        this.onEditedAccountUsernameChanged = this.onEditedAccountUsernameChanged.bind(this);
        this.onResetPassword = this.onResetPassword.bind(this);
        this.onUpdateAccountMetadata = this.onUpdateAccountMetadata.bind(this);
        this.onCancelUpdateAccount = this.onCancelUpdateAccount.bind(this);
        this.renderCreateAccountForm = this.renderCreateAccountForm.bind(this);
        this.renderCreateCompanyForm = this.renderCreateCompanyForm.bind(this);
        this.renderCreateButtonText = this.renderCreateButtonText.bind(this);
        this.renderAccountAdministrationData = this.renderAccountAdministrationData.bind(this);
        this.renderEditUserForm = this.renderEditUserForm.bind(this);
        this.renderBackdrop = this.renderBackdrop.bind(this);
        this.renderRoleComponents = this.renderRoleComponents.bind(this);
    }

    componentWillReceiveProps(newProperties) {

    }

    handleKeyPress(e) {
        if (e.keyCode == 27) {
            this.setState({
                editedAccount: null,
            });
        }
    }

    componentDidMount() {
        document.addEventListener('keydown', this.handleKeyPress);
    }

    componentWillUnmount() {
        document.removeEventListener('keydown', this.handleKeyPress);
    }

    onNewAccountNameChanged(value) {
        const account = this.state.newAccount;
        account.setName(value);

        this.setState({
            newAccount: account,
            createAccountButtonState: this.SaveButtonState.submit
        });
    }

    onNewAccountUsernameChanged(value) {
        const account = this.state.newAccount;
        account.setUsername(value);

        this.setState({
            newAccount: account,
            createAccountButtonState: this.SaveButtonState.submit,
        });
    }

    onNewAccountCompanyChanged(value) {
        const account = this.state.newAccount;
        const companies = this.props.companies;

        for (let i in companies) {
            const company = companies[i];
            if (company.getName() == value) {
                account.setCompany(company);
                break;
            }
        }

        this.setState({
            newAccount: account,
            createAccountButtonState: this.SaveButtonState.submit
        });
    }

    onNewCompanyNameChanged(value) {
        const newCompany = this.state.newCompany;
        newCompany.setName(value);

        this.setState({
            newCompany: newCompany,
            createCompanyButtonState: this.SaveButtonState.submit
        });
    }

    onSubmitNewAccount(event) {
        event.preventDefault();
        const account = this.state.newAccount;
        const accountJson = Account.toJson(account);
        const thisApp = this;

        this.setState({
            createAccountButtonState: this.SaveButtonState.saving
        });

        createNewAccount(accountJson, function(data) {
            if (data.wasSuccess) {
                const usernameString = "Username: " + account.getUsername();
                const passwordString = "Password: " + data.password;
                const accounts = thisApp.state.accounts;
                accounts.push(account);

                const alertString = <div>
                                        Account ID {data.accountId} created with the following login information:
                                        <br/>
                                        <br/> {usernameString}
                                        <br/>
                                        <br/> {passwordString}
                                    </div>;
                app.App.alert("Account Successfully Created", alertString);

                const newAccount = new Account();
                const newCompany = new Company();
                newAccount.setCompany(newCompany);

                const loginRole = new Role();
                loginRole.setName("Login");
                newAccount.addRole(loginRole);


                thisApp.setState({
                    createAccountButtonState: thisApp.SaveButtonState.saved,
                    newAccount: newAccount,
                    accounts: accounts
                });
            }
            else {
                app.App.alert("Unable to create account.", data.errorMessage);

                thisApp.setState({
                    createAccountButtonState: thisApp.SaveButtonState.submit,
                });
            }
        });
    }

    onSubmitNewCompany(event) {
        event.preventDefault();
        const newCompany = this.state.newCompany;
        const thisApp = this;

        if (typeof this.props.onCreateCompany == "function") {
            this.setState({
                createCompanyButtonState: this.SaveButtonState.saving
            });

            this.props.onCreateCompany(newCompany, function(wasSuccess) {
                thisApp.setState({
                    newCompany: wasSuccess ? new Company() : newCompany,
                    createCompanyButtonState: wasSuccess ? thisApp.SaveButtonState.saved : thisApp.SaveButtonState.submit
                });
            });
        }
    }

    onRoleChange(account, roleName, checked, isNewAccount) {
        if (checked) {
            // add role
            const role = new Role();
            role.setName(roleName);

            account.addRole(role);
        } else {
            // remove role
            account.removeRoleByName(roleName);
        }
        this.setState({
            accounts: this.state.accounts,
            newAccount: isNewAccount ? account : this.state.newAccount
        })
        // get new set of roles
        const roles = account.getRoles();
        const roleNames = [];
        for (let i in roles) {
            const role = roles[i];
            roleNames.push(role.getName());
        }

        // Don't call API if role was changed for the Create New Account form.
        if (isNewAccount) {
            return;
        }

        updateAccountRoles(account.getId(), roleNames, function (data) {
            if (!data.wasSuccess) {
                app.App.alert(
                    "Unable to update roles",
                    <div>
                        Unable to remove role {roleName} from {account.getName()} ({account.getUsername()}).<br/>
                        <br/>
                        Please refresh the page to reset role information.
                    </div>
                );
            }
        });
    }

    onAccountClicked(account) {
        const editedAccount = Account.fromJson(Account.toJson(account));
        this.setState({
            isEditingAccount: true,
            editedAccount: editedAccount,
            editedAccountButtonState: this.SaveButtonState.save,
        });
    }

    onUpdateAccountMetadata(event) {
        event.preventDefault();
        const editedAccount = this.state.editedAccount;
        const editedAccountJson = Account.toJson(editedAccount);
        const thisApp = this;

        this.setState({
            editedAccountButtonState: this.SaveButtonState.saving,
        });

        updateAccountMetadata(editedAccountJson, function(data) {
            if (data.wasSuccess) {
                app.App.alert("Update Account", editedAccount.getName() + "'s account has been successfully updated.");

                const editedAccountId = editedAccount.getId();
                const accounts = thisApp.state.accounts.filter(function(value) {
                    return value.getId() != editedAccountId;
                });
                accounts.push(editedAccount);

                thisApp.setState({
                    accounts: accounts,
                    editedAccount: null,
                });
            }
            else {
                app.App.alert("Update Account", data.errorMessage);

                thisApp.setState({
                    editedAccountButtonState: thisApp.SaveButtonState.save,
                });
            }
        });
    }

    onEditedAccountNameChanged(value) {
        const account = this.state.editedAccount;
        account.setName(value);

        this.setState({
            editedAccount: account,
            editedAccountButtonState: this.SaveButtonState.save,
        });
    }


    onEditedAccountUsernameChanged(value) {
        const account = this.state.editedAccount;
        account.setUsername(value);

        this.setState({
            editedAccount: account,
            editedAccountButtonState: this.SaveButtonState.save,
        });
    }

    onEditedAccountCompanyChanged(value) {
        const account = this.state.editedAccount;
        const companies = this.props.companies;

        for (let i in companies) {
            const company = companies[i];
            if (company.getName() == value) {
                account.setCompany(company);
                break;
            }
        }

        this.setState({
            editedAccount: account,
            editedAccountButtonState: this.SaveButtonState.save,
        });
    }

    onResetPassword(account) {
        if (typeof this.props.onResetPassword == "function") {
            this.props.onResetPassword(account);
        }
    }

    onCancelUpdateAccount(event) {
        event.preventDefault();

        this.setState({
            editedAccount: null
        });
    }

    renderCreateButtonText(typeOfObjectCreated) {
        let buttonState = this.state.createAccountButtonState;
        if (typeOfObjectCreated == "Company") {
            buttonState = this.state.createCompanyButtonState;
        }
        else if (this.state.editedAccount) {
            buttonState: this.editedAccountButtonState;
        }

        switch (buttonState) {
            case this.SaveButtonState.save:
                return "Save";

            case this.SaveButtonState.submit:
                return "Submit";

            case this.SaveButtonState.saving:
                return (
                    <i className="fa fa-refresh fa-spin"/>
                );

            case this.SaveButtonState.saved:
                return typeOfObjectCreated + " Created";
        }
    }

    renderCreateAccountForm() {
        const account = this.state.newAccount;
        const companies = this.props.companies;
        let companyOptions = [];

        for (let i in companies) {
            companyOptions.push(companies[i].getName());
        }
        companyOptions.sort(function(a, b) {
            return a.localeCompare(b, undefined, {numeric : true, sensitivity: 'base'});
        });

        let createAccountSaveButton = <input type="submit" id="create-account-button" className="button" value={this.renderCreateButtonText("Account")} />;
        if (this.state.createAccountButtonState === this.SaveButtonState.saving) {
            createAccountSaveButton = <div id="create-account-button" className="button">{this.renderCreateButtonText("Account")}</div>;
        }

        return (
            <form className="small-container" onSubmit={this.onSubmitNewAccount}>
                <h1>Create Account</h1>
                <app.InputField type="text" label="Username" name="username" value={account.getUsername()} onChange={this.onNewAccountUsernameChanged} isRequired={true}/>
                <app.InputField type="text" label="Name" name="name" value={account.getName()} onChange={this.onNewAccountNameChanged} isRequired={true}/>
                <app.InputField type="select" label="Company" name="company" value={account.getCompany().getName()} options={companyOptions} onChange={this.onNewAccountCompanyChanged} isRequired={true}/>
                {this.renderRoleComponents(account, true)}
                {createAccountSaveButton}
            </form>
        );
    }

    renderCreateCompanyForm() {
        const newCompany = this.state.newCompany;

        let createCompanySaveButton = <input type="submit" id="create-company-button" className="button" value={this.renderCreateButtonText("Company")} />;
        if (this.state.createCompanyButtonState === this.SaveButtonState.saving) {
            createCompanySaveButton = <div id="create-company-button" className="button">{this.renderCreateButtonText("Company")}</div>;
        }

        return (
            <form className="small-container" onSubmit={this.onSubmitNewCompany}>
                <h1>Create Company</h1>
                <app.InputField type="text" label="Name" name="name" value={newCompany.getName()} onChange={this.onNewCompanyNameChanged} isRequired={true}/>
                {createCompanySaveButton}
            </form>
        );
    }

    renderAccountAdministrationData() {
        if (this.state.isLoadingAccounts) {
            return (
                <div className="center">
                    <i className="fa fa-3x fa-spin fa-refresh"></i>
                </div>
            );
        }

        const administrationTableRows = [];
        const accounts = this.state.accounts.sort(function(a, b) {
            return a.getName().localeCompare(b.getName(), undefined, {numeric : true, sensitivity: 'base'});
        });

        for (let i in accounts) {
            const account = accounts[i];

            administrationTableRows.push(
                <tr key={i}>
                    <td key="name" className="user-column" onClick={() => this.onAccountClicked(account)}><i className="fa fa-edit"/>{account.getName()}<br/>({account.getUsername()})</td>
                    <td key="roles">{this.renderRoleComponents(account)}</td>
                    <td key="reset"><div className="button" onClick={() => this.onResetPassword(account)}>Reset Password</div></td>
                </tr>
            );
        }

        return (
            <table className="accounts-table">
                <thead>
                    <tr>
                        <th key="name">User</th>
                        <th key="roles">Roles</th>
                        <th key="reset"></th>
                    </tr>
                </thead>
                <tbody>
                    {administrationTableRows}
                </tbody>
            </table>
        );
    }

    renderRoleComponents(account, isNewAccount) {
        return (
            <div className="role-components">
                <app.InputField key="1" type="checkbox" label="Login" checked={account.hasRole("Login")} onChange={(value) => this.onRoleChange(account, "Login", value, isNewAccount)} isSmallInputField={true}/>
                <app.InputField key="2" type="checkbox" label="Admin" checked={account.hasRole("Admin")} onChange={(value) => this.onRoleChange(account, "Admin", value, isNewAccount)} isSmallInputField={true}/>
                <app.InputField key="3" type="checkbox" label="Release" checked={account.hasRole("Release")} onChange={(value) => this.onRoleChange(account, "Release", value, isNewAccount)} isSmallInputField={true}/>
                <app.InputField key="4" type="checkbox" label="Modify" checked={account.hasRole("Modify")} onChange={(value) => this.onRoleChange(account, "Modify", value, isNewAccount)} isSmallInputField={true}/>
                <app.InputField key="5" type="checkbox" label="Review" checked={account.hasRole("Review")} onChange={(value) => this.onRoleChange(account, "Review", value, isNewAccount)} isSmallInputField={true}/>
                <app.InputField key="6" type="checkbox" label="View" checked={account.hasRole("View")} onChange={(value) => this.onRoleChange(account, "View", value, isNewAccount)} isSmallInputField={true}/>
            </div>
        );
    }

    renderEditUserForm() {
        if (this.state.editedAccount) {
            const editedAccount = this.state.editedAccount;
            const companies = this.props.companies;
            let companyOptions = [];

            for (let i in companies) {
                companyOptions.push(companies[i].getName());
            }
            companyOptions.sort(function(a, b) {
                return a.localeCompare(b, undefined, {numeric : true, sensitivity: 'base'});
            });

            let editedAccountSaveButton = <button type="save" id="create-account-button" className="button">{this.renderCreateButtonText("Account")}</button>;
            if (this.state.editedAccountButtonState === this.SaveButtonState.saving) {
                editedAccountSaveButton = <div id="create-account-button" className="button">{this.renderCreateButtonText("Account")}</div>;
            }

            return (
                <form id="edit-user" className="popup-container" onSubmit={this.onUpdateAccountMetadata}>
                    <h1>Edit Account</h1>
                    <app.InputField type="text" label="Username" name="username" value={editedAccount.getUsername()} onChange={this.onEditedAccountUsernameChanged} isRequired={true}/>
                    <app.InputField type="text" label="Name" name="name" value={editedAccount.getName()} onChange={this.onEditedAccountNameChanged} isRequired={true} />
                    <app.InputField type="select" label="Company" name="company" value={editedAccount.getCompany().getName()} options={companyOptions} onChange={this.onEditedAccountCompanyChanged} isRequired={true}/>
                    {editedAccountSaveButton}
                    <div className="cancel-button"><button className="button" onClick={this.onCancelUpdateAccount}>Cancel</button></div>
                </form>
            );
        }
    }

    renderBackdrop() {
        if (this.state.editedAccount) {
            return(<div id="backdrop" onClick={() => this.setState({ editedAccount: null })} />);
        }
    }

    render() {
        return (
            <div className="account-administration-area">
                <div className="create-account-area" key="create-account-area">
                    {this.renderCreateAccountForm()}
                    {this.renderCreateCompanyForm()}
                    {this.renderBackdrop()}
                    {this.renderEditUserForm()}
                </div>
                <div className="accounts-area large-container" key="accounts-area">
                    <div className="center">
                        <h1>Accounts</h1>
                    </div>
                    {this.renderAccountAdministrationData()}
                </div>
            </div>
        );
    }
}

registerClassWithGlobalScope("AccountsPage", AccountsPage);

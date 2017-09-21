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

        const newCompany = new Company();

        this.state = {
            newAccount: account,
            newCompany: newCompany,
            createAccountButtonState: this.SaveButtonState.submit,
            createCompanyButtonState: this.SaveButtonState.submit,
            isLoadingAccounts: true,
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

        this.onNewAccountUsernameChanged = this.onNewAccountUsernameChanged.bind(this);
        this.onNewAccountNameChanged = this.onNewAccountNameChanged.bind(this);
        this.onNewAccountCompanyChanged = this.onNewAccountCompanyChanged.bind(this);
        this.onNewCompanyNameChanged = this.onNewCompanyNameChanged.bind(this);
        this.onSubmitNewAccount = this.onSubmitNewAccount.bind(this);
        this.onSubmitNewCompany = this.onSubmitNewCompany.bind(this);
        this.onResetPassword = this.onResetPassword.bind(this);
        this.renderCreateAccountForm = this.renderCreateAccountForm.bind(this);
        this.renderCreateCompanyForm = this.renderCreateCompanyForm.bind(this);
        this.renderCreateButtonText = this.renderCreateButtonText.bind(this);
        this.renderAccountAdministrationData = this.renderAccountAdministrationData.bind(this);
        this.renderRoleComponents = this.renderRoleComponents.bind(this);
    }

    componentWillReceiveProps(newProperties) {

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

    onResetPassword(account) {
        if (typeof this.props.onResetPassword == "function") {
            this.props.onResetPassword(account);
        }
    }

    renderCreateButtonText(typeOfObjectCreated) {
        let buttonState = this.state.createAccountButtonState;
        if (typeOfObjectCreated == "Company") {
            buttonState = this.state.createCompanyButtonState;
        }

        switch (buttonState) {
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
        const companyOptions = [];

        for (let i in companies) {
            companyOptions.push(companies[i].getName());
        }

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

        for (let i in this.state.accounts) {
            const account = this.state.accounts[i];

            administrationTableRows.push(
                <tr key={i}>
                    <td key="name">{account.getName()}<br/>({account.getUsername()})</td>
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
                <app.InputField key="1" type="checkbox" label="Admin" checked={account.hasRole("Admin")} onChange={(value) => this.onRoleChange(account, "Admin", value, isNewAccount)} isSmallInputField={true}/>
                <app.InputField key="2" type="checkbox" label="Release" checked={account.hasRole("Release")} onChange={(value) => this.onRoleChange(account, "Release", value, isNewAccount)} isSmallInputField={true}/>
                <app.InputField key="3" type="checkbox" label="Modify" checked={account.hasRole("Modify")} onChange={(value) => this.onRoleChange(account, "Modify", value, isNewAccount)} isSmallInputField={true}/>
                <app.InputField key="4" type="checkbox" label="Review" checked={account.hasRole("Review")} onChange={(value) => this.onRoleChange(account, "Review", value, isNewAccount)} isSmallInputField={true}/>
                <app.InputField key="5" type="checkbox" label="View" checked={account.hasRole("View")} onChange={(value) => this.onRoleChange(account, "View", value, isNewAccount)} isSmallInputField={true}/>
            </div>
        );
    }

    render() {
        return (
            <div className="account-administration-area">
                <div className="create-account-area" key="create-account-area">
                    {this.renderCreateAccountForm()}
                    {this.renderCreateCompanyForm()}
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

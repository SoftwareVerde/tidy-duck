class AccountsPage extends React.Component {
    constructor(props) {
        super(props);

        const account = new Account();
        const company = new Company();
        account.setCompany(company);

        this.SaveButtonState = {
            submit: 'submit',
            save:   'save',
            saving: 'saving',
            saved:  'saved'
        };

        this.state = {
            newAccount: account,
            createAccountButtonState: this.SaveButtonState.submit,
            isLoadingAccounts: true
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
        this.onNewAccountCompanyIdChanged = this.onNewAccountCompanyIdChanged.bind(this);
        this.onSubmitNewAccount = this.onSubmitNewAccount.bind(this);
        this.renderCreateAccountForm = this.renderCreateAccountForm.bind(this);
        this.renderCreateAccountButtonText = this.renderCreateAccountButtonText.bind(this);
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

    onNewAccountCompanyIdChanged(value) {
        // TODO: maybe this should be a select field with company names?
        const account = this.state.newAccount;
        const company = account.getCompany();
        company.setId(value);
        account.setCompany(company);

        this.setState({
            newAccount: account,
            createAccountButtonState: this.SaveButtonState.submit
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
                const alertString = <div className="alert-content">
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
                    newAccount: newAccount
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

    renderCreateAccountButtonText() {
        switch (this.state.createAccountButtonState) {
            case this.SaveButtonState.submit:
                return "Submit";

            case this.SaveButtonState.saving:
                return (
                    <i className="fa fa-refresh fa-spin"/>
                );

            case this.SaveButtonState.saved:
                return "Account Created";
        }
    }

    renderCreateAccountForm() {
        const account = this.state.newAccount;

        let createAccountSaveButton = <input type="submit" id="create-account-button" className="button" value={this.renderCreateAccountButtonText()} />;
        if (this.state.createAccountButtonState === this.SaveButtonState.saving) {
            createAccountSaveButton = <div type="submit" id="create-account-button" className="button">{this.renderCreateAccountButtonText()}</div>;
        }

        return (
            <form className="small-container" onSubmit={this.onSubmitNewAccount}>
                <h1>Create Account</h1>
                <app.InputField type="text" label="Username" name="username" value={account.getUsername()} onChange={this.onNewAccountUsernameChanged} isRequired={true}/>
                <app.InputField type="text" label="Name" name="name" value={account.getName()} onChange={this.onNewAccountNameChanged} isRequired={true}/>
                <app.InputField type="text" label="Company ID" name="name" value={account.getCompany().getId()} onChange={this.onNewAccountCompanyIdChanged} isRequired={true}/>
                {createAccountSaveButton}
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
                    <td key="name">{account.getName()}</td>
                    <td key="roles">{this.renderRoleComponents(account)}</td>
                    <td key="reset"><div className="button">Reset Password</div></td>
                </tr>
            );
        }

        return (
            <table className="accounts-table">
                <thead>
                    <tr>
                        <th key="name">Name</th>
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

    renderRoleComponents(account) {
        return (
            <div className="role-components">
                <app.InputField key="1" type="checkbox" label="Admin" checked={account.hasRole("Admin")} isSmallInputField={true}/>
                <app.InputField key="2" type="checkbox" label="Release" checked={account.hasRole("Release")} isSmallInputField={true}/>
                <app.InputField key="3" type="checkbox" label="Modify" checked={account.hasRole("Modify")} isSmallInputField={true}/>
                <app.InputField key="4" type="checkbox" label="Review" checked={account.hasRole("Review")} isSmallInputField={true}/>
                <app.InputField key="5" type="checkbox" label="View" checked={account.hasRole("View")} isSmallInputField={true}/>
            </div>
        );
    }

    render() {
        return (
            <div className="account-administration-area">
                <div className="create-account-area" key="create-account-area">
                    {this.renderCreateAccountForm()}
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

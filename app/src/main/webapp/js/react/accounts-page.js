class AccountsPage extends React.Component {
    constructor(props) {
        super(props);

        const account = new Account();
        const company = this.props.companies[0];
        account.setCompany(company);

        this.SaveButtonState = {
            submit: 'submit',
            save:   'save',
            saving: 'saving',
            saved:  'saved'
        };

        this.state = {
            newAccount: account,
            createAccountButtonState: this.SaveButtonState.submit
        };

        this.onNewAccountUsernameChanged = this.onNewAccountUsernameChanged.bind(this);
        this.onNewAccountNameChanged = this.onNewAccountNameChanged.bind(this);
        this.onNewAccountCompanyChanged = this.onNewAccountCompanyChanged.bind(this);
        this.onSubmitNewAccount = this.onSubmitNewAccount.bind(this);
        this.renderCreateAccountForm = this.renderCreateAccountForm.bind(this);
        this.renderCreateAccountButtonText = this.renderCreateAccountButtonText.bind(this);
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
        const companies = this.props.companies;
        const companyOptions = [];

        for (let i in companies) {
            companyOptions.push(companies[i].getName());
        }

        let createAccountSaveButton = <input type="submit" id="create-account-button" className="button" value={this.renderCreateAccountButtonText()} />;
        if (this.state.createAccountButtonState === this.SaveButtonState.saving) {
            createAccountSaveButton = <div type="submit" id="create-account-button" className="button">{this.renderCreateAccountButtonText()}</div>;
        }

        return (
            <form id="settings-container" onSubmit={this.onSubmitNewAccount}>
                <h1>Create Account</h1>
                <app.InputField type="text" label="Username" name="username" value={account.getUsername()} onChange={this.onNewAccountUsernameChanged} isRequired={true}/>
                <app.InputField type="text" label="Name" name="name" value={account.getName()} onChange={this.onNewAccountNameChanged} isRequired={true}/>
                <app.InputField type="select" label="Company" name="company" value={account.getCompany().getName()} options={companyOptions} onChange={this.onNewAccountCompanyChanged} isRequired={true}/>
                {createAccountSaveButton}
            </form>
        );
    }

    render() {
        return (
            <div className="account-administration-area">
                {this.renderCreateAccountForm()}
            </div>
        );
    }
}

registerClassWithGlobalScope("AccountsPage", AccountsPage);

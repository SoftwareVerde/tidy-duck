class SettingsPage extends React.Component {
    constructor(props) {
        super(props);

        this.SaveButtonState = {
            save:   'save',
            saving: 'saving',
            saved:  'saved'
        };

        this.state = {
            currentTheme:       this.props.theme,
            newPassword:        "",
            newPasswordRetype:  "",
            oldPassword:        "",
            saveButtonState:    this.SaveButtonState.save,
            passwordsMatch:     false,
        };

        this.onThemeChange = this.onThemeChange.bind(this);
        this.onNewPasswordChanged = this.onNewPasswordChanged.bind(this);
        this.onNewPasswordRetypeChanged = this.onNewPasswordRetypeChanged.bind(this);
        this.onOldPasswordChanged = this.onOldPasswordChanged.bind(this);
        this.onSaveNewPassword = this.onSaveNewPassword.bind(this);
        this.onSave = this.onSave.bind(this);
        this.renderPasswordsMatchText = this.renderPasswordsMatchText.bind(this);
        this.renderSaveButtonText = this.renderSaveButtonText.bind(this);
    }

    componentWillReceiveProps(newProperties) {
        this.setState({
            currentTheme:       newProperties.theme,
        });
    }

    onThemeChange(value) {
        this.props.onThemeChange(value);
        this.setState({
            currentTheme:       value,
            saveButtonState:    this.SaveButtonState.save
        });
    }

    onOldPasswordChanged(value) {
        this.setState({
            oldPassword: value
        });
    }

    onNewPasswordChanged(value) {
        let passwordsMatch = this.state.passwordsMatch;

        if (value === this.state.newPasswordRetype) {
            passwordsMatch = true;
        }
        else {
            passwordsMatch = false;
        }
        this.setState({
            newPassword: value,
            passwordsMatch: passwordsMatch
        });
    }

    onNewPasswordRetypeChanged(value) {
        let passwordsMatch = this.state.passwordsMatch;

        if (value === this.state.newPassword) {
            passwordsMatch = true;
        }
        else {
            passwordsMatch = false;
        }

        this.setState({
            newPasswordRetype: value,
            passwordsMatch: passwordsMatch
        });
    }

    onSaveNewPassword(event) {
        event.preventDefault();
        const newPassword = this.state.newPassword;
        const accountId = this.props.accountId;

        // Validate new password fields
        if (newPassword !== this.state.newPasswordRetype) {
            alert("New passwords do not match. ");
            return;
        }

        // TODO: add validation for new password, and checking if old password is entered twice (and matches).
        if (newPassword.length < 8) {
            alert("New password is invalid. Please enter at least 8 characters.");
            return;
        }

        const oldPassword = this.state.oldPassword;
        const thisApp = this;

        changePassword(accountId, oldPassword, newPassword, function(data) {
            if (! data.wasSuccess) {
                alert("Unable to change password: " + data.errorMessage);
            }
            else {
                alert("Password successfully changed.");
                thisApp.setState({
                    newPassword: "",
                    newPasswordRetype: "",
                    oldPassword: ""
                });
            }
        });
    }

    onSave() {
        const settings = {
            theme: this.state.currentTheme
        };
        this.setState({
            saveButtonState: this.SaveButtonState.saving
        });
        const thisButton = this;
        updateSettings(settings, function(data) {
            if (data.wasSuccess) {
                thisButton.setState({
                    saveButtonState: thisButton.SaveButtonState.saved
                });
            } else {
                thisButton.setState({
                    saveButtonState: thisButton.SaveButtonState.save
                });
            }
        });
    }

    renderPasswordsMatchText() {
        let visibility = 'hidden';
        let errorText = 'Password must be at least 8 characters long.';
        const checkRetypedPassword = this.state.newPasswordRetype.length > 0;

        if (this.state.newPassword.length > 0 || checkRetypedPassword) {
            if (this.state.newPassword.length < 8) {
                visibility = 'visible';
            }
            else if (checkRetypedPassword && ! this.state.passwordsMatch) {
                visibility = 'visible';
                errorText = 'New passwords do not match.';
            }
        }

        return(
            <div style={{color: 'red', textAlign: 'center', visibility: visibility}}>{errorText}</div>
        );
    }

    renderSaveButtonText() {
        switch (this.state.saveButtonState) {
            case this.SaveButtonState.save:
                return "Save";

            case this.SaveButtonState.saving:
                return (
                    <i className="fa fa-refresh fa-spin"/>
                );

            case this.SaveButtonState.saved:
                return "Settings Saved";
        }
    }

    render() {
        const themeOptions = ['Tidy', 'Darkwing'];
        const reactComponents = [];

        reactComponents.push(
            <div key="Theme Container" id="settings-container">
                <app.InputField type="select" label="Theme" name="theme" value={this.state.currentTheme} options={themeOptions} onChange={this.onThemeChange}/>
                <div id="save-settings-button" className="button" onClick={this.onSave}>{this.renderSaveButtonText()}</div>
            </div>
        );

        reactComponents.push(
            <form key="Password Container" id="settings-container" onSubmit={this.onSaveNewPassword}>
                <app.InputField type="password" label="Current Password" name="old-password1" value={this.state.oldPassword} options={themeOptions} onChange={this.onOldPasswordChanged} isRequired={true}/>
                <app.InputField type="password" label="New Password" name="new-password" value={this.state.newPassword} options={themeOptions} onChange={this.onNewPasswordChanged} isRequired={true}/>
                <app.InputField type="password" label="Retype New Password" name="new-password-retype" value={this.state.newPasswordRetype} options={themeOptions} onChange={this.onNewPasswordRetypeChanged} isRequired={true}/>
                {this.renderPasswordsMatchText()}
                <input type="submit" id="save-settings-button" className="button" value={this.renderSaveButtonText()} />
            </form>
        );

        return(
            <div id="main-content" className="container">{reactComponents}</div>
        );
    }
}

registerClassWithGlobalScope("SettingsPage", SettingsPage);

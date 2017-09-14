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
            saveButtonState:    this.SaveButtonState.save
        };

        this.onThemeChange = this.onThemeChange.bind(this);
        this.onNewPasswordChange = this.onNewPasswordChange.bind(this);
        this.onSaveNewPassword = this.onSaveNewPassword.bind(this);
        this.onSave = this.onSave.bind(this);
        this.renderSaveButtonText = this.renderSaveButtonText.bind(this);
    }

    onThemeChange(value) {
        this.props.onThemeChange(value);
        this.setState({
            currentTheme:       value,
            saveButtonState:    this.SaveButtonState.save
        });
    }

    onNewPasswordChange(value) {
        this.setState({
            newPassword: value
        });
    }

    onSaveNewPassword() {
        const newPassword = this.state.newPassword;
        const account = this.props.account;
        const accountId = account.getId();
        // TODO: get old password!
        const oldPassword = "Password";

        // TODO: add validation for new password, and checking if old password is entered twice (and matches).
        if (newPassword.length < 3) {
            alert("New password is invalid. Please enter at least 4 characters.");
            return;
        }

        changePassword(accountId, oldPassword, newPassword, function(data) {
            if (! data.wasSuccess) {
                alert("Unable to change password: " + data.errorMessage);
            }
            else {
                alert("Password successfully changed.");
                this.setState({
                    newPassword: ""
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
            <div key="Password Container" id="settings-container">
                <app.InputField type="text" label="New Password" name="new-password" value={this.state.newPassword} options={themeOptions} onChange={this.onNewPasswordChange}/>
                <div id="save-settings-button" className="button" onClick={this.onSaveNewPassword}>{this.renderSaveButtonText()}</div>
            </div>
        );

        return(
            <div id="main-content" className="container">{reactComponents}</div>
        );
    }
}

registerClassWithGlobalScope("SettingsPage", SettingsPage);

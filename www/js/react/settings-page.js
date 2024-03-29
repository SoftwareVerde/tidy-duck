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
            currentDefaultMode: this.props.defaultMode,
            showCheckmark:      false,
            newPassword:        "",
            newPasswordRetype:  "",
            oldPassword:        "",
            settingsSaveButtonState:    this.SaveButtonState.save,
            passwordSaveButtonState:    this.SaveButtonState.save,
            applicationSettingsSaveButtonState:  this.SaveButtonState.save,
            passwordsMatch:     false,
        };

        const thisPage = this;
        getApplicationSettingValue("REVIEW_APPROVAL_MINIMUM_UPVOTES", function(value) {
            if (value == null) {
                app.Alert("Loading Application Settings", "Unable to load review approval minimum upvotes property.");
                return;
            }
            thisPage.setState({
                reviewApprovalMinimumUpvotes: value
            });
        });

        this.handleKeyPress = this.handleKeyPress.bind(this);
        this.onThemeChange = this.onThemeChange.bind(this);
        this.onDefaultModeChanged = this.onDefaultModeChanged.bind(this);
        this.onNewPasswordChanged = this.onNewPasswordChanged.bind(this);
        this.onNewPasswordRetypeChanged = this.onNewPasswordRetypeChanged.bind(this);
        this.onOldPasswordChanged = this.onOldPasswordChanged.bind(this);
        this.onSaveNewPassword = this.onSaveNewPassword.bind(this);
        this.onSettingsSave = this.onSettingsSave.bind(this);
        this.onReviewApprovalMinimumUpvotesChanged = this.onReviewApprovalMinimumUpvotesChanged.bind(this);
        this.onApplicationSettingsSaved = this.onApplicationSettingsSaved.bind(this);
        this.renderPasswordsMatchWarning = this.renderPasswordsMatchWarning.bind(this);
        this.renderSettingsSaveButtonText = this.renderSettingsSaveButtonText.bind(this);
        this.renderApplicationSettingsSaveButtonText = this.renderApplicationSettingsSaveButtonText.bind(this);
        this.renderPasswordSaveButtonText = this.renderPasswordSaveButtonText.bind(this);
    }

    handleKeyPress(event) {
        if (event.key == 'Escape') {
            if (typeof this.props.handleSettingsClick == "function") {
                this.props.handleSettingsClick();
            }
        }
    }

    componentDidMount() {
        document.addEventListener('keydown', this.handleKeyPress);
    }

    componentWillUnmount() {
        document.removeEventListener('keydown', this.handleKeyPress);
    }

    componentWillReceiveProps(newProperties) {
        this.setState({
            currentTheme:       newProperties.theme,
            currentDefaultMode: newProperties.defaultMode
        });
    }

    onThemeChange(value) {
        if (typeof this.props.onThemeChange == "function") {
            this.props.onThemeChange(value);
            this.onSettingsSave(value, this.state.currentDefaultMode);
        }

        this.setState({
            currentTheme:       value,
        });
    }

    onDefaultModeChanged(value) {
        if (typeof this.props.onDefaultModeChanged == "function") {
            this.onSettingsSave(this.state.currentTheme, value);
            this.props.onDefaultModeChanged(value);
        }

        this.setState({
            currentDefaultMode:         value,
        });
    }

    onReviewApprovalMinimumUpvotesChanged(value) {
        this.setState({
            reviewApprovalMinimumUpvotes: value,
            applicationSettingsSaveButtonState: this.SaveButtonState.save
        });
    }

    onApplicationSettingsSaved(event) {
        event.preventDefault();

        this.setState({
            applicationSettingsSaveButtonState:  this.SaveButtonState.saving
        });
        const thisPage = this;
        setApplicationSettingValue("REVIEW_APPROVAL_MINIMUM_UPVOTES", this.state.reviewApprovalMinimumUpvotes, function(wasSuccess) {
            if (!wasSuccess) {
                app.App.alert("Unable to Save Application Settings", "Unable to save application settings.  Please try again later.");
                thisPage.setState({
                    applicationSettingsSaveButtonState:  thisPage.SaveButtonState.save
                });
                return;
            }
            else {
                thisPage.setState({
                    applicationSettingsSaveButtonState:  thisPage.SaveButtonState.saved
                });
            }
        });
    }

    onOldPasswordChanged(value) {
        this.setState({
            oldPassword: value,
            passwordSaveButtonState:    this.SaveButtonState.save,
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
            passwordsMatch: passwordsMatch,
            passwordSaveButtonState:    this.SaveButtonState.save
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
            passwordsMatch: passwordsMatch,
            passwordSaveButtonState:    this.SaveButtonState.save
        });
    }

    onSaveNewPassword(event) {
        event.preventDefault();
        const newPassword = this.state.newPassword;
        const accountId = this.props.account.getId();
        const saveButtonState = this.SaveButtonState;

        // Validate new password fields
        if (newPassword.length < 8) {
            alert("New password is invalid. Please enter at least 8 characters.");
            return;
        }
        if (newPassword !== this.state.newPasswordRetype) {
            alert("New passwords do not match. ");
            return;
        }

        const oldPassword = this.state.oldPassword;
        const thisApp = this;

        this.setState({
            passwordSaveButtonState:    saveButtonState.saving,
        });

        changePassword(accountId, oldPassword, newPassword, function(data) {
            if (data.wasSuccess) {
                thisApp.setState({
                    newPassword: "",
                    newPasswordRetype: "",
                    oldPassword: "",
                    passwordSaveButtonState: saveButtonState.saved,
                });
            } else {
                alert("Unable to change password: " + data.errorMessage);

                thisApp.setState({
                    passwordSaveButtonState: saveButtonState.save,
                });
            }
        });
    }

    onSettingsSave(theme, defaultMode) {
        const settings = {
            theme:          theme,
            defaultMode:    defaultMode
        };

        const thisPage = this;

        this.setState({
            showCheckmark: false
        });

        updateSettings(settings, function(data) {
            if (data.wasSuccess) {
                thisPage.setState({
                    showCheckmark: true
                });
            }
            else {
                app.App.alert("Update Settings", data.errorMessage);
            }
        });
    }

    renderPasswordsMatchWarning() {
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

    renderSettingsSaveButtonText() {
        switch (this.state.settingsSaveButtonState) {
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

    renderApplicationSettingsSaveButtonText() {
        switch (this.state.applicationSettingsSaveButtonState) {
            case this.SaveButtonState.save:
                return "Save";

            case this.SaveButtonState.saving:
                return (
                    <i className="fa fa-refresh fa-spin"/>
                );

            case this.SaveButtonState.saved:
                return "Saved";
        }
    }

    renderPasswordSaveButtonText() {
        switch (this.state.passwordSaveButtonState) {
            case this.SaveButtonState.save:
                return "Save";

            case this.SaveButtonState.saving:
                return (
                    <i className="fa fa-refresh fa-spin"/>
                );

            case this.SaveButtonState.saved:
                return "New Password Saved";
        }
    }

    render() {
        const themeOptions = ['Tidy', 'Darkwing'];
        const modeOptions = this.props.roles;
        const reactComponents = [];

        const showCheckmark = (this.state.showCheckmark ? "visible" : "hidden");

        reactComponents.push(
            <div key="Theme Container" id="settings-container" className="small-container">
                <h1>User Settings</h1>
                <app.InputField type="select" label="Theme" name="theme" value={this.state.currentTheme} options={themeOptions} onChange={this.onThemeChange}/>
                <app.InputField type="select" label="Default Tab" name="defaultMode" value={this.state.currentDefaultMode} options={modeOptions} onChange={this.onDefaultModeChanged}/>
                <div className="settings-success-container" style={{visibility: showCheckmark}}>
                    <h2>CHANGES SAVED</h2>
                    <i className="fa fa-check-circle"/>
                </div>
            </div>
        );

        if (this.props.account.hasPermission("ADMIN_MODIFY_APPLICATION_SETTINGS")) {
            // add application settings form
            let applicationSettingsSaveButton = <input type="submit" className="button" id="application-settings-save-button" value={this.renderApplicationSettingsSaveButtonText()}/>
            if (this.state.applicationSettingsSaveButtonState === this.SaveButtonState.saving) {
                applicationSettingsSaveButton = <div id="application-settings-save-button" className="button">{this.renderApplicationSettingsSaveButtonText()}</div>;
            }

            reactComponents.push(
                <form key="Application Settings Container" id="application-settings-container" className="small-container" onSubmit={this.onApplicationSettingsSaved}>
                    <h1>Application Settings</h1>
                    <app.InputField type="number" min="1" label="Minimum Upvotes for Review Approval" value={this.state.reviewApprovalMinimumUpvotes} onChange={this.onReviewApprovalMinimumUpvotesChanged} />
                    {applicationSettingsSaveButton}
                </form>
            );
        }

        let passwordSaveButton = <input type="submit" id="save-settings-button" className="button" value={this.renderPasswordSaveButtonText()} />;
        if (this.state.passwordSaveButtonState === this.SaveButtonState.saving) {
            passwordSaveButton = <div type="submit" id="save-settings-button" className="button">{this.renderPasswordSaveButtonText()}</div>;
        }

        reactComponents.push(
            <form key="Password Container" className="small-container" onSubmit={this.onSaveNewPassword}>
                <h1>Change Password</h1>
                <app.InputField type="password" label="Current Password" name="old-password1" value={this.state.oldPassword} onChange={this.onOldPasswordChanged} isRequired={true}/>
                <app.InputField type="password" label="New Password" name="new-password" value={this.state.newPassword} onChange={this.onNewPasswordChanged} isRequired={true}/>
                <app.InputField type="password" label="Retype New Password" name="new-password-retype" value={this.state.newPasswordRetype} onChange={this.onNewPasswordRetypeChanged} isRequired={true}/>
                {this.renderPasswordsMatchWarning()}
                {passwordSaveButton}
            </form>
        );

        return(
            <div style={{width: '100%', textAlign: 'center'}}>{reactComponents}</div>
        );
    }
}

registerClassWithGlobalScope("SettingsPage", SettingsPage);

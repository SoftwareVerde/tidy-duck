class SettingsPage extends React.Component {
    constructor(props) {
        super(props);

        this.SaveButtonState = {
            save:   'save',
            saving: 'saving',
            saved:  'saved'
        };

        this.state = {
            currentTheme:       this.props.currentTheme,
            saveButtonState:    this.SaveButtonState.save
        };

        this.onThemeChange = this.onThemeChange.bind(this);
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
        return (
            <div id="settings-container">
                <app.InputField type="select" label="Theme" name="theme" value={this.state.currentTheme} options={themeOptions} onChange={this.onThemeChange}/>

                <div id="save-settings-button" className="button" onClick={this.onSave}>{this.renderSaveButtonText()}</div>
            </div>
        );
    }
}

registerClassWithGlobalScope("SettingsPage", SettingsPage);

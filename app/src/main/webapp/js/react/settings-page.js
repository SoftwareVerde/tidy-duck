class SettingsPage extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            currentTheme: this.props.currentTheme || 'Tidy'
        };

        this.onThemeChange = this.onThemeChange.bind(this);
    }

    onThemeChange(value) {
        this.props.onThemeChange(value);
        this.setState({
            currentTheme: value
        });
    }

    render() {
        const themeOptions = ['Tidy', 'Darkwing'];
        return (
            <div id="settings-container">
                <app.InputField type="select" label="Theme" name="theme" value={this.state.currentTheme} options={themeOptions} onChange={this.onThemeChange}/>

                <div id="save-settings-button" className="button">Save</div>
            </div>
        );
    }
}

registerClassWithGlobalScope("SettingsPage", SettingsPage);

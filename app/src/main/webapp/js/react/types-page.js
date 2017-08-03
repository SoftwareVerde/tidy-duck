class TypesPage extends React.Component {
    constructor(props) {
        super(props);

        this.options = ["Create Type", "Edit Type"];

        this.state = {
            selectedOption: this.options[0]
        };

        this.handleOptionClick = this.handleOptionClick.bind(this);
        this.renderChildElements = this.renderChildElements.bind(this);
        this.onSave = this.onSave.bind(this);
    }

    handleOptionClick(option) {
        this.setState({
            selectedOption: option
        });
    }

    onSave() {
        // TODO: save type
        console.log("Type save button clicked.");
    }

    renderChildElements() {
        return (
            <div className="center">
                <div>
                    <span>Placeholder for inputs.</span>
                </div>
                <div className="button" onClick={this.onSave}>Save</div>
            </div>
        )
    }

    render() {
        return (
            <div id="types-container">
                <div id="types-options-container" className="center">
                    <app.RoleToggle roleItems={this.options} handleClick={this.handleOptionClick} activeRole={this.state.selectedOption} />
                </div>
                {this.renderChildElements()}
            </div>
        );
    }
}

registerClassWithGlobalScope("TypesPage", TypesPage);

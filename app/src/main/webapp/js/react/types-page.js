class TypesPage extends React.Component {
    constructor(props) {
        super(props);

        this.options = ["Create Type", "Edit Type"];

        this.state = {
            selectedOption: this.options[0]
        };

        this.onTypeNameChanged = this.onTypeNameChanged.bind(this);
        this.onBaseTypeChanged = this.onBaseTypeChanged.bind(this);
        this.handleOptionClick = this.handleOptionClick.bind(this);
        this.renderFormElements = this.renderFormElements.bind(this);
        this.renderBaseTypeSpecificInputs = this.renderBaseTypeSpecificInputs.bind(this);
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

    onTypeNameChanged(value) {
        this.setState({
            typeName: value
        });
    }

    onBaseTypeChanged(value) {
        this.setState({
            baseType: value
        });
    }

    renderFormElements() {
        return (
            <div>
                <app.InputField id="type-name-input-field" type="text" label="Type Name" name="type-name" value={this.state.typeName} onChange={this.onTypeNameChanged}/>
                <app.InputField id="base-type-input-field" type="select" label="Base Type" name="base-type" value={this.state.baseType} options={this.props.baseTypes} onChange={this.onBaseTypeChanged}/>
                {this.renderBaseTypeSpecificInputs()}
            </div>
        )
    }

    renderBaseTypeSpecificInputs() {
        const reactComponents = [];

        switch (this.state.baseType) {
            case 'TBitField': {
                reactComponents.push(<app.InputField type="text" label="Length" name="bitfield-length"/>)
            } // fall through
            case 'TBool': {
                reactComponents.push(<app.InputField type="text" label="Bit Position" name="bit-position"/>);
                reactComponents.push(<app.InputField type="text" label="True Description" name="true-description"/>);
                reactComponents.push(<app.InputField type="text" label="False Description" name="false-description"/>);
            } break;
            case 'TEnum': {
                reactComponents.push(<app.InputField type="text" label="Enum Value Name" name="enum-value-name" />);
                reactComponents.push(<app.InputField type="text" label="Enum Value Code" name="enum-value-code" />);
            } break;
            case 'TNumber': {
                reactComponents.push(<app.InputField type="select" label="Basis Data Type" name="basis-data-type" />);
                reactComponents.push(<app.InputField type="text" label="Exponent" name="exponent" />);
                reactComponents.push(<app.InputField type="text" label="Range Min" name="range-min" />);
                reactComponents.push(<app.InputField type="text" label="Range Max" name="range-max" />);
                reactComponents.push(<app.InputField type="text" label="Step" name="step" />);
                reactComponents.push(<app.InputField type="select" label="Unit" name="unit" options={this.props.units} />);
            } break;
            case 'TString': {
                reactComponents.push(<app.InputField type="text" label="Max Size" name="string-max-size" />);
            } break;
            case 'TArray': {
                reactComponents.push(<app.InputField type="text" label="Array Name" name="array-name"/>);
                reactComponents.push(<app.InputField type="textarea" label="Array Description" name="array-description"/>);
                reactComponents.push(<app.InputField type="select" label="Array Element Type" name="array-element-type" options={this.props.types} />);
            } break;
            case 'TRecord': {
                reactComponents.push(<app.InputField type="text" label="Record Name" name="record-name"/>);
                reactComponents.push(<app.InputField type="textarea" label="Record Description" name="record-description" />);
                reactComponents.push(<app.InputField type="text" label="Record Field Name" name="record-field-name" />);
                reactComponents.push(<app.InputField type="text" label="Record Field Description" name="record-field-description" />);
                reactComponents.push(<app.InputField type="select" label="Record Field Type" name="record-field-type" options={this.props.types} />);
            } break;
            default: {
                if (this.state.baseType != null) {
                    console.error("Base type " + this.state.baseType + " is not implemented.");
                }
            }
        }

        return (
            <div id="extended-type-fields">
                {reactComponents}
            </div>
        );
    }

    render() {
        return (
            <div id="types-container">
                <div id="types-options-container" className="center">
                    <app.RoleToggle roleItems={this.options} handleClick={this.handleOptionClick} activeRole={this.state.selectedOption} />
                </div>
                {this.renderFormElements()}
                <div className="button" onClick={this.onSave}>Save</div>
            </div>
        );
    }
}

registerClassWithGlobalScope("TypesPage", TypesPage);

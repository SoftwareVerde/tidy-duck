class TypesPage extends React.Component {
    constructor(props) {
        super(props);

        this.options = ["Create Type", "Edit Type"];

        let baseType = null;
        if (this.props.primitiveTypes && this.props.primitiveTypes.length > 0) {
            baseType = this.props.primitiveTypes[0].getName();
        }

        this.state = {
            selectedOption: this.options[0],
            baseType:       baseType
        };

        this.onTypeNameChanged = this.onTypeNameChanged.bind(this);
        this.onBaseTypeChanged = this.onBaseTypeChanged.bind(this);
        this.onTypeSelected = this.onTypeSelected.bind(this);
        this.onStringMaxSizeChanged = this.onStringMaxSizeChanged.bind(this);
        this.onNumberBaseTypeChanged = this.onNumberBaseTypeChanged.bind(this);
        this.onNumberExponentChanged = this.onNumberExponentChanged.bind(this);
        this.onNumberRangeMinChanged = this.onNumberRangeMinChanged.bind(this);
        this.onNumberRangeMaxChanged = this.onNumberRangeMaxChanged.bind(this);
        this.onNumberStepChanged = this.onNumberStepChanged.bind(this);
        this.onNumberUnitChanged = this.onNumberUnitChanged.bind(this);
        this.onClassifiedStreamMaxLengthChanged = this.onClassifiedStreamMaxLengthChanged.bind(this);
        this.onClassifiedStreamMediaTypeChanged = this.onClassifiedStreamMediaTypeChanged.bind(this);
        this.onShortStreamMaxLengthChanged = this.onShortStreamMaxLengthChanged.bind(this);
        this.onArrayNameChanged = this.onArrayNameChanged.bind(this);
        this.onArrayDescriptionChanged = this.onArrayDescriptionChanged.bind(this);
        this.onArrayElementTypeChanged = this.onArrayElementTypeChanged.bind(this);

        this.getBaseTypes = this.getBaseTypes.bind(this);
        this.getPrimaryTypes = this.getPrimaryTypes.bind(this);
        this.getNumberBaseTypes = this.getNumberBaseTypes.bind(this);
        this.getStreamParamTypes = this.getStreamParamTypes.bind(this);
        this.getArrayTypes = this.getArrayTypes.bind(this);
        this.getRecordTypes = this.getRecordTypes.bind(this);
        this.getUnits = this.getUnits.bind(this);``

        this.handleOptionClick = this.handleOptionClick.bind(this);
        this.renderFormElements = this.renderFormElements.bind(this);
        this.renderBaseTypeSpecificInputs = this.renderBaseTypeSpecificInputs.bind(this);
        this.onSave = this.onSave.bind(this);
    }

    componentWillReceiveProps(newProps) {
        let baseType = null;
        if (this.props.primitiveTypes && this.props.primitiveTypes.length > 0) {
            baseType = this.props.primitiveTypes[0].getName();
        }
        this.state = {
            selectedOption: this.options[0],
            baseType:       baseType
        };
    }

    handleOptionClick(option) {
        this.setState({
            selectedOption: option
        });
    }

    getBaseTypes() {
        const baseTypes = [];

        for (let i in this.props.primitiveTypes) {
            let type = this.props.primitiveTypes[i];
            if (type.isBaseType()) {
                baseTypes.push(type.getName());
            }
        }

        return baseTypes;
    }

    getPrimaryTypes() {
        const primaryTypes = [];

        for (let i in this.props.mostTypes) {
            let type = this.props.mostTypes[i];
            if (type.isPrimaryType()) {
                primaryTypes.push(type.getName());
            }
        }

        return primaryTypes;
    }

    getNumberBaseTypes() {
        const numberBaseTypes = [];

        for (let i in this.props.primitiveTypes) {
            let type = this.props.primitiveTypes[i];
            if (type.isNumberBaseType()) {
                numberBaseTypes.push(type.getName());
            }
        }

        return numberBaseTypes;
    }

    getStreamParamTypes() {
        const streamParamTypes = [];

        for (let i in this.props.mostTypes) {
            let type = this.props.mostTypes[i];
            if (type.getPrimitiveType().isStreamParamType()) {
                streamParamTypes.push(type.getName());
            }
        }

        return streamParamTypes;
    }

    getArrayTypes() {
        const arrayTypes = [];

        for (let i in this.props.mostTypes) {
            let type = this.props.mostTypes[i];
            if (type.getPrimitiveType().isArrayType()) {
                arrayTypes.push(type.getName());
            }
        }

        return arrayTypes;
    }

    getRecordTypes() {
        const recordTypes = [];

        for (let i in this.props.mostTypes) {
            let type = this.props.mostTypes[i];
            if (type.getPrimitiveType().isRecordType()) {
                recordTypes.push(type.getName());
            }
        }

        return recordTypes;
    }

    getUnits() {
        const units = [];

        for (let i in this.props.mostUnits) {
            let unit = this.props.mostUnits[i];
            units.push(unit.getDefinitionName());
        }

        return units;
    }

    onSave() {
        // TODO: save type
        console.log("Type save button clicked.");
    }

    onStringMaxSizeChanged(value) {
        this.setState({
            stringMaxSize: value
        });
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

    onTypeSelected(value) {
        this.setState({
            selectedType: value
        });
    }

    onNumberBaseTypeChanged(value) {
        this.setState({
            numberBaseType: value
        });
    }

    onNumberExponentChanged(value) {
        this.setState({
            numberExponent: value
        });
    }

    onNumberRangeMinChanged(value) {
        this.setState({
            numberRangeMin: value
        });
    }

    onNumberRangeMaxChanged(value) {
        this.setState({
            numberRangeMax: value
        });
    }

    onNumberStepChanged(value) {
        this.setState({
            numberStep: value
        });
    }

    onNumberUnitChanged(value) {
        this.setState({
            numberUnit: value
        });
    }

    onClassifiedStreamMaxLengthChanged(value) {
        this.setState({
            classifiedStreamMaxLength: value
        });
    }

    onClassifiedStreamMediaTypeChanged(value) {
        this.setState({
            classifiedStreamMediaType: value
        });
    }

    onShortStreamMaxLengthChanged(value) {
        this.setState({
            shortStreamMaxLength: value
        });
    }

    onArrayNameChanged(value) {
        this.setState({
            arrayName: value
        });
    }

    onArrayDescriptionChanged(value) {
        this.setState({
            arrayDescription: value
        });
    }

    onArrayElementTypeChanged(value) {
        this.setState({
            arrayElementType: value
        });
    }

    renderFormElements() {
        let typeSelector = "";
        if (this.state.selectedOption == "Edit Type") {
            const primaryTypes = this.getPrimaryTypes();
            let selectedType = this.state.selectedType;
            if (!selectedType) {
                selectedType = primaryTypes[0];
            }
            typeSelector = <app.InputField key="type-selector" type="select" label="Type to Edit" name="type-selector" value={selectedType} options={primaryTypes} onChange={this.onTypeSelected} />
        }

        return (
            <div>
                <div id="types-main-inputs">
                    {typeSelector}
                    <app.InputField key="type-name" type="text" label="Type Name" name="type-name" value={this.state.typeName} onChange={this.onTypeNameChanged}/>
                    <app.InputField key="base-type" type="select" label="Base Type" name="base-type" value={this.state.baseType} options={this.getBaseTypes()} onChange={this.onBaseTypeChanged}/>
                </div>
                {this.renderBaseTypeSpecificInputs()}
            </div>
        );
    }

    renderBaseTypeSpecificInputs() {
        const reactComponents = [];

        switch (this.state.baseType) {
            case 'TBitField': {
                reactComponents.push(<app.InputField key="bitfield1" type="text" label="Length" name="bitfield-length"/>)
            } // fall through
            case 'TBool': {
                reactComponents.push(<app.InputField key="bool1" type="text" label="Bit Position" name="bit-position"/>);
                reactComponents.push(<app.InputField key="bool2" type="text" label="True Description" name="true-description"/>);
                reactComponents.push(<app.InputField key="bool3" type="text" label="False Description" name="false-description"/>);
            } break;
            case 'TEnum': {
                reactComponents.push(<app.InputField key="enum1" type="text" label="Enum Value Name" name="enum-value-name" />);
                reactComponents.push(<app.InputField key="enum2" type="text" label="Enum Value Code" name="enum-value-code" />);
            } break;
            case 'TNumber': {
                reactComponents.push(<app.InputField key="number1" type="select" label="Basis Data Type" name="basis-data-type" value={this.state.numberBaseType} options={this.getNumberBaseTypes()} onChange={this.onNumberBaseTypeChanged} />);
                reactComponents.push(<app.InputField key="number2" type="text" label="Exponent" name="exponent" value={this.state.numberExponent} onChange={this.onNumberExponentChanged} />);
                reactComponents.push(<app.InputField key="number3" type="text" label="Range Min" name="range-min" value={this.state.numberRangeMin} onChange={this.onNumberRangeMinChanged} />);
                reactComponents.push(<app.InputField key="number4" type="text" label="Range Max" name="range-max" value={this.state.numberRangeMax} onChange={this.onNumberRangeMaxChanged} />);
                reactComponents.push(<app.InputField key="number5" type="text" label="Step" name="step" value={this.state.numberStep} onChange={this.onNumberStepChanged} />);
                reactComponents.push(<app.InputField key="number6" type="select" label="Unit" name="unit" value={this.state.numberUnit} options={this.getUnits()} onChange={this.onNumberUnitChanged} />);
            } break;
            case 'TString': {
                reactComponents.push(<app.InputField key="string1" type="text" label="Max Size" name="string-max-size" value={this.state.stringMaxSize} onChange={this.onStringMaxSizeChanged} />);
            } break;
            case 'TStream': {
                // TODO: implement
            } break;
            case 'TCStream': {
                reactComponents.push(<app.InputField key="cstream1" type="text" label="Max Length" name="cstream-max-length" value={this.state.classifiedStreamMaxLength} onChange={this.onClassifiedStreamMaxLengthChanged} />);
                reactComponents.push(<app.InputField key="cstream2" type="text" label="Media Type" name="cstream-media-type" value={this.state.classifiedStreamMediaType} onChange={this.onClassifiedStreamMediaTypeChanged} />);
            } break;
            case 'TShortStream': {
                reactComponents.push(<app.InputField key="shortstream1" type="text" label="Max Length" name="short-stream-max-length" value={this.state.shortStreamMaxLength} onChange={this.onShortStreamMaxLengthChanged} />);
            } break;
            case 'TArray': {
                reactComponents.push(<app.InputField key="array1" type="text" label="Array Name" name="array-name" value={this.state.arrayName} onChange={this.onArrayNameChanged} />);
                reactComponents.push(<app.InputField key="array2" type="textarea" label="Array Description" name="array-description" value={this.state.arrayDescription} onChange={this.onArrayDescriptionChanged} />);
                reactComponents.push(<app.InputField key="array3" type="select" label="Array Element Type" name="array-element-type" options={this.getArrayTypes()} value={this.state.arrayElementType} onChange={this.onArrayElementTypeChanged} />);
            } break;
            case 'TRecord': {
                reactComponents.push(<app.InputField key="record1" type="text" label="Record Name" name="record-name"/>);
                reactComponents.push(<app.InputField key="record2" type="textarea" label="Record Description" name="record-description" />);
                reactComponents.push(<app.InputField key="record3" type="text" label="Record Field Name" name="record-field-name" />);
                reactComponents.push(<app.InputField key="record4" type="text" label="Record Field Description" name="record-field-description" />);
                reactComponents.push(<app.InputField key="record5" type="select" label="Record Field Type" name="record-field-type" options={this.getRecordTypes()} />);
            } break;
            default: {
                if (this.state.baseType != null) {
                    console.error("Base type " + this.state.baseType + " is not implemented.");
                }
            }
        }

        return (
            <div key="extended-type-fields" id="extended-type-fields" className="clearfix">
                {reactComponents}
            </div>
        );
    }

    render() {
        return (
            <div id="types-container">
                <div key="types-options-container" id="types-options-container" className="center">
                    <app.RoleToggle roleItems={this.options} handleClick={this.handleOptionClick} activeRole={this.state.selectedOption} />
                </div>
                {this.renderFormElements()}
                <div key="save-button" className="button" onClick={this.onSave}>Save</div>
            </div>
        );
    }
}

registerClassWithGlobalScope("TypesPage", TypesPage);

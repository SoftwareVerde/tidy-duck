class TypesPage extends React.Component {
    constructor(props) {
        super(props);

        this.options = ["Create Type", "Edit Type"];

        let mostType = TypesPage.createNewMostType(this.props.primitiveTypes);

        this.state = {
            selectedOption: this.options[0],
            mostType:       mostType,
            saveButtonText: 'Save'
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
        this.onArraySizeChanged = this.onArraySizeChanged.bind(this);

        this.getMostTypeByName = this.getMostTypeByName.bind(this);
        this.getPrimitiveTypeByName = this.getPrimitiveTypeByName.bind(this);
        this.getMostUnitByName = this.getMostUnitByName.bind(this);
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
        let mostType = TypesPage.createNewMostType(newProps.primitiveTypes);

        this.state = {
            selectedOption: this.options[0],
            mostType:       mostType,
            saveButtonText: 'Save'
        };
    }

    static createNewMostType(primitiveTypes) {
        const mostType = new MostType();
        // for now, always create a primary type
        mostType.setIsPrimaryType(true);
        return mostType;
    }

    handleOptionClick(option) {
        const mostType = TypesPage.createNewMostType(this.props.primitiveTypes);
        this.setState({
            selectedOption: option,
            selectedType:   null,
            mostType:       mostType
        });
    }

    getMostTypeByName(name) {
        for (let i in this.props.mostTypes) {
            const mostType = this.props.mostTypes[i];
            if (mostType.getName() == name) {
                return mostType;
            }
        }
    }

    getPrimitiveTypeByName(name) {
        for (let i in this.props.primitiveTypes) {
            const primitiveType = this.props.primitiveTypes[i];
            if (primitiveType.getName() == name) {
                return primitiveType;
            }
        }
    }

    getMostUnitByName(name) {
        for (let i in this.props.mostUnits) {
            const mostUnit = this.props.mostUnits[i];
            if (mostUnit.getDefinitionName() == name) {
                return mostUnit;
            }
        }
    }

    getBaseTypes() {
        const baseTypes = [];

        for (let i in this.props.primitiveTypes) {
            let type = this.props.primitiveTypes[i];
            if (type.isBaseType()) {
                baseTypes.push(type.getName());
            }
        }

        return baseTypes.sort();
    }

    getPrimaryTypes() {
        const primaryTypes = [];

        for (let i in this.props.mostTypes) {
            let type = this.props.mostTypes[i];
            if (type.isPrimaryType()) {
                primaryTypes.push(type.getName());
            }
        }

        return primaryTypes.sort();
    }

    getNumberBaseTypes() {
        const numberBaseTypes = [];

        for (let i in this.props.primitiveTypes) {
            let type = this.props.primitiveTypes[i];
            if (type.isNumberBaseType()) {
                numberBaseTypes.push(type.getName());
            }
        }

        return numberBaseTypes.sort();
    }

    getStreamParamTypes() {
        const streamParamTypes = [];

        for (let i in this.props.mostTypes) {
            let type = this.props.mostTypes[i];
            if (type.getPrimitiveType().isStreamParamType()) {
                streamParamTypes.push(type.getName());
            }
        }

        return streamParamTypes.sort();
    }

    getArrayTypes() {
        const arrayTypes = [];

        for (let i in this.props.mostTypes) {
            let type = this.props.mostTypes[i];
            if (type.getPrimitiveType().isArrayType()) {
                arrayTypes.push(type.getName());
            }
        }

        return arrayTypes.sort();
    }

    getRecordTypes() {
        const recordTypes = [];

        for (let i in this.props.mostTypes) {
            let type = this.props.mostTypes[i];
            if (type.getPrimitiveType().isRecordType()) {
                recordTypes.push(type.getName());
            }
        }

        return recordTypes.sort();
    }

    getUnits() {
        const units = [];

        for (let i in this.props.mostUnits) {
            let unit = this.props.mostUnits[i];
            units.push(unit.getDefinitionName());
        }

        return units.sort();
    }

    onSave() {
        const mostTypeJson = MostType.toJson(this.state.mostType);
        const thisApp = this;
        this.setState({
            saveButtonText: <i className="fa fa-refresh fa-spin"></i>
        });
        insertMostType(mostTypeJson, function(data) {
            if (data.wasSuccess) {
                if (typeof thisApp.props.onTypeCreated == "function") {
                    thisApp.props.onTypeCreated(thisApp.state.mostType);
                }
            } else {
                alert("Unable to create type: " + data.errorMessage);
            }
            // reset fields
            thisApp.setState({
                mostType: TypesPage.createNewMostType(thisApp.props.primitiveTypes),
                saveButtonText: 'Saved'
            })
        });
    }

    onTypeSelected(value) {
        const newMostType = this.getMostTypeByName(value);

        this.setState({
            mostType: newMostType,
            selectedType: value,
            saveButtonText: 'Save'
        })
    }

    onTypeNameChanged(value) {
        const mostType = this.state.mostType;

        mostType.setName(value);

        this.setState({
            mostType: mostType,
            saveButtonText: 'Save'
        });
    }

    onBaseTypeChanged(value) {
        const mostType = this.state.mostType;

        const newPrimitiveType = this.getPrimitiveTypeByName(value);
        mostType.setPrimitiveType(newPrimitiveType);

        this.setState({
            mostType: mostType
        });
    }

    onNumberBaseTypeChanged(value) {
        const mostType = this.state.mostType;

        const newNumberBaseType = this.getPrimitiveTypeByName(value);
        mostType.setNumberBaseType(newNumberBaseType);

        this.setState({
            mostType: mostType
        });
    }

    onNumberExponentChanged(value) {
        const mostType = this.state.mostType;

        mostType.setNumberExponent(value);

        this.setState({
            mostType: mostType
        });
    }

    onNumberRangeMinChanged(value) {
        const mostType = this.state.mostType;

        mostType.setNumberRangeMin(value);

        this.setState({
            mostType: mostType
        });
    }

    onNumberRangeMaxChanged(value) {
        const mostType = this.state.mostType;

        mostType.setNumberRangeMax(value);

        this.setState({
            mostType: mostType
        });
    }

    onNumberStepChanged(value) {
        const mostType = this.state.mostType;

        mostType.setNumberStep(value);

        this.setState({
            mostType: mostType
        });
    }

    onNumberUnitChanged(value) {
        const mostType = this.state.mostType;

        const newMostUnit = this.getMostUnitByName(value);
        mostType.setNumberUnit(newMostUnit);

        this.setState({
            mostType: mostType
        });
    }

    onStringMaxSizeChanged(value) {
        const mostType = this.state.mostType;

        mostType.setStringMaxSize(value);

        this.setState({
            mostType: mostType
        });
    }

    onClassifiedStreamMaxLengthChanged(value) {
        const mostType = this.state.mostType;

        mostType.setStreamMaxLength(value);

        this.setState({
            mostType: mostType
        });
    }

    onClassifiedStreamMediaTypeChanged(value) {
        const mostType = this.state.mostType;

        mostType.setStreamMediaType(value);

        this.setState({
            mostType: mostType
        });
    }

    onShortStreamMaxLengthChanged(value) {
        const mostType = this.state.mostType;

        mostType.setStreamMaxLength(value);

        this.setState({
            mostType: mostType
        });
    }

    onArrayNameChanged(value) {
        const mostType = this.state.mostType;

        mostType.setArrayName(value);

        this.setState({
            mostType: mostType
        });
    }

    onArrayDescriptionChanged(value) {
        const mostType = this.state.mostType;

        mostType.setArrayDescription(value);

        this.setState({
            mostType: mostType
        });
    }

    onArrayElementTypeChanged(value) {
        const mostType = this.state.mostType;

        const newArrayElementType = this.getMostTypeByName(value);
        mostType.setArrayElementType(newArrayElementType);

        this.setState({
            mostType: mostType
        });
    }

    onArraySizeChanged(value) {
        const mostType = this.state.mostType;

        mostType.setArraySize(value);

        this.setState({
            mostType: mostType
        });
    }

    renderFormElements() {
        if (this.props.mostTypes.length == 0 || this.props.primitiveTypes.length == 0 || this.props.mostUnits.length == 0) {
            // types props must not have been populated yet, show loading icon
            return (
                <div className="center">
                    <i className="fa fa-spin fa-3x fa-refresh"></i>
                </div>
            );
        }

        let typeSelector = "";
        if (this.state.selectedOption == "Edit Type") {
            // add empty option in selector
            const primaryTypes = [''].concat(this.getPrimaryTypes());
            let selectedType = this.state.selectedType;
            if (!selectedType) {
                selectedType = primaryTypes[0];
            }
            typeSelector = <app.InputField key="type-selector" type="select" label="Type to Edit" name="type-selector" value={selectedType} options={primaryTypes} onChange={this.onTypeSelected} />
            // if no type is selected, only render that
            if (selectedType == '') {
                return (
                    <div id="types-main-inputs">
                        {typeSelector}
                    </div>
                );
            }
        }

        const mostType = this.state.mostType;

        const baseTypes = this.getBaseTypes();
        if (mostType.getPrimitiveType() == null && baseTypes.length > 0) {
            mostType.setPrimitiveType(this.getPrimitiveTypeByName(baseTypes[0]));
        }

        const typeName = mostType.getName();
        const baseTypeName = mostType.getPrimitiveType().getName();

        return (
            <div>
                <div id="types-main-inputs">
                    {typeSelector}
                    <app.InputField key="type-name" type="text" label="Type Name" name="type-name" value={typeName} onChange={this.onTypeNameChanged}/>
                    <app.InputField key="base-type" type="select" label="Base Type" name="base-type" value={baseTypeName} options={baseTypes} onChange={this.onBaseTypeChanged}/>
                </div>
                {this.renderBaseTypeSpecificInputs()}
                <div key="save-button" className="button" onClick={this.onSave}>{this.state.saveButtonText}</div>
            </div>
        );
    }

    renderBaseTypeSpecificInputs() {
        const reactComponents = [];
        const mostType = this.state.mostType;

        if (!mostType.getPrimitiveType()) {
            return;
        }

        switch (mostType.getPrimitiveType().getName()) {
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
                const numberBaseTypes = this.getNumberBaseTypes();
                if (mostType.getNumberBaseType() == null) {
                    mostType.setNumberBaseType(this.getPrimitiveTypeByName(numberBaseTypes[0]));
                }
                const units = this.getUnits();
                if (mostType.getNumberUnit() == null) {
                    mostType.setNumberUnit(this.getMostUnitByName(units[0]));
                }
                const numberBaseTypeName = mostType.getNumberBaseType().getName();
                const numberExponent = mostType.getNumberExponent();
                const numberRangeMin = mostType.getNumberRangeMin();
                const numberRangeMax = mostType.getNumberRangeMax();
                const numberStep = mostType.getNumberStep();
                const numberUnitName = mostType.getNumberUnit().getDefinitionName();
                reactComponents.push(<app.InputField key="number1" type="select" label="Basis Data Type" name="basis-data-type" value={numberBaseTypeName} options={numberBaseTypes} onChange={this.onNumberBaseTypeChanged} />);
                reactComponents.push(<app.InputField key="number2" type="text" label="Exponent" name="exponent" value={numberExponent} onChange={this.onNumberExponentChanged} />);
                reactComponents.push(<app.InputField key="number3" type="text" label="Range Min" name="range-min" value={numberRangeMin} onChange={this.onNumberRangeMinChanged} />);
                reactComponents.push(<app.InputField key="number4" type="text" label="Range Max" name="range-max" value={numberRangeMax} onChange={this.onNumberRangeMaxChanged} />);
                reactComponents.push(<app.InputField key="number5" type="text" label="Step" name="step" value={numberStep} onChange={this.onNumberStepChanged} />);
                reactComponents.push(<app.InputField key="number6" type="select" label="Unit" name="unit" value={numberUnitName} options={units} onChange={this.onNumberUnitChanged} />);
            } break;
            case 'TString': {
                const stringMaxSize = mostType.getStringMaxSize();
                reactComponents.push(<app.InputField key="string1" type="text" label="Max Size" name="string-max-size" value={stringMaxSize} onChange={this.onStringMaxSizeChanged} />);
            } break;
            case 'TStream': {
                // TODO: implement
            } break;
            case 'TCStream': {
                const streamMaxLength = mostType.getStreamMaxLength();
                const streamMediaType = mostType.getStreamMediaType();
                reactComponents.push(<app.InputField key="cstream1" type="text" label="Max Length" name="cstream-max-length" value={streamMaxLength} onChange={this.onClassifiedStreamMaxLengthChanged} />);
                reactComponents.push(<app.InputField key="cstream2" type="text" label="Media Type" name="cstream-media-type" value={streamMediaType} onChange={this.onClassifiedStreamMediaTypeChanged} />);
            } break;
            case 'TShortStream': {
                const streamMaxLength = mostType.getStreamMaxLength();
                reactComponents.push(<app.InputField key="shortstream1" type="text" label="Max Length" name="short-stream-max-length" value={streamMaxLength} onChange={this.onShortStreamMaxLengthChanged} />);
            } break;
            case 'TArray': {
                const arrayElementTypes = this.getArrayTypes();
                if (mostType.getArrayElementType() == null) {
                    mostType.setArrayElementType(this.getMostTypeByName(arrayElementTypes[0]));
                }
                const arrayName = mostType.getArrayName();
                const arrayDescription = mostType.getArrayDescription();
                const arrayElementTypeName = mostType.getArrayElementType().getName();
                const arraySize = mostType.getArraySize();
                reactComponents.push(<app.InputField key="array1" type="text" label="Array Name" name="array-name" value={arrayName} onChange={this.onArrayNameChanged} />);
                reactComponents.push(<app.InputField key="array2" type="textarea" label="Array Description" name="array-description" value={arrayDescription} onChange={this.onArrayDescriptionChanged} />);
                reactComponents.push(<app.InputField key="array3" type="select" label="Array Element Type" name="array-element-type" value={arrayElementTypeName} options={arrayElementTypes} onChange={this.onArrayElementTypeChanged} />);
                reactComponents.push(<app.InputField key="array4" type="text" label="Array Size" name="array-size" value={arraySize} onChange={this.onArraySizeChanged} />);
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
            </div>
        );
    }
}

registerClassWithGlobalScope("TypesPage", TypesPage);

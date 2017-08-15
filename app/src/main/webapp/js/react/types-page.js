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
        this.onBitFieldLengthChanged = this.onBitFieldLengthChanged.bind(this);
        this.onBoolFieldAddButtonClicked = this.onBoolFieldAddButtonClicked.bind(this);
        this.onBoolFieldRemoveButtonClicked = this.onBoolFieldRemoveButtonClicked.bind(this);
        this.onBooleanFieldBitPositionChanged = this.onBooleanFieldBitPositionChanged.bind(this);
        this.onBooleanFieldTrueDescriptionChanged = this.onBooleanFieldTrueDescriptionChanged.bind(this);
        this.onBooleanFieldFalseDescriptionChanged = this.onBooleanFieldFalseDescriptionChanged.bind(this);
        this.onEnumValueAddButtonClicked = this.onEnumValueAddButtonClicked.bind(this);
        this.onEnumValueRemoveButtonClicked = this.onEnumValueRemoveButtonClicked.bind(this);
        this.onEnumValueNameChanged = this.onEnumValueNameChanged.bind(this);
        this.onEnumValueCodeChanged = this.onEnumValueCodeChanged.bind(this);
        this.onStringMaxSizeChanged = this.onStringMaxSizeChanged.bind(this);
        this.onNumberBaseTypeChanged = this.onNumberBaseTypeChanged.bind(this);
        this.onNumberExponentChanged = this.onNumberExponentChanged.bind(this);
        this.onNumberRangeMinChanged = this.onNumberRangeMinChanged.bind(this);
        this.onNumberRangeMaxChanged = this.onNumberRangeMaxChanged.bind(this);
        this.onNumberStepChanged = this.onNumberStepChanged.bind(this);
        this.onNumberUnitChanged = this.onNumberUnitChanged.bind(this);
        this.onStreamCaseAddButtonClicked = this.onStreamCaseAddButtonClicked.bind(this);
        this.onStreamCaseRemoveButtonClicked = this.onStreamCaseRemoveButtonClicked.bind(this);
        this.onStreamCaseSignalAddButtonClicked = this.onStreamCaseSignalAddButtonClicked.bind(this);
        this.onStreamCaseParameterAddButtonClicked = this.onStreamCaseParameterAddButtonClicked.bind(this);
        this.onStreamCasePositionXChanged = this.onStreamCasePositionXChanged.bind(this);
        this.onStreamCasePositionYChanged = this.onStreamCasePositionYChanged.bind(this);
        this.onStreamCaseParameterNameChanged = this.onStreamCaseParameterNameChanged.bind(this);
        this.onStreamCaseParameterDescriptionChanged = this.onStreamCaseParameterDescriptionChanged.bind(this);
        this.onStreamCaseParameterTypeChanged = this.onStreamCaseParameterTypeChanged.bind(this);
        this.onStreamCaseParameterRemoveButtonClicked = this.onStreamCaseParameterRemoveButtonClicked.bind(this);
        this.onStreamSignalNameChanged = this.onStreamSignalNameChanged.bind(this);
        this.onStreamSignalDescriptionChanged = this.onStreamSignalDescriptionChanged.bind(this);
        this.onStreamSignalBitLengthChanged = this.onStreamSignalBitLengthChanged.bind(this);
        this.onStreamCaseSignalRemoveButtonClicked = this.onStreamCaseSignalRemoveButtonClicked.bind(this);
        this.onClassifiedStreamMaxLengthChanged = this.onClassifiedStreamMaxLengthChanged.bind(this);
        this.onClassifiedStreamMediaTypeChanged = this.onClassifiedStreamMediaTypeChanged.bind(this);
        this.onShortStreamMaxLengthChanged = this.onShortStreamMaxLengthChanged.bind(this);
        this.onArrayNameChanged = this.onArrayNameChanged.bind(this);
        this.onArrayDescriptionChanged = this.onArrayDescriptionChanged.bind(this);
        this.onArrayElementTypeChanged = this.onArrayElementTypeChanged.bind(this);
        this.onArraySizeChanged = this.onArraySizeChanged.bind(this);
        this.onRecordFieldAddButtonClicked = this.onRecordFieldAddButtonClicked.bind(this);
        this.onRecordFieldRemoveButtonClicked = this.onRecordFieldRemoveButtonClicked.bind(this);
        this.onRecordNameChanged = this.onRecordNameChanged.bind(this);
        this.onRecordDescriptionChanged = this.onRecordDescriptionChanged.bind(this);
        this.onRecordSizeChanged = this.onRecordSizeChanged.bind(this);
        this.onRecordFieldNameChanged = this.onRecordFieldNameChanged.bind(this);
        this.onRecordFieldDescriptionChanged = this.onRecordFieldDescriptionChanged.bind(this);
        this.onRecordFieldTypeChanged = this.onRecordFieldTypeChanged.bind(this);

        this.getMostTypeByName = this.getMostTypeByName.bind(this);
        this.getPrimitiveTypeByName = this.getPrimitiveTypeByName.bind(this);
        this.getMostUnitByName = this.getMostUnitByName.bind(this);
        this.getBaseTypes = this.getBaseTypes.bind(this);
        this.getPrimaryTypes = this.getPrimaryTypes.bind(this);
        this.getNumberBaseTypes = this.getNumberBaseTypes.bind(this);
        this.getStreamParamTypes = this.getStreamParamTypes.bind(this);
        this.getArrayTypes = this.getArrayTypes.bind(this);
        this.getRecordTypes = this.getRecordTypes.bind(this);
        this.getUnits = this.getUnits.bind(this);

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
        // not sorted, should be displayed in the provided order
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
        // not sorted, should be displayed in the provided order
        return units;
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
        const oldMostType = this.state.mostType;

        const mostType = TypesPage.createNewMostType(this.props.primitiveTypes);

        mostType.setName(oldMostType.getName());

        const newPrimitiveType = this.getPrimitiveTypeByName(value);
        mostType.setPrimitiveType(newPrimitiveType);

        this.setState({
            mostType: mostType
        });
    }

    onBitFieldLengthChanged(value) {
        const mostType = this.state.mostType;

        mostType.setBitFieldLength(value);

        this.setState({
            mostType: mostType
        });
    }

    onBoolFieldAddButtonClicked() {
        const mostType = this.state.mostType;
        const booleanField = new BooleanField();

        mostType.addBooleanField(booleanField);
        booleanField.setFieldIndex(mostType.getBooleanFields().length);

        this.setState({
            mostType: mostType
        });
    }

    onBoolFieldRemoveButtonClicked(booleanField) {
        const mostType = this.state.mostType;
        const booleanFields = mostType.getBooleanFields();
        const newBooleanFields = [];
        const booleanFieldIndex = booleanField.getFieldIndex();

        let indexCounter = 1;
        for (let i in booleanFields) {
            const existingBooleanField = booleanFields[i];
            if (existingBooleanField.getFieldIndex() !== booleanFieldIndex) {
                existingBooleanField.setFieldIndex(indexCounter);
                newBooleanFields.push(existingBooleanField);
                indexCounter++;
            }
        }

        mostType.setBooleanFields(newBooleanFields);
        this.setState({
            mostType: mostType
        });
    }

    onBooleanFieldBitPositionChanged(booleanField, bitPosition) {
        booleanField.setBitPosition(bitPosition);
    }

    onBooleanFieldTrueDescriptionChanged(booleanField, trueDescription) {
        booleanField.setTrueDescription(trueDescription);
    }

    onBooleanFieldFalseDescriptionChanged(booleanField, falseDescription) {
        booleanField.setFalseDescription(falseDescription);
    }

    onEnumValueAddButtonClicked() {
        const mostType = this.state.mostType;
        const enumValue = new EnumValue();

        mostType.addEnumValue(enumValue);
        enumValue.setValueIndex(mostType.getEnumValues().length);

        this.setState({
            mostType: mostType
        });
    }

    onEnumValueRemoveButtonClicked(enumValue) {
        const mostType = this.state.mostType;
        const enumValues = mostType.getEnumValues();
        const newEnumValues = [];
        const enumValueIndex = enumValue.getValueIndex();

        let indexCounter = 1;
        for (let i in enumValues) {
            const existingEnumValue = enumValues[i];
            if (existingEnumValue.getValueIndex() !== enumValueIndex) {
                existingEnumValue.setValueIndex(indexCounter);
                newEnumValues.push(existingEnumValue);
                indexCounter++;
            }
        }

        mostType.setEnumValues(newEnumValues);
        this.setState({
            mostType: mostType
        });
    }

    onEnumValueNameChanged(enumValue, name) {
        enumValue.setName(name);
    }

    onEnumValueCodeChanged(enumValue, code) {
        enumValue.setCode(code);
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

    onStreamCaseAddButtonClicked() {
        const mostType = this.state.mostType;
        const streamCase = new StreamCase();

        mostType.addStreamCase(streamCase);
        streamCase.setCaseIndex(mostType.getStreamCases().length);

        this.setState({
            mostType: mostType
        });
    }

    onStreamCaseRemoveButtonClicked(streamCase) {
        const mostType = this.state.mostType;
        const streamCases = mostType.getStreamCases();
        const newStreamCases = [];
        const streamCaseIndex = streamCase.getCaseIndex();

        let indexCounter = 1;
        for (let i in streamCases) {
            const existingStreamCase = streamCases[i];
            if (existingStreamCase.getCaseIndex() !== streamCaseIndex) {
                existingStreamCase.setCaseIndex(indexCounter);
                newStreamCases.push(existingStreamCase);
                indexCounter++;
            }
        }

        mostType.setStreamCases(newStreamCases);
        this.setState({
            mostType: mostType
        });
    }

    onStreamCaseParameterAddButtonClicked(streamCase) {
        const mostType = this.state.mostType;
        const streamCaseParameter = new StreamCaseParameter();
        streamCase.addStreamParameter(streamCaseParameter);
        streamCaseParameter.setParameterIndex(streamCase.getStreamParameters().length);

        this.setState({
            mostType: mostType
        });
    }

    onStreamCaseParameterRemoveButtonClicked(streamCase, streamCaseParameter) {
        const mostType = this.state.mostType;
        const streamCaseParameters = streamCase.getStreamParameters();
        const newStreamParameters = [];
        const streamParameterIndex = streamCaseParameter.getParameterIndex();

        let indexCounter = 1;
        for (let i in streamCaseParameters) {
            if (streamCaseParameters[i].getParameterIndex() !== streamParameterIndex) {
                streamCaseParameters[i].setParameterIndex(indexCounter);
                newStreamParameters.push(streamCaseParameters[i]);
                indexCounter++;
            }
        }

        streamCase.setStreamParameters(newStreamParameters);
        this.setState({
            mostType: mostType
        });
    }

    onStreamCaseSignalRemoveButtonClicked(streamCase, streamSignal) {
        const mostType = this.state.mostType;
        const streamCaseSignals = streamCase.getStreamSignals();
        const newStreamSignals = [];
        const streamSignalIndex = streamSignal.getSignalIndex();

        let indexCounter = 1;
        for (let i in streamCaseSignals) {
            if (streamCaseSignals[i].getSignalIndex() !== streamSignalIndex) {
                streamCaseSignals[i].setSignalIndex(indexCounter);
                newStreamSignals.push(streamCaseSignals[i]);
                indexCounter++;
            }
        }

        streamCase.setStreamSignals(newStreamSignals);
        this.setState({
            mostType: mostType
        });
    }

    onStreamCaseSignalAddButtonClicked(streamCase) {
        const mostType = this.state.mostType;
        const streamCaseSignal = new StreamCaseSignal();
        streamCase.addStreamSignal(streamCaseSignal);
        streamCaseSignal.setSignalIndex(streamCase.getStreamSignals().length);

        this.setState({
            mostType: mostType
        });
    }

    onStreamCasePositionXChanged(streamCase, positionX) {
        streamCase.setStreamPositionX(positionX);
    }

    onStreamCasePositionYChanged(streamCase, positionY) {
        streamCase.setStreamPositionY(positionY);
    }

    onStreamCaseParameterNameChanged(streamCaseParameter, name) {
        streamCaseParameter.setParameterName(name);
    }

    onStreamCaseParameterDescriptionChanged(streamCaseParameter, description) {
        streamCaseParameter.setParameterDescription(description);
    }

    onStreamCaseParameterTypeChanged(caseParameter, typeName) {
        const mostType = this.state.mostType;

        const newParameterType = this.getMostTypeByName(typeName);
        caseParameter.setParameterType(newParameterType);

        this.setState({
            mostType: mostType
        });
    }

    onStreamSignalNameChanged(streamSignal, name) {
        streamSignal.setSignalName(name);
    }

    onStreamSignalDescriptionChanged(streamSignal, description) {
        streamSignal.setSignalDescription(description);
    }

    onStreamSignalBitLengthChanged(streamSignal, bitLength) {
        streamSignal.setSignalBitLength(bitLength);
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

    onRecordFieldAddButtonClicked() {
        const mostType = this.state.mostType;
        const recordField = new RecordField();

        mostType.addRecordField(recordField);
        recordField.setFieldIndex(mostType.getRecordFields().length);

        this.setState({
            mostType: mostType
        });
    }

    onRecordFieldRemoveButtonClicked(recordField) {
        const mostType = this.state.mostType;
        const recordFields = mostType.getRecordFields();
        const newRecordFields = [];
        const recordFieldIndex = recordField.getFieldIndex();

        let indexCounter = 1;
        for (let i in recordFields) {
            const existingRecordField = recordFields[i];
            if (existingRecordField.getFieldIndex() !== recordFieldIndex) {
                existingRecordField.setFieldIndex(indexCounter);
                newRecordFields.push(existingRecordField);
                indexCounter++;
            }
        }

        mostType.setRecordFields(newRecordFields);
        this.setState({
            mostType: mostType
        });
    }

    onRecordNameChanged(value) {
        const mostType = this.state.mostType;

        mostType.setRecordName(value);

        this.setState({
            mostType: mostType
        });
    }

    onRecordDescriptionChanged(value) {
        const mostType = this.state.mostType;

        mostType.setRecordDescription(value);

        this.setState({
            mostType: mostType
        });
    }

    onRecordSizeChanged(value) {
        const mostType = this.state.mostType;

        mostType.setRecordSize(value);

        this.setState({
            mostType: mostType
        });
    }

    onRecordFieldNameChanged(recordField, name) {
        recordField.setFieldName(name);
    }

    onRecordFieldDescriptionChanged(recordField, description) {
        recordField.setFieldDescription(description);
    }

    onRecordFieldTypeChanged(recordField, typeName) {
        const mostType = this.state.mostType;

        const newRecordFieldType = this.getMostTypeByName(typeName);
        recordField.setFieldType(newRecordFieldType);

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
                reactComponents.push(<div key="bitfield-length" className="clearfix"><app.InputField key="bitfield1" type="text" label="Length" name="bitfield-length" value={mostType.getBitFieldLength()} onChange={this.onBitFieldLengthChanged} /></div>);
            } // fall through
            case 'TBool': {
                const thisPage = this;
                let i = 1;
                mostType.getBooleanFields().forEach(function (booleanField) {
                    const key = "boolfield" + i;
                    reactComponents.push(
                        <div key={key} className="repeating-field clearfix">
                            <div className="repeating-field-header clearfix">Field {booleanField.getFieldIndex()}
                                <i className="remove-button fa fa-remove fa-2x" onClick={() => thisPage.onBoolFieldRemoveButtonClicked(booleanField)} />
                            </div>
                            <app.InputField key="bool1" type="text" label="Bit Position" name="bit-position" value={booleanField.getBitPosition()} onChange={(bitPosition) => thisPage.onBooleanFieldBitPositionChanged(booleanField, bitPosition)} />
                            <app.InputField key="bool2" type="text" label="True Description" name="true-description" value={booleanField.getTrueDescription()} onChange={(trueDescription) => thisPage.onBooleanFieldTrueDescriptionChanged(booleanField, trueDescription)} />
                            <app.InputField key="bool3" type="text" label="False Description" name="false-description" value={booleanField.getFalseDescription()} onChange={(falseDescription) => thisPage.onBooleanFieldFalseDescriptionChanged(booleanField, falseDescription)} />
                        </div>
                    );
                    i++;
                });
                reactComponents.push(<div key="plus-button" className="button" onClick={this.onBoolFieldAddButtonClicked}><i className="fa fa-plus"></i></div>);
            } break;
            case 'TEnum': {
                const thisPage = this;
                let i = 1;
                mostType.getEnumValues().forEach(function (enumValue) {
                    const key = "enum" + i;
                    reactComponents.push(
                        <div className="repeating-field clearfix" key={key}>
                            <div className="repeating-field-header clearfix">Enum Value {enumValue.getValueIndex()}
                                <i className="remove-button fa fa-remove fa-2x" onClick={() => thisPage.onEnumValueRemoveButtonClicked(enumValue)} />
                            </div>
                            <app.InputField key="enum1" type="text" label="Enum Value Name" name="enum-value-name" value={enumValue.getName()} onChange={(value) => thisPage.onEnumValueNameChanged(enumValue, value)}/>
                            <app.InputField key="enum2" type="text" label="Enum Value Code" name="enum-value-code" value={enumValue.getCode()} onChange={(code) => thisPage.onEnumValueCodeChanged(enumValue, code)}/>
                        </div>
                    );
                    i++;
                });
                reactComponents.push(<div key="plus-button" className="button" onClick={this.onEnumValueAddButtonClicked}><i className="fa fa-plus"></i></div>);
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
                const thisPage = this;
                const primaryTypes = this.getPrimaryTypes();
                let i = 1;
                mostType.getStreamCases().forEach(function (streamCase) {
                    const key = "streamCase" + i;
                    // Populate stream parameters (repeats)
                    const streamParameters = [];
                    const parameterAddButtonKey = "addStreamParameter" + i;
                    let j = 1;
                    streamCase.getStreamParameters().forEach(function (streamParameter) {
                        const parameterKey = ("streamParameter" + i) + j;
                        if (streamParameter.getParameterType() == null) {
                            streamParameter.setParameterType(thisPage.getMostTypeByName(primaryTypes[0]));
                        }
                        const parameterTypeName = streamParameter.getParameterType().getName();

                        streamParameters.push(
                            <div key={parameterKey} className="parameter">
                                <div>Stream Parameter {streamParameter.getParameterIndex()}</div>
                                <app.InputField name="name" type="text" label="Name" isSmallInputField={true} value={streamParameter.getParameterName()} onChange={(name) => thisPage.onStreamCaseParameterNameChanged(streamParameter, name)}/>
                                <app.InputField name="description" type="textarea" label="Description" isSmallInputField={true} value={streamParameter.getParameterDescription()} onChange={(description) => thisPage.onStreamCaseParameterDescriptionChanged(streamParameter, description)}/>
                                <app.InputField name="type" type="select" label="Type" isSmallInputField={true} value={parameterTypeName} options={primaryTypes} onChange={(value) => thisPage.onStreamCaseParameterTypeChanged(streamParameter, value)} />
                                <i className="remove-button fa fa-remove fa-3x" onClick={() => thisPage.onStreamCaseParameterRemoveButtonClicked(streamCase, streamParameter)} />
                            </div>
                        );
                       j++;
                    });
                    // Populate stream signals (repeats)
                    const streamSignals = [];
                    const signalAddKey = "addStreamSignal" + i;
                    j = 1;
                    streamCase.getStreamSignals().forEach(function (streamSignal) {
                        const signalKey = ("streamSignal" + i) + j;
                        streamSignals.push(
                            <div key={signalKey} className="parameter">
                                <div>Stream Signal {streamSignal.getSignalIndex()}</div>
                                <app.InputField name="name" type="text" label="Name" isSmallInputField={true} value={streamSignal.getSignalName()} onChange={(name) => thisPage.onStreamSignalNameChanged(streamSignal, name)}/>
                                <app.InputField name="description" type="textarea" label="Description" isSmallInputField={true} value={streamSignal.getSignalDescription()} onChange={(description) => thisPage.onStreamSignalDescriptionChanged(streamSignal, description)}/>
                                <app.InputField name="bit-length" type="text" label="Bit Length" isSmallInputField={true} value={streamSignal.getSignalBitLength()} onChange={(bitLength) => thisPage.onStreamSignalBitLengthChanged(streamSignal, bitLength)}/>
                                <i className="remove-button fa fa-remove fa-3x" onClick={() => thisPage.onStreamCaseSignalRemoveButtonClicked(streamCase, streamSignal)} />
                            </div>
                        );
                        j++;
                    });

                    reactComponents.push(
                        <div key={key} className="repeating-field clearfix">
                            <div className="repeating-field-header clearfix">Stream Case {streamCase.getCaseIndex()}
                                <i className="remove-button fa fa-remove fa-2x" onClick={() => thisPage.onStreamCaseRemoveButtonClicked(streamCase)} />
                            </div>
                            <app.InputField key="streamcase1" type="text" label="Position X" name="position-x" value={streamCase.getStreamPositionX()} onChange={(positionX) => thisPage.onStreamCasePositionXChanged(streamCase, positionX)} />
                            <app.InputField key="streamcase2" type="text" label="Position Y" name="position-y" value={streamCase.getStreamPositionY()} onChange={(positionY) => thisPage.onStreamCasePositionYChanged(streamCase, positionY)} />
                            <div key="parameter-display-area" className="parameter-display-area clearfix">
                                <div className="metadata-form-title">Stream Parameters</div>
                                {streamParameters}
                                <i key={parameterAddButtonKey} className="assign-button fa fa-plus-square fa-3x" onClick={() => thisPage.onStreamCaseParameterAddButtonClicked(streamCase)}/>
                            </div>
                            <div key="signal-display-area" className="parameter-display-area clearfix">
                                <div className="metadata-form-title">Stream Signals</div>
                                {streamSignals}
                                <i key={signalAddKey} className="assign-button fa fa-plus-square fa-3x" onClick={() => thisPage.onStreamCaseSignalAddButtonClicked(streamCase)}/>
                            </div>
                        </div>
                    );
                    i++;
                });
                reactComponents.push(<div key="plus-button" className="button" onClick={this.onStreamCaseAddButtonClicked}><i className="fa fa-plus"></i></div>);
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
                const thisPage = this;
                const recordName = mostType.getRecordName();
                const recordDescription = mostType.getRecordDescription();
                const recordSize = mostType.getRecordSize();

                let i = 1;
                // TODO: make repeating
                const recordFields = [];
                const recordTypes = this.getRecordTypes();
                mostType.getRecordFields().forEach(function (recordField) {
                    if (recordField.getFieldType() == null) {
                        recordField.setFieldType(thisPage.getMostTypeByName(thisPage.getRecordTypes()[0]));
                    }

                    const key = "recordField" + i;
                    const recordFieldTypeName = recordField.getFieldType().getName();
                    recordFields.push(
                        <div className="repeating-field clearfix" key={key}>
                            <div className="repeating-field-header clearfix">Record Field {recordField.getFieldIndex()}
                                <i className="remove-button fa fa-remove fa-2x" onClick={() => thisPage.onRecordFieldRemoveButtonClicked(recordField)} />
                            </div>
                            <app.InputField key="recordField1" type="text" label="Record Field Name" name="record-field-name" value={recordField.getFieldName()} onChange={(name) => thisPage.onRecordFieldNameChanged(recordField, name)} />
                            <app.InputField key="recordField2" type="text" label="Record Field Description" name="record-field-description" value={recordField.getFieldDescription()} onChange={(description) => thisPage.onRecordFieldDescriptionChanged(recordField, description)} />
                            <app.InputField key="recordField3" type="select" label="Record Field Type" name="record-field-type" value={recordFieldTypeName} options={recordTypes} onChange={(value) => thisPage.onRecordFieldTypeChanged(recordField, value)} />
                        </div>
                    );
                    i++;
                });

                reactComponents.push(
                    <div key="TRecord" className="clearfix">
                        <app.InputField key="record1" type="text" label="Record Name" name="record-name" value={recordName} onChange={this.onRecordNameChanged} />
                        <app.InputField key="record2" type="textarea" label="Record Description" name="record-description" value={recordDescription} onChange={this.onRecordDescriptionChanged} />
                        <app.InputField key="record3" type="text" label="Record Size" name="record-size" value={recordSize} onChange={this.onRecordSizeChanged} />
                    </div>
                );
                reactComponents.push(recordFields);
                reactComponents.push(<div key="plus-button" className="button" onClick={this.onRecordFieldAddButtonClicked}><i className="fa fa-plus"></i></div>);
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

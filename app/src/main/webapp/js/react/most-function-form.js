class MostFunctionForm extends React.Component {
    constructor(props) {
        super(props);

        this.stereotypeNames = {
            event:                      'Event',
            readOnlyProperty:           'ReadOnlyProperty',
            readOnlyPropertyWithEvent:  'ReadOnlyPropertyWithEvent',
            PropertyWithEvent:          'PropertyWithEvent',
            commandWithAck:             'CommandWithAck',
            requestResponse:            'Request/Response',
        };

        this.functionTypes = {
            property:   "Property",
            method:     "Method"
        };

        const mostFunctionStereotypes = this.props.mostFunctionStereotypes;
        const isNewMostFunction = (! this.props.mostFunction);
        const mostFunction = isNewMostFunction ? new MostFunction() : copyMostObject(MostFunction, this.props.mostFunction);

        let stereotypeName = this.props.selectedFunctionStereotype;
         // If a new function is created, set the Return Type to default. If not, use the name of the existing function's stereotype.
        if (isNewMostFunction) {
            mostFunction.setReturnType(this.props.mostTypes[0]);
        } else {
            stereotypeName = mostFunction.getStereotype().getName();
        }

        // Obtain full stereotype data using the appropriate stereotype name.
        for (let i in mostFunctionStereotypes) {
            const mostFunctionStereotype = mostFunctionStereotypes[i];
            if (stereotypeName === mostFunctionStereotype.getName()) {
                mostFunction.setStereotype(mostFunctionStereotype);
                mostFunction.setFunctionType(mostFunctionStereotype.getCategory());
                mostFunction.setSupportsNotification(mostFunctionStereotype.getSupportsNotification());
                break;
            }
        }

        this.state = {
            showTitle:                  this.props.showTitle,
            shouldShowSaveAnimation:    this.props.shouldShowSaveAnimation,
            mostFunction:               mostFunction,
            mostFunctionStereotypes:    mostFunctionStereotypes,
            buttonTitle:                (this.props.buttonTitle || "Submit"),
            defaultButtonTitle:         this.props.defaultButtonTitle,
            shouldUpdateFunction:       !isNewMostFunction
        };

        this.onMostIdChanged = this.onMostIdChanged.bind(this);
        this.onNameChanged = this.onNameChanged.bind(this);
        this.onDescriptionChange = this.onDescriptionChange.bind(this);
        this.onReleaseVersionChanged = this.onReleaseVersionChanged.bind(this);
        this.onStereotypeChanged = this.onStereotypeChanged.bind(this);
        this.onReturnTypeChanged = this.onReturnTypeChanged.bind(this);
        this.onParameterChanged = this.onParameterChanged.bind(this);

        this.onAddParameterClicked = this.onAddParameterClicked.bind(this);
        this.onDeleteParameterClicked = this.onDeleteParameterClicked.bind(this);
        this.onClick = this.onClick.bind(this);
        this.onSubmit = this.onSubmit.bind(this);

        this.renderParameters = this.renderParameters.bind(this);
        this.renderFormTitle = this.renderFormTitle.bind(this);
        this.getStereotypeOperations = this.getStereotypeOperations.bind(this);
        this.renderOperationCheckboxes = this.renderOperationCheckboxes.bind(this);
        this.renderSubmitButton = this.renderSubmitButton.bind(this);
    }

    componentWillReceiveProps(newProperties) {
        const isNewMostFunction = (! newProperties.mostFunction);
        const mostFunction = isNewMostFunction ? new MostFunction() : copyMostObject(MostFunction, newProperties.mostFunction);

        const mostFunctionStereotypes = newProperties.mostFunctionStereotypes;
        let stereotypeName = newProperties.selectedFunctionStereotype;
        // If a new function is created, set the Return Type to default. If not, use the name of the existing function's stereotype.
        if (isNewMostFunction) {
            mostFunction.setReturnType(newProperties.mostTypes[0]);
        } else {
            stereotypeName = mostFunction.getStereotype().getName();
        }

        // Obtain full stereotype data using the appropriate stereotype name.
        for (let i in mostFunctionStereotypes) {
            const mostFunctionStereotype = mostFunctionStereotypes[i];
            if (stereotypeName === mostFunctionStereotype.getName()) {
                mostFunction.setStereotype(mostFunctionStereotype);
                mostFunction.setFunctionType(mostFunctionStereotype.getCategory());
                mostFunction.setSupportsNotification(mostFunctionStereotype.getSupportsNotification());
                break;
            }
        }

        this.setState({
            showTitle:                  newProperties.showTitle,
            shouldShowSaveAnimation:    newProperties.shouldShowSaveAnimation,
            mostFunction:               mostFunction,
            mostFunctionStereotypes:    mostFunctionStereotypes,
            buttonTitle:                (newProperties.buttonTitle || "Submit"),
            defaultButtonTitle:         newProperties.defaultButtonTitle,
            selectedFunctionStereotype: newProperties.selectedFunctionStereotype
        });
    }

    onMostIdChanged(newValue) {
        const mostFunction = this.state.mostFunction;
        mostFunction.setMostId(newValue);

        const defaultButtonTitle = this.state.defaultButtonTitle;
        this.setState({buttonTitle: defaultButtonTitle});

        if (typeof this.props.onUpdate == "function") {
            this.props.onUpdate();
        }
    }

    onNameChanged(newValue) {
        const mostFunction = this.state.mostFunction;
        mostFunction.setName(newValue);

        const defaultButtonTitle = this.state.defaultButtonTitle;
        this.setState({buttonTitle: defaultButtonTitle});

        if (typeof this.props.onUpdate == "function") {
            this.props.onUpdate();
        }
    }

    onDescriptionChange(newValue) {
        const mostFunction = this.state.mostFunction;
        mostFunction.setDescription(newValue);

        const defaultButtonTitle = this.state.defaultButtonTitle;
        this.setState({buttonTitle: defaultButtonTitle});

        if (typeof this.props.onUpdate == "function") {
            this.props.onUpdate();
        }
    }

    onReleaseVersionChanged(newValue) {
        const mostFunction = this.state.mostFunction;
        mostFunction.setReleaseVersion(newValue);

        const defaultButtonTitle = this.state.defaultButtonTitle;
        this.setState({buttonTitle: defaultButtonTitle});

        if (typeof this.props.onUpdate == "function") {
            this.props.onUpdate();
        }
    }

    onStereotypeChanged(newValue) {
        const mostFunction = this.state.mostFunction;
        const mostFunctionStereotypes = this.state.mostFunctionStereotypes;

        for (let i in mostFunctionStereotypes) {
            const mostFunctionStereotype = mostFunctionStereotypes[i];
            if (newValue === mostFunctionStereotype.getName()) {
                const mostFunctionCategory = mostFunctionStereotype.getCategory();
                mostFunction.setStereotype(mostFunctionStereotype);
                mostFunction.setFunctionType(mostFunctionCategory);
                mostFunction.setSupportsNotification(mostFunctionStereotype.getSupportsNotification());

                // Clear array of parameters if new stereotype is a property.
                if (mostFunctionCategory === "property") {
                    const newParameters = [];
                    mostFunction.setParameters(newParameters);
                }

                break;
            }
        }

        const defaultButtonTitle = this.state.defaultButtonTitle;
        this.setState({
            buttonTitle: defaultButtonTitle,
            mostFunction: mostFunction
        });

        if (typeof this.props.onUpdate == "function") {
            this.props.onUpdate();
        }
    }

    onReturnTypeChanged(newValue) {
        const mostFunction = this.state.mostFunction;

        const mostTypes = this.props.mostTypes;
        for (let i in mostTypes) {
            if (mostTypes[i].getName() == newValue) {
                mostFunction.setReturnType(mostTypes[i]);
                break;
            }
        }

        const defaultButtonTitle = this.state.defaultButtonTitle;
        this.setState({buttonTitle: defaultButtonTitle});

        if (typeof this.props.onUpdate == "function") {
            this.props.onUpdate();
        }
    }

    onAddParameterClicked() {
        const mostFunction = this.state.mostFunction;
        const parameters = mostFunction.getParameters();

        const parameter = new Parameter();
        parameter.setParameterIndex(parameters.length);
        parameter.setType(this.props.mostTypes[0]);

        parameters.push(parameter);

        mostFunction.setParameters(parameters);

        const defaultButtonTitle = this.state.defaultButtonTitle;
        this.setState({
            mostFunction: mostFunction,
            buttonTitle: defaultButtonTitle
        });

        if (typeof this.props.onUpdate == "function") {
            this.props.onUpdate();
        }
    }

    onParameterChanged(parameter) {
        const mostFunction = this.state.mostFunction;
        // TODO: verify this is the right thing to do
        mostFunction.getParameters()[parameter.getParameterIndex()] = parameter;

        const defaultButtonTitle = this.state.defaultButtonTitle;
        this.setState({
            mostFunction: mostFunction,
            buttonTitle: defaultButtonTitle
        });
    }

    onDeleteParameterClicked(parameter) {
        const mostFunction = this.state.mostFunction;

        const parameters = mostFunction.getParameters();
        const newParameters = [];
        const parameterId = parameter.getParameterIndex();

        for (let i in parameters) {
            if (parameters[i].getParameterIndex() !== parameterId) {
                parameters[i].setParameterIndex(i);
                newParameters.push(parameters[i]);
            }
        }

        mostFunction.setParameters(newParameters);

        const defaultButtonTitle = this.state.defaultButtonTitle;
        this.setState({
            mostFunction: mostFunction,
            buttonTitle: defaultButtonTitle
        });

        if (typeof this.props.onUpdate == "function") {
            this.props.onUpdate();
        }
    }

    onClick(event) {
        event.stopPropagation();
    }

    onSubmit() {
        const createdMostFunction = this.state.mostFunction;
        if (typeof this.props.onSubmit == "function") {
            this.props.onSubmit(createdMostFunction);
        }
    }

    renderFormTitle() {
        if (! this.state.showTitle) {
            return null;
        }

        const mostFunction = this.state.mostFunction;
        if (this.state.shouldUpdateFunction) {
            return (<div className="metadata-form-title">Update Function: {mostFunction.getName()} ({mostFunction.getStereotype().getName()})</div>);
        }
        return (<div className="metadata-form-title">New Function ({mostFunction.getStereotype().getName()})</div>);
    }

    getStereotypeOperations() {
        const mostFunctionStereotype = this.state.mostFunction.getStereotype();
        const operationsJson = {};
        const operations = mostFunctionStereotype.getOperations();
        for (let i in operations) {
            const operation = operations[i];
            operationsJson[operation.getName()] = true;
        }

        return operationsJson;
    }

    renderOperationCheckboxes() {
        const readOnly = true;
        const mostFunction = this.state.mostFunction;
        const supportsNotification = mostFunction.getSupportsNotification();
        const operationsJson = this.getStereotypeOperations();

        return (
            <div className="operation-display-area">
                <app.InputField id="operation-get" name="get" type="checkbox" label="Get" isSmallInputField={true} value={operationsJson["Get"] == true} checked={operationsJson["Get"] == true} readOnly={readOnly}/>
                <app.InputField id="operation-set" name="set" type="checkbox" label="Set" isSmallInputField={true} value={operationsJson["Set"] == true} checked={operationsJson["Set"] == true} readOnly={readOnly}/>
                <app.InputField id="operation-status" name="status" type="checkbox" label="Status" isSmallInputField={true} value={operationsJson["Status"] == true} checked={operationsJson["Status"] == true} readOnly={readOnly}/>
                <app.InputField id="operation-error" name="error" type="checkbox" label="Error" isSmallInputField={true} value={operationsJson["Error"] == true} checked={operationsJson["Error"] == true} readOnly={readOnly}/>
                <app.InputField id="operation-start-result-ack" name="startResultAck" type="checkbox" label="StartResultAck" isSmallInputField={true} value={operationsJson["StartResultAck"] == true} checked={operationsJson["StartResultAck"] == true} readOnly={readOnly}/>
                <app.InputField id="operation-error-ack" name="errorAck" type="checkbox" label="ErrorAck" isSmallInputField={true} value={operationsJson["ErrorAck"] == true} checked={operationsJson["ErrorAck"] == true} readOnly={readOnly}/>
                <app.InputField id="operation-result-ack" name="resultAck" type="checkbox" label="ResultAck" isSmallInputField={true} value={operationsJson["ResultAck"] == true} checked={operationsJson["ResultAck"] == true} readOnly={readOnly}/>
                <app.InputField id="operation-processing-ack" name="processingAck" type="checkbox" label="ProcessingAck" isSmallInputField={true} value={operationsJson["ProcessingAck"] == true} checked={operationsJson["ProcessingAck"] == true} readOnly={readOnly}/>
                <app.InputField id="operation-abort-ack" name="abortAck" type="checkbox" label="AbortAck" isSmallInputField={true} value={operationsJson["AbortAct"] == true} checked={operationsJson["AbortAck"] == true} readOnly={readOnly}/>
                <app.InputField id="operation-notification" name="notification" type="checkbox" label="Notification" isSmallInputField={true} value={supportsNotification} checked={supportsNotification} readOnly={readOnly}/>
            </div>
        );
    }

    renderParameters() {
        // Check if current function is a method. If so, display parameters and add parameter button.
        if(this.state.mostFunction.getFunctionType() === this.functionTypes.method) {
            const parameterComponents = [];
            const parameters = this.state.mostFunction.getParameters();

            for (let i in parameters) {
                const parameter = parameters[i];
                const parameterKey = "parameter" + i;
                parameterComponents.push(<app.MostFunctionParameter
                    key={parameterKey}
                    parameter={parameter}
                    onUpdate={this.onParameterChanged}
                    onDeleteParameterClicked={this.onDeleteParameterClicked}
                    mostTypes={this.props.mostTypes}
                />);
            }
            // Push button for adding parameters.
            parameterComponents.push(<i key="add-parameter-button" className="assign-button fa fa-plus-square fa-4x" onClick={this.onAddParameterClicked}/>);

            return(
                <div className="parameter-display-area">
                    <div className="metadata-form-title">Function Parameters</div>
                    {parameterComponents}
                </div>
            );
        }
    }

    renderSubmitButton() {
        if (this.state.shouldShowSaveAnimation)  {
            return(<div className="center"><div className="button submit-button" id="most-function-submit"><i className="fa fa-refresh fa-spin"></i></div></div>);
        }
        return(<div className="center"><div className="button submit-button" id="most-function-submit" onClick={this.onSubmit}>{this.state.buttonTitle}</div></div>);
    }

    render() {
        const mostFunction = this.state.mostFunction;

        const stereotypeOptions = [];
        stereotypeOptions.push('Event');
        stereotypeOptions.push('ReadOnlyProperty');
        stereotypeOptions.push('ReadOnlyPropertyWithEvent');
        stereotypeOptions.push('PropertyWithEvent');
        stereotypeOptions.push('CommandWithAck');
        stereotypeOptions.push('Request/Response');

        const stereotypeName = mostFunction.getStereotype().getName();

        const returnTypeName = mostFunction.getReturnType() ? mostFunction.getReturnType().getName() : "";

        const mostTypeNames = [];
        for (let i in this.props.mostTypes) {
            const typeName = this.props.mostTypes[i].getName();
            mostTypeNames.push(typeName);
        }

        const reactComponents = [];
        reactComponents.push(<app.InputField key="most-function-most-id" id="most-function-most-id" name="id" type="text" label="ID" value={mostFunction.getMostId()} readOnly={this.props.readOnly} onChange={this.onMostIdChanged} />);
        reactComponents.push(<app.InputField key="most-function-name" id="most-function-name" name="name" type="text" label="Name" value={mostFunction.getName()} readOnly={this.props.readOnly} onChange={this.onNameChanged} />);
        reactComponents.push(<app.InputField key="most-function-description" id="most-function-description" name="description" type="textarea" label="Description" value={mostFunction.getDescription()} readOnly={this.props.readOnly} onChange={this.onDescriptionChange} />);
        reactComponents.push(<app.InputField key="most-function-release-version" id="most-function-release-version" name="releaseVersion" type="text" label="Release" value={mostFunction.getReleaseVersion()} readOnly={this.props.readOnly} onChange={this.onReleaseVersionChanged} />);
        reactComponents.push(<app.InputField key="most-function-stereotype" id="most-function-stereotype" name="stereotype" type="select" label="Stereotype" value={stereotypeName} options={stereotypeOptions} readOnly={this.props.readOnly} onChange={this.onStereotypeChanged} />);
        reactComponents.push(<app.InputField key="most-function-return-type" id="most-function-return-type" name="returnType" type="select" label="Return Type" value={returnTypeName} options={mostTypeNames} readOnly={this.props.readOnly} onChange={this.onReturnTypeChanged} />);

        return (
            <div className="metadata-form" onClick={this.onClick}>
                {this.renderFormTitle()}
                <div className="metadata-form-inputs">{reactComponents}</div>
                <div className="metadata-form-title">Operations</div>
                {this.renderOperationCheckboxes()}
                {this.renderParameters()}
                {this.renderSubmitButton()}
            </div>
        );
    }
}

registerClassWithGlobalScope("MostFunctionForm", MostFunctionForm);

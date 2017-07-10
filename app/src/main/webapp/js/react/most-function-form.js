class MostFunctionForm extends React.Component {
    constructor(props) {
        super(props);

        const isNewMostFunction = (! this.props.MostFunction);
        const mostFunction = MostFunction.fromJson(MostFunction.toJson(isNewMostFunction ? new MostFunction() : this.props.mostFunction));

        this.state = {
            showTitle:                  this.props.showTitle,
            shouldShowSaveAnimation:    this.props.shouldShowSaveAnimation,
            mostFunction:               mostFunction,
            buttonTitle:                (this.props.buttonTitle || "Submit"),
            defaultButtonTitle:         this.props.defaultButtonTitle,
            parameters:                 mostFunction.getParameters()
        };

        // TODO: Bind functions to this.
        this.onMostIdChanged = this.onMostIdChanged.bind(this);
        this.onNameChanged = this.onNameChanged.bind(this);
        this.onDescriptionChange = this.onDescriptionChange.bind(this);
        this.onReleaseVersionChanged = this.onReleaseVersionChanged.bind(this);
        this.onStereotypeChanged = this.onStereotypeChanged.bind(this);
        this.onReturnTypeChanged = this.onReturnTypeChanged.bind(this);

        this.onAddParameterClicked = this.onAddParameterClicked.bind(this);
        this.onClick = this.onClick.bind(this);
        this.onSubmit = this.onSubmit.bind(this);

        this.renderFormTitle = this.renderFormTitle.bind(this);
        this.renderOperationCheckboxes = this.renderOperationCheckboxes.bind(this);
        this.renderSubmitButton = this.renderSubmitButton.bind(this);
    }

    componentWillReceiveProps(newProperties) {
        const isNewMostFunction = (! this.props.MostFunction);
        const mostFunction = MostFunction.fromJson(MostFunction.toJson(isNewMostFunction ? new MostFunction() : this.props.mostFunction));

        mostFunction.setId((newProperties.mostFunction || mostFunction).getId());
        this.setState({
            showTitle:                  newProperties.showTitle,
            shouldShowSaveAnimation:    newProperties.shouldShowSaveAnimation,
            mostFunction:               mostFunction,
            buttonTitle:                (newProperties.buttonTitle || "Submit"),
            defaultButtonTitle:         newProperties.defaultButtonTitle,
            parameters:                 mostFunction.getParameters()
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
        mostFunction.setStereotype(newValue);

        const defaultButtonTitle = this.state.defaultButtonTitle;
        this.setState({buttonTitle: defaultButtonTitle});

        if (typeof this.props.onUpdate == "function") {
            this.props.onUpdate();
        }
    }

    onReturnTypeChanged(newValue) {
        const mostFunction = this.state.mostFunction;
        mostFunction.setReturnType(newValue);

        const defaultButtonTitle = this.state.defaultButtonTitle;
        this.setState({buttonTitle: defaultButtonTitle});

        if (typeof this.props.onUpdate == "function") {
            this.props.onUpdate();
        }
    }

    onAddParameterClicked() {
        const mostFunction = this.state.mostFunction;
        const parameter = new Parameter();
        parameter.setName("Parameter Name");

        const parameters = mostFunction.getParameters();
        parameters.push(parameter);

        mostFunction.setParameters(parameters);

        this.setState({
            parameters: parameters
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

        return (<div className="metadata-form-title">New Function ({this.props.selectedFunctionStereotype})</div>);
    }

    renderOperationCheckboxes() {
        // TODO: logic for which checkboxes should be checked, based on what toolbar button was clicked.

        const readOnly = false;
        let shouldCheckGet = false;
        let shouldCheckSet = false;
        let shouldCheckStatus = false;
        let shouldCheckError = false;
        let shouldCheckStartResultAck = false;
        let shouldCheckErrorAck = false;
        let shouldCheckResultAck = false;
        let shouldCheckProcessingAck = false
        let shouldCheckAbortAck = false;
        let shouldCheckNotification = false;

        const functionStereotypes = this.props.functionStereotypes;
        switch(this.props.selectedFunctionStereotype) {
            case functionStereotypes.event:
                shouldCheckStatus = true;
                shouldCheckError = true;
                shouldCheckNotification = true;
                break;
            case functionStereotypes.readOnlyProperty:
                shouldCheckGet = true;
                shouldCheckStatus = true;
                shouldCheckError = true;
                break;
            case functionStereotypes.readOnlyPropertyWithEvent:
                shouldCheckGet = true;
                shouldCheckStatus = true;
                shouldCheckError = true;
                shouldCheckNotification = true;
                break;
            case functionStereotypes.propertyWithEvent:
                shouldCheckGet = true;
                shouldCheckSet = true;
                shouldCheckStatus = true;
                shouldCheckError = true;
                shouldCheckNotification = true;
                break;
            case functionStereotypes.commandWithAck:
                shouldCheckStartResultAck = true;
                shouldCheckErrorAck = true;
                shouldCheckResultAck = true;
                shouldCheckProcessingAck = true;
                break;
            case functionStereotypes.requestResponse:
                shouldCheckStartResultAck = true;
                shouldCheckAbortAck = true;
                shouldCheckErrorAck = true;
                shouldCheckResultAck = true;
                shouldCheckProcessingAck = true;
                break;
        }

        return (
            <div>
                <app.InputField id="operation-get" name="get" type="checkbox" label="Get" value={shouldCheckGet} checked={shouldCheckGet} readOnly={readOnly}/>
                <app.InputField id="operation-set" name="set" type="checkbox" label="Set" value={shouldCheckSet} checked={shouldCheckSet} readOnly={readOnly}/>
                <app.InputField id="operation-status" name="status" type="checkbox" label="Status" value={shouldCheckStatus} checked={shouldCheckStatus} readOnly={readOnly}/>
                <app.InputField id="operation-error" name="error" type="checkbox" label="Error" value={shouldCheckError} checked={shouldCheckError} readOnly={readOnly}/>
                <app.InputField id="operation-start-result-ack" name="startResultAck" type="checkbox" label="StartResultAck" value={shouldCheckStartResultAck} checked={shouldCheckStartResultAck} readOnly={readOnly}/>
                <app.InputField id="operation-error-ack" name="errorAck" type="checkbox" label="ErrorAck" value={shouldCheckErrorAck} checked={shouldCheckErrorAck} readOnly={readOnly}/>
                <app.InputField id="operation-result-ack" name="resultAck" type="checkbox" label="ResultAck" value={shouldCheckResultAck} checked={shouldCheckResultAck} readOnly={readOnly}/>
                <app.InputField id="operation-processing-ack" name="processingAck" type="checkbox" label="ProcessingAck" value={shouldCheckProcessingAck} checked={shouldCheckProcessingAck} readOnly={readOnly}/>
                <app.InputField id="operation-abort-ack" name="abortAck" type="checkbox" label="AbortAck" value={shouldCheckAbortAck} checked={shouldCheckAbortAck} readOnly={readOnly}/>
                <app.InputField id="operation-notification" name="notification" type="checkbox" label="Notification" value={shouldCheckNotification} checked={shouldCheckNotification} readOnly={readOnly}/>
            </div>
        );
    }

    renderSubmitButton() {
        if(this.state.shouldShowSaveAnimation)  {
            return(<div className="center"><div className="button submit-button" id="function-block-submit"><i className="fa fa-refresh fa-spin"></i></div></div>);
        }
        return(<div className="center"><div className="button submit-button" id="function-block-submit" onClick={this.onSubmit}>{this.state.buttonTitle}</div></div>);
    }

    render() {
        const mostFunction = this.state.mostFunction;
        const stereotypeOptions = [];
        // TODO: push all correct stereotypes
        stereotypeOptions.push('Event');

        const returnTypeOptions = [];
        // TODO: push all correct stereotypes
        returnTypeOptions.push('Property');

        const reactComponents = [];
        reactComponents.push(<app.InputField key="most-function-most-id" id="most-function-most-id" name="id" type="text" label="ID" value={mostFunction.getMostId()} readOnly={this.props.readOnly} onChange={this.onMostIdChanged} />);
        reactComponents.push(<app.InputField key="most-function-name" id="most-function-name" name="name" type="text" label="Name" value={mostFunction.getName()} readOnly={this.props.readOnly} onChange={this.onNameChanged} />);
        reactComponents.push(<app.InputField key="most-function-description" id="most-function-description" name="description" type="textarea" label="Description" value={mostFunction.getDescription()} readOnly={this.props.readOnly} onChange={this.onDescriptionChange} />);
        reactComponents.push(<app.InputField key="most-function-release-version" id="most-function-release-version" name="releaseVersion" type="text" label="Release" value={mostFunction.getReleaseVersion()} readOnly={this.props.readOnly} onChange={this.onReleaseVersionChanged} />);
        reactComponents.push(<app.InputField key="most-function-stereotype" id="most-function-stereotype" name="stereotype" type="select" label="Stereotype" value={mostFunction.getStereotype()} options={stereotypeOptions} readOnly={this.props.readOnly} onChange={this.onStereotypeChanged} />);
        reactComponents.push(<app.InputField key="most-function-return-type" id="most-function-return-type" name="returnType" type="select" label="Return Type" value={mostFunction.getReturnType()} options={returnTypeOptions} readOnly={this.props.readOnly} onChange={this.onReturnTypeChanged} />);

        const parameterComponents = [];
        const parameters = this.state.parameters;//mostFunction.getParameters();

        for (let i in parameters) {
            const parameter = parameters[i];
            console.log(parameter.getName());
            const parameterKey = "parameter" + i;
            // TODO: pass onParameterUpdate, onParameterAdd, and onParameterDelete function as props.
            parameterComponents.push(<app.MostFunctionParameter key={parameterKey} parameter={parameter}/>);
        }

        // Push button for adding parameters.
        // TODO: need to position this button correctly.
        parameterComponents.push(<i key="add-parameter-button" className="assign-button fa fa-plus-square fa-4x" onClick={this.onAddParameterClicked}/>);

        return (
            <div className="metadata-form" onClick={this.onClick}>
                {this.renderFormTitle()}
                <div className="metadata-form-inputs">{reactComponents}</div>
                <div className="metadata-form-title">Function Parameters</div>
                <div className="parameter-display-area">{parameterComponents}</div>
                {this.renderOperationCheckboxes()}
                {this.renderSubmitButton()}
            </div>
        );
    }
}


registerClassWithGlobalScope("MostFunctionForm", MostFunctionForm);

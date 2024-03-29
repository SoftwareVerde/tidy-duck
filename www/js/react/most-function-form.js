class MostFunctionForm extends React.Component {
    constructor(props) {
        super(props);

        this.functionTypes = {
            property:   "Property",
            method:     "Method"
        };

        const mostFunctionStereotypes = this.props.mostFunctionStereotypes;
        const isNewMostFunction = (! this.props.mostFunction);
        const mostFunction = isNewMostFunction ? new MostFunction() : copyMostObject(MostFunction, this.props.mostFunction);

        if (isNewMostFunction) {
            // determine stereotype information
            let stereotypeName = this.props.selectedFunctionStereotype;
            for (let i in mostFunctionStereotypes) {
                const mostFunctionStereotype = mostFunctionStereotypes[i];
                if (stereotypeName === mostFunctionStereotype.getName()) {
                    mostFunction.setStereotype(mostFunctionStereotype);
                    mostFunction.setFunctionType(mostFunctionStereotype.getCategory());
                    mostFunction.setSupportsNotification(mostFunctionStereotype.getSupportsNotification());
                    const copyOfOperations = mostFunctionStereotype.getOperations().map(x => copyMostObject(Operation, x));
                    mostFunction.setOperations(copyOfOperations);
                    break;
                }
            }
        }

        this.state = {
            showTitle:                  this.props.showTitle,
            shouldShowSaveAnimation:    this.props.shouldShowSaveAnimation,
            mostFunction:               mostFunction,
            mostFunctionStereotypes:    mostFunctionStereotypes,
            buttonTitle:                (this.props.buttonTitle || "Submit"),
            defaultButtonTitle:         this.props.defaultButtonTitle,
            shouldUpdateFunction:       !isNewMostFunction,
        };

        this.onMostIdChanged = this.onMostIdChanged.bind(this);
        this.onNameChanged = this.onNameChanged.bind(this);
        this.onDescriptionChange = this.onDescriptionChange.bind(this);
        this.onReleaseVersionChanged = this.onReleaseVersionChanged.bind(this);
        this.onStereotypeChanged = this.onStereotypeChanged.bind(this);
        this.onReturnParameterNameChanged = this.onReturnParameterNameChanged.bind(this);
        this.onReturnParameterDescriptionChanged = this.onReturnParameterDescriptionChanged.bind(this);
        this.onReturnTypeSelected = this.onReturnTypeSelected.bind(this);
        this.onParameterChanged = this.onParameterChanged.bind(this);

        this.onAddParameterClicked = this.onAddParameterClicked.bind(this);
        this.onDeleteParameterClicked = this.onDeleteParameterClicked.bind(this);
        this.onOperationChannelChanged = this.onOperationChannelChanged.bind(this);
        this.onClick = this.onClick.bind(this);
        this.onSubmit = this.onSubmit.bind(this);

        this.renderParameters = this.renderParameters.bind(this);
        this.renderFormTitle = this.renderFormTitle.bind(this);
        this.renderOperationChannelSelects = this.renderOperationChannelSelects.bind(this);
        this.renderOperationCheckboxes = this.renderOperationCheckboxes.bind(this);
        this.renderSubmitButton = this.renderSubmitButton.bind(this);
    }

    componentWillReceiveProps(newProperties) {
        const isNewMostFunction = (! this.props.mostFunction && ! this.state.mostFunction);
        const mostFunction = MostFunction.fromJson(MostFunction.toJson(isNewMostFunction ? new MostFunction() : this.state.mostFunction || newProperties.mostFunction));

        const mostFunctionStereotypes = newProperties.mostFunctionStereotypes;

        // determine stereotype information
        let stereotypeName = newProperties.selectedFunctionStereotype;
        for (let i in mostFunctionStereotypes) {
            const mostFunctionStereotype = mostFunctionStereotypes[i];
            if (stereotypeName === mostFunctionStereotype.getName()) {
                mostFunction.setStereotype(mostFunctionStereotype);
                mostFunction.setFunctionType(mostFunctionStereotype.getCategory());
                mostFunction.setSupportsNotification(mostFunctionStereotype.getSupportsNotification());
                mostFunction.setOperations(mostFunctionStereotype.getOperations());
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
                const supportsNotification = mostFunctionStereotype.getSupportsNotification();
                const copyOfOperations = mostFunctionStereotype.getOperations().map(x => copyMostObject(Operation, x));

                mostFunction.setStereotype(mostFunctionStereotype);
                mostFunction.setFunctionType(mostFunctionCategory);
                mostFunction.setSupportsNotification(supportsNotification);
                mostFunction.setOperations(copyOfOperations);

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

    onReturnParameterNameChanged(newValue) {
        const mostFunction = this.state.mostFunction;

        mostFunction.setReturnParameterName(newValue);

        const defaultButtonTitle = this.state.defaultButtonTitle;
        this.setState({buttonTitle: defaultButtonTitle});

        if (typeof this.props.onUpdate == "function") {
            this.props.onUpdate();
        }
    }

    onReturnParameterDescriptionChanged(newValue) {
        const mostFunction = this.state.mostFunction;

        mostFunction.setReturnParameterDescription(newValue);

        const defaultButtonTitle = this.state.defaultButtonTitle;
        this.setState({buttonTitle: defaultButtonTitle});

        if (typeof this.props.onUpdate == "function") {
            this.props.onUpdate();
        }
    }

    onReturnTypeSelected(newValue) {
        const mostFunction = this.state.mostFunction;
        let returnTypeName = mostFunction.getReturnType() ? mostFunction.getReturnType().getName() : "";

        const mostTypes = this.props.mostTypes;
        for (let i in mostTypes) {
            const mostType = mostTypes[i];
            const mostTypeName = mostType.getName();
            if (mostTypeName == newValue) {
                mostFunction.setReturnType(mostType);
                returnTypeName = mostTypeName;
                break;
            }
        }

        const defaultButtonTitle = this.state.defaultButtonTitle;
        this.setState({
            buttonTitle: defaultButtonTitle
        });

        if (typeof this.props.onUpdate == "function") {
            this.props.onUpdate();
        }
    }

    onAddParameterClicked() {
        const mostFunction = this.state.mostFunction;
        const parameters = mostFunction.getParameters();

        const parameter = new Parameter();
        parameter.setParameterIndex(parameters.length+1);
        parameter.setType(this.props.mostTypes[0]);

        parameters.push(parameter);
        mostFunction.setParameters(parameters);
        //parameters[parameter.getParameterIndex()] = parameter;

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
        mostFunction.getParameters()[parameter.getParameterIndex()-1] = parameter;

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

        let indexCounter = 1;
        for (let i in parameters) {
            if (parameters[i].getParameterIndex() !== parameterId) {
                parameters[i].setParameterIndex(indexCounter);
                newParameters.push(parameters[i]);
                indexCounter++;
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

    onOperationChannelChanged(operation, channel) {
        operation.setChannel(channel);

        this.setState({
            mostFunction: this.state.mostFunction
        });
    }

    onClick(event) {
        event.stopPropagation();
    }

    onSubmit(event) {
        const createdMostFunction = this.state.mostFunction;
        if (typeof this.props.onSubmit == "function") {
            this.props.onSubmit(createdMostFunction);
        }

        event.preventDefault();
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

    renderOperationChannelSelects() {
        const channelOptions = ['Control', 'MOST-High'];

        const operations = this.state.mostFunction.getOperations();
        const reactComponents = [];
        for (let i in operations) {
            const operation = operations[i];

            reactComponents.push(<app.InputField key={i} type="select" name={operation.getName()} label={operation.getName()} isSmallInputField={true} options={channelOptions} value={operation.getChannel()} readOnly={this.props.readOnly} onChange={(channel) => this.onOperationChannelChanged(operation, channel)}/>);
        }
        return reactComponents;
    }

    renderOperationCheckboxes() {
        const mostFunction = this.state.mostFunction;
        const supportsNotification = mostFunction.getSupportsNotification();

        return (
            <div className="operation-display-area">
                {this.renderOperationChannelSelects()}
                <app.InputField key="n" id="operation-notification" name="notification" type="checkbox" label="Notification" isSmallInputField={true} value={supportsNotification} checked={supportsNotification} readOnly={true} tabIndex={-1}/>
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
                    readOnly={this.props.readOnly}
                />);
            }
            // Push button for adding parameters.
            if (! this.props.readOnly) {
                parameterComponents.push(<i key="add-parameter-button" className="assign-button fa fa-plus-square fa-4x" onClick={this.onAddParameterClicked}/>);
            }

            return(
                <div className="parameter-display-area">
                    <div className="metadata-form-title">Function Parameters</div>
                    {parameterComponents}
                </div>
            );
        }
    }

    renderSubmitButton() {
        if (! this.props.readOnly) {
            if (this.state.shouldShowSaveAnimation)  {
                return(<div className="center"><div className="button submit-button" id="most-function-submit"><i className="fa fa-refresh fa-spin"></i></div></div>);
            }
            return(<div className="center"><input type="submit" className="button submit-button" id="most-function-submit" value={this.state.buttonTitle} /></div>);
        }
    }

    render() {
        const mostFunction = this.state.mostFunction;
        const version = mostFunction.getReleaseVersion()

        const stereotypeOptions = [];
        stereotypeOptions.push('Event');
        stereotypeOptions.push('ReadOnlyProperty');
        stereotypeOptions.push('ReadOnlyPropertyWithEvent');
        stereotypeOptions.push('PropertyWithEvent');
        stereotypeOptions.push('CommandWithAck');
        stereotypeOptions.push('Request/Response');

        const stereotypeName = mostFunction.getStereotype().getName();

        const returnParameterName = mostFunction.getReturnParameterName();
        const returnParameterDescription = mostFunction.getReturnParameterDescription();
        const returnTypeName = mostFunction.getReturnType() ? mostFunction.getReturnType().getName() : "";

        let mostTypeNames = [];
        for (let i in this.props.mostTypes) {
            const typeName = this.props.mostTypes[i].getName();
            mostTypeNames.push(typeName);
        }
        mostTypeNames = mostTypeNames.sort(function(a, b) {
            return a.localeCompare(b, undefined, {numeric : true, sensitivity: 'base'});
        });

        // 0x000 through 0xFFE, case insensitive
        // Regex break-down:     0[xX](        FF[0-E]     or    0x[0-E][0-F][0-F]    or       0x[0-F][0-E][0-F]        )
        const functionIdRegex = "0[xX](?:[Ff][Ff][0-9A-Ea-e]|[0-9A-Ea-e][0-9A-Fa-f]{2}|[0-9A-Fa-f][0-9A-Ea-e][0-9A-Fa-f])";

        const reactComponents = [];
        reactComponents.push(
            <div key="input-group1" className="clearfix">
                <app.InputField key="most-function-most-id" id="most-function-most-id" name="id" type="text" label="ID (0x000 - 0xFFE)" pattern={functionIdRegex} title="0x000 through 0xFFE" value={mostFunction.getMostId()} readOnly={this.props.readOnly} onChange={this.onMostIdChanged} isRequired={true} />
                <app.InputField key="most-function-name" id="most-function-name" name="name" type="text" label="Name" pattern="[A-Za-z0-9]+" title="Only alpha-numeric characters." value={mostFunction.getName()} readOnly={this.props.readOnly} onChange={this.onNameChanged} isRequired={true} />
                <app.InputField key="most-function-release-version" id="most-function-release-version" name="releaseVersion" type="text" label="Release" pattern="[0-9]+\.[0-9]+(\.[0-9]+)?" title="Major.Minor(.Patch)" value={version} readOnly={this.props.readOnly} onChange={this.onReleaseVersionChanged} isRequired={true} />
                <app.InputField key="most-function-stereotype" id="most-function-stereotype" name="stereotype" type="select" label="Stereotype" value={stereotypeName} options={stereotypeOptions} readOnly={this.props.readOnly} onChange={this.onStereotypeChanged} />
            </div>
        );
        reactComponents.push(
            <div key="input-group2" className="clearfix">
                <app.InputField key="most-function-return-parameter-name" id="most-function-return-parameter-name" name="returnName" type="text" label="Return Parameter Name" value={returnParameterName} readOnly={this.props.readOnly} onChange={this.onReturnParameterNameChanged} isRequired={true}/>
                <app.InputField key="most-function-return-type" id="most-function-return-type" name="returnType" type="dropdown" label="Return Type" options={mostTypeNames} defaultValue={returnTypeName} readOnly={this.props.readOnly} onSelect={this.onReturnTypeSelected} isRequired={true}/>
            </div>
        );
        reactComponents.push(
            <div key="input-group3" className="clearfix">
                <app.InputField key="most-function-description" id="most-function-description" name="description" type="textarea" label="Description" value={mostFunction.getDescription()} readOnly={this.props.readOnly} onChange={this.onDescriptionChange} />
                <app.InputField key="most-function-return-parameter-description" id="most-function-return-parameter-description" name="returnDescription" type="textarea" label="Return Parameter Description" value={returnParameterDescription} readOnly={this.props.readOnly} onChange={this.onReturnParameterDescriptionChanged} />
            </div>
        );

        return (
            <form className="metadata-form clearfix" onClick={this.onClick} onSubmit={this.onSubmit}>
                {this.renderFormTitle()}
                {reactComponents}
                <div className="metadata-form-title">Operations</div>
                {this.renderOperationCheckboxes()}
                {this.renderParameters()}
                {this.renderSubmitButton()}
            </form>
        );
    }
}

registerClassWithGlobalScope("MostFunctionForm", MostFunctionForm);

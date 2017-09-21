class MostFunctionParameter extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            parameter: this.props.parameter,
        };

        this.onNameChanged = this.onNameChanged.bind(this);
        this.onDescriptionChanged = this.onDescriptionChanged.bind(this);
        this.onTypeNameChanged = this.onTypeNameChanged.bind(this);
        this.renderDeleteIcon = this.renderDeleteIcon.bind(this);
        this.onDeleteParameter = this.onDeleteParameter.bind(this);
    }

    componentWillReceiveProps(newProperties) {
        this.setState({
            parameter:  newProperties.parameter,
        });
    }

    onNameChanged(newValue) {
        const parameter = this.state.parameter;
        parameter.setName(newValue);

        if (typeof this.props.onUpdate == "function") {
            this.props.onUpdate(parameter);
        }
    }

    onDescriptionChanged(newValue) {
        const parameter = this.state.parameter;
        parameter.setDescription(newValue);

        if (typeof this.props.onUpdate == "function") {
            this.props.onUpdate(parameter);
        }
    }

    onTypeNameChanged(newValue) {
        const parameter = this.state.parameter;

        const mostTypes = this.props.mostTypes;
        let newType = null;
        for (let i in mostTypes) {
            if (mostTypes[i].getName() == newValue) {
                newType = mostTypes[i];
                break;
            }
        }
        parameter.setType(newType);

        if (typeof this.props.onUpdate == "function") {
            this.props.onUpdate(parameter);
        }
    }

    onDeleteParameter() {
        if (typeof this.props.onDeleteParameterClicked == "function") {
            this.props.onDeleteParameterClicked(this.state.parameter);
        }
    }

    renderDeleteIcon() {
        if (! this.props.readOnly) {
            return (
                <i className="remove-button fa fa-remove fa-2x" onClick={this.onDeleteParameter} />
            );
        }
    }

    render() {
        const parameter = this.state.parameter;

        const mostTypes = this.props.mostTypes;
        let options = [];
        for (let i in mostTypes) {
            options.push(mostTypes[i].getName());
        }
        options = options.sort(function(a, b) {
            return a.localeCompare(b, undefined, {numeric : true, sensitivity: 'base'});
        });

        const parameterName = parameter.getName();
        const parameterDescription = parameter.getDescription();
        const parameterTypeName = parameter.getType() ? parameter.getType().getName() : "";

        return (
          <div className="parameter">
              <div>Parameter {parameter.getParameterIndex()}</div>
              <app.InputField id="name" name="name" type="text" label="Name" isSmallInputField={true} readOnly={this.props.readOnly} value={parameterName} onChange={this.onNameChanged} isRequired={true}/>
              <app.InputField id="description" name="description" type="textarea" label="Description" isSmallInputField={true} readOnly={this.props.readOnly} value={parameterDescription} onChange={this.onDescriptionChanged} />
              <app.InputField id="type" name="type" type="select" label="Type" isSmallInputField={true} readOnly={this.props.readOnly} options={options} value={parameterTypeName} onChange={this.onTypeNameChanged} />
              {this.renderDeleteIcon()}
          </div>
        );
    }
}

registerClassWithGlobalScope("MostFunctionParameter", MostFunctionParameter);

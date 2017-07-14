class MostFunctionParameter extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            parameter:  (this.props.parameter || new Parameter()),
        };

        this.onTypeNameChanged = this.onTypeNameChanged.bind(this);
        this.renderDeleteIcon = this.renderDeleteIcon.bind(this);
        this.onDeleteParameter = this.onDeleteParameter.bind(this);
    }

    componentWillReceiveProps(newProperties) {
        this.setState({
            parameter:  (newProperties.parameter || new Parameter()),
        });
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
        return (
            <i className="remove-button fa fa-remove fa-3x" onClick={this.onDeleteParameter} />
        );
    }

    render() {
        const parameter = this.state.parameter;

        const mostTypes = this.props.mostTypes;
        const options = [];
        for (let i in mostTypes) {
            options.push(mostTypes[i].getName());
        }

        const parameterTypeName = parameter.getType() ? parameter.getType().getName() : "";

        return (
          <div className="parameter">
              <app.InputField id="type" name="type" type="select" label="Type" readOnly={this.props.readOnly} options={options} value={parameterTypeName} onChange={this.onTypeNameChanged} />
              {this.renderDeleteIcon()}
          </div>
        );
    }
}

registerClassWithGlobalScope("MostFunctionParameter", MostFunctionParameter);
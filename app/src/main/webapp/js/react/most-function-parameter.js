class MostFunctionParameter extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            parameter:  (this.props.parameter || new Parameter()),
        };

        this.onNameChanged = this.onNameChanged.bind(this);
        this.renderDeleteIcon = this.renderDeleteIcon.bind(this);
        this.onDeleteParameter = this.onDeleteParameter.bind(this);
    }

    componentWillReceiveProps(newProperties) {
        this.setState({
            parameter:  (newProperties.parameter || new Parameter()),
        });
    }

    onNameChanged(newValue) {
        const parameter = this.state.parameter;
        parameter.setName(newValue);

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
            <i className="remove-button fa fa-remove fa-4x" onClick={this.onDeleteParameter} />
        );
    }

    render() {
        const parameter = this.state.parameter;
        const options = [];
        options.push(parameter.getName());
        options.push("Parameter Name 2");
        options.push("Parameter Name 3");
        return (
          <div className="parameter">
              <app.InputField id="name" name="name" type="select" label="Name" readOnly={this.props.readOnly} options={options} onChange={this.onNameChanged} />
              {this.renderDeleteIcon()}
          </div>
        );
    }
}

registerClassWithGlobalScope("MostFunctionParameter", MostFunctionParameter);
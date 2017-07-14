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
        parameter.setTypeName(newValue);

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
        const options = [];
        const mostTypeNames = this.props.mostTypeNames;

        for (let i in mostTypeNames) {
            options.push(mostTypeNames[i]);
        }


        return (
          <div className="parameter">
              <app.InputField id="type" name="type" type="select" label="Type" readOnly={this.props.readOnly} options={options} value={parameter.getTypeName()} onChange={this.onTypeNameChanged} />
              {this.renderDeleteIcon()}
          </div>
        );
    }
}

registerClassWithGlobalScope("MostFunctionParameter", MostFunctionParameter);
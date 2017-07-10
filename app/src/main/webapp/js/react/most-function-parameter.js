class MostFunctionParameter extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            parameter:  (this.props.parameter || new Parameter()),
        };

        this.onNameChanged = this.onNameChanged.bind(this);
        this.renderDeleteIcon = this.renderDeleteIcon.bind(this);
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

    renderDeleteIcon() {
        return (
            <i className="assign-button fa fa-remove fa-3x" />
        );
    }

    render() {
        const parameter = this.state.parameter;
        return (
          <div className="search-result">
              <app.InputField id="name" name="name" type="select" label="Name" value={parameter.getName()} readOnly={this.props.readOnly} onChange={this.onNameChanged} />
              {this.renderDeleteIcon()}
          </div>
        );
    }
}

registerClassWithGlobalScope("MostFunctionParameter", MostFunctionParameter);
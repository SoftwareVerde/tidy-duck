class InputField extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            value: (this.props.value || "")
        };

        this.onInputChanged = this.onInputChanged.bind(this);
        this.getValue = this.getValue.bind(this);
    }

    onInputChanged(event) {
        var newValue = event.target.value;

        if (! this.props.readOnly) {
            this.setState({value: newValue});
        }

        if (this.props.onChange) {
            this.props.onChange(newValue, this.props.name)
        }
    }

    componentWillReceiveProps(newProperties) {
        this.setState({
            value: (newProperties.value || "")
        });
    }

    getValue() {
        return this.state.value;
    }

    render() {
        return (
            <div className="input-container">
                <label htmlFor={this.props.id}>{this.props.label}:</label>
                <input type={this.props.type} id={this.props.id} name={this.props.name} value={this.state.value} onChange={this.onInputChanged} readOnly={this.props.readOnly} />
            </div>
        );
    }
}

registerClassWithGlobalScope("InputField", InputField);

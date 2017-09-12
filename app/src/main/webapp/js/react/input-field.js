class InputField extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            value: (this.props.value || "")
        };

        this.onInputChanged = this.onInputChanged.bind(this);
        this.renderInput = this.renderInput.bind(this);
        this.renderOptions = this.renderOptions.bind(this);
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

    renderInput() {
        switch (this.props.type) {
            case 'select':
                return (
                    <select id={this.props.id} name={this.props.name} value={this.state.value} onChange={this.onInputChanged} readOnly={this.props.readOnly} disabled={this.props.readOnly} required={this.props.isRequired} >
                        {this.renderOptions()}
                    </select>
                );
                break;
            case 'textarea':
                return (
                    <textarea id={this.props.id} name={this.props.name} value={this.state.value} onChange={this.onInputChanged} readOnly={this.props.readOnly} required={this.props.isRequired} />
                );
                break;
            case 'checkbox':
                return (
                    <input type="checkbox" name={this.props.name} value={this.state.value} onChange={this.onInputChanged} readOnly={this.props.readOnly} tabIndex="-1" checked={this.props.checked}/>
                );
                break;
            default:
                return (
                    <input type={this.props.type} id={this.props.id} name={this.props.name} value={this.state.value} onChange={this.onInputChanged} readOnly={this.props.readOnly} required={this.props.isRequired}/>
                );
        }
    }

    renderOptions() {
        const options = []
        for (let i in this.props.options) {
            const optionName = this.props.options[i];
            options.push(<option key={optionName + i} value={optionName}>{optionName}</option>);
        }
        return options;
    }

    render() {
        let label = '';
        if (this.props.label) {
            label = <label htmlFor={this.props.id}>{this.props.label}:</label>;
        }
        if (this.props.isSmallInputField) {
            return (
                <div className="input-field-small">
                    {label}
                    {this.renderInput()}
                </div>
            );
        }
        return (
            <div className="input-field">
                {label}
                {this.renderInput()}
            </div>
        );
    }
}

registerClassWithGlobalScope("InputField", InputField);

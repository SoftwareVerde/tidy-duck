class InputField extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            value: (this.props.value || this.props.defaultValue || ""),
            showDropdown: false,
            selectedResult: ""
        };

        this.onInputChanged = this.onInputChanged.bind(this);
        this.onDropdownKeyPress = this.onDropdownKeyPress.bind(this);
        this.onDropdownFocus = this.onDropdownFocus.bind(this);
        this.onDropdownBlur = this.onDropdownBlur.bind(this);
        this.renderInput = this.renderInput.bind(this);
        this.renderOptions = this.renderOptions.bind(this);
        this.renderFilteredResults = this.renderFilteredResults.bind(this);
        this.getFilteredResults = this.getFilteredResults.bind(this);
        this.getValue = this.getValue.bind(this);
    }

    onInputChanged(event) {
        var inputType = this.props.type;
        var newValue = inputType == "checkbox" ? event.target.checked : event.target.value;

        if (! this.props.readOnly) {
            this.setState({
                value: newValue,
                showDropdown: (inputType == "dropdown")
            });
        }

        if (this.props.onChange) {
            this.props.onChange(newValue, this.props.name)
        }
    }

    componentWillReceiveProps(newProperties) {
        this.setState({
            value: (newProperties.value || ""),
        });
    }

    getValue() {
        return this.state.value;
    }

    getFilteredResults() {
        const filterString = this.state.value.toLowerCase();
        const options = this.props.options;
        const filteredOptions = options.filter(function(value) {
            const lowerCaseValue = value.toLowerCase();
            return lowerCaseValue.includes(filterString);
        });

        return filteredOptions.sort(function(a, b) {
            return a.localeCompare(b, undefined, {numeric : true, sensitivity: 'base'});
        });
    }

    onDropdownKeyPress(e) {
        const options = this.getFilteredResults();
        const selectedResult = this.state.selectedResult || this.state.value;
        const selectedResultIndex = options.indexOf(selectedResult);
        const previousOption = options[Math.max(0, selectedResultIndex-1)];
        const nextOption = options[Math.min(options.length-1, selectedResultIndex+1)];

        switch (e.keyCode) {
            case 13:
            case 32:
                e.preventDefault();
                this.setState({
                    value: selectedResult,
                    showDropdown: false
                });
                break;

            case 38:
                e.preventDefault();
                this.setState({
                    showDropdown: true,
                    selectedResult: previousOption
                });
                break;

            case 40:
                e.preventDefault();
                this.setState({
                    showDropdown: true,
                    selectedResult: nextOption
                });
                break;
        }
    }

    onDropdownFocus() {
        this.setState({
            showDropdown: true,
        });
    }

    onDropdownBlur() {
        if (typeof this.props.onBlur == "function") {
            this.props.onBlur(this.state.value, this.props.name)
        }

        this.setState({
            showDropdown: false,
        });
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
                    <input type="checkbox" name={this.props.name} value={this.state.value} onChange={this.onInputChanged} readOnly={this.props.readOnly} tabIndex={this.props.tabIndex} checked={this.props.checked}/>
                );
                break;
            case 'dropdown':
                return (
                    <div className="dropdown" onKeyDown={this.onDropdownKeyPress} onFocus={this.onDropdownFocus} onBlur={this.onDropdownBlur}>
                        <input type="text" id={this.props.id} name={this.props.name} value={this.state.value} onChange={this.onInputChanged} readOnly={this.props.readOnly} pattern={this.props.pattern} title={this.props.title} required={this.props.isRequired} step={this.props.step} min={this.props.min} max={this.props.max}/>
                        {this.renderFilteredResults()}
                    </div>
                );
                break;
            default:
                return (
                    <input type={this.props.type} id={this.props.id} name={this.props.name} value={this.state.value} onChange={this.onInputChanged} readOnly={this.props.readOnly} pattern={this.props.pattern} title={this.props.title} required={this.props.isRequired} step={this.props.step} min={this.props.min} max={this.props.max}/>
                );
        }
    }

    renderOptions() {
        const options = []
        for (let i in this.props.options) {
            const optionName = this.props.options[i];
            const optionLabel = this.props.optionLabels ? this.props.optionLabels[i] : optionName;
            options.push(<option key={optionName + i} value={optionName}>{optionLabel}</option>);
        }
        return options;
    }

    renderFilteredResults() {
        if (this.state.showDropdown) {
            const reactComponents = [];
            const options = this.getFilteredResults();

            for (let i in options) {
                const resultStyle = (options[i] == this.state.selectedResult) ? "selected-result" : "filtered-result";
                reactComponents.push(<div key={i} className={resultStyle}>{options[i]}</div>)
            }

            return(
                <div className="filtered-results">
                    {reactComponents}
                </div>
            );
        }
    }

    render() {
        let className = "input-field" + (this.props.isSmallInputField ? "-small" : "");
        if (this.props.className) {
            className += (" "+ this.props.className);
        }

        let label = '';
        if (this.props.label) {
            label = <label htmlFor={this.props.id}>{this.props.label}:</label>;
        }

        let icons = '';
        if (this.props.icons) {
            icons = <span className="input-icons">{this.props.icons}</span>
        }

        return (
            <div className={className}>
                {icons}
                {label}
                {this.renderInput()}
            </div>
        );
    }
}

registerClassWithGlobalScope("InputField", InputField);

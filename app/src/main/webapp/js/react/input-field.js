class InputField extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            value:          (this.props.value || this.props.defaultValue || ""),
            filterString:   (this.props.value || this.props.defaultValue || ""),
            showDropdown:   false,
            selectedResult: "",
            ignoreMouse:    false
        };

        this.onInputChanged = this.onInputChanged.bind(this);
        this.onDropdownIconClick = this.onDropdownIconClick.bind(this);
        this.onDropdownKeyPress = this.onDropdownKeyPress.bind(this);
        this.onDropdownFocus = this.onDropdownFocus.bind(this);
        this.onDropdownBlur = this.onDropdownBlur.bind(this);
        this.onFilterStringChanged = this.onFilterStringChanged.bind(this);
        this.onFilteredResultMouseOver = this.onFilteredResultMouseOver.bind(this);
        this.onFilteredResultsMouseMove = this.onFilteredResultsMouseMove.bind(this);
        this.onFilteredResultClick = this.onFilteredResultClick.bind(this);
        this.renderInput = this.renderInput.bind(this);
        this.renderOptions = this.renderOptions.bind(this);
        this.renderFilteredResults = this.renderFilteredResults.bind(this);
        this.getFilteredResults = this.getFilteredResults.bind(this);
        this.getValue = this.getValue.bind(this);
    }

    componentWillReceiveProps(newProperties) {
        this.setState({
            value:          (newProperties.value || newProperties.defaultValue || ""),
            filterString:   (newProperties.value || newProperties.defaultValue || "")
        });
    }

    getValue() {
        return this.state.value;
    }

    getFilteredResults() {
        const filterString = this.state.filterString.toLowerCase();
        const options = this.props.options;
        const filteredOptions = options.filter(function(value) {
            const lowerCaseValue = value.toLowerCase();
            return lowerCaseValue.includes(filterString);
        });

        return filteredOptions.sort(function(a, b) {
            return a.localeCompare(b, undefined, {numeric : true, sensitivity: 'base'});
        });
    }

    onInputChanged(event) {
        var inputType = this.props.type;
        var newValue = inputType == "checkbox" ? event.target.checked : event.target.value;

        if (! this.props.readOnly) {
            this.setState({
                value:          newValue,
                filterString:   newValue,
                showDropdown:   true
            });
        }

        if (this.props.onChange) {
            this.props.onChange(newValue, this.props.name)
        }
    }

    onDropdownIconClick() {
        if (this.state.showDropdown) {
            // drop-down is shown, hide it
            this.setState({
                showDropdown: false,
                ignoreMouse: false
            })
        }
        else {
            // focus input and show dropdown
            let searchInput = this.searchInput;
            searchInput.focus();
            this.setState({
                showDropdown: true
            });
        }
    }

    onDropdownKeyPress(event) {
        if (! this.props.readOnly) {
            const options = this.getFilteredResults();
            const selectedResult = this.state.selectedResult || options[0];
            const selectedResultIndex = options.indexOf(selectedResult);
            const previousOption = options[Math.max(0, selectedResultIndex-1)];
            const nextOption = options[Math.min(options.length-1, selectedResultIndex+1)];

            switch (event.key) {
                case 'Enter':
                    event.preventDefault();
                    if (typeof this.props.onSelect == "function") {
                        this.props.onSelect(selectedResult, this.props.name)
                    }
                    this.setState({
                        value: selectedResult,
                        filterString: selectedResult,
                        showDropdown: false,
                    });

                    if (this.props.onChange) {
                        this.props.onChange(selectedResult, this.props.name)
                    }
                    break;

                case 'ArrowUp':
                    const previousElement = document.getElementById(previousOption);
                    if (previousElement) {
                        event.preventDefault();

                        if (navigator.userAgent.indexOf('Firefox') > -1) {
                            previousElement.scrollIntoView(false);
                        }
                        else {
                            previousElement.scrollIntoViewIfNeeded(false);
                        }

                        this.setState({
                            showDropdown: true,
                            selectedResult: previousOption,
                            ignoreMouse: true
                        });
                    }
                    else {
                        this.setState({
                            showDropdown: true
                        });
                    }
                    break;

                case 'ArrowDown':
                    const nextElement = document.getElementById(nextOption);
                    if (nextElement) {
                        event.preventDefault();

                        if (navigator.userAgent.indexOf('Firefox') > -1) {
                            nextElement.scrollIntoView(false);
                        }
                        else {
                            nextElement.scrollIntoViewIfNeeded(false);
                        }

                        this.setState({
                            showDropdown: true,
                            selectedResult: nextOption,
                            ignoreMouse: true
                        });
                    }
                    else {
                        this.setState({
                            showDropdown: true
                        });
                    }
                    break;

                default:
                    this.setState({
                        selectedResult: selectedResult,
                        showDropdown: true,
                    });
                    break;
            }
        }
    }

    onDropdownFocus() {
        if (! this.props.readOnly) {
            this.setState({
                filterString: "",
                showDropdown: false
            });
        }
    }

    onDropdownBlur() {
        if (! this.props.readOnly) {
            this.setState({
                filterString: this.state.value,
                showDropdown: false
            });
        }
    }

    onFilterStringChanged(event) {
        if (! this.props.readOnly) {
            let value = event.target.value;
            this.setState({
                filterString: value,
                showDropdown: true
            });
        }
    }

    onFilteredResultsMouseMove() {
        this.setState({
            ignoreMouse: false
        });
    }

    onFilteredResultMouseOver(value) {
        if (! this.state.ignoreMouse) {
            this.setState({
                selectedResult: value
            });
        }
    }

    onFilteredResultClick() {
        let selectedResult = this.state.selectedResult;
        if (typeof this.props.onSelect == "function") {
            this.props.onSelect(selectedResult, this.props.name)
        }

        this.setState({
            value:          selectedResult,
            filterString:   selectedResult,
            showDropdown:   false,
        });

        if (this.props.onChange) {
            this.props.onChange(selectedResult, this.props.name)
        }
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
                    <input type="checkbox" name={this.props.name} value={this.state.value} onChange={this.onInputChanged} readOnly={this.props.readOnly} disabled={this.props.readOnly} tabIndex={this.props.tabIndex} checked={this.props.checked}/>
                );
                break;
            case 'dropdown':
                const sortIcon = this.props.readOnly ? "" : <i className="fa fa-sort" onClick={this.onDropdownIconClick} onMouseDown={(event) => event.preventDefault() }/>;
                return (
                    <div className="dropdown" onKeyDown={this.onDropdownKeyPress} onBlur={this.onDropdownBlur} onFocus={this.onDropdownFocus}>
                        <input type="text" ref={(input) => { this.searchInput = input; }} id={this.props.id} name={this.props.name} autoComplete="off" onChange={this.onFilterStringChanged} value={this.state.filterString} readOnly={this.props.readOnly} pattern={this.props.pattern} title={this.props.title} required={this.props.isRequired} step={this.props.step} min={this.props.min} max={this.props.max}/>
                        {sortIcon}
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
            const selectedResult = this.state.selectedResult || options[0];

            for (let i in options) {
                const option = options[i];
                const resultStyle = (option == selectedResult) ? "selected-result" : "filtered-result";
                reactComponents.push(<div key={"result" + i} id={option} className={resultStyle} onMouseMove={this.onFilteredResultsMouseMove} onMouseOver={() => this.onFilteredResultMouseOver(option)} onMouseDown={(event) => event.preventDefault() }>{option}</div>)
            }

            return(
                <div className="filtered-results" id="filtered-results" onClick={this.onFilteredResultClick} >
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

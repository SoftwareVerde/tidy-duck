class SearchBar extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            value:          (this.props.value || ""),
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
            <span className="search-bar">
                <input type="text" id="search-bar" name="search-bar" value={this.state.value} placeholder={this.props.defaultValue} onChange={this.onInputChanged} readOnly={this.props.readOnly}/>
                <i className="fa fa-search"/>
            </span>
        )
    }
}

registerClassWithGlobalScope("SearchBar", SearchBar);
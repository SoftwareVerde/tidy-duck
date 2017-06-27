class SearchBar extends React.Component {
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
            <div className="search-bar">
                <input type="text" id="search-bar" name="search-bar" value={this.state.value} onChange={this.onInputChanged} readOnly={this.props.readOnly}/>
                <span className="search-bar-icon"><i className="fa fa-search"/></span>
            </div>
        )
    }
}

registerClassWithGlobalScope("SearchBar", SearchBar);
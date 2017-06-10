class InputField extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            value: ""
        };

        this.handleChange = this.handleChange.bind(this);
        this.getValue = this.getValue.bind(this);
    }

    handleChange(event) {
        var newValue = event.target.value;

        if (! this.props.readOnly) {
            this.setState({value: newValue});
        }

        if (this.props.onChange) {
            this.props.onChange(newValue, this.props.name)
        }
    }

    getValue() {
        return this.state.value;
    }

    render() {
        return (
            <div className="input-container">
                <label htmlFor={this.props.id}>{this.props.label}:</label>
                <input type={this.props.type} id={this.props.id} name={this.props.name} value={this.state.value} onChange={this.handleChange} readOnly={this.props.readOnly} />
            </div>
        );
    }
}

(function (app) {
    app.InputField = InputField;
})(window.app || (window.app = { }))

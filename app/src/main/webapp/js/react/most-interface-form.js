class MostInterfaceForm extends React.Component {
    constructor(props) {
        super(props);

        const isNewMostInterface = (! this.props.mostInterface);
        const mostInterface = MostInterface.fromJson(MostInterface.toJson(isNewMostInterface ? new MostInterface() : this.props.mostInterface));

        // Default values for the interface...
        if (isNewMostInterface) {
            injectDefaultValues(mostInterface);
        }

        this.state = {
            showTitle:      this.props.showTitle,
            mostInterface:  mostInterface,
            buttonTitle:    (this.props.buttonTitle || "Submit")
        };

        this.onMostIdChanged = this.onMostIdChanged.bind(this);
        this.onNameChanged = this.onNameChanged.bind(this);
        this.onDescriptionChange = this.onDescriptionChange.bind(this);
        this.onVersionChanged = this.onVersionChanged.bind(this);

        this.onClick = this.onClick.bind(this);
        this.onSubmit = this.onSubmit.bind(this);

        this.renderFormTitle = this.renderFormTitle.bind(this);
    }

    componentWillReceiveProps(newProperties) {
        const isNewMostInterface = (! this.props.mostInterface);
        const mostInterface = MostInterface.fromJson(MostInterface.toJson(isNewMostInterface ? new MostInterface() : newProperties.mostInterface));

        // Default values for the function block...
        if (isNewMostInterface) {
            injectDefaultValues(mostInterface);
        }

        mostInterface.setId((newProperties.mostInterface || mostInterface).getId());
        this.setState({
            showTitle:      newProperties.showTitle,
            mostInterface:  mostInterface,
            buttonTitle:    (newProperties.buttonTitle || "Submit")
        });
    }

    onMostIdChanged(newValue) {
        const mostInterface = this.state.mostInterface;
        mostInterface.setMostId(newValue);

        if (typeof this.props.onUpdate == "function") {
            this.props.onUpdate();
        }
    }

    onNameChanged(newValue) {
        const mostInterface = this.state.mostInterface;
        mostInterface.setName(newValue);

        if (typeof this.props.onUpdate == "function") {
            this.props.onUpdate();
        }
    }

    onDescriptionChange(newValue) {
        const mostInterface = this.state.mostInterface;
        mostInterface.setDescription(newValue);

        if (typeof this.props.onUpdate == "function") {
            this.props.onUpdate();
        }
    }

    onVersionChanged(newValue) {
        const mostInterface = this.state.mostInterface;
        mostInterface.setReleaseVersion(newValue);

        if (typeof this.props.onUpdate == "function") {
            this.props.onUpdate();
        }
    }

    onClick(event) {
        event.stopPropagation();
    }

    onSubmit() {
        const createdMostInterface = this.state.mostInterface;
        if (typeof this.props.onSubmit == "function") {
            this.props.onSubmit(createdMostInterface);
        }
    }

    renderFormTitle() {
        if (! this.state.showTitle) {
            return null;
        }

        return (<div className="metadata-form-title">New Interface</div>);
    }

    render() {
        return (
            <div className="metadata-form" onClick={this.onClick}>
                {this.renderFormTitle()}
                <app.InputField id="function-block-most-id" name="id" type="text" label="ID" value={this.state.mostInterface.getMostId()} readOnly={this.props.readOnly} onChange={this.onMostIdChanged} />
                <app.InputField id="function-block-name" name="name" type="text" label="Name" value={this.state.mostInterface.getName()} readOnly={this.props.readOnly} onChange={this.onNameChanged} />
                <app.InputField id="function-block-description" name="description" type="text" label="Description" value={this.state.mostInterface.getDescription()} readOnly={this.props.readOnly} onChange={this.onDescriptionChange} />
                <app.InputField id="function-block-version" name="version" type="text" label="Version" value={this.state.mostInterface.getVersion()} readOnly={this.props.readOnly} onChange={this.onVersionChanged} />
                <div className="center"><div className="button submit-button" id="interface-submit" onClick={this.onSubmit}>{this.state.buttonTitle}</div></div>
            </div>
        );
    }
}

registerClassWithGlobalScope("MostInterfaceForm", MostInterfaceForm);

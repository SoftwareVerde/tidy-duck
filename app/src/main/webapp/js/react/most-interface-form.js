class MostInterfaceForm extends React.Component {
    constructor(props) {
        super(props);

        const isNewMostInterface = (! this.props.mostInterface);
        const mostInterface = isNewMostInterface ? new MostInterface() : copyMostObject(MostInterface, this.props.mostInterface);

        this.state = {
            showTitle:                  this.props.showTitle,
            shouldShowSaveAnimation:    this.props.shouldShowSaveAnimation,
            mostInterface:              mostInterface,
            buttonTitle:                (this.props.buttonTitle || "Submit"),
            defaultButtonTitle:         this.props.defaultButtonTitle,
            readOnly:                   (this.props.readOnly || mostInterface.isApproved() || mostInterface.isReleased())
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

        mostInterface.setId((newProperties.mostInterface || mostInterface).getId());
        this.setState({
            showTitle:                  newProperties.showTitle,
            shouldShowSaveAnimation:    newProperties.shouldShowSaveAnimation,
            mostInterface:              mostInterface,
            buttonTitle:                (newProperties.buttonTitle || "Submit"),
            defaultButtonTitle:         newProperties.defaultButtonTitle,
            readOnly:                   (newProperties.readOnly || mostInterface.isApproved() || mostInterface.isReleased())
        });
    }

    onMostIdChanged(newValue) {
        const mostInterface = this.state.mostInterface;
        mostInterface.setMostId(newValue);

        const defaultButtonTitle = this.state.defaultButtonTitle;
        this.setState({buttonTitle: defaultButtonTitle});

        if (typeof this.props.onUpdate == "function") {
            this.props.onUpdate();
        }
    }

    onNameChanged(newValue) {
        const mostInterface = this.state.mostInterface;
        mostInterface.setName(newValue);

        const defaultButtonTitle = this.state.defaultButtonTitle;
        this.setState({buttonTitle: defaultButtonTitle});

        if (typeof this.props.onUpdate == "function") {
            this.props.onUpdate();
        }
    }

    onDescriptionChange(newValue) {
        const mostInterface = this.state.mostInterface;
        mostInterface.setDescription(newValue);

        const defaultButtonTitle = this.state.defaultButtonTitle;
        this.setState({buttonTitle: defaultButtonTitle});

        if (typeof this.props.onUpdate == "function") {
            this.props.onUpdate();
        }
    }

    onVersionChanged(newValue) {
        const mostInterface = this.state.mostInterface;
        mostInterface.setReleaseVersion(newValue);

        const defaultButtonTitle = this.state.defaultButtonTitle;
        this.setState({buttonTitle: defaultButtonTitle});

        if (typeof this.props.onUpdate == "function") {
            this.props.onUpdate();
        }
    }

    onClick(event) {
        event.stopPropagation();
    }

    onSubmit(event) {
        const createdMostInterface = this.state.mostInterface;
        if (typeof this.props.onSubmit == "function") {
            this.props.onSubmit(createdMostInterface);
        }

        event.preventDefault();
    }

    renderFormTitle() {
        if (! this.state.showTitle) {
            return null;
        }

        let formTitle = "New Interface";
        if (this.props.showCustomTitle) {
            formTitle = this.props.mostInterface.getName();
        }

        return (<div className="metadata-form-title">{formTitle}</div>);
    }

    render() {
        const reactComponents = [];
        const mostInterface = this.state.mostInterface;
        const version = mostInterface.isApproved() ? mostInterface.getDisplayVersion() : mostInterface.getReleaseVersion();
        const readOnly = this.state.readOnly;

        reactComponents.push(<app.InputField key="most-interface-most-id" id="most-interface-most-id" name="id" type="text" pattern="(?:0|[1-9][0-9]*)" title="Positive number" label="ID" value={mostInterface.getMostId()} readOnly={readOnly} onChange={this.onMostIdChanged} isRequired={true} />);
        reactComponents.push(<app.InputField key="most-interface-name" id="most-interface-name" name="name" type="text" pattern="I[A-Za-z0-9]+" title="Only alpha-numeric characters, start with 'I'." label="Name" value={mostInterface.getName()} readOnly={readOnly} onChange={this.onNameChanged} isRequired={true} />);
        reactComponents.push(<app.InputField key="most-interface-description" id="most-interface-description" name="description" type="textarea" label="Description" value={mostInterface.getDescription()} readOnly={readOnly} onChange={this.onDescriptionChange} />);
        reactComponents.push(<app.InputField key="most-interface-version" id="most-interface-version" name="version" type="text" pattern="[1-9][0-9]*" title="Positive number" label="Version" value={version} readOnly={readOnly} onChange={this.onVersionChanged} isRequired={true} />);

        if (! readOnly) {
            if(this.state.shouldShowSaveAnimation)  {
                reactComponents.push(<div key="button submit-button" className="center"><div className="button submit-button" id="interface-submit"><i className="fa fa-refresh fa-spin"></i></div></div>);
            } else {
                reactComponents.push(<div key="button submit-button" className="center"><input type="submit" className="button submit-button" id="interface-submit" value={this.state.buttonTitle} /></div>);
            }
        }

        return (
            <form className="metadata-form clearfix" onClick={this.onClick} onSubmit={this.onSubmit}>
                {this.renderFormTitle()}
                {reactComponents}
            </form>
        );
    }
}

registerClassWithGlobalScope("MostInterfaceForm", MostInterfaceForm);
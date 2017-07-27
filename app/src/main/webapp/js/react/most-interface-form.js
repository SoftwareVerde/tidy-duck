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
            defaultButtonTitle:         this.props.defaultButtonTitle
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
            defaultButtonTitle:          newProperties.defaultButtonTitle
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
        mostInterface.setVersion(newValue);

        const defaultButtonTitle = this.state.defaultButtonTitle;
        this.setState({buttonTitle: defaultButtonTitle});

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

        let formTitle = "New Interface";
        if (this.props.showCustomTitle) {
            formTitle = this.props.mostInterface.getName();
        }

        return (<div className="metadata-form-title">{formTitle}</div>);
    }

    render() {
        const reactComponents = [];

        reactComponents.push(<app.InputField key="most-interface-most-id" id="most-interface-most-id" name="id" type="text" label="ID" value={this.state.mostInterface.getMostId()} readOnly={this.props.readOnly} onChange={this.onMostIdChanged} />);
        reactComponents.push(<app.InputField key="most-interface-name" id="most-interface-name" name="name" type="text" label="Name" value={this.state.mostInterface.getName()} readOnly={this.props.readOnly} onChange={this.onNameChanged} />);
        reactComponents.push(<app.InputField key="most-interface-description" id="most-interface-description" name="description" type="textarea" label="Description" value={this.state.mostInterface.getDescription()} readOnly={this.props.readOnly} onChange={this.onDescriptionChange} />);
        reactComponents.push(<app.InputField key="most-interface-version" id="most-interface-version" name="version" type="text" label="Version" value={this.state.mostInterface.getVersion()} readOnly={this.props.readOnly} onChange={this.onVersionChanged} />);

        if(this.state.shouldShowSaveAnimation)  {
            reactComponents.push(<div key="button submit-button" className="center"><div className="button submit-button" id="interface-submit"><i className="fa fa-refresh fa-spin"></i></div></div>);
        } else {
            reactComponents.push(<div key="button submit-button" className="center"><div className="button submit-button" id="interface-submit" onClick={this.onSubmit}>{this.state.buttonTitle}</div></div>);
        }

        return (
            <div className="metadata-form" onClick={this.onClick}>
                {this.renderFormTitle()}
                {reactComponents}
            </div>
        );
    }
}

registerClassWithGlobalScope("MostInterfaceForm", MostInterfaceForm);
class FunctionCatalogForm extends React.Component {
    constructor(props) {
        super(props);

        const functionCatalog = FunctionCatalog.fromJson(FunctionCatalog.toJson(this.props.functionCatalog || new FunctionCatalog()));
        this.state = {
            showTitle:                  this.props.showTitle,
            shouldShowSaveAnimation:    this.props.shouldShowSaveAnimation,
            functionCatalog:            functionCatalog,
            buttonTitle:                (this.props.buttonTitle || "Submit"),
            defaultButtonTitle:         this.props.defaultButtonTitle
        };

        this.onNameChanged = this.onNameChanged.bind(this);
        this.onReleaseVersionChanged = this.onReleaseVersionChanged.bind(this);

        this.onClick = this.onClick.bind(this);
        this.onSubmit = this.onSubmit.bind(this);

        this.renderFormTitle = this.renderFormTitle.bind(this);
    }

    componentWillReceiveProps(newProperties) {
        const functionCatalog = FunctionCatalog.fromJson(FunctionCatalog.toJson(newProperties.functionCatalog || new FunctionCatalog()));
        functionCatalog.setId((newProperties.functionCatalog || functionCatalog).getId());
        this.setState({
            showTitle:                  newProperties.showTitle,
            shouldShowSaveAnimation:    newProperties.shouldShowSaveAnimation,
            functionCatalog:            functionCatalog,
            buttonTitle:                (newProperties.buttonTitle || "Submit"),
            defaultButtonTitle:         newProperties.defaultButtonTitle
        });
    }

    onNameChanged(newValue) {
        const functionCatalog = this.state.functionCatalog;
        functionCatalog.setName(newValue);

        const defaultButtonTitle = this.state.defaultButtonTitle;
        this.setState({buttonTitle: defaultButtonTitle});

        if (typeof this.props.onUpdate == "function") {
            this.props.onUpdate();
        }
    }

    onReleaseVersionChanged(newValue) {
        const functionCatalog = this.state.functionCatalog;
        functionCatalog.setReleaseVersion(newValue);

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
        const createdFunctionCatalog = this.state.functionCatalog;
        if (typeof this.props.onSubmit == "function") {
            this.props.onSubmit(createdFunctionCatalog);
        }

        /*
            // Clear the form...
            const functionCatalog = new FunctionCatalog();
            this.setState({
                functionCatalog: functionCatalog
            });
        */
    }

    renderFormTitle() {
        if (! this.state.showTitle) {
            return null;
        }

        return (<div className="metadata-form-title">New Function Catalog</div>);
    }

    render() {
        const reactComponents = [];
        reactComponents.push(<app.InputField key="function-catalog-name" id="function-catalog-name" name="name" type="text" label="Name" value={this.state.functionCatalog.getName()} readOnly={this.props.readOnly} onChange={this.onNameChanged} />);
        reactComponents.push(<app.InputField key="function-catalog-release-version" id="function-catalog-release-version" name="releaseVersion" type="text" label="Release" value={this.state.functionCatalog.getReleaseVersion()} readOnly={this.props.readOnly} onChange={this.onReleaseVersionChanged} />);

        if(this.state.shouldShowSaveAnimation)  {
            reactComponents.push(<div key="button submit-button" className="center"><div className="button submit-button" id="function-catalog-submit"><i className="fa fa-refresh fa-spin"></i></div></div>);
        } else {
            reactComponents.push(<div key="button submit-button" className="center"><div className="button submit-button" id="function-catalog-submit" onClick={this.onSubmit}>{this.state.buttonTitle}</div></div>);
        }
        return (
            <div className="metadata-form" onClick={this.onClick}>
                {this.renderFormTitle()}
                {reactComponents}
            </div>
        );
    }
}

registerClassWithGlobalScope("FunctionCatalogForm", FunctionCatalogForm);

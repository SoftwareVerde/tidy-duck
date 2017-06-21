class FunctionCatalogForm extends React.Component {
    constructor(props) {
        super(props);

        const functionCatalog = FunctionCatalog.fromJson(FunctionCatalog.toJson(this.props.functionCatalog || new FunctionCatalog()));
        this.state = {
            showTitle:          this.props.showTitle,
            functionCatalog:    functionCatalog,
            buttonTitle:        (this.props.buttonTitle || "Submit"),
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
            showTitle:          newProperties.showTitle,
            functionCatalog:    functionCatalog,
            buttonTitle:        (newProperties.buttonTitle || "Submit")
        });
    }

    onNameChanged(newValue) {
        const functionCatalog = this.state.functionCatalog;
        functionCatalog.setName(newValue);

        if (typeof this.props.onUpdate == "function") {
            this.props.onUpdate();
        }
    }

    onReleaseVersionChanged(newValue) {
        const functionCatalog = this.state.functionCatalog;
        functionCatalog.setReleaseVersion(newValue);

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
        return (
            <div className="metadata-form" onClick={this.onClick}>
                {this.renderFormTitle()}
                <app.InputField id="function-catalog-name" name="name" type="text" label="Name" value={this.state.functionCatalog.getName()} readOnly={this.props.readOnly} onChange={this.onNameChanged} />
                <app.InputField id="function-catalog-release-version" name="releaseVersion" type="text" label="Release" value={this.state.functionCatalog.getReleaseVersion()} readOnly={this.props.readOnly} onChange={this.onReleaseVersionChanged} />
                <div className="center"><div className="button submit-button" id="function-catalog-submit" onClick={this.onSubmit}>{this.state.buttonTitle}</div></div>
            </div>
        );
    }
}

registerClassWithGlobalScope("FunctionCatalogForm", FunctionCatalogForm);

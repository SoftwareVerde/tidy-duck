class FunctionCatalogForm extends React.Component {
    constructor(props) {
        super(props);

        const functionCatalog = copyMostObject(FunctionCatalog, this.props.functionCatalog || new FunctionCatalog());
        this.state = {
            showTitle:                  this.props.showTitle,
            shouldShowSaveAnimation:    this.props.shouldShowSaveAnimation,
            functionCatalog:            functionCatalog,
            isDuplicateFunctionCatalog: false,
            buttonTitle:                (this.props.buttonTitle || "Submit"),
            defaultButtonTitle:         this.props.defaultButtonTitle,
            readOnly:                   (this.props.readOnly || functionCatalog.isApproved() || functionCatalog.isReleased())
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
            defaultButtonTitle:         newProperties.defaultButtonTitle,
            readOnly:                   (newProperties.readOnly || functionCatalog.isApproved() || functionCatalog.isReleased())
        });
    }

    onNameChanged(newValue) {
        const functionCatalog = this.state.functionCatalog;
        functionCatalog.setName(newValue);

        const thisInput = this;
        checkForDuplicateFunctionCatalog(newValue, functionCatalog.getBaseVersionId(), function (data) {
            if (data.wasSuccess) {
                thisInput.setState({
                    isDuplicateFunctionCatalog: data.matchFound
                });
            }
        });

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

    onSubmit(event) {
        event.preventDefault();

        const createdFunctionCatalog = this.state.functionCatalog;

        if (this.state.isDuplicateFunctionCatalog) {
            const submitAnyway = confirm("There is another function catalog with this name?  Are you sure you want to save this?");
            if (!submitAnyway) {
                return;
            }
        }

        if (typeof this.props.onSubmit == "function") {
            this.props.onSubmit(createdFunctionCatalog);
        }
    }

    renderFormTitle() {
        if (! this.state.showTitle) {
            return null;
        }

        return (<div className="metadata-form-title">New Function Catalog</div>);
    }

    render() {
        const reactComponents = [];
        const functionCatalog = this.state.functionCatalog;
        const version = functionCatalog.isApproved() ? functionCatalog.getDisplayVersion() : functionCatalog.getReleaseVersion();
        const readOnly = this.state.readOnly;

        let duplicateNameElement = '';
        if (this.state.isDuplicateFunctionCatalog) {
            const iconStyle = { color: 'red' };
            duplicateNameElement = <i className="fa fa-files-o" title="Duplicate function catalog name." style={iconStyle}></i>;
        }

        reactComponents.push(<app.InputField key="function-catalog-name" id="function-catalog-name" name="name" type="text" label="Name" icons={duplicateNameElement} value={functionCatalog.getName()} readOnly={readOnly} onChange={this.onNameChanged} pattern="[A-Za-z0-9]+" title="Only alpha-numeric characters." isRequired={true}/>);
        reactComponents.push(<app.InputField key="function-catalog-release-version" id="function-catalog-release-version" name="releaseVersion" type="text" label="Release" value={version} readOnly={readOnly} onChange={this.onReleaseVersionChanged} pattern="[0-9]+\.[0-9]+(\.[0-9]+)?" title="Major.Minor(.Patch)" isRequired={true} />);

        if (! readOnly) {
            if(this.state.shouldShowSaveAnimation)  {
                reactComponents.push(<div key="button submit-button" className="center"><div className="button submit-button" id="function-catalog-submit"><i className="fa fa-refresh fa-spin"></i></div></div>);
            } else {
                reactComponents.push(<div key="button submit-button" className="center"><input type="submit" className="button submit-button" id="function-catalog-submit" value={this.state.buttonTitle}/></div>);
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

registerClassWithGlobalScope("FunctionCatalogForm", FunctionCatalogForm);

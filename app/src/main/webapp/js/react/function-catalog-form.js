class FunctionCatalogForm extends React.Component {
    constructor(props) {
        super(props);

        const functionCatalog = FunctionCatalog.fromJson(FunctionCatalog.toJson(this.props.functionCatalog || new FunctionCatalog()));
        this.state = {
            functionCatalog: functionCatalog,
            formButton : this.props.isFunctionCatalogSelected ? "Save" : "Submit"
        };

        this.onNameChanged = this.onNameChanged.bind(this);
        this.onReleaseVersionChanged = this.onReleaseVersionChanged.bind(this);
        this.onReleaseDateChanged = this.onReleaseDateChanged.bind(this);
        this.onAuthorChanged = this.onAuthorChanged.bind(this);
        this.onCompanyChanged = this.onCompanyChanged.bind(this);

        this.onSubmit = this.onSubmit.bind(this);
        this.onSave = this.onSave.bind(this);
    }

    componentWillReceiveProps(newProperties) {
        const functionCatalog = FunctionCatalog.fromJson(FunctionCatalog.toJson(newProperties.functionCatalog || new FunctionCatalog()));
        functionCatalog.setId((newProperties.functionCatalog || functionCatalog).getId());
        this.setState({
                functionCatalog: functionCatalog,
                formButton : newProperties.isFunctionCatalogSelected ? "Save" : "Submit"
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

    onReleaseDateChanged(newValue) {
        const functionCatalog = this.state.functionCatalog;
        functionCatalog.setReleaseDate(newValue);

        if (typeof this.props.onUpdate == "function") {
            this.props.onUpdate();
        }
    }

    onAuthorChanged(newValue) {
        const functionCatalog = this.state.functionCatalog;

        const author = (functionCatalog.getAuthor() || new Author());
        author.setId(newValue);
        functionCatalog.setAuthor(author);

        if (typeof this.props.onUpdate == "function") {
            this.props.onUpdate();
        }
    }

    onCompanyChanged(newValue) {
        const functionCatalog = this.state.functionCatalog;

        const company = (functionCatalog.getCompany() || new Company());
        company.setId(newValue);
        functionCatalog.setCompany(company);

        if (typeof this.props.onUpdate == "function") {
            this.props.onUpdate();
        }
    }

    onSubmit() {
        const createdFunctionCatalog = this.state.functionCatalog;
        if (typeof this.props.onSubmit == "function") {
            this.props.onSubmit(createdFunctionCatalog);
        }

        const functionCatalog = new FunctionCatalog();
        this.setState({
            functionCatalog: functionCatalog
        });
    }

    onSave() {
        const modifiedFunctionCatalog = this.state.functionCatalog;
        if (typeof this.props.onSubmit == "function") {
            this.props.onSubmit(modifiedFunctionCatalog);
        }

        const functionCatalog = new FunctionCatalog();
        this.setState({
            functionCatalog: functionCatalog
        });
    }

    render() {
        const author = this.state.functionCatalog.getAuthor();
        const company = this.state.functionCatalog.getCompany();

        var authorId = "";
        var companyId = "";
        if(author != undefined) authorId = author.getId();
        if(company != undefined) companyId = company.getId();

        return (
            <div className="metadata-form">
                <app.InputField id="function-catalog-name" name="name" type="text" label="Name" value={this.state.functionCatalog.getName()} readOnly={this.props.readOnly} onChange={this.onNameChanged} />
                <app.InputField id="function-catalog-release-version" name="releaseVersion" type="text" label="Release" value={this.state.functionCatalog.getReleaseVersion()} readOnly={this.props.readOnly} onChange={this.onReleaseVersionChanged} />
                <app.InputField id="function-catalog-date" name="date" type="text" label="Date" value={this.state.functionCatalog.getReleaseDate()} readOnly={this.props.readOnly} onChange={this.onReleaseDateChanged} />
                <app.InputField id="function-catalog-author" name="author" type="text" label="Author" value={companyId} readOnly={this.props.readOnly} onChange={this.onAuthorChanged} />
                <app.InputField id="function-catalog-company" name="company" type="text" label="Company" value={authorId} readOnly={this.props.readOnly} onChange={this.onCompanyChanged} />
                <div className="center"><div className="submit-button" id="function-catalog-submit" onClick={this.props.isFunctionCatalogSelected ? this.onSave : this.onSubmit}>{this.state.formButton}</div></div>
            </div>
        );
    }
}

registerClassWithGlobalScope("FunctionCatalogForm", FunctionCatalogForm);

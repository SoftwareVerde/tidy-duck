class FunctionCatalogForm extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            functionCatalog:    (this.props.functionCatalog || new FunctionCatalog())
        };

        this.onNameChanged = this.onNameChanged.bind(this);
        this.onReleaseVersionChanged = this.onReleaseVersionChanged.bind(this);
        this.onReleaseDateChanged = this.onReleaseDateChanged.bind(this);
        this.onAuthorChanged = this.onAuthorChanged.bind(this);
        this.onCompanyChanged = this.onCompanyChanged.bind(this);

        this.onSubmit = this.onSubmit.bind(this);
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
        if (typeof this.props.onSubmit == "function") {
            this.props.onSubmit(this.state.functionCatalog);
        }
    }

    render() {
        return (
            <div>
                <app.InputField id="function-catalog-name" name="name" type="text" label="Name" value={this.state.functionCatalog.getName()} readOnly={this.props.readOnly} onChange={this.onNameChanged} />
                <app.InputField id="function-catalog-release-version" name="releaseVersion" type="text" label="Release" value={this.state.functionCatalog.getReleaseVersion()} readOnly={this.props.readOnly} onChange={this.onReleaseVersionChanged} />
                <app.InputField id="function-catalog-date" name="date" type="text" label="Date" value={this.state.functionCatalog.getReleaseDate()} readOnly={this.props.readOnly} onChange={this.onReleaseDateChanged} />
                <app.InputField id="function-catalog-author" name="author" type="text" label="Author" value={this.state.functionCatalog.getAuthor()} readOnly={this.props.readOnly} onChange={this.onAuthorChanged} />
                <app.InputField id="function-catalog-company" name="company" type="text" label="Company" value={this.state.functionCatalog.getCompany()} readOnly={this.props.readOnly} onChange={this.onCompanyChanged} />
                <div className="center"><div className="submit-button" id="function-catalog-submit" onClick={this.onSubmit}>Submit</div></div>
            </div>
        );
    }
}

(function (app) {
    app.FunctionCatalogForm = FunctionCatalogForm;
})(window.app || (window.app = { }))

class FunctionBlockForm extends React.Component {
    constructor(props) {
        super(props);

        const functionBlock = FunctionBlock.fromJson(FunctionBlock.toJson(this.props.functionBlock || new FunctionBlock()));
        this.state = {
            functionBlock: functionBlock,
            formButton : this.props.isChildItemSelected ? "Save" : "Submit"
        };

        this.onMostIdChanged = this.onMostIdChanged.bind(this);
        this.onKindChanged = this.onKindChanged.bind(this);
        this.onNameChanged = this.onNameChanged.bind(this);
        this.onDescriptionChange = this.onDescriptionChange.bind(this);
        this.onReleaseVersionChanged = this.onReleaseVersionChanged.bind(this);
        this.onKindChanged = this.onKindChanged.bind(this);
        this.onAuthorChanged = this.onAuthorChanged.bind(this);
        this.onCompanyChanged = this.onCompanyChanged.bind(this);

        this.onSubmit = this.onSubmit.bind(this);
        this.onSave = this.onSave.bind(this);
    }

    componentWillReceiveProps(newProperties) {
        const functionBlock = FunctionBlock.fromJson(FunctionBlock.toJson(newProperties.functionBlock || new FunctionBlock()));
        functionBlock.setId((newProperties.functionBlock || functionBlock).getId());
        this.setState({
            functionBlock: functionBlock,
            formButton: newProperties.isChildItemSelected ? "Save" : "Submit"
        });
    }

    onMostIdChanged(newValue) {
        const functionBlock = this.state.functionBlock;
        functionBlock.setMostId(newValue);

        if (typeof this.props.onUpdate == "function") {
            this.props.onUpdate();
        }
    }

    onKindChanged(newValue) {
        const functionBlock = this.state.functionBlock;
        functionBlock.setKind(newValue);

        if (typeof this.props.onUpdate == "function") {
            this.props.onUpdate();
        }
    }

    onNameChanged(newValue) {
        const functionBlock = this.state.functionBlock;
        functionBlock.setName(newValue);

        if (typeof this.props.onUpdate == "function") {
            this.props.onUpdate();
        }
    }

    onDescriptionChange(newValue) {
        const functionBlock = this.state.functionBlock;
        functionBlock.setDescription(newValue);

        if (typeof this.props.onUpdate == "function") {
            this.props.onUpdate();
        }
    }

    onReleaseVersionChanged(newValue) {
        const functionBlock = this.state.functionBlock;
        functionBlock.setReleaseVersion(newValue);

        if (typeof this.props.onUpdate == "function") {
            this.props.onUpdate();
        }
    }
    
    onAuthorChanged(newValue) {
        const functionBlock = this.state.functionBlock;

        const author = (functionBlock.getAuthor() || new Author());
        author.setId(newValue);
        functionBlock.setAuthor(author);

        if (typeof this.props.onUpdate == "function") {
            this.props.onUpdate();
        }
    }

    onCompanyChanged(newValue) {
        const functionBlock = this.state.functionBlock;

        const company = (functionBlock.getCompany() || new Company());
        company.setId(newValue);
        functionBlock.setCompany(company);

        if (typeof this.props.onUpdate == "function") {
            this.props.onUpdate();
        }
    }

    onSubmit() {
        const createdFunctionBlock = this.state.functionBlock;
        if (typeof this.props.onSubmit == "function") {
            this.props.onSubmit(createdFunctionBlock);
        }

        const functionBlock = new functionBlock();
        this.setState({
            functionBlock: functionBlock
        });
    }

    onSave() {
        const modifiedFunctionBlock = this.state.functionBlock;
        if (typeof this.props.onSubmit == "function") {
            this.props.onSubmit(modifiedFunctionBlock);
        }

        const functionBlock = new functionBlock();
        this.setState({
            functionBlock: functionBlock
        });
    }

    render() {
        const author = this.state.functionBlock.getAuthor();
        const company = this.state.functionBlock.getCompany();

        var authorId = "";
        var companyId = "";
        if(author != undefined) authorId = author.getId();
        if(company != undefined) companyId = company.getId();

        return (
            <div className="metadata-form">
                <app.InputField id="function-block-most-id" name="id" type="text" label="ID" value={this.state.functionBlock.getMostId()} readOnly={this.props.readOnly} onChange={this.onMostIdChanged} />
                <app.InputField id="function-block-kind" name="kind" type="text" label="Kind" value={this.state.functionBlock.getKind()} readOnly={this.props.readOnly} onChange={this.onKindChanged} />
                <app.InputField id="function-block-name" name="name" type="text" label="Name" value={this.state.functionBlock.getName()} readOnly={this.props.readOnly} onChange={this.onNameChanged} />
                <app.InputField id="function-block-description" name="description" type="text" label="Description" value={this.state.functionBlock.getDescription()} readOnly={this.props.readOnly} onChange={this.onDescriptionChange} />
                <app.InputField id="function-block-release-version" name="releaseVersion" type="text" label="Release" value={this.state.functionBlock.getReleaseVersion()} readOnly={this.props.readOnly} onChange={this.onReleaseVersionChanged} />
                <app.InputField id="function-block-author" name="author" type="text" label="Author" value={companyId} readOnly={this.props.readOnly} onChange={this.onAuthorChanged} />
                <app.InputField id="function-block-company" name="company" type="text" label="Company" value={authorId} readOnly={this.props.readOnly} onChange={this.onCompanyChanged} />
                <div className="center"><div className="button submit-button" id="function-block-submit" onClick={this.props.isChildItemSelected ? this.onSave : this.onSubmit}>{this.state.formButton}</div></div>
            </div>
        );
    }
}

registerClassWithGlobalScope("FunctionBlockForm", FunctionBlockForm);

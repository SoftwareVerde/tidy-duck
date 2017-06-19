function injectDefaultValues(functionBlock) {
    functionBlock.setKind("Proprietary");
    functionBlock.setAccess("public");
}

class FunctionBlockForm extends React.Component {
    constructor(props) {
        super(props);

        const isNewFunctionBlock = (! this.props.functionBlock);
        const functionBlock = FunctionBlock.fromJson(FunctionBlock.toJson(isNewFunctionBlock ? new FunctionBlock() : this.props.functionBlock));

        // Default values for the function block...
        if (isNewFunctionBlock) {
            injectDefaultValues(functionBlock);
        }

        this.state = {
            showTitle:      this.props.showTitle,
            functionBlock:  functionBlock,
            buttonTitle:    (this.props.buttonTitle || "Submit")
        };

        this.onMostIdChanged = this.onMostIdChanged.bind(this);
        this.onKindChanged = this.onKindChanged.bind(this);
        this.onNameChanged = this.onNameChanged.bind(this);
        this.onDescriptionChange = this.onDescriptionChange.bind(this);
        this.onReleaseVersionChanged = this.onReleaseVersionChanged.bind(this);
        this.onKindChanged = this.onKindChanged.bind(this);
        this.onAuthorChanged = this.onAuthorChanged.bind(this);
        this.onCompanyChanged = this.onCompanyChanged.bind(this);
        this.onAccessChanged = this.onAccessChanged.bind(this);

        this.onClick = this.onClick.bind(this);
        this.onSubmit = this.onSubmit.bind(this);

        this.renderFormTitle = this.renderFormTitle.bind(this);
    }

    componentWillReceiveProps(newProperties) {
        const isNewFunctionBlock = (! this.props.functionBlock);
        const functionBlock = FunctionBlock.fromJson(FunctionBlock.toJson(isNewFunctionBlock ? new FunctionBlock() : newProperties.functionBlock));

        // Default values for the function block...
        if (isNewFunctionBlock) {
            injectDefaultValues(functionBlock);
        }

        functionBlock.setId((newProperties.functionBlock || functionBlock).getId());
        this.setState({
            showTitle:      newProperties.showTitle,
            functionBlock:  functionBlock,
            buttonTitle:    (newProperties.buttonTitle || "Submit")
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

    onAccessChanged(newValue) {
        const functionBlock = this.state.functionBlock;
        functionBlock.setAccess(newValue);

        if (typeof this.props.onUpdate == "function") {
            this.props.onUpdate();
        }
    }

    onClick(event) {
        event.stopPropagation();
    }

    onSubmit() {
        const createdFunctionBlock = this.state.functionBlock;
        if (typeof this.props.onSubmit == "function") {
            this.props.onSubmit(createdFunctionBlock);
        }

        /*
            // Clear the form...
            const functionBlock = new FunctionBlock();
            this.setState({
                functionBlock: functionBlock
            });
        */
    }

    renderFormTitle() {
        if (! this.state.showTitle) {
            return null;
        }

        return (<div className="metadata-form-title">New Function Block</div>);
    }

    render() {
        const author = this.state.functionBlock.getAuthor();
        const company = this.state.functionBlock.getCompany();

        var authorId = "";
        var companyId = "";
        if(author != undefined) authorId = author.getId();
        if(company != undefined) companyId = company.getId();

        return (
            <div className="metadata-form" onClick={this.onClick}>
                {this.renderFormTitle()}
                <app.InputField id="function-block-most-id" name="id" type="text" label="ID" value={this.state.functionBlock.getMostId()} readOnly={this.props.readOnly} onChange={this.onMostIdChanged} />
                <app.InputField id="function-block-kind" name="kind" type="text" label="Kind" value={this.state.functionBlock.getKind()} readOnly={this.props.readOnly} onChange={this.onKindChanged} />
                <app.InputField id="function-block-name" name="name" type="text" label="Name" value={this.state.functionBlock.getName()} readOnly={this.props.readOnly} onChange={this.onNameChanged} />
                <app.InputField id="function-block-description" name="description" type="text" label="Description" value={this.state.functionBlock.getDescription()} readOnly={this.props.readOnly} onChange={this.onDescriptionChange} />
                <app.InputField id="function-block-release-version" name="releaseVersion" type="text" label="Release" value={this.state.functionBlock.getReleaseVersion()} readOnly={this.props.readOnly} onChange={this.onReleaseVersionChanged} />
                <app.InputField id="function-block-author" name="author" type="text" label="Author" value={companyId} readOnly={this.props.readOnly} onChange={this.onAuthorChanged} />
                <app.InputField id="function-block-company" name="company" type="text" label="Company" value={authorId} readOnly={this.props.readOnly} onChange={this.onCompanyChanged} />
                <app.InputField id="function-block-access" name="access" type="text" label="Access" value={this.state.functionBlock.getAccess()} readOnly={this.props.readOnly} onChange={this.onAccessChanged} />
                <div className="center"><div className="button submit-button" id="function-block-submit" onClick={this.onSubmit}>{this.state.buttonTitle}</div></div>
            </div>
        );
    }
}

registerClassWithGlobalScope("FunctionBlockForm", FunctionBlockForm);

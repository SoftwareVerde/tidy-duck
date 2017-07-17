function injectDefaultValues(functionBlock) {
    functionBlock.setKind("Proprietary");
    functionBlock.setAccess("public");
}

class FunctionBlockForm extends React.Component {
    constructor(props) {
        super(props);

        const isNewFunctionBlock = (! this.props.functionBlock);
        const functionBlock = isNewFunctionBlock ? new FunctionBlock() : copyMostObject(FunctionBlock, this.props.functionBlock);

        // Default values for the function block...
        if (isNewFunctionBlock) {
            injectDefaultValues(functionBlock);
        }

        this.state = {
            showTitle:                  this.props.showTitle,
            shouldShowSaveAnimation:    this.props.shouldShowSaveAnimation,
            functionBlock:              functionBlock,
            buttonTitle:                (this.props.buttonTitle || "Submit"),
            defaultButtonTitle:         this.props.defaultButtonTitle
        };

        this.onMostIdChanged = this.onMostIdChanged.bind(this);
        this.onKindChanged = this.onKindChanged.bind(this);
        this.onNameChanged = this.onNameChanged.bind(this);
        this.onDescriptionChange = this.onDescriptionChange.bind(this);
        this.onReleaseVersionChanged = this.onReleaseVersionChanged.bind(this);
        this.onKindChanged = this.onKindChanged.bind(this);
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
            showTitle:                  newProperties.showTitle,
            shouldShowSaveAnimation:    newProperties.shouldShowSaveAnimation,
            functionBlock:              functionBlock,
            buttonTitle:                (newProperties.buttonTitle || "Submit"),
            defaultButtonTitle:         newProperties.defaultButtonTitle
        });
    }

    onMostIdChanged(newValue) {
        const functionBlock = this.state.functionBlock;
        functionBlock.setMostId(newValue);

        const defaultButtonTitle = this.state.defaultButtonTitle;
        this.setState({buttonTitle: defaultButtonTitle});

        if (typeof this.props.onUpdate == "function") {
            this.props.onUpdate();
        }
    }

    onKindChanged(newValue) {
        const functionBlock = this.state.functionBlock;
        functionBlock.setKind(newValue);

        const defaultButtonTitle = this.state.defaultButtonTitle;
        this.setState({buttonTitle: defaultButtonTitle});

        if (typeof this.props.onUpdate == "function") {
            this.props.onUpdate();
        }
    }

    onNameChanged(newValue) {
        const functionBlock = this.state.functionBlock;
        functionBlock.setName(newValue);

        const defaultButtonTitle = this.state.defaultButtonTitle;
        this.setState({buttonTitle: defaultButtonTitle});

        if (typeof this.props.onUpdate == "function") {
            this.props.onUpdate();
        }
    }

    onDescriptionChange(newValue) {
        const functionBlock = this.state.functionBlock;
        functionBlock.setDescription(newValue);

        const defaultButtonTitle = this.state.defaultButtonTitle;
        this.setState({buttonTitle: defaultButtonTitle});

        if (typeof this.props.onUpdate == "function") {
            this.props.onUpdate();
        }
    }

    onReleaseVersionChanged(newValue) {
        const functionBlock = this.state.functionBlock;
        functionBlock.setReleaseVersion(newValue);

        const defaultButtonTitle = this.state.defaultButtonTitle;
        this.setState({buttonTitle: defaultButtonTitle});

        if (typeof this.props.onUpdate == "function") {
            this.props.onUpdate();
        }
    }

    onAccessChanged(newValue) {
        const functionBlock = this.state.functionBlock;
        functionBlock.setAccess(newValue);

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
        const createdFunctionBlock = this.state.functionBlock;
        if (typeof this.props.onSubmit == "function") {
            this.props.onSubmit(createdFunctionBlock);
        }
    }

    renderFormTitle() {
        if (! this.state.showTitle) {
            return null;
        }

        return (<div className="metadata-form-title">New Function Block</div>);
    }

    render() {

        const accessOptions = [];
        accessOptions.push('public');
        accessOptions.push('private');
        accessOptions.push('preliminary');

        const reactComponents = [];
        reactComponents.push(<app.InputField key="function-block-most-id" id="function-block-most-id" name="id" type="text" label="ID" value={this.state.functionBlock.getMostId()} readOnly={this.props.readOnly} onChange={this.onMostIdChanged} />);
        reactComponents.push(<app.InputField key="function-block-kind" id="function-block-kind" name="kind" type="text" label="Kind" value={this.state.functionBlock.getKind()} readOnly={this.props.readOnly} onChange={this.onKindChanged} />);
        reactComponents.push(<app.InputField key="function-block-name" id="function-block-name" name="name" type="text" label="Name" value={this.state.functionBlock.getName()} readOnly={this.props.readOnly} onChange={this.onNameChanged} />);
        reactComponents.push(<app.InputField key="function-block-description" id="function-block-description" name="description" type="textarea" label="Description" value={this.state.functionBlock.getDescription()} readOnly={this.props.readOnly} onChange={this.onDescriptionChange} />);
        reactComponents.push(<app.InputField key="function-block-release-version" id="function-block-release-version" name="releaseVersion" type="text" label="Release" value={this.state.functionBlock.getReleaseVersion()} readOnly={this.props.readOnly} onChange={this.onReleaseVersionChanged} />);
        reactComponents.push(<app.InputField key="function-block-access" id="function-block-access" name="access" type="select" label="Access" value={this.state.functionBlock.getAccess()} options={accessOptions} readOnly={this.props.readOnly} onChange={this.onAccessChanged} />);

        if(this.state.shouldShowSaveAnimation)  {
            reactComponents.push(<div key="button submit-button" className="center"><div className="button submit-button" id="function-block-submit"><i className="fa fa-refresh fa-spin"></i></div></div>);
        } else {
            reactComponents.push(<div key="button submit-button" className="center"><div className="button submit-button" id="function-block-submit" onClick={this.onSubmit}>{this.state.buttonTitle}</div></div>);
        }

        return (
            <div className="metadata-form" onClick={this.onClick}>
                {this.renderFormTitle()}
                {reactComponents}
            </div>
        );
    }
}

registerClassWithGlobalScope("FunctionBlockForm", FunctionBlockForm);

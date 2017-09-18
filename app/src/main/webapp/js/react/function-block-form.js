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
            defaultButtonTitle:         this.props.defaultButtonTitle,
            readOnly:                   (this.props.readOnly || functionBlock.isApproved() || functionBlock.isReleased())
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
            defaultButtonTitle:         newProperties.defaultButtonTitle,
            readOnly:                   (newProperties.readOnly || functionBlock.isApproved() || functionBlock.isReleased())
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

    onSubmit(event) {
        const createdFunctionBlock = this.state.functionBlock;
        if (typeof this.props.onSubmit == "function") {
            this.props.onSubmit(createdFunctionBlock);
        }

        event.preventDefault();
    }

    renderFormTitle() {
        if (! this.state.showTitle) {
            return null;
        }

        let formTitle = "New Function Block";
        if (this.props.showCustomTitle) {
            formTitle = this.props.functionBlock.getName();
        }

        return (<div className="metadata-form-title">{formTitle}</div>);
    }

    render() {
        const functionBlock = this.state.functionBlock;
        const readOnly = this.state.readOnly;
        const version = functionBlock.isApproved() ? functionBlock.getDisplayVersion() : functionBlock.getReleaseVersion();
        const accessOptions = [];
        accessOptions.push('public');
        accessOptions.push('private');
        accessOptions.push('preliminary');

        const reactComponents = [];
        reactComponents.push(<app.InputField key="function-block-most-id" id="function-block-most-id" name="id" type="text" label="ID (0x00 - 0xFF)" pattern="0[xX][0-9A-Fa-f]{2}" value={functionBlock.getMostId()} readOnly={readOnly} onChange={this.onMostIdChanged} isRequired={true} />);
        reactComponents.push(<app.InputField key="function-block-kind" id="function-block-kind" name="kind" type="text" label="Kind" value={functionBlock.getKind()} readOnly={readOnly} onChange={this.onKindChanged} isRequired={true} />);
        reactComponents.push(<app.InputField key="function-block-name" id="function-block-name" name="name" type="text" label="Name" value={functionBlock.getName()} readOnly={readOnly} onChange={this.onNameChanged} isRequired={true} />);
        reactComponents.push(<app.InputField key="function-block-description" id="function-block-description" name="description" type="textarea" label="Description" value={functionBlock.getDescription()} readOnly={readOnly} onChange={this.onDescriptionChange} />);
        reactComponents.push(<app.InputField key="function-block-release-version" id="function-block-release-version" name="releaseVersion" type="text" label="Release" value={version} readOnly={readOnly} onChange={this.onReleaseVersionChanged} isRequired={true} />);
        reactComponents.push(<app.InputField key="function-block-access" id="function-block-access" name="access" type="select" label="Access" value={functionBlock.getAccess()} options={accessOptions} readOnly={readOnly} onChange={this.onAccessChanged} isRequired={true} />);

        if (! readOnly) {
            if(this.state.shouldShowSaveAnimation)  {
                reactComponents.push(<div key="button submit-button" className="center"><div className="button submit-button" id="function-block-submit"><i className="fa fa-refresh fa-spin"></i></div></div>);
            } else {
                reactComponents.push(<div key="button submit-button" className="center"><input type="submit" className="button submit-button" id="function-block-submit" value={this.state.buttonTitle} /></div>);
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

registerClassWithGlobalScope("FunctionBlockForm", FunctionBlockForm);

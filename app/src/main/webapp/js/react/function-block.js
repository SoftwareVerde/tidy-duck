class FunctionBlock extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            showMenu:           false,
            showWorkingIcon:    false
        };

        this.onMenuButtonClick = this.onMenuButtonClick.bind(this);
        this.renderVersionOptions = this.renderVersionOptions.bind(this);
        this.onClick = this.onClick.bind(this);
        this.deleteFunctionBlock = this.deleteFunctionBlock.bind(this);
        this.onVersionChanged = this.onVersionChanged.bind(this);
        this.onVersionClicked = this.onVersionClicked.bind(this);

        window.app.navigation = this;
    }

    componentWillReceiveProps(newProperties) {
        this.setState({
            showMenu:           false,
            showWorkingIcon:    false
        });
    }

    onMenuButtonClick(event) {
        event.stopPropagation();
        const shouldShowMenu = (! this.state.showMenu);
        this.setState({
            showMenu: shouldShowMenu
        });
    }

    onVersionChanged(event) {
        const newValue = event.target.value;
        const functionBlock = this.props.functionBlock;
        const versionsJson = functionBlock.getVersionsJson();


        for (let i in versionsJson) {
            const newFunctionBlockJson = versionsJson[i];
            let newVersion = newFunctionBlockJson.releaseVersion;
            if (!newFunctionBlockJson.isReleased) {
                newVersion += "-" + newFunctionBlockJson.id;
            }

            if (newVersion === newValue) {
                if (typeof this.props.onVersionChanged == "function") {
                    this.props.onVersionChanged(functionBlock, newFunctionBlockJson, versionsJson);
                }
                break;
            }
        }
    }

    onVersionClicked(event) {
        event.stopPropagation();
    }

    deleteFunctionBlock(event) {
        event.stopPropagation();
        if (typeof this.props.onDelete == "function") {
            this.setState({
                showWorkingIcon: true
            });
            const thisFunctionBlock = this;
            this.props.onDelete(this.props.functionBlock, function() {
                thisFunctionBlock.setState({
                    showWorkingIcon: false
                });
            });
        }
    }

    onClick() {
        if (typeof this.props.onClick == "function") {
            this.props.onClick(this.props.functionBlock, false);
        }
    }


    renderVersionOptions() {
        const versionOptions = [];
        const versionsJson = this.props.functionBlock.getVersionsJson();

        for (let i in versionsJson) {
            let optionName = versionsJson[i].releaseVersion;
            if (!versionsJson[i].isReleased) {
                optionName += "-" + versionsJson[i].id;
            }
            versionOptions.push(<option key={optionName + i} value={optionName}>{optionName}</option>);
        }

        return versionOptions;
    }

    render() {
        const author = this.props.functionBlock.getAuthor();
        const company = this.props.functionBlock.getCompany();
        const name = this.props.functionBlock.getName();

        const childItemStyle = (this.props.functionBlock.isApproved() ? "child-item" : "unreleased-child-item") + " tidy-object";
        const workingIcon = (this.state.showWorkingIcon ? <i className="delete-working-icon fa fa-refresh fa-spin icon"/> : "");
        const releasedIcon = (this.props.functionBlock.isReleased() ? <i className="release-icon fa fa-book icon" title="This Function Block has been released." /> : "");
        const approvedIcon = (this.props.functionBlock.isApproved() ? <i className="approved-icon fa fa-thumbs-o-up icon" title="This Function Block has been approved." /> : "");

        let displayVersion = <div className="child-function-catalog-property version">{this.props.functionBlock.getReleaseVersion()}</div>;
        if (! this.props.displayVersionsList) {
            displayVersion = <select name="Version" title="Version" value={this.props.functionBlock.getDisplayVersion()} onClick={this.onVersionClicked} onChange={this.onVersionChanged}>{this.renderVersionOptions()}</select>;
        }
        
        return (
            <div className={childItemStyle} onClick={this.onClick}>
                <div className="child-item-title">
                    <span className="child-item-title-name" title={name}>{name}</span>
                </div>
                <div className="action-bar">
                    {workingIcon}
                    {approvedIcon}
                    {releasedIcon}
                    <i className="fa fa-remove action-button" onClick={this.deleteFunctionBlock} title="Remove"/>
                </div>
                {displayVersion}
                <div className="description-wrapper">
                    <div className="description" onClick={(event) => event.stopPropagation()}>
                        {this.props.functionBlock.getMostId()}
                        {(this.props.functionBlock.getDescription() ? " - " : "")}
                        {this.props.functionBlock.getDescription()}
                    </div>
                </div>
            </div>
        );
    }
}

registerClassWithGlobalScope("FunctionBlock", FunctionBlock);

class FunctionBlock extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            showMenu:           false,
            showWorkingIcon:    false
        };

        this.onMenuButtonClick = this.onMenuButtonClick.bind(this);
        this.renderMenu = this.renderMenu.bind(this);
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
            this.props.onDelete(this.props.functionBlock, function () {
                thisFunctionBlock.setState({
                    showWorkingIcon: false
                });
            });
        }
    }

    renderMenu() {
        if (! this.state.showMenu) { return; }

        return (
            <div className="child-item-menu">
                <div className="child-item-menu-item" onClick={this.deleteFunctionBlock}>
                    Remove
                    <i className="fa fa-remove" />
                </div>
            </div>
        );
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
        const shortDescription = shortenString(this.props.functionBlock.getDescription(), 25);
        const childItemStyle = this.props.functionBlock.isApproved() ? "child-item" : "unreleased-child-item";

        const workingIcon = this.state.showWorkingIcon ? <i className="delete-working-icon fa fa-refresh fa-spin"/> : "";

        // TODO: remove displayVersion if Release mode allows changing versions and saving those changes to a parent function catalog.
        const displayVersion = this.props.displayVersionsList ? <div className="child-function-catalog-property">{this.props.functionBlock.getReleaseVersion()}</div> :
            <select name={"Version"} value={this.props.functionBlock.getDisplayVersion()} onClick={this.onVersionClicked} onChange={this.onVersionChanged}>{this.renderVersionOptions()}</select>;
        
        return (
            <div className={childItemStyle} onClick={this.onClick}>
                <div className="child-item-title">
                    {name}
                    {workingIcon}
                    <i className="menu-button fa fa-bars" onClick={this.onMenuButtonClick} />
                    {this.renderMenu()}
                </div>
                <div className="child-function-catalog-property">{this.props.functionBlock.getMostId()}</div>
                <div className="child-function-catalog-property">{this.props.functionBlock.getKind()}</div>
                <div className="child-function-catalog-property">{shortDescription}</div>
                {displayVersion}
                <div className="child-function-catalog-property">{(author ? author.getName() : "")}</div>
                <div className="child-function-catalog-property">{(company ? company.getName() : "")}</div>
            </div>
        );
    }
}

registerClassWithGlobalScope("FunctionBlock", FunctionBlock);

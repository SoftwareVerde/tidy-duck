class FunctionBlock extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            showMenu:           false,
            showWorkingIcon:    false
        };

        this.onMenuButtonClick = this.onMenuButtonClick.bind(this);
        this.renderMenu = this.renderMenu.bind(this);
        this.onClick = this.onClick.bind(this);
        this.deleteFunctionBlock = this.deleteFunctionBlock.bind(this);
        this.onVersionChanged = this.onVersionChanged.bind(this);

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

    onVersionChanged(newValue) {
        this.props.functionBlock.setReleaseVersion(newValue);
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
            this.props.onClick(this.props.functionBlock, false, true);
        }
    }


    renderVersionOptions() {
        const versionOptions = [];
        const versionsJson = this.props.functionBlock.getVersionsJson();

        for (let i in versionsJson) {
            const optionName = versionsJson[i].releaseVersion;
            versionOptions.push(<option key={optionName + i} value={optionName}>{optionName}</option>);
        }

        versionOptions.push(<option key="dummyOption" value={"Dummy Option"}>Dummy Option</option>);
        return versionOptions;
    }

    render() {
        const author = this.props.functionBlock.getAuthor();
        const company = this.props.functionBlock.getCompany();
        const name = this.props.functionBlock.getName();
        const shortDescription = shortenString(this.props.functionBlock.getDescription(), 25);

        const workingIcon = this.state.showWorkingIcon ? <i className="delete-working-icon fa fa-refresh fa-spin"/> : "";
        
        return (
            <div className="child-item" onClick={this.onClick}>
                <div className="child-item-title">
                    {name}
                    {workingIcon}
                    <i className="menu-button fa fa-bars" onClick={this.onMenuButtonClick} />
                    {this.renderMenu()}
                </div>
                <div className="child-function-catalog-property">{this.props.functionBlock.getMostId()}</div>
                <div className="child-function-catalog-property">{this.props.functionBlock.getKind()}</div>
                <div className="child-function-catalog-property">{shortDescription}</div>
                <select name={"Version"} value={this.props.functionBlock.getReleaseVersion()} onClick={this.onVersionClicked} onChange={this.onVersionChanged}>{this.renderVersionOptions()}</select>
                <div className="child-function-catalog-property">{(author ? author.getName() : "")}</div>
                <div className="child-function-catalog-property">{(company ? company.getName() : "")}</div>
            </div>
        );
    }
}

registerClassWithGlobalScope("FunctionBlock", FunctionBlock);

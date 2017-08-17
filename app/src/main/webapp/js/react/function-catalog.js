class FunctionCatalog extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            showMenu:           false,
            showWorkingIcon:    false
        };

        this.onMenuButtonClick = this.onMenuButtonClick.bind(this);
        this.renderVersionOptions = this.renderVersionOptions.bind(this);
        this.renderMenu = this.renderMenu.bind(this);
        this.onClick = this.onClick.bind(this);
        this.deleteFunctionCatalog = this.deleteFunctionCatalog.bind(this);
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
        const functionCatalog = this.props.functionCatalog;
        const versionsJson = functionCatalog.getVersionsJson();


        for (let i in versionsJson) {
            const newFunctionCatalogJson = versionsJson[i];
            let newVersion = newFunctionCatalogJson.releaseVersion;
            if (!newFunctionCatalogJson.isReleased) {
                newVersion += "-" + newFunctionCatalogJson.id;
            }

            if (newVersion === newValue) {
                if (typeof this.props.onVersionChanged == "function") {
                    this.props.onVersionChanged(functionCatalog, newFunctionCatalogJson, versionsJson);
                }
                break;
            }
        }
    }

    onVersionClicked(event) {
        event.stopPropagation();
    }

    deleteFunctionCatalog(event) {
        event.stopPropagation();
        if (typeof this.props.onDelete == "function") {
            this.setState({
                showWorkingIcon: true
            });

            const thisFunctionCatalog = this;
            this.props.onDelete(this.props.functionCatalog, function () {
                thisFunctionCatalog.setState({
                    showWorkingIcon: false
                });
            });
        }
    }

    renderMenu() {
        if (! this.state.showMenu) { return; }

        return (
            <div className="child-item-menu">
                <div className="child-item-menu-item" onClick={this.deleteFunctionCatalog}>
                    Remove
                    <i className="fa fa-remove" />
                </div>
            </div>
        );
    }

    onClick() {
        if (typeof this.props.onClick == "function") {
            this.props.onClick(this.props.functionCatalog, false, true);
        }
    }

    renderVersionOptions() {
        const versionOptions = [];
        const versionsJson = this.props.functionCatalog.getVersionsJson();

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
        const author = this.props.functionCatalog.getAuthor();
        const company = this.props.functionCatalog.getCompany();
        const name = this.props.functionCatalog.getName();
        const childItemStyle = this.props.functionCatalog.isReleased() ? "child-item" : "unreleased-child-item";

        const workingIcon = this.state.showWorkingIcon ? <i className="delete-working-icon fa fa-refresh fa-spin"/> : "";

        return (
            <div className={childItemStyle} onClick={this.onClick}>
                <div className="child-item-title">
                    {name}
                    {workingIcon}
                    <i className="menu-button fa fa-bars" onClick={this.onMenuButtonClick} />
                    {this.renderMenu()}
                </div>
                <select name={"Version"} value={this.props.functionCatalog.getDisplayVersion()} onClick={this.onVersionClicked} onChange={this.onVersionChanged}>{this.renderVersionOptions()}</select>
                <div className="child-function-catalog-property">{(author ? author.getName() : "")}</div>
                <div className="child-function-catalog-property">{(company ? company.getName() : "")}</div>
            </div>
        );
    }
}

registerClassWithGlobalScope("FunctionCatalog", FunctionCatalog);

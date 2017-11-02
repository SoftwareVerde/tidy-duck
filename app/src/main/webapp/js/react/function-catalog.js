class FunctionCatalog extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            showMenu:           false,
            showWorkingIcon:    false
        };

        this.onMenuButtonClick = this.onMenuButtonClick.bind(this);
        this.renderVersionOptions = this.renderVersionOptions.bind(this);
        this.onClick = this.onClick.bind(this);
        this.deleteFunctionCatalog = this.deleteFunctionCatalog.bind(this);
        this.onExportFunctionCatalogClicked = this.onExportFunctionCatalogClicked.bind(this);
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
            this.props.onDelete(this.props.functionCatalog, function() {
                thisFunctionCatalog.setState({
                    showWorkingIcon: false
                });
            });
        }
    }

    onExportFunctionCatalogClicked(event) {
        event.stopPropagation();
        if (typeof this.props.onExportFunctionCatalog == "function") {
            this.props.onExportFunctionCatalog(this.props.functionCatalog.getId());
        }
    }

    onClick() {
        if (typeof this.props.onClick == "function") {
            this.props.onClick(this.props.functionCatalog, false);
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
        const childItemStyle = (this.props.functionCatalog.isApproved() ? "child-item" : "unreleased-child-item") + " tidy-object";

        const workingIcon = (this.state.showWorkingIcon ? <i className="delete-working-icon fa fa-refresh fa-spin icon"/> : "");
        const releasedIcon = (this.props.functionCatalog.isReleased() ? <i className="release-icon fa fa-book icon" title="This Function Catalog has been released." /> : "");
        const approvedIcon = (this.props.functionCatalog.isApproved() ? <i className="approved-icon fa fa-thumbs-o-up icon" title="This Function Catalog has been approved." /> : "");

        return (
            <div className={childItemStyle} onClick={this.onClick}>
                <div className="child-item-title">
                    <span className="child-item-title-name" title={name}>{name}</span>
                </div>
                <div className="action-bar">
                    {workingIcon}
                    {approvedIcon}
                    {releasedIcon}
                    <i className="fa fa-remove action-button" onClick={this.deleteFunctionCatalog} title="Remove"/>
                    <i className="fa fa-download action-button" onClick={this.onExportFunctionCatalogClicked} title="Downlad MOST XML" />
                </div>
                <select name="Version" title="Version" value={this.props.functionCatalog.getDisplayVersion()} onClick={this.onVersionClicked} onChange={this.onVersionChanged}>{this.renderVersionOptions()}</select>
                <div className="description-wrapper">
                    <div className="description" onClick={(event) => event.stopPropagation()}>
                        {(author ? author.getName() : "")}
                        {((author && company) ? "-" : "")}
                        {(company ? company.getName() : "")}
                    </div>
                </div>
            </div>
        );
    }
}

registerClassWithGlobalScope("FunctionCatalog", FunctionCatalog);

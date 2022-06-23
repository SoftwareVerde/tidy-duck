class FunctionCatalog extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            showMenu:           false,
            showWorkingIcon:    false,
            lastMouseDown:      null
        };

        this.lastMouseDown = {
            description: "Description",
            parent:      "Parent"
        };

        this.onMenuButtonClick = this.onMenuButtonClick.bind(this);
        this.renderVersionOptions = this.renderVersionOptions.bind(this);
        this.onClick = this.onClick.bind(this);
        this.onDescriptionMouseDown = this.onDescriptionMouseDown.bind(this);
        this.onParentMouseDown = this.onParentMouseDown.bind(this);
        this.deleteFunctionCatalog = this.deleteFunctionCatalog.bind(this);
        this.onMarkAsDeletedClicked = this.onMarkAsDeletedClicked.bind(this);
        this.onRestoreFromTrashClicked = this.onRestoreFromTrashClicked.bind(this);
        this.onApprovalReviewClicked = this.onApprovalReviewClicked.bind(this);
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

    onMarkAsDeletedClicked(event) {
        event.stopPropagation();
        this.setState({
            showWorkingIcon: true
        });

        const thisFunctionCatalog = this;
        const functionCatalog = this.props.functionCatalog;
        const versionsJson = functionCatalog.getVersionsJson();

        this.props.onMarkAsDeleted(this.props.functionCatalog, function() {
            if (thisFunctionCatalog.props.displayVersionsList) {
                let newVersionJson = versionsJson[0];
                for (let i in versionsJson) {
                    const versionJson = versionsJson[i];
                    if (versionJson.isApproved) {
                        if (versionJson.id > newVersionJson.id)
                            newVersionJson = versionJson;
                    }
                }

                thisFunctionCatalog.props.onVersionChanged(functionCatalog, newVersionJson, versionsJson);
            }

            thisFunctionCatalog.setState({
                showWorkingIcon: false
            });
        });
    }

    onRestoreFromTrashClicked(event) {
        event.stopPropagation();
        this.setState({
            showWorkingIcon: true
        });

        const thisFunctionCatalog = this;
        this.props.onRestoreFromTrash(this.props.functionCatalog, function() {
            thisFunctionCatalog.setState({
                showWorkingIcon: false
            });
        });
    }

    onApprovalReviewClicked(event) {
        event.stopPropagation();
        this.props.onApprovalReviewClicked(this.props.functionCatalog);
    }

    onClick() {
        if (this.state.lastMouseDown == this.lastMouseDown.parent) {
            if (typeof this.props.onClick == "function") {
                this.props.onClick(this.props.functionCatalog, false);
            }
        }
    }

    onDescriptionMouseDown(event) {
        event.stopPropagation();
        this.setState({
            lastMouseDown : this.lastMouseDown.description
        });
    }

    onParentMouseDown() {
        this.setState({
            lastMouseDown : this.lastMouseDown.parent
        });
    }

    renderVersionOptions() {
        const versionOptions = [];
        const versionsJson = this.props.functionCatalog.getVersionsJson();
        const showDeletedVersions = this.props.showDeletedVersions;

        for (let i in versionsJson) {
            let versionJson = versionsJson[i];
            // Only display versions marked as deleted if the app isn't hiding them.
            if ((! versionJson.isDeleted) || showDeletedVersions) {
                let optionName = versionJson.releaseVersion;
                if (! versionJson.isReleased) {
                    optionName += "-" + versionJson.id;
                }
                versionOptions.push(<option key={optionName + i} value={optionName}>{optionName}</option>);
            }
        }

        return versionOptions;
    }

    render() {
        const versionOptions = this.renderVersionOptions();

        let displayVersion = <div className="child-function-catalog-property version">{this.props.functionCatalog.getReleaseVersion()}</div>;
        if (this.props.displayVersionsList) {
            if (versionOptions.length < 1) {
                // If no version options are available to be displayed, return nothing.
                return(<div></div>);
            }
            
            displayVersion = <select name="Version" title="Version" value={this.props.functionCatalog.getDisplayVersion()} onClick={this.onVersionClicked} onChange={this.onVersionChanged}>{versionOptions}</select>;
        }

        const author = this.props.functionCatalog.getAuthor();
        const company = this.props.functionCatalog.getCompany();
        const name = this.props.functionCatalog.getName();
        const isDeleted = this.props.functionCatalog.isDeleted();
        const isApproved = this.props.functionCatalog.isApproved();
        const isReleased = this.props.functionCatalog.isReleased();
        const childItemStyle = (isApproved ? "child-item" : "unreleased-child-item") + " tidy-object" + (isDeleted ? " deleted-tidy-object" : "");

        const workingIcon = (this.state.showWorkingIcon ? <i className="delete-working-icon fa fa-refresh fa-spin icon"/> : "");
        const releasedIcon = (this.props.functionCatalog.isReleased() ? <i className="release-icon fa fa-book icon" title="This Function Catalog has been released." /> : "");
        const approvedIcon = (this.props.functionCatalog.isApproved() ? <i className="approved-icon fa fa-thumbs-o-up icon" title="This Function Catalog has been approved." /> : "");
        const approvalReviewIcon = this.props.functionCatalog.getApprovalReviewId() ? <i className="fa fa-clipboard action-button" onClick={this.onApprovalReviewClicked} title="Go to the pending or approved Review for this Function Catalog."/> : "";

        let deleteIcon = "";
        let trashOrRestoreIcon = <i className="fa fa-trash action-button" onClick={this.onMarkAsDeletedClicked} title="Move to Trash Bin"/>;
        if (isDeleted) {
            deleteIcon = <i className="fa fa-remove action-button" onClick={this.deleteFunctionCatalog} title="Delete"/>;
            trashOrRestoreIcon = <i className="fa fa-undo action-button" onClick={this.onRestoreFromTrashClicked} title="Remove from Trash Bin"/>;
        }
        else if (isReleased) {
            trashOrRestoreIcon = <i className="fa fa-trash action-button disabled-action-button" onClick={(event) => event.stopPropagation()} title="This Function Catalog cannot be moved to trash; it is released."/>;
        }

        return (
            <div className={childItemStyle} onMouseDown={this.onParentMouseDown} onClick={this.onClick}>
                <div className="child-item-title">
                    <span className="child-item-title-name" title={name}>{name}</span>
                </div>
                <div className="action-bar">
                    {workingIcon}
                    {deleteIcon}
                    {trashOrRestoreIcon}
                    {approvalReviewIcon}
                    {approvedIcon}
                    {releasedIcon}
                    <i className="fa fa-download action-button" onClick={this.onExportFunctionCatalogClicked} title="Download MOST XML" />
                </div>
                {displayVersion}
                <div className="description-wrapper">
                    <div className="description" onMouseDown={this.onDescriptionMouseDown} onClick={(event) => event.stopPropagation()}>
                        {(author ? author.getName() : "")}
                        {((author && company) ? ", " : "")}
                        {(company ? company.getName() : "")}
                    </div>
                </div>
            </div>
        );
    }
}

registerClassWithGlobalScope("FunctionCatalog", FunctionCatalog);

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
        this.disassociateFunctionBlockFromParent = this.disassociateFunctionBlockFromParent.bind(this);
        this.deleteFunctionBlock = this.deleteFunctionBlock.bind(this);
        this.onMarkAsDeletedClicked = this.onMarkAsDeletedClicked.bind(this);
        this.onRestoreFromTrashClicked = this.onRestoreFromTrashClicked.bind(this);
        this.onApprovalReviewClicked = this.onApprovalReviewClicked.bind(this);
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

    disassociateFunctionBlockFromParent(event) {
        event.stopPropagation();
        if (typeof this.props.onDisassociate == "function") {
            this.setState({
                showWorkingIcon: true
            });
            const thisFunctionBlock = this;
            this.props.onDisassociate(this.props.functionBlock, function() {
                thisFunctionBlock.setState({
                    showWorkingIcon: false
                });
            });
        }
    }

    onMarkAsDeletedClicked(event) {
        event.stopPropagation();
        this.setState({
            showWorkingIcon: true
        });

        const thisFunctionBlock = this;
        const functionBlock = this.props.functionBlock;
        const versionsJson = functionBlock.getVersionsJson();

        this.props.onMarkAsDeleted(functionBlock, function() {
            if (thisFunctionBlock.props.displayVersionsList) {
                let newVersionJson = versionsJson[0];
                for (let i in versionsJson) {
                    const versionJson = versionsJson[i];
                    if (versionJson.isApproved) {
                        if (versionJson.id > newVersionJson.id)
                            newVersionJson = versionJson;
                    }
                }

                thisFunctionBlock.props.onVersionChanged(functionBlock, newVersionJson, versionsJson);
            }

            thisFunctionBlock.setState({
                showWorkingIcon: false
            });
        });
    }

    onRestoreFromTrashClicked(event) {
        event.stopPropagation();
        this.setState({
            showWorkingIcon: true
        });

        const thisFunctionBlock = this;
        this.props.onRestoreFromTrash(this.props.functionBlock, function() {
            thisFunctionBlock.setState({
                showWorkingIcon: false
            });
        });
    }

    onApprovalReviewClicked(event) {
        event.stopPropagation();
        this.props.onApprovalReviewClicked(this.props.functionBlock);
    }

    onClick() {
        if (typeof this.props.onClick == "function") {
            this.props.onClick(this.props.functionBlock, false);
        }
    }

    renderVersionOptions() {
        const versionOptions = [];
        const versionsJson = this.props.functionBlock.getVersionsJson();
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
        const showDeletedVersions = this.props.showDeletedVersions;

        let displayVersion = <div className="child-function-catalog-property version">{this.props.functionBlock.getReleaseVersion()}</div>;
        if (this.props.displayVersionsList) {
            if (versionOptions.length < 1) {
                // If no version options are available to be displayed, return nothing.
                return(<div></div>);
            }

            displayVersion = <select name="Version" title="Version" value={this.props.functionBlock.getDisplayVersion()} onClick={this.onVersionClicked} onChange={this.onVersionChanged}>{versionOptions}</select>;
        }
        else if (this.props.functionBlock.isDeleted() && ! showDeletedVersions) {
            // Return an empty div if deleted child objects should be hidden.
            return(<div></div>);
        }

        const name = this.props.functionBlock.getName();
        const isDeleted = this.props.functionBlock.isDeleted();
        const isApproved = this.props.functionBlock.isApproved();
        const parent = this.props.parent;

        const childItemStyle = (isApproved ? "child-item" : "unreleased-child-item") + " tidy-object" + (isDeleted ? " deleted-tidy-object" : "");
        const workingIcon = (this.state.showWorkingIcon ? <i className="delete-working-icon fa fa-refresh fa-spin icon"/> : "");
        const releasedIcon = (this.props.functionBlock.isReleased() ? <i className="release-icon fa fa-book icon" title="This Function Block has been released." /> : "");
        const approvedIcon = (this.props.functionBlock.isApproved() ? <i className="approved-icon fa fa-thumbs-o-up icon" title="This Function Block has been approved." /> : "");
        let removeIcon = "";
        let trashOrDeleteIcon = "";
        if (parent == null || !parent.isApproved()) {
            removeIcon = (!isDeleted && parent != null) ? <i className="fa fa-minus action-button" onClick={this.disassociateFunctionBlockFromParent} title="Remove"/> : "";

            if (this.props.functionBlock.hasApprovedParent()) {
                trashOrDeleteIcon =  <i className="fa fa-trash action-button disabled-action-button" onClick={(event) => event.stopPropagation()} title="This Function Block cannot be moved to trash; it is the child of an approved Function Catalog."/>;
            }
            else {
                trashOrDeleteIcon = isDeleted ? <i className="fa fa-remove action-button" onClick={this.deleteFunctionBlock} title="Delete"/>
                    : <i className="fa fa-trash action-button" onClick={this.onMarkAsDeletedClicked} title="Move to Trash Bin"/>;
            }
        }
        const restoreIcon = isDeleted ? <i className="fa fa-undo action-button" onClick={this.onRestoreFromTrashClicked} title="Remove from Trash Bin"/> : "";
        const approvalReviewIcon = this.props.functionBlock.getApprovalReviewId() ? <i className="fa fa-clipboard action-button" onClick={this.onApprovalReviewClicked} title="Go to the pending or approved Review for this Function Block."/> : "";
        
        return (
            <div className={childItemStyle} onClick={this.onClick}>
                <div className="child-item-title">
                    <span className="child-item-title-name" title={name}>{name}</span>
                </div>
                <div className="action-bar">
                    {workingIcon}
                    {removeIcon}
                    {trashOrDeleteIcon}
                    {restoreIcon}
                    {approvalReviewIcon}
                    {approvedIcon}
                    {releasedIcon}
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

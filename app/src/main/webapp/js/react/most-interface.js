class MostInterface extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            showMenu:           false,
            showWorkingIcon:    false
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
        this.disassociateMostInterfaceFromParent = this.disassociateMostInterfaceFromParent.bind(this);
        this.deleteMostInterface = this.deleteMostInterface.bind(this);
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
        const mostInterface = this.props.mostInterface;
        const versionsJson = mostInterface.getVersionsJson();

        for (let i in versionsJson) {
            const newMostInterfaceJson = versionsJson[i];
            let newVersion = newMostInterfaceJson.releaseVersion;
            if (!newMostInterfaceJson.isReleased) {
                newVersion += "-" + newMostInterfaceJson.id;
            }

            if (newVersion === newValue) {
                if (typeof this.props.onVersionChanged == "function") {
                    this.props.onVersionChanged(mostInterface, newMostInterfaceJson, versionsJson);
                }
                break;
            }
        }
    }

    onVersionClicked(event) {
        event.stopPropagation();
    }


    deleteMostInterface(event) {
        event.stopPropagation();
        if (typeof this.props.onDelete == "function") {
            this.setState({
                showWorkingIcon: true
            });
            const thisMostInterface = this;
            this.props.onDelete(this.props.mostInterface, function() {
                thisMostInterface.setState({
                    showWorkingIcon: false
                });
            });
        }
    }

    disassociateMostInterfaceFromParent(event) {
        event.stopPropagation();
        if (typeof this.props.onDisassociate == "function") {
            this.setState({
                showWorkingIcon: true
            });
            const thisMostInterface = this;
            this.props.onDisassociate(this.props.mostInterface, function() {
                thisMostInterface.setState({
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

        const thisMostInterface = this;
        const mostInterface = this.props.mostInterface;
        const versionsJson = mostInterface.getVersionsJson();

        this.props.onMarkAsDeleted(this.props.mostInterface, function() {
            if (thisMostInterface.props.displayVersionsList) {
                let newVersionJson = versionsJson[0];
                for (let i in versionsJson) {
                    const versionJson = versionsJson[i];
                    if (versionJson.isApproved) {
                        if (versionJson.id > newVersionJson.id)
                            newVersionJson = versionJson;
                    }
                }

                thisMostInterface.props.onVersionChanged(mostInterface, newVersionJson, versionsJson);
            }

            thisMostInterface.setState({
                showWorkingIcon: false
            });
        });
    }

    onRestoreFromTrashClicked(event) {
        event.stopPropagation();
        this.setState({
            showWorkingIcon: true
        });

        const thisMostInterface = this;
        this.props.onRestoreFromTrash(this.props.mostInterface, function() {
            thisMostInterface.setState({
                showWorkingIcon: false
            });
        });
    }

    onApprovalReviewClicked(event) {
        event.stopPropagation();
        this.props.onApprovalReviewClicked(this.props.mostInterface);
    }

    onClick() {
        if (this.state.lastMouseDown == this.lastMouseDown.parent) {
            if (typeof this.props.onClick == "function") {
                this.props.onClick(this.props.mostInterface, false);
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
        const versionsJson = this.props.mostInterface.getVersionsJson();
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

        let displayVersion = <div className="child-function-catalog-property version">{this.props.mostInterface.getReleaseVersion()}</div>;
        if (this.props.displayVersionsList) {
            if (versionOptions.length < 1) {
                // If no version options are available to be displayed, return an empty div..
                return(<div></div>);
            }

            displayVersion = <select name="Version" title="Version" value={this.props.mostInterface.getDisplayVersion()} onClick={this.onVersionClicked} onChange={this.onVersionChanged}>{versionOptions}</select>;
        }
        else if (this.props.mostInterface.isDeleted() && ! showDeletedVersions) {
            // Return an empty div if deleted child objects should be hidden.
            return(<div></div>);
        }

        const name = this.props.mostInterface.getName();
        const isDeleted = this.props.mostInterface.isDeleted();
        const isApproved = this.props.mostInterface.isApproved();
        const parent = this.props.parent;

        const childItemStyle = (this.props.mostInterface.isApproved() ? "child-item" : "unreleased-child-item") + " tidy-object" + (isDeleted ? " deleted-tidy-object" : "");
        const workingIcon = (this.state.showWorkingIcon ? <i className="delete-working-icon fa fa-refresh fa-spin icon"/> : "");
        const releasedIcon = (this.props.mostInterface.isReleased() ? <i className="release-icon fa fa-book icon" title="This Interface has been released." /> : "");
        const approvedIcon = (isApproved ? <i className="approved-icon fa fa-thumbs-o-up icon" title="This Interface has been approved." /> : "");
        let removeIcon = "";
        let trashOrDeleteIcon = "";
        if (parent == null || !parent.isApproved()) {
            removeIcon = (!isDeleted && parent != null) ? <i className="fa fa-minus action-button" onClick={this.disassociateMostInterfaceFromParent} title="Remove"/> : "";

            if (this.props.mostInterface.hasApprovedParent()) {
                trashOrDeleteIcon =  <i className="fa fa-trash action-button disabled-action-button" onClick={(event) => event.stopPropagation()} title="This Interface cannot be moved to trash; it is the child of an approved Function Block."/>;
            }
            else {
                trashOrDeleteIcon = isDeleted ? <i className="fa fa-remove action-button" onClick={this.deleteMostInterface} title="Delete"/>
                    : <i className="fa fa-trash action-button" onClick={this.onMarkAsDeletedClicked} title="Move to Trash Bin"/>;
            }
        }
        const restoreIcon = isDeleted ? <i className="fa fa-undo action-button" onClick={this.onRestoreFromTrashClicked} title="Remove from Trash Bin"/> : "";
        const approvalReviewIcon = this.props.mostInterface.getApprovalReviewId() ? <i className="fa fa-clipboard action-button" onClick={this.onApprovalReviewClicked} title="Go to the pending or approved Review for this Interface."/> : "";

        return (
            <div className={childItemStyle} onMouseDown={this.onParentMouseDown} onClick={this.onClick}>
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
                    <div className="description" onMouseDown={this.onDescriptionMouseDown} onClick={(event) => event.stopPropagation()}>
                        {this.props.mostInterface.getMostId()}
                        {(this.props.mostInterface.getDescription() ? " - " : "")}
                        {this.props.mostInterface.getDescription()}
                    </div>
                </div>
            </div>
        );
    }
}

registerClassWithGlobalScope("MostInterface", MostInterface);

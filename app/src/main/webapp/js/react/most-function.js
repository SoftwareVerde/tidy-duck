class MostFunction extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            showMenu:         false,
            showWorkingIcon:  false
        };

        this.onMenuButtonClick = this.onMenuButtonClick.bind(this);
        this.onClick = this.onClick.bind(this);
        this.deleteMostFunction = this.deleteMostFunction.bind(this);
        this.onMarkAsDeletedClicked = this.onMarkAsDeletedClicked.bind(this);
        this.onRestoreFromTrashClicked = this.onRestoreFromTrashClicked.bind(this);

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

    deleteMostFunction(event) {
        event.stopPropagation();
        if (typeof this.props.onDelete == "function") {
            this.setState({
                showWorkingIcon: true
            });
            const thisMostFunction = this;
            this.props.onDelete(this.props.mostFunction, function() {
                thisMostFunction.setState({
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

        const thisMostFunction = this;
        this.props.onMarkAsDeleted(this.props.mostFunction, function() {
            thisMostFunction.setState({
                showWorkingIcon: false
            });
        });
    }

    onRestoreFromTrashClicked(event) {
        event.stopPropagation();
        this.setState({
            showWorkingIcon: true
        });

        const thisMostFunction = this;
        this.props.onRestoreFromTrash(this.props.mostFunction, function() {
            thisMostFunction.setState({
                showWorkingIcon: false
            });
        });
    }

    onClick() {
        if (typeof this.props.onClick == "function") {
            this.props.onClick(this.props.mostFunction);
        }
    }

    render() {
        if (this.props.mostFunction.isDeleted() && (! this.props.showDeletedMostFunctions)) {
            // If no version options are available to be displayed, return nothing.
            return(<div></div>);
        }

        const author = this.props.mostFunction.getAuthor();
        const company = this.props.mostFunction.getCompany();
        const name = this.props.mostFunction.getName();
        const isDeleted = this.props.mostFunction.isDeleted();
        const childItemStyle = ((this.props.mostFunction.isApproved() && this.props.isInterfaceApproved) ? "child-item" : "unreleased-child-item") + " tidy-object" + (isDeleted ? " deleted-tidy-object" : "");

        const workingIcon = (this.state.showWorkingIcon ? <i className="delete-working-icon fa fa-refresh fa-spin icon"/> : "");
        const releasedIcon = (this.props.mostFunction.isReleased() ? <i className="release-icon fa fa-book icon" title="This Function has been released." /> : "");
        const approvedIcon = (this.props.mostFunction.isApproved() ? <i className="approved-icon fa fa-thumbs-o-up icon" title="This Function has been approved." /> : "");
        let trashOrDeleteIcon = "";
        if (!this.props.isInterfaceApproved) {
            trashOrDeleteIcon = isDeleted ? <i className="fa fa-remove action-button" onClick={this.deleteMostFunction} title="Remove"/>
                                          : <i className="fa fa-trash action-button" onClick={this.onMarkAsDeletedClicked} title="Move to Trash Bin"/>;
        }
        const restoreIcon = isDeleted ? <i className="fa fa-undo action-button" onClick={this.onRestoreFromTrashClicked} title="Remove from Trash Bin"/> : "";

        return (
            <div className={childItemStyle} onClick={this.onClick}>
                <div className="child-item-title">
                    <span className="child-item-title-name" title={name}>{name}</span>
                </div>
                <div className="action-bar">
                    {workingIcon}
                    {trashOrDeleteIcon}
                    {restoreIcon}
                    {approvedIcon}
                    {releasedIcon}
                </div>
                <div className="child-function-catalog-property version">{this.props.mostFunction.getReleaseVersion()}</div>
                <div className="description-wrapper">
                    <div className="description" onClick={(event) => event.stopPropagation()}>
                        {this.props.mostFunction.getMostId()}
                        {(this.props.mostFunction.getDescription() ? " - " : "")}
                        {this.props.mostFunction.getDescription()}
                    </div>
                </div>
            </div>
        );
    }
}

registerClassWithGlobalScope("MostFunction", MostFunction)

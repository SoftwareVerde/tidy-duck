class MostInterface extends React.Component {
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
        this.deleteMostInterface = this.deleteMostInterface.bind(this);
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

    renderMenu() {
        if (! this.state.showMenu) { return; }

        return (
            <div className="child-item-menu">
                <div className="child-item-menu-item" onClick={this.deleteMostInterface}>
                    Remove
                    <i className="fa fa-remove" />
                </div>
            </div>
        );
    }

    onClick() {
        if (typeof this.props.onClick == "function") {
            this.props.onClick(this.props.mostInterface, false);
        }
    }

    renderVersionOptions() {
        const versionOptions = [];
        const versionsJson = this.props.mostInterface.getVersionsJson();

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
        const name = this.props.mostInterface.getName();
        const childItemStyle = this.props.mostInterface.isApproved() ? "child-item" : "unreleased-child-item";

        const workingIcon = (this.state.showWorkingIcon ? <i className="delete-working-icon fa fa-refresh fa-spin"/> : "");
        const releasedIcon = (this.props.mostInterface.isReleased() ? <i className="release-icon fa fa-book" title="This Interface has been released." /> : "");
        const approvedIcon = (this.props.mostInterface.isApproved() ? <i className="approved-icon fa fa-thumbs-o-up" title="This Interface has been approved." /> : "");

        const displayVersion = this.props.displayVersionsList ? <div className="child-function-catalog-property">{this.props.mostInterface.getReleaseVersion()}</div> :
            <select name="Version" title="Version" value={this.props.mostInterface.getDisplayVersion()} onClick={this.onVersionClicked} onChange={this.onVersionChanged}>{this.renderVersionOptions()}</select>;

        return (
            <div className={childItemStyle} onClick={this.onClick}>
                <div className="child-item-title">
                    <span className="child-item-title-name" title={name}>{name}</span>
                    {workingIcon}
                    <i className="menu-button fa fa-bars" onClick={this.onMenuButtonClick} />
                    {approvedIcon}
                    {releasedIcon}
                    {this.renderMenu()}
                </div>
                <div className="child-function-catalog-property">{this.props.mostInterface.getMostId()}</div>
                <div className="child-function-catalog-property">{this.props.mostInterface.getDescription()}</div>
                {displayVersion}
            </div>
        );
    }
}

registerClassWithGlobalScope("MostInterface", MostInterface);

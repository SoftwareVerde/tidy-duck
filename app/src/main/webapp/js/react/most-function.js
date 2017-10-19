class MostFunction extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            showMenu:         false,
            showWorkingIcon:  false
        };

        this.onMenuButtonClick = this.onMenuButtonClick.bind(this);
        this.renderMenu = this.renderMenu.bind(this);
        this.onClick = this.onClick.bind(this);
        this.deleteMostFunction = this.deleteMostFunction.bind(this);

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

    onClick() {
        if (typeof this.props.onClick == "function") {
            this.props.onClick(this.props.mostFunction);
        }
    }

    renderMenu() {
        if (! this.state.showMenu) { return; }

        return (
            <div className="child-item-menu">
                <div className="child-item-menu-item" onClick={this.deleteMostFunction}>
                    Remove
                    <i className="fa fa-remove" />
                </div>
            </div>
        );
    }

    render() {
        const author = this.props.mostFunction.getAuthor();
        const company = this.props.mostFunction.getCompany();
        const name = this.props.mostFunction.getName();
        const shortName = shortenString(name, 35, false);
        const childItemStyle = (this.props.mostFunction.isApproved() && this.props.isInterfaceApproved) ? "child-item" : "unreleased-child-item";

        const workingIcon = this.state.showWorkingIcon ? <i className="delete-working-icon fa fa-refresh fa-spin"/> : "";
        const releasedIcon = this.props.mostFunction.isReleased() ? <i className="release-icon fa fa-book" title="This Function is Released" /> : "";

        return (
            <div className={childItemStyle} onClick={this.onClick}>
                <div className="child-item-title">
                    <span title={name}>{shortName}</span>
                    {workingIcon}
                    <i className="menu-button fa fa-bars" onClick={this.onMenuButtonClick} />
                    {releasedIcon}
                    {this.renderMenu()}
                </div>
                <div className="child-function-catalog-property">{this.props.mostFunction.getMostId()}</div>
                <div className="child-function-catalog-property">{this.props.mostFunction.getDescription()}</div>
                <div className="child-function-catalog-property">{this.props.mostFunction.getReleaseVersion()}</div>
                <div className="child-function-catalog-property">{(author ? author.getName() : "")}</div>
                <div className="child-function-catalog-property">{(company ? company.getName() : "")}</div>
            </div>
        );
    }
}

registerClassWithGlobalScope("MostFunction", MostFunction)
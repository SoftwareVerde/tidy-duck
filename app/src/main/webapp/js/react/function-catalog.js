class FunctionCatalog extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            showMenu: false
        };

        this.onMenuButtonClick = this.onMenuButtonClick.bind(this);
        this.renderMenu = this.renderMenu.bind(this);
        this.onClick = this.onClick.bind(this);
        this.deleteFunctionCatalog = this.deleteFunctionCatalog.bind(this);

        window.app.navigation = this;
    }

    onMenuButtonClick(event) {
        event.stopPropagation();
        const shouldShowMenu = (! this.state.showMenu);
        this.setState({
            showMenu: shouldShowMenu
        });
    }

    deleteFunctionCatalog(event) {
        event.stopPropagation();
        if (typeof this.props.onDelete == "function") {
            this.props.onDelete(this.props.functionCatalog);
        }
    }

    renderMenu() {
        if (! this.state.showMenu) { return; }

        return (
            <div className="function-catalog-menu">
                <div className="function-catalog-menu-item" onClick={this.deleteFunctionCatalog}>
                    Delete
                    <i className="fa fa-remove" />
                </div>
            </div>
        );
    }

    onClick() {
        if (typeof this.props.onClick == "function") {
            this.props.onClick(this.props.functionCatalog);
        }
    }

    render() {
        const account = this.props.functionCatalog.getAccount();
        const company = this.props.functionCatalog.getCompany();
        const name = this.props.functionCatalog.getName();

        return (
            <div className="function-catalog" onClick={this.onClick}>
                <div className="function-catalog-title">
                    {name}
                    <i className="menu-button fa fa-bars" onClick={this.onMenuButtonClick} />
                    {this.renderMenu()}
                </div>
                <div className="child-function-catalog-property">{this.props.functionCatalog.getReleaseVersion()}</div>
                <div className="child-function-catalog-property">{this.props.functionCatalog.getReleaseDate()}</div>
                <div className="child-function-catalog-property">{(account ? account.getId() : "")}</div>
                <div className="child-function-catalog-property">{(company ? company.getId() : "")}</div>
            </div>
        );
    }
}

registerClassWithGlobalScope("FunctionCatalog", FunctionCatalog);

class NavigationItem extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            showMenu: false
        };

        this.onMenuButtonClick = this.onMenuButtonClick.bind(this);
        this.renderMenu = this.renderMenu.bind(this);
        this.downloadFunctionCatalog = this.downloadFunctionCatalog.bind(this);
    }

    onMenuButtonClick() {
        const shouldShowMenu = (! this.state.showMenu);
        this.setState({
            showMenu: shouldShowMenu
        });
    }

    renderMenu() {
        if (! this.state.showMenu) { return; }

        return (
            <div className="navigation-item-menu">
                <div className="navigation-item-menu-item" onClick={this.downloadFunctionCatalog}>
                    Download MOST XML
                    <i className="fa fa-cloud-download" />
                </div>
            </div>
        );
    }

    // TODO: This functionality needs to be brought outside this class...
    downloadFunctionCatalog() {
        const functionCatalog = this.props.navigationItem;
        const functionCatalogId = functionCatalog.getId();
        exportFunctionCatalogToMost(functionCatalogId);
    }

    render() {
        const navigationItem = this.props.navigationItem;
        const navigationItemTitle = navigationItem.getName();
        return (
            <div className="navigation-item" onClick={this.props.onClick}>
                {navigationItemTitle}
                <i className="menu-button fa fa-bars" onClick={this.onMenuButtonClick} />
                {this.renderMenu()}
            </div>
        );
    }
}

(function (app) {
    app.NavigationItem = NavigationItem;
})(window.app || (window.app = { }))

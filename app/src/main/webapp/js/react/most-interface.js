class MostInterface extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            showMenu: false
        };

        this.onMenuButtonClick = this.onMenuButtonClick.bind(this);
        this.renderMenu = this.renderMenu.bind(this);
        this.onClick = this.onClick.bind(this);
        this.deleteMostInterface = this.deleteMostInterface.bind(this);

        window.app.navigation = this;
    }

    onMenuButtonClick(event) {
        event.stopPropagation();
        const shouldShowMenu = (! this.state.showMenu);
        this.setState({
            showMenu: shouldShowMenu
        });
    }

    deleteMostInterface(event) {
        event.stopPropagation();
        if (typeof this.props.onDelete == "function") {
            this.props.onDelete(this.props.mostInterface);
        }
    }

    renderMenu() {
        if (! this.state.showMenu) { return; }

        return (
            <div className="function-catalog-menu">
                <div className="function-catalog-menu-item" onClick={this.deleteMostInterface}>
                    Delete
                    <i className="fa fa-remove" />
                </div>
            </div>
        );
    }

    onClick() {
        if (typeof this.props.onClick == "function") {
            this.props.onClick(this.props.mostInterface);
        }
    }

    render() {
        const name = this.props.mostInterface.getName();
        const shortDescription = shortenString(this.props.mostInterface.getDescription(), 25);

        return (
            <div className="function-catalog" onClick={this.onClick}>
                <div className="function-catalog-title">
                    {name}
                    <i className="menu-button fa fa-bars" onClick={this.onMenuButtonClick} />
                    {this.renderMenu()}
                </div>
                <div className="child-function-catalog-property">{this.props.mostInterface.getMostId()}</div>
                <div className="child-function-catalog-property">{shortDescription}</div>
                <div className="child-function-catalog-property">{this.props.mostInterface.getVersion()}</div>
            </div>
        );
    }
}

registerClassWithGlobalScope("MostInterface", MostInterface);

class FunctionBlock extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            showMenu:           false,
            showWorkingIcon:    false
        };

        this.onMenuButtonClick = this.onMenuButtonClick.bind(this);
        this.renderMenu = this.renderMenu.bind(this);
        this.onClick = this.onClick.bind(this);
        this.deleteFunctionBlock = this.deleteFunctionBlock.bind(this);

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

    deleteFunctionBlock(event) {
        event.stopPropagation();
        if (typeof this.props.onDelete == "function") {
            this.setState({
                showWorkingIcon: true
            });
            this.props.onDelete(this.props.functionBlock);
        }
    }

    renderMenu() {
        if (! this.state.showMenu) { return; }

        return (
            <div className="function-catalog-menu">
                <div className="function-catalog-menu-item" onClick={this.deleteFunctionBlock}>
                    Delete
                    <i className="fa fa-remove" />
                </div>
            </div>
        );
    }

    onClick() {
        if (typeof this.props.onClick == "function") {
            this.props.onClick(this.props.functionBlock);
        }
    }

    render() {
        const author = this.props.functionBlock.getAuthor();
        const company = this.props.functionBlock.getCompany();
        const name = this.props.functionBlock.getName();
        const shortDescription = shortenString(this.props.functionBlock.getDescription(), 25);

        const workingIcon = this.state.showWorkingIcon ? <i className="delete-working-icon fa fa-refresh fa-spin"/> : "";
        
        return (
            <div className="function-catalog" onClick={this.onClick}>
                <div className="function-catalog-title">
                    {name}
                    {workingIcon}
                    <i className="menu-button fa fa-bars" onClick={this.onMenuButtonClick} />
                    {this.renderMenu()}
                </div>
                <div className="child-function-catalog-property">{this.props.functionBlock.getMostId()}</div>
                <div className="child-function-catalog-property">{this.props.functionBlock.getKind()}</div>
                <div className="child-function-catalog-property">{shortDescription}</div>
                <div className="child-function-catalog-property">{this.props.functionBlock.getReleaseVersion()}</div>
                <div className="child-function-catalog-property">{(author ? author.getName() : "")}</div>
                <div className="child-function-catalog-property">{(company ? company.getName() : "")}</div>
            </div>
        );
    }
}

registerClassWithGlobalScope("FunctionBlock", FunctionBlock);

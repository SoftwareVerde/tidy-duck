class NavigationItem extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            showMenu: false
        };

        this.onClick = this.onClick.bind(this);
        this.onMenuButtonClick = this.onMenuButtonClick.bind(this);
        this.renderMenu = this.renderMenu.bind(this);
        this.renderForm = this.renderForm.bind(this);
    }

    onMenuButtonClick() {
        const shouldShowMenu = (! this.state.showMenu);
        this.setState({
            showMenu: shouldShowMenu
        });
    }

    onClick() {
        const config = this.props.navigationItemConfig || new NavigationItemConfig();

        const callback = config.getOnClickCallback();
        if (typeof callback == "function") {
            callback();
        }
    }

    renderMenu() {
        const config = this.props.navigationItemConfig || new NavigationItemConfig();

        if (config.getMenuItemConfigs().length == 0) { return []; }

        const menuItemConfigs = config.getMenuItemConfigs();
        if (menuItemConfigs.length == 0) { return []; }

        const reactComponents = [];

        reactComponents.push(
            <i key="nav-item-menu" className={"menu-button fa "+ config.getIconName()} onClick={this.onMenuButtonClick} />
        );

        if (this.state.showMenu) {
            for (let i in menuItemConfigs) {
                const menuItemConfig = menuItemConfigs[i];

                reactComponents.push(
                    <div key={"navi-item-"+ i} className="navigation-item-menu">
                        <div className="navigation-item-menu-item" onClick={menuItemConfig.getOnClickCallback()}>
                            {menuItemConfig.getTitle()}
                            <i className={"fa "+ menuItemConfig.getIconName()} />
                        </div>
                    </div>
                );
            }
        }

        return reactComponents;
    }

    renderForm() {
        const config = this.props.navigationItemConfig || new NavigationItemConfig();

        const form = config.getForm();
        return form;
    }

    render() {
        const config = this.props.navigationItemConfig || new NavigationItemConfig();

        return (
            <div className="navigation-item" onClick={this.onClick}>
                {config.getTitle()}
                {this.renderMenu()}
                {this.renderForm()}
            </div>
        );
    }
}

registerClassWithGlobalScope("NavigationItem", NavigationItem);

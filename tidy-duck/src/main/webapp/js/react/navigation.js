class Navigation extends React.Component {
    constructor(props) {
        super(props);
    }

    renderNavigationItems() {
        var navigationItemComponents = [];
        var navigationItems = (this.props.navigationItems || []);
        for (var i in navigationItems) {
            navigationItemComponents.push(
                <app.NavigationItem title="Navigation Item"/>
            );
        }
        return navigationItemComponents;
    }

    render() {
        return (
            <div className="navigation-column">
                <app.NavigationRootItem title="Navigation" />
                {this.renderNavigationItems()}
            </div>
        );
    }
}

(function (app) {
    app.Navigation = Navigation;
})(window.app || (window.app = { }))

class Navigation extends React.Component {
    constructor(props) {
        super(props);
    }

    renderNavigationItems() {
        var navigationItemComponents = [];
        var navigationItems = (this.props.navigationItems || []);
        for (var i in navigationItems) {
            const navigationItemTitle = this.props.navigationItems[i];

            navigationItemComponents.push(
                <app.NavigationItem key={i} title={navigationItemTitle} />
            );
        }

        return navigationItemComponents;
    }

    render() {
        return (
            <div className="navigation-column">
                <app.NavigationRootItem title="Navigation" onClick={this.props.onRootItemClicked} />
                {this.renderNavigationItems()}
            </div>
        );
    }
}

(function (app) {
    app.Navigation = Navigation;
})(window.app || (window.app = { }))

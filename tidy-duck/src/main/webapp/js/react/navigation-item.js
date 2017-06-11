class NavigationItem extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        return (
            <div className="navigation-item" onClick={this.props.onClick}>
                {this.props.title}
            </div>
        );
    }
}

(function (app) {
    app.NavigationItem = NavigationItem;
})(window.app || (window.app = { }))

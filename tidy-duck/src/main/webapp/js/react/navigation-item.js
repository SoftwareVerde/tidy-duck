class NavigationItem extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        return (
            <div onClick={this.props.onClick}>
                {this.props.title}
            </div>
        );
    }
}

(function (app) {
    app.NavigationItem = NavigationItem;
})(window.app || (window.app = { }))

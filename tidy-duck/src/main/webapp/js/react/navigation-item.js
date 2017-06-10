class NavigationItem extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            classes: {
                "navigation-entry": true,
                "hovered": false
            },
            className: ""
        };

        this.generateClassNames = this.generateClassNames.bind(this);
        this.state.className = this.generateClassNames(this);
    }

    generateClassNames() {
        var classes = "";
        for (var className in this.state.classes) {
            var isEnabled = this.state.classes[className];
            if (isEnabled) {
                classes += className +" ";
            }
        }
        return classes;
    }

    returnToParentNavigationEntry() {
        var navigationColumn = document.getElementsByClassName("navigation-column")[0];

        ReactDOM.render(<app.FunctionCatalogForm />, document.getElementsByClassName("metadata-form")[0]);

        for(var i = 1; i < navigationColumn.childElementCount; i++){
            navigationColumn.removeChild(navigationColumn.childNodes[i]);
        }
    }

    toggleHover() {
        var shouldBeHovered = (! this.state.classes['hovered']);
        this.state.classes['hovered'] = shouldBeHovered
        this.setState({className: this.generateClassNames()});
    }

    render() {
        return <div
                className={this.state.className}
                onClick={() => this.returnToParentNavigationEntry()}
                onMouseEnter={() => this.toggleHover()}
                onMouseLeave={() => this.toggleHover()}
            >
            {this.props.name}
        </div>;
    }
}

(function (app) {
    app.NavigationItem = NavigationItem;
})(window.app || (window.app = { }))

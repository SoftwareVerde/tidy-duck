class Toolbar extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            navigationLevel:        this.props.navigationLevel,
            currentNavigationLevel: this.props.currentNavigationLevel
        };

        this.handleKeyPress = this.handleKeyPress.bind(this);
        this.renderItemCreateButton = this.renderItemCreateButton.bind(this);
        this.renderSearchButton = this.renderSearchButton.bind(this);
    }

    componentWillReceiveProps(newProperties) {
        this.setState({
            navigationLevel:        newProperties.navigationLevel,
            currentNavigationLevel: newProperties.currentNavigationLevel
        });
    }

    handleKeyPress(e) {
        if (e.keyCode == 27) {
            if (typeof this.props.onCancel == "function") {
                this.props.onCancel();
            }
        }
    }
  
    componentDidMount() {
        document.addEventListener('keydown', this.handleKeyPress);
    }

    componentWillUnmount() {
        document.removeEventListener('keydown', this.handleKeyPress);
    }

    renderItemCreateButton() {
        const navigationLevel = this.state.navigationLevel;
        const currentNavigationLevel = this.state.currentNavigationLevel;

        switch(currentNavigationLevel) {
            case navigationLevel.versions:
            case navigationLevel.functionCatalogs:
            case navigationLevel.functionBlocks:
            case navigationLevel.mostInterfaces:
                return (
                    <div className="toolbar-item create" onClick={this.props.onCreateClicked}>
                        <i className="fa fa-4 fa-plus" />
                    </div>
                );
                break;
        }
    }

    renderSearchButton() {
        const navigationLevel = this.state.navigationLevel;
        const currentNavigationLevel = this.state.currentNavigationLevel;

        switch(currentNavigationLevel) {
            case navigationLevel.functionCatalogs:
            case navigationLevel.functionBlocks:
            case navigationLevel.mostInterfaces:
                return (
                    <div className="toolbar-item search" onClick={this.props.onSearchClicked}>
                        <i className="fa fa-4 fa-search" />
                    </div>
                );
                break;
        }
    }

    render() {
        return (
            <div className="toolbar">
                {this.renderItemCreateButton()}
                {this.renderSearchButton()}
            </div>
        );
    }
}

registerClassWithGlobalScope("Toolbar", Toolbar);

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
        let shouldShowButton = false;
        let buttonTitleType = "";

        switch(currentNavigationLevel) {
            case navigationLevel.versions:
                shouldShowButton = true;
                buttonTitleType = "Function Catalog";
                break;
            case navigationLevel.functionCatalogs:
                shouldShowButton = true;
                buttonTitleType = "Function Block";
                break;
            case navigationLevel.functionBlocks:
                shouldShowButton = true;
                buttonTitleType = "Interface";
                break;
            case navigationLevel.mostInterfaces:
                shouldShowButton = true;
                buttonTitleType = "Function";
                break;
        }

        const buttonTitle = "Create New " + buttonTitleType;

        if (shouldShowButton) {
            return (
                <div className="toolbar-item create" onClick={this.props.onCreateClicked} title={buttonTitle}>
                    <i className="fa fa-4 fa-plus" />
                </div>
            );
        }
    }

    renderSearchButton() {
        const navigationLevel = this.state.navigationLevel;
        const currentNavigationLevel = this.state.currentNavigationLevel;
        let shouldShowButton = false;
        let buttonTitleType = "";

        switch(currentNavigationLevel) {
            case navigationLevel.functionCatalogs:
                shouldShowButton = true;
                buttonTitleType = "Function Blocks";
                break;
            case navigationLevel.functionBlocks:
                shouldShowButton = true;
                buttonTitleType = "Interfaces";
                break;
            case navigationLevel.mostInterfaces:
                shouldShowButton = true;
                buttonTitleType = "Functions";
                break;
        }

        const buttonTitle = "Find and Associate " + buttonTitleType;

        if (shouldShowButton) {
            return (
                <div className="toolbar-item search" onClick={this.props.onSearchClicked} title={buttonTitle}>
                    <i className="fa fa-4 fa-search" />
                </div>
            );
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

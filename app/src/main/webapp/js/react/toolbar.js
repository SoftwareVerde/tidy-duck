class Toolbar extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            navigationLevel:        this.props.navigationLevel,
            currentNavigationLevel: this.props.currentNavigationLevel
        };

        this.handleKeyPress = this.handleKeyPress.bind(this);
        this.onStereotypeEventClicked = this.onStereotypeEventClicked.bind(this);
        this.onStereotypeReadOnlyPropertyClicked = this.onStereotypeReadOnlyPropertyClicked.bind(this);
        this.onStereotypeReadOnlyPropertyWithEventClicked = this.onStereotypeReadOnlyPropertyWithEventClicked.bind(this);
        this.onStereotypeRequestResponseClicked = this.onStereotypeRequestResponseClicked.bind(this);
        this.onStereotypeCommandWithAckClicked = this.onStereotypeCommandWithAckClicked.bind(this);
        this.onStereotypePropertyWithEventClicked = this.onStereotypePropertyWithEventClicked.bind(this);
        this.renderItemCreateButton = this.renderItemCreateButton.bind(this);
        this.renderSearchButton = this.renderSearchButton.bind(this);
        this.renderAddFunctionButtons = this.renderAddFunctionButtons.bind(this);
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

    onStereotypeEventClicked() {

        if (typeof this.props.handleFunctionStereotypeClick == "function") {
            this.props.handleFunctionStereotypeClick(this.props.functionStereotypes.event);
        }
    }

    onStereotypeReadOnlyPropertyClicked() {
        if (typeof this.props.handleFunctionStereotypeClick == "function") {

            this.props.handleFunctionStereotypeClick(this.props.functionStereotypes.readOnlyProperty);
        }
    }

    onStereotypeReadOnlyPropertyWithEventClicked() {
        if (typeof this.props.handleFunctionStereotypeClick == "function") {
            this.props.handleFunctionStereotypeClick(this.props.functionStereotypes.readOnlyPropertyWithEvent);
        }
    }

    onStereotypeRequestResponseClicked() {
        if (typeof this.props.handleFunctionStereotypeClick == "function") {
            this.props.handleFunctionStereotypeClick(this.props.functionStereotypes.requestResponse);
        }
    }

    onStereotypeCommandWithAckClicked() {
        if (typeof this.props.handleFunctionStereotypeClick == "function") {
            this.props.handleFunctionStereotypeClick(this.props.functionStereotypes.commandWithAck);
        }
    }

    onStereotypePropertyWithEventClicked() {
        if (typeof this.props.handleFunctionStereotypeClick == "function") {
            this.props.handleFunctionStereotypeClick(this.props.functionStereotypes.propertyWithEvent);
        }
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


    renderAddFunctionButtons() {
        const navigationLevel = this.state.navigationLevel;
        const currentNavigationLevel = this.state.currentNavigationLevel;

        if (currentNavigationLevel === navigationLevel.mostInterfaces) {
            const reactComponents = [];
            reactComponents.push(<div key="event" className="toolbar-item event" onClick={this.onStereotypeEventClicked} title="Event">E</div>);
            reactComponents.push(<div key="readOnlyProperty" className="toolbar-item readOnlyProperty" onClick={this.onStereotypeReadOnlyPropertyClicked} title="ReadOnlyProperty">ROP</div>);
            reactComponents.push(<div key="readOnlyPropertyWithEvent" className="toolbar-item readOnlyPropertyWithEvent" onClick={this.onStereotypeReadOnlyPropertyWithEventClicked} title="ReadOnlyPropertyWithEvent">ROPwE</div>);
            reactComponents.push(<div key="propertyWithEvent" className="toolbar-item propertyWithEvent" onClick={this.onStereotypePropertyWithEventClicked} title="PropertyWithEvent">PwE</div>);
            reactComponents.push(<div key="commandWithAck" className="toolbar-item commandWithAck" onClick={this.onStereotypeCommandWithAckClicked} title="CommandWithAck">CwA</div>);
            reactComponents.push(<div key="requestResponse" className="toolbar-item requestResponse" onClick={this.onStereotypeRequestResponseClicked} title="Request/Response">R/R</div>);



            return reactComponents;
        }
    }

    render() {
        return (
            <div className="toolbar">
                {this.renderItemCreateButton()}
                {this.renderSearchButton()}
                {this.renderAddFunctionButtons()}
            </div>
        );
    }
}

registerClassWithGlobalScope("Toolbar", Toolbar);

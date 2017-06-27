class MostInterfaceSearchForm extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            mostInterfaces: this.props.mostInterfaces,
            functionBlock:  this.props.functionBlock,
            searchString:   ""
        };

        this.onSearchFieldChanged = this.onSearchFieldChanged.bind(this);
    }

    componentWillReceiveProps(newProperties) {
        this.setState({
            mostInterfaces: newProperties.mostInterfaces,
            functionBlock: newProperties.functionBlock,
        });
    }

    onSearchFieldChanged(newValue) {
        this.setState({
           searchString : newValue
        });

        if (typeof this.props.onUpdate == "function") {
            this.props.onUpdate();
        }
    }

    render() {
        const reactComponents = [];

        reactComponents.push(<app.InputField key="most-interface-search" id="most-interface-search" name="search" type="text" label="Search" onChange={this.onSearchFieldChanged} />);

        //Populate search results as React components.
        // TODO: create new React element for search results, using MostInterfaces as a placeholder for debugging search results.
        const mostInterfaces = this.state.mostInterfaces;
        for(let i in mostInterfaces) {
            const mostInterface = mostInterfaces[i];
            const interfaceKey = "Interface" + i;
            reactComponents.push(<app.MostInterface key={interfaceKey} mostInterface={mostInterface} />);
        }

        return reactComponents;
        // TODO wire up onUpdate callback to App.js function for searching interfaces.
    }

}

registerClassWithGlobalScope("MostInterfaceSearchForm", MostInterfaceSearchForm);
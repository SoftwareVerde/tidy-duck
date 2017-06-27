class MostInterfaceSearchForm extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            showTitle:      this.props.showTitle,
            mostInterfaces: (this.props.mostInterfaces || []),
            functionBlock:  this.props.functionBlock,
            searchString:   ""
        };

        this.onSearchFieldChanged = this.onSearchFieldChanged.bind(this);
        this.renderFormTitle = this.renderFormTitle.bind(this);
    }

    componentWillReceiveProps(newProperties) {
        this.setState({
            showTitle:      newProperties.showTitle,
            mostInterfaces: newProperties.mostInterfaces,
            functionBlock: newProperties.functionBlock,
        });
    }

    onSearchFieldChanged(newValue) {
        this.setState({
           searchString : newValue
        });

        if (typeof this.props.onUpdate == "function") {
            this.props.onUpdate(newValue);
        }
    }

    renderFormTitle() {
        if (! this.state.showTitle) {
            return null;
        }

        return (<div className="metadata-form-title">Search Interfaces</div>);
    }

    render() {
        const reactComponents = [];

        reactComponents.push(<app.InputField key="most-interface-search" id="most-interface-search" name="search" type="text" label="Search" value={this.state.searchString} readOnly={false} onChange={this.onSearchFieldChanged} />);

        //Populate search results as React components.
        // TODO: create new React element for search results, using MostInterfaces as a placeholder for debugging search results.
        const mostInterfaces = this.state.mostInterfaces;
        for(let i in mostInterfaces) {
            const mostInterface = mostInterfaces[i];
            const interfaceKey = "Interface" + i;
            reactComponents.push(<app.MostInterfaceSearchResult key={interfaceKey} mostInterface={mostInterface} />);
            //reactComponents.push(<app.MostInterface key={interfaceKey} mostInterface={mostInterface} />);
        }

        return (
            <div className="metadata-form">
                {this.renderFormTitle()}
                {reactComponents}
            </div>
        );
    }

}

registerClassWithGlobalScope("MostInterfaceSearchForm", MostInterfaceSearchForm);
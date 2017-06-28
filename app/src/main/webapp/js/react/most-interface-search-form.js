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

        return (<div className="search-form-title">Search Interfaces</div>);
    }

    render() {
        const reactComponents = [];
        reactComponents.push();

        //Populate search results as React components.
        const mostInterfaces = this.state.mostInterfaces;
        for(let i in mostInterfaces) {
            const mostInterface = mostInterfaces[i];
            const interfaceKey = "Interface" + i;
            reactComponents.push(<app.MostInterfaceSearchResult key={interfaceKey} mostInterface={mostInterface} />);
        }

        return (
            <div className="search-form">
                {this.renderFormTitle()}
                <app.SearchBar id="most-interface-search" name="search" type="text" label="Search" value={this.state.searchString} readOnly={false} onChange={this.onSearchFieldChanged}/>
                <div className="search-result-form">{reactComponents}</div>

            </div>
        );
    }

}

registerClassWithGlobalScope("MostInterfaceSearchForm", MostInterfaceSearchForm);
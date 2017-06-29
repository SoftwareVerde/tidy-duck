class SearchForm extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            formTitle:              this.props.formTitle,
            navigationLevel:        this.props.navigationLevel,
            currentNavigationLevel: this.props.currentNavigationLevel,
            showTitle:              this.props.showTitle,
            searchResults:          (this.props.searchResults || []),
            selectedItem:           this.props.selectedItem,
            searchString:           ""
        };

        this.onSearchFieldChanged = this.onSearchFieldChanged.bind(this);
        this.renderFormTitle = this.renderFormTitle.bind(this);
        this.renderSearchResultLabels = this.renderSearchResultLabels.bind(this);
    }

    componentWillReceiveProps(newProperties) {
        this.setState({
            formTitle:              newProperties.formTitle,
            showTitle:              newProperties.showTitle,
            navigationLevel:        newProperties.navigationLevel,
            currentNavigationLevel: newProperties.currentNavigationLevel,
            searchResults:          newProperties.searchResults,
            selectedItem:           newProperties.selectedItem,
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

        return (<div className="search-form-title">{this.state.formTitle}</div>);
    }

    renderSearchResultLabels() {
        const navigationLevel = this.state.navigationLevel;
        const currentNavigationLevel = this.state.currentNavigationLevel;

        switch(currentNavigationLevel) {
            case navigationLevel.functionCatalogs:
                return (
                    <div className="search-result-labels">
                        <span className="search-result-property">{"Name"}</span>
                        <span className="search-result-property-short">{"ID"}</span>
                        <span className="search-result-property-short">{"Kind"}</span>
                        <span className="search-result-property">{"Description"}</span>
                        <span className="search-result-property-short">{"Release"}</span>
                        <span className="search-result-property-short">{"Access"}</span>
                    </div>
                );
                break;
            case navigationLevel.functionBlocks:
                return (
                    <div className="search-result-labels">
                        <span className="search-result-property">{"Name"}</span>
                        <span className="search-result-property-short">{"ID"}</span>
                        <span className="search-result-property">{"Description"}</span>
                        <span className="search-result-property-short">{"Version"}</span>
                    </div>
                );
                break;
            // TODO: add Function Block labels.
        }

    }

    render() {
        const reactComponents = [];
        const navigationLevel = this.state.navigationLevel;
        const currentNavigationLevel = this.state.currentNavigationLevel;
        const searchResults = this.state.searchResults;
        const selectedItem = this.state.selectedItem;

        //Populate search results as React components.
        for(let i in searchResults) {
            const searchResult = searchResults[i];
            const searchResultKey = "search-result" + i;
            reactComponents.push(<app.SearchResult key={searchResultKey} selectedItem={selectedItem} searchResult={searchResult} onPlusButtonClick={this.props.onPlusButtonClick} navigationLevel={navigationLevel} currentNavigationLevel={currentNavigationLevel}/>);
        }

        return (
            <div className="search-form">
                {this.renderFormTitle()}
                <app.SearchBar id="search-bar" name="search" type="text" label="Search" value={this.state.searchString} readOnly={false} onChange={this.onSearchFieldChanged}/>
                {this.renderSearchResultLabels()}
                <div className="search-result-form">{reactComponents}</div>
            </div>
        );
    }

}

registerClassWithGlobalScope("SearchForm", SearchForm);
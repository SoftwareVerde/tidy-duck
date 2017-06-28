class SearchResult extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            navigationLevel:        this.props.navigationLevel,
            currentNavigationLevel: this.props.currentNavigationLevel,
            selectedItem:           this.props.selectedItem,
            searchResult:           this.props.searchResult
        };

        this.onPlusButtonClick = this.onPlusButtonClick.bind(this);
    }

    onPlusButtonClick() {
        if (typeof this.props.onPlusButtonClick == "function") {
            this.props.onPlusButtonClick(this.state.searchResult, this.state.selectedItem);
        }
    }

    render() {
        const searchResult = this.state.searchResult;
        const navigationLevel = this.state.navigationLevel;
        const currentNavigationLevel = this.state.currentNavigationLevel;
        var shortDescription = "";

        switch (currentNavigationLevel) {
            case navigationLevel.functionCatalogs:
                shortDescription = shortenString(searchResult.getDescription(), 25);
                return (
                    <div className="search-result">
                        <div className="search-result-property">{searchResult.getName()}</div>
                        <div className="search-result-property-short">{searchResult.getMostId()}</div>
                        <div className="search-result-property-short">{searchResult.getKind()}</div>
                        <div className="search-result-property">{shortDescription}</div>
                        <div className="search-result-property-short">{searchResult.getReleaseVersion()}</div>
                        <div className="search-result-property-short">{searchResult.getAccess()}</div>
                        <i className="fa fa-plus-square fa-3x" onClick={this.onPlusButtonClick}/>
                    </div>
                );
                break;
            case navigationLevel.functionBlocks:
                shortDescription = shortenString(searchResult.getDescription(), 25);
                return (
                    <div className="search-result">
                        <div className="search-result-property">{searchResult.getName()}</div>
                        <div className="search-result-property-short">{searchResult.getMostId()}</div>
                        <div className="search-result-property">{shortDescription}</div>
                        <div className="search-result-property-short">{searchResult.getVersion()}</div>
                        <i className="fa fa-plus-square fa-3x" onClick={this.onPlusButtonClick}/>
                    </div>
                );
                break;
        }
    }
}

registerClassWithGlobalScope("SearchResult", SearchResult);
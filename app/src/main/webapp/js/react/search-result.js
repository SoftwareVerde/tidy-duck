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
        if (typeof this.props.onClick == "function") {
            this.props.onClick(this.props.functionCatalog);
        }
    }

    render() {
        const searchResult = this.state.searchResult;
        const navigationLevel = this.state.navigationLevel;
        const currentNavigationLevel = this.state.currentNavigationLevel;

        switch (currentNavigationLevel) {
            case navigationLevel.functionBlocks:
                const shortDescription = shortenString(searchResult.getDescription(), 25);
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

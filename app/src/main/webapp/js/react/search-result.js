class SearchResult extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            navigationLevel:        this.props.navigationLevel,
            currentNavigationLevel: this.props.currentNavigationLevel,
            selectedItem:           this.props.selectedItem,
            searchResult:           this.props.searchResult,
            showWorkingIcon:        false
        };

        this.onPlusButtonClick = this.onPlusButtonClick.bind(this);
        this.renderIcon = this.renderIcon.bind(this);
    }

    componentWillReceiveProps(newProperties) {
        this.state = {
            navigationLevel:        newProperties.navigationLevel,
            currentNavigationLevel: newProperties.currentNavigationLevel,
            selectedItem:           newProperties.selectedItem,
            searchResult:           newProperties.searchResult,
            showWorkingIcon:        false
        };
    }

    onPlusButtonClick() {
        this.setState({
            showWorkingIcon: true
        });
        if (typeof this.props.onPlusButtonClick == "function") {
            this.props.onPlusButtonClick(this.state.searchResult, this.state.selectedItem);
        }
    }

    renderIcon() {
        if (this.state.showWorkingIcon) {
            return (
                <i className="assign-button fa fa-refresh fa-spin fa-3x" onClick={this.onPlusButtonClick}/>
            );
        } else {
            return (
                <i className="assign-button fa fa-plus-square fa-3x" onClick={this.onPlusButtonClick}/>
            );
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
                        {this.renderIcon()}
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
                        {this.renderIcon()}
                    </div>
                );
                break;
        }
    }
}

registerClassWithGlobalScope("SearchResult", SearchResult);

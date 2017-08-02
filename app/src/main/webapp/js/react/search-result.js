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
        this.onVersionChanged = this.onVersionChanged.bind(this);
        this.renderVersionOptions = this.renderVersionOptions.bind(this);
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

    onVersionChanged(event) {
        const newValue = event.target.value;
        const searchResult = this.props.searchResult;
        const versionsJson = searchResult.getVersionsJson();


        for (let i in versionsJson) {
            const newSearchResultJson = versionsJson[i];
            let newVersion = newSearchResultJson.releaseVersion;
            if (!newSearchResultJson.isReleased) {
                newVersion += "-" + newSearchResultJson.id;
            }

            if (newVersion === newValue) {
                if (typeof this.props.onVersionChanged == "function") {
                    this.props.onVersionChanged(searchResult, newSearchResultJson, versionsJson);
                }
                break;
            }
        }
    }

    renderVersionOptions() {
        const versionOptions = [];
        const versionsJson = this.props.searchResult.getVersionsJson();

        for (let i in versionsJson) {
            let optionName = versionsJson[i].releaseVersion;
            if (!versionsJson[i].isReleased) {
                optionName += "-" + versionsJson[i].id;
            }
            versionOptions.push(<option key={optionName + i} value={optionName}>{optionName}</option>);
        }

        return versionOptions;
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
                        <div className="search-result-property-short">
                            <select name={"Version"} value={searchResult.getDisplayVersion()} onChange={this.onVersionChanged}>{this.renderVersionOptions()}</select>
                        </div>
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
                        <div className="search-result-property-short">
                            <select name={"Version"} value={searchResult.getDisplayVersion()} onChange={this.onVersionChanged}>{this.renderVersionOptions()}</select>
                        </div>
                        {this.renderIcon()}
                    </div>
                );
                break;
        }
    }
}

registerClassWithGlobalScope("SearchResult", SearchResult);

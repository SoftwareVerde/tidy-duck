class MostInterfaceSearchResult extends React.Component {
    constructor(props) {
        super(props);
    }

    // TODO: functions for adding result to currently selected Function Block.
    onPlusButtonClick() {

    }

    render() {
        const mostInterface = this.props.mostInterface;
        const shortDescription = shortenString(mostInterface.getDescription(), 25);
        return (
            <div className="search-result">
                <div className="search-result-property">{mostInterface.getName()}</div>
                <div className="search-result-property-short">{mostInterface.getMostId()}</div>
                <div className="search-result-property">{shortDescription}</div>
                <div className="search-result-property-short">{mostInterface.getVersion()}</div>
                <i className="fa fa-plus-square fa-3x" onClick={this.onPlusButtonClick}/>
            </div>
        );
    }
}

registerClassWithGlobalScope("MostInterfaceSearchResult", MostInterfaceSearchResult);

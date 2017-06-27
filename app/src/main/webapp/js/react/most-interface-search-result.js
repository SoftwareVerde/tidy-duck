class MostInterfaceSearchResult extends React.Component {
    constructor(props) {
        super(props);
    }

    // TODO: functions for adding result to currently selected Function Block.

    render() {
        const mostInterface = this.props.mostInterface;
        const shortDescription = shortenString(mostInterface.getDescription(), 25);
        return (
            <div className="search-result">
                <div className="search-result-property">{mostInterface.getName()}</div>
                <div className="search-result-property">{mostInterface.getMostId()}</div>
                <div className="search-result-property">{shortDescription}</div>
                <div className="search-result-property">{mostInterface.getVersion()}</div>
            </div>
        );
    }
}

registerClassWithGlobalScope("MostInterfaceSearchResult", MostInterfaceSearchResult);

class ReleasePage extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            showLoadingReleaseItemsIcon:    true,
            showSaveAnimation:              false
        };

        this.onRelease = this.onRelease.bind(this);
        this.renderReleaseItems = this.renderReleaseItems.bind(this);
        this.renderSaveButton = this.renderSaveButton.bind(this);
    }

    componentWillReceiveProps(newProps) {

    }

    onRelease() {
        if (typeof this.props.onRelease == "function") {
            // TODO: provide arguments
            this.props.onRelease();
        }
    }

    renderReleaseItems() {
        if (this.state.showLoadingReleaseItemsIcon) {
            return (
                <div className="center">
                    <i className="fa fa-4x fa-spin fa-refresh"></i>
                </div>
            );
        }

        // TODO: display release items
    }

    renderSaveButton() {
        if (this.state.showLoadingReleaseItemsIcon) {
            // loading, don't display save button
            return '';
        }

        if (this.state.shouldShowSaveAnimation)  {
            return(<div className="center"><div className="button submit-button" id="most-function-submit"><i className="fa fa-refresh fa-spin"></i></div></div>);
        }
        return(<div className="center"><button className="button submit-button" id="most-function-submit" onClick={this.onSubmit}>{this.state.buttonTitle}</button></div>);
    }

    render() {
        const functionCatalog = this.props.functionCatalog;

        return (
            <div className="release-area accent primary-bg">
                <div key="release-area-title" id="release-area-title" className="secondary-bg">Releasing: {functionCatalog.getName()}</div>
                <div key="release-area-content" id="release-area-content">
                    {this.renderReleaseItems()}
                    <div key="release-button-area" id="release-button-area">
                        {this.renderSaveButton()}
                    </div>
                </div>
            </div>
        );
    }
}

registerClassWithGlobalScope("ReleasePage", ReleasePage);

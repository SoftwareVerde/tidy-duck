class Toolbar extends React.Component {
    constructor(props) {
        super(props);

        this.handleKeyPress = this.handleKeyPress.bind(this);
    }
  
    handleKeyPress(e) {
        if (e.keyCode == 27) {
            if (typeof this.props.onCancel == "function") {
                this.props.onCancel();
            }
        }
    }
  
    componentDidMount() {
        document.addEventListener('keydown', this.handleKeyPress);
    }

    componentWillUnmount() {
        document.removeEventListener('keydown', this.handleKeyPress);
    }

    render() {
        //TODO return array containing only the toolbar items that are needed at the current navigation level.
        return (
            <div className="toolbar">
                <div className="toolbar-item create" onClick={this.props.onCreateClicked}>
                    <i className="fa fa-4 fa-plus" />
                </div>
                <div className="toolbar-item search" onClick={this.props.onSearchClicked}>
                    <i className="fa fa-4 fa-search" />
                </div>
            </div>
        );
    }
}

registerClassWithGlobalScope("Toolbar", Toolbar);

class Alert extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            shouldShow: (this.props.shouldShow || false),
            title:      (this.props.title || ""),
            content:    (this.props.content || ""),
            onConfirm:  (this.props.onConfirm || null),
            width:      (this.props.width || 400),
            x:          (this.props.x || ( (window.innerWidth / 2) - 400)),
            y:          (this.props.y || 100)
        };

        this.onClick = this.onClick.bind(this);
        this.onButtonClick = this.onButtonClick.bind(this);
    }

    onClick(event) {
        event = (event || window.event);
        event.preventDefault();

        console.log(event);

        this.setState({
            x:  event.pageX,
            y:  event.pageY
        });

        return false;
    }

    onButtonClick(event) {
        if (this.props.onConfirm) {
            this.props.onConfirm();
        }
    }

    componentWillReceiveProps(newProperties) {
        this.setState({
            shouldShow: (newProperties.shouldShow || this.props.shouldShow || false),
            title:      (newProperties.title || this.props.title || ""),
            content:    (newProperties.content || this.props.content || ""),
            onConfirm:  (newProperties.onConfirm || this.props.onConfirm || null),
            width:      (newProperties.width || this.props.width || 400),
            x:          (newProperties.x || this.props.x || ( (window.innerWidth / 2) - 400)),
            y:          (newProperties.y || this.props.y || 100)
        });
    }

    render() {
        if (! this.state.shouldShow) {
            return null;
        }

        const divStyle = {
            "top":    this.state.y,
            "left":   this.state.x,
            "width":  this.state.width
        };

        return (
            <div className="alert" onClick={this.onClick} style={divStyle} >
                <div className="alert-title">{this.state.title}</div>
                <div className="alert-content">{this.state.content}</div>
                <div className="alert-button" onClick={this.onButtonClick}>Got it!</div>
            </div>
        );
    }
}

registerClassWithGlobalScope("Alert", Alert);

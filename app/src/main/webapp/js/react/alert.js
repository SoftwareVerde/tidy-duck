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
            y:          (this.props.y || 100),
            isMoving:   false,
            lastMoveTime: 0
        };

        this.onMouseDown = this.onMouseDown.bind(this);
        this.onMouseMove = this.onMouseMove.bind(this);
        this.onMouseUp = this.onMouseUp.bind(this);
        this.onMouseOut = this.onMouseOut.bind(this);
        this.onButtonClick = this.onButtonClick.bind(this);
    }

    onMouseDown(event) {
        event.preventDefault();

        this.setState({
            isMoving:   true
        });

        return true;
    }

    onMouseMove(event) {
        if (! this.state.isMoving) { return; }
        this.state.lastMoveTime = (new Date()).getTime();

        const width = this.refs.alertContainer.offsetWidth;
        const height = this.refs.alertContainer.offsetWidth;

        this.setState({
            x:  event.pageX - (width / 2),
            y:  event.pageY - (25)
        });
    }

    onMouseUp(event) {
        event.preventDefault();

        this.setState({
            isMoving:   false
        });

        return true;
    }

    onMouseOut(event) {
        event.preventDefault();

        if (this.state.isMoving) {
            const eventTime = (new Date()).getTime();
            event.persist();
            const _this = this;
            setTimeout(function() {
                if (_this.state.lastMoveTime >= eventTime) { return; }

                if (_this.state.isMoving) {
                    _this.onMouseMove(event);
                }
            }, 10);
        }

        /*
            this.setState({
                isMoving:   false
            });
        */

        return true;
    }

    onButtonClick(event) {
        if (this.props.onConfirm) {
            this.props.onConfirm();
        }

        this.setState({
            shouldShow: false
        });
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

        const hasMoved = (this.state.lastMoveTime > 0);
        if (this.state.isMoving) {
            OnMouseMove.addCallback(this.onMouseMove);
        }
        else if (hasMoved) {
            OnMouseMove.removeCallback(this.onMouseMove);
        }

        return (
            <div ref="alertContainer" className="alert" onMouseOut={this.onMouseOut} style={divStyle} >
                <div className="alert-title" onMouseUp={this.onMouseUp} onMouseDown={this.onMouseDown}>{this.state.title}</div>
                <div className="alert-content">{this.state.content}</div>
                <div className="alert-button" onClick={this.onButtonClick}>Got it!</div>
            </div>
        );
    }
}

registerClassWithGlobalScope("Alert", Alert);

class RoleToggle extends React.Component{
    constructor(props) {
        super(props);

        this.state = {
            roleItems:              (this.props.roleItems || []),
            activeRoleItem:        this.props.activeRoleItem
        };

        this.onRoleClicked = this.onRoleClicked.bind(this);
    }

    componentWillReceiveProps(newProperties) {
        this.setState({
            roleItems:          (newProperties.roleItems || []),
            activeRoleItem:    newProperties.activeRoleItem
        });
    }

    onRoleClicked(roleItem) {
        this.setState({
            activeRoleItem: roleItem
        });

        if (typeof this.props.handleClick == "function") {
            this.props.handleClick(roleItem);
        }
    }


    render() {
        const reactElements = [];
        const activeRoleItem = this.state.activeRoleItem;
        const roleItems = this.state.roleItems;

        for (let i in roleItems) {
            const roleKey = "roleItem" + i;
            const isActiveRoleItem = roleItems[i] === activeRoleItem;
            reactElements.push(<app.RoleItem key={roleKey} roleName={roleItems[i]} isActiveRoleItem={isActiveRoleItem} onClick={this.onRoleClicked}/>);
        }

        return (
            <div className="role-toggle">
                {reactElements}
            </div>
        );
    }

}


registerClassWithGlobalScope("RoleToggle", RoleToggle);
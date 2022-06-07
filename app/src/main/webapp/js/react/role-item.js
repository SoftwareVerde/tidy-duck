class RoleItem extends React.Component {
     constructor(props) {
         super(props);

         this.state = {
             isActiveRoleItem: this.props.isActiveRoleItem
         };

         this.onClick = this.onClick.bind(this);
     }

     componentWillReceiveProps(newProperties) {
         this.setState({
             isActiveRoleItem: newProperties.isActiveRoleItem
         });
     }

     onClick() {
         this.setState({
             isActiveRoleItem: true
         });

         if (typeof this.props.onClick == "function") {
             this.props.onClick(this.props.roleName);
         }
     }

     render() {
         const roleName = this.props.displayName;

         if (this.props.isActiveRoleItem) {
             return(<div className="active-role" onClick={this.onClick}>{roleName}</div>);
         }
         return (<div className="role" onClick={this.onClick}>{roleName}</div>);
     }
}

registerClassWithGlobalScope("RoleItem", RoleItem);
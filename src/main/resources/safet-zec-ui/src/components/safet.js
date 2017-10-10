import React from 'react'

class Safet extends React.Component {
    constructor() {
        super();
        this.state = {
            right: 0,
            top: 0
        }
    }

    render() {
        return (
            <div style={{position: "absolute", right: parseInt(this.state.right, 10), top: parseInt(this.state.top, 10)}}>
                <img style={{width: "200px"}} src="http://kliker.info/v2/wp-content/uploads/2016/07/SafetZ3.jpg" alt="Safet Zec"/>
            </div>
        );
    }
}

export default Safet;

import React from 'react'
import safetJpg from '../images/safet.jpg'

class Safet extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            top: props.initialTop,
            right: props.initialRight
        }
    }

    moveSafet = (currentTop, currentRight) => {
        const top_c = 100;
        const right_c = 100;
        const radius = 100;

        const currentTheta = Math.atan2(top_c - currentTop, right_c - currentRight);
        const nextTheta = currentTheta + 0.03;
        const nextTop = top_c - (Math.sin(nextTheta) * radius);
        const nextRight = right_c - (Math.cos(nextTheta) * radius);

        return {
            top: nextTop,
            right: nextRight
        };

    };

    componentWillMount() {
        setInterval(() => {
            const nextTopRight = this.moveSafet(this.state.top, this.state.right);
            this.setState(nextTopRight);
        }, 10);
    }

    render() {
        return (
            <div style={{position: "absolute", zIndex: "1", right: `${parseInt(this.state.right, 10)}px`, top: `${parseInt(this.state.top, 10)}px`}}>
                <img style={{width: "150px"}} src={safetJpg} alt="Safet Zec"/>
            </div>
        );
    }
}

export default Safet;

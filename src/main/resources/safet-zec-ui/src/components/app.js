import React from 'react'
import {Router, Route, Component} from 'jumpsuit'

import {Editor} from './editor'

export default Component(
    {
        render() {
            return (
                <div className="container">
                    <Router>
                        <Route path="/" component={Editor}/>
                        <Route path="*" component={() => <h1>404 Not Found</h1>}/>
                    </Router>
                </div>
            )
        }
    }
);

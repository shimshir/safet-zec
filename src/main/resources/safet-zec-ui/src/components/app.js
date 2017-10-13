import React from 'react'
import {Router, Route, Component} from 'jumpsuit'

import {Editor} from './editor'
import {Navbar} from "react-bootstrap";

export default Component(
    {
        render() {
            return (
                <div>
                    <Navbar className="main-navbar">
                        <Navbar.Header>
                            <Navbar.Brand>
                                Safet Zec
                            </Navbar.Brand>
                        </Navbar.Header>
                    </Navbar>
                    <div className="container content-container">
                        <Router>
                            <Route path="/" component={Editor}/>
                            <Route path="*" component={() => <h1>404 Not Found</h1>}/>
                        </Router>
                    </div>
                </div>
            )
        }
    }
);

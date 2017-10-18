import React from 'react'
import {Nav, Navbar, NavItem} from 'react-bootstrap'
import Select from 'react-select'
import {Actions} from 'jumpsuit'

const TemplateEditorNavbar = ({engine, onChangeEngine, templateName}) => {
    return (
        <Navbar className="editor-navbar">
            <Navbar.Header>
                <Navbar.Brand>
                    Template
                </Navbar.Brand>
            </Navbar.Header>
            <Nav>
                <NavItem onClick={() => Actions.openSaveTemplateModal()}>Save</NavItem>
                <NavItem onClick={() => Actions.openLoadTemplateModal()}>Load</NavItem>
            </Nav>
            <div className="template-name">{templateName}</div>
            <Nav pullRight>
                <NavItem className="engine-selector-navitem">
                    <div style={{width: '150px', marginRight: '10px'}}>
                        <Select
                            style={{cursor: 'pointer'}}
                            name="form-field-name"
                            value={engine}
                            options={
                                [
                                    {
                                        value: 'FREEMARKER',
                                        label: <div><img style={{height: '16px'}}
                                                         src='http://freemarker.org/favicon.png'
                                                         alt='freemarker'/> Freemarker</div>
                                    },
                                    {
                                        value: 'HANDLEBARS',
                                        label: <div><img style={{height: '16px'}}
                                                         src='http://handlebarsjs.com/images/favicon.png'
                                                         alt='handlebars'/> Handlebars</div>
                                    },
                                    {
                                        value: 'DUST',
                                        label: <div><img style={{height: '16px'}}
                                                         src='https://d30y9cdsu7xlg0.cloudfront.net/png/915985-200.png'
                                                         alt='dust'/> Dust</div>
                                    }
                                ]}
                            onChange={onChangeEngine}
                        />
                    </div>
                </NavItem>
            </Nav>
        </Navbar>
    );
};

export default TemplateEditorNavbar;

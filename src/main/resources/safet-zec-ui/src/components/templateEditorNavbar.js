import React from 'react'
import {Nav, Navbar, NavItem} from 'react-bootstrap'
import Select from 'react-select'
import {Actions} from 'jumpsuit'

import freemarkerIcon from '../images/freemarker-icon.png'
import handlebarsIcon from '../images/handlebars-icon.png'
import dustIcon from '../images/dust-icon.png'

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
                                        label: <div><img src={freemarkerIcon}
                                                         alt='freemarker'/> Freemarker</div>
                                    },
                                    {
                                        value: 'HANDLEBARS',
                                        label: <div><img src={handlebarsIcon}
                                                         alt='handlebars'/> Handlebars</div>
                                    },
                                    {
                                        value: 'DUST',
                                        label: <div><img src={dustIcon}
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

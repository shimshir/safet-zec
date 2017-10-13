import React from 'react'
import {Component, State, Actions} from 'jumpsuit'
import AceEditor from 'react-ace'
import {Row, Col, Button, Navbar, Nav, NavItem, NavDropdown, MenuItem, FormControl} from "react-bootstrap"
import axios from 'axios'
import Safet from './safet'
import Select from 'react-select'
import 'react-select/dist/react-select.css'

import 'brace/mode/json'
import 'brace/mode/ftl'
import 'brace/mode/plain_text'
import 'brace/theme/tomorrow_night'

const exampleData = {
    name: "Admir",
    count: 3,
    messages: [
        {
            from: "Wife",
            value: "Lets go to the cinema today"
        },
        {
            from: "Nigerian Prince",
            value: "Hello, I want to transfer 100.000 $ to your account"
        },
        {
            from: "Colleague",
            value: "Lets have lunch at 2 pm"
        }
    ]
};

const exampleFtlTemplate = "<p>Hello ${name}, you have ${count} new messages</p>\n<ul>\n<#list messages as msg>\n  <li>From: ${msg.from}, Message: ${msg.value}</li>\n</#list>\n</ul>";
const exampleHbsTemplate = "<p>Hello {{name}}, you have {{count}} new messages</p>\n<ul>\n{{~#messages}}\n\ \ <li>From: {{from}}, Message: {{value}}</li>\n{{~/messages}}\n</ul>";
const exampleDustTemplate = "<p>Hello {name}, you have {count} new messages</p>{~n}\n<ul>\n{#messages}{~n}\n{~s}{~s}<li>From: {from}, Message: {value}</li>\n{/messages}{~n}\n</ul>";

const exampleTemplateMap = {
    FREEMARKER: exampleFtlTemplate,
    HANDLEBARS: exampleHbsTemplate,
    DUST: exampleDustTemplate
};

const EditorState = State(
    {
        initial: {
            dataEditorText: "",
            templateEditorText: "",
            resultEditorText: "",
            engine: "FREEMARKER",
            dataEditorHiddenClass: '',
            templateEditorHiddenClass: '',
            resultEditorHiddenClass: '',
            dataEditorHeight: '275px',
            templateEditorHeight: '275px',
            dataEditorWidth: 6,
            templateEditorWidth: 6
        },
        setDataEditorText(state, dataEditorText) {
            return {...state, dataEditorText};
        },
        setTemplateEditorText(state, templateEditorText) {
            return {...state, templateEditorText};
        },
        setResultEditorText(state, resultEditorText) {
            return {...state, resultEditorText};
        },
        setEngine(state, engine) {
            return {...state, engine};
        },
        setDataEditorWidth(state, size) {
            return {...state, dataEditorWidth: size}
        },
        setTemplateEditorWidth(state, size) {
            return {...state, templateEditorWidth: size}
        },
        setDataEditorHeight(state, size) {
            return {...state, dataEditorHeight: size}
        },
        setTemplateEditorHeight(state, size) {
            return {...state, templateEditorHeight: size}
        },
        setDataEditorHiddenClass(state, className) {
            return {...state, dataEditorHiddenClass: className}
        },
        setTemplateEditorHiddenClass(state, className) {
            return {...state, templateEditorHiddenClass: className}
        },
        setResultEditorHiddenClass(state, className) {
            return {...state, resultEditorHiddenClass: className}
        }
    }
);

const Editor = Component(
    {
        componentWillMount() {
            const initialDataText = JSON.stringify(exampleData, null, 2);
            const initialTemplateText = exampleTemplateMap[this.props.engine];
            Actions.setDataEditorText(initialDataText);
            Actions.setTemplateEditorText(initialTemplateText);
            setTimeout(() => this.submitForRendering(initialDataText, initialTemplateText, this.props.engine), 1000);
        },
        changeData(text) {
            Actions.setDataEditorText(text);
            this.submitForRendering(text, this.props.templateEditorText, this.props.engine);
        },
        changeTemplate(text) {
            Actions.setTemplateEditorText(text);
            this.submitForRendering(this.props.dataEditorText, text, this.props.engine);
        },
        changeEngine(option) {
            const engine = !option ? null : option.value;
            Actions.setTemplateEditorText(exampleTemplateMap[engine]);
            Actions.setEngine(engine);
            this.submitForRendering(this.props.dataEditorText, exampleTemplateMap[engine], engine);
        },
        submitForRendering(dataText, templateText, engine) {
            if (engine) {
                try {
                    const dataJson = JSON.parse(dataText);
                    const url = `http://${window.location.hostname}:5151/api/render`;
                    console.log(url);
                    axios.post(url, {data: dataJson, template: {value: templateText, engine}}).then(res => {
                        Actions.setResultEditorText(res.data)
                    }).catch(error => {
                        if (error.response) {
                            Actions.setResultEditorText(error.response.data)
                        }
                    })
                } catch (err) {
                    Actions.setResultEditorText(err.stack);
                }
            }
        },
        toggleDataEditorWidth() {
            if (this.props.templateEditorHiddenClass === 'hidden') {
                Actions.setDataEditorWidth(6);
                Actions.setTemplateEditorHiddenClass('');
            } else {
                Actions.setDataEditorWidth(12);
                Actions.setTemplateEditorHiddenClass('hidden');
            }
        },
        toggleTemplateEditorWidth() {
            if (this.props.dataEditorHiddenClass === 'hidden') {
                Actions.setTemplateEditorWidth(6);
                Actions.setDataEditorHiddenClass('');
            } else {
                Actions.setTemplateEditorWidth(12);
                Actions.setDataEditorHiddenClass('hidden');
            }
        },
        toggleDataEditorHeight() {
            if (this.props.dataEditorHeight === '275px') {
                Actions.setDataEditorHeight('550px');
            } else {
                Actions.setDataEditorHeight('275px');
            }
        },
        toggleTemplateEditorHeight() {
            if (this.props.templateEditorHeight === '275px') {
                Actions.setTemplateEditorHeight('550px');
            } else {
                Actions.setTemplateEditorHeight('275px');
            }
        },
        render() {
            return (
                <div>
                    {/*<Safet initialTop={100} initialRight={0}/>*/}
                    <Row>
                        <Col xs={this.props.dataEditorWidth} className={this.props.dataEditorHiddenClass}>
                            <Navbar className="editor-navbar">
                                <Navbar.Header>
                                    <Navbar.Brand>
                                        Data
                                    </Navbar.Brand>
                                </Navbar.Header>
                            </Navbar>
                            <div style={{height: this.props.dataEditorHeight}}>
                                <div style={{height: '100%', width: '100%', display: 'inline-block'}}>
                                    <AceEditor
                                        value={this.props.dataEditorText}
                                        width="100%"
                                        height="100%"
                                        fontSize={14}
                                        mode="json"
                                        theme="tomorrow_night"
                                        onChange={this.changeData}
                                        name="data-editor"
                                        style={{zIndex: '0'}}
                                        editorProps={{$blockScrolling: true}}
                                        setOptions={{showInvisibles: true}}
                                    />
                                </div>
                                <div style={{
                                    width: '12px',
                                    display: 'inline-block',
                                    verticalAlign: 'top',
                                    height: this.props.dataEditorHeight,
                                    position: 'absolute'
                                }}>
                                    <Button id="vertical-data-button" style={{width: '100%', height: '100%', padding: 'inherit'}}
                                            onClick={this.toggleDataEditorWidth}/>
                                </div>
                            </div>
                            <Button id="horizontal-data-button" style={{width: '100%', height: '12px', padding: '0', verticalAlign: 'top'}}
                                    onClick={this.toggleDataEditorHeight}/>
                        </Col>
                        <Col xs={this.props.templateEditorWidth} className={this.props.templateEditorHiddenClass}>
                            <Navbar className="editor-navbar">
                                <Navbar.Header>
                                    <Navbar.Brand>
                                        Template
                                    </Navbar.Brand>
                                </Navbar.Header>
                                <Nav>
                                    <NavItem>Save</NavItem>
                                    <NavItem>Load</NavItem>
                                    <NavItem className="template-name-input">
                                        <FormControl
                                            type="text"
                                            value="template.xyz"
                                            onChange={x => console.log(x)}
                                        />
                                    </NavItem>
                                </Nav>
                                <Nav pullRight>
                                    <NavItem className="engine-selector-navitem">
                                        <div style={{width: '150px', marginRight: '10px'}}>
                                            <Select
                                                style={{cursor: 'pointer'}}
                                                name="form-field-name"
                                                value={this.props.engine}
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
                                                onChange={this.changeEngine}
                                            />
                                        </div>
                                    </NavItem>
                                </Nav>
                            </Navbar>
                            <div style={{height: this.props.templateEditorHeight}}>
                                <div style={{
                                    width: '12px',
                                    display: 'inline-block',
                                    verticalAlign: 'top',
                                    height: '275px',
                                    position: 'absolute',
                                    marginLeft: '-12px'
                                }}>
                                    <Button id="vertical-template-button" style={{width: '100%', height: this.props.templateEditorHeight, padding: 'inherit'}}
                                            onClick={this.toggleTemplateEditorWidth}/>
                                </div>
                                <div style={{height: '100%', width: '100%', display: 'inline-block'}}>
                                    <AceEditor
                                        value={this.props.templateEditorText}
                                        width="100%"
                                        height="100%"
                                        fontSize={14}
                                        mode="ftl"
                                        theme="tomorrow_night"
                                        onChange={this.changeTemplate}
                                        name="template-editor"
                                        style={{zIndex: '0'}}
                                        editorProps={{$blockScrolling: true}}
                                        setOptions={{showInvisibles: true}}
                                    />
                                </div>
                            </div>
                            <Button id="horizontal-template-button" style={{width: '100%', height: '12px', padding: '0', verticalAlign: 'top'}}
                                    onClick={this.toggleTemplateEditorHeight}/>
                        </Col>
                    </Row>
                    <Row>
                        <Col xs={12}>
                            <Navbar className="editor-navbar">
                                <Navbar.Header>
                                    <Navbar.Brand>
                                        Result
                                    </Navbar.Brand>
                                </Navbar.Header>
                            </Navbar>
                            <AceEditor
                                value={this.props.resultEditorText}
                                width="100%"
                                height="275px"
                                fontSize={14}
                                mode="plain_text"
                                theme="tomorrow_night"
                                readOnly={true}
                                name="result-editor"
                                style={{zIndex: '0'}}
                                editorProps={{$blockScrolling: true}}
                                setOptions={{showInvisibles: true}}
                            />
                        </Col>
                    </Row>
                </div>
            )
        }
    }, state => state.editor
);

export {Editor, EditorState};
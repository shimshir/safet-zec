import React from 'react'
import {Component, State, Actions} from 'jumpsuit'
import AceEditor from 'react-ace'
import {Row, Col, Navbar, Button} from 'react-bootstrap'
import axios from 'axios'
import Safet from './safet'

import 'react-select/dist/react-select.css'

import 'brace/mode/json'
import 'brace/mode/ftl'
import 'brace/mode/plain_text'
import 'brace/theme/tomorrow_night'
import DataEditor from "./dataEditor"
import TemplateEditor from "./templateEditor";

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
            templateName: null,
            resultEditorText: "",
            engine: "FREEMARKER",
            dataEditorHiddenClass: '',
            templateEditorHiddenClass: '',
            resultEditorHiddenClass: '',
            dataEditorHeight: '275px',
            templateEditorHeight: '275px',
            resultEditorHeight: '275px',
            dataEditorWidth: 6,
            templateEditorWidth: 6
        },
        setDataEditorText(state, dataEditorText) {
            submitForRendering(dataEditorText, state.templateEditorText, state.engine);
            return {...state, dataEditorText};
        },
        setTemplateEditorText(state, templateEditorText) {
            submitForRendering(state.dataEditorText, templateEditorText, state.engine);
            return {...state, templateEditorText};
        },
        setTemplateName(state, templateName) {
            return {...state, templateName};
        },
        setResultEditorText(state, resultEditorText) {
            console.log(resultEditorText);
            return {...state, resultEditorText};
        },
        setEngine(state, engine) {
            submitForRendering(state.dataEditorText, state.templateEditorText, engine);
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
        setResultEditorHeight(state, size) {
            return {...state, resultEditorHeight: size}
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

const submitForRendering = (dataText, templateText, engine) => {
    if (engine) {
        try {
            const dataJson = JSON.parse(dataText);
            const url = `http://${window.location.hostname}:5151/api/render`;
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
};

const Editor = Component(
    {
        componentWillMount() {
            const initialDataText = JSON.stringify(exampleData, null, 2);
            const initialTemplateText = exampleTemplateMap[this.props.engine];
            Actions.setDataEditorText(initialDataText);
            Actions.setTemplateEditorText(initialTemplateText);
        },
        changeEngine(option) {
            const engine = !option ? null : option.value;
            Actions.setEngine(engine);
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
        toggleResultEditorHeight() {
            if (this.props.resultEditorHeight === '275px') {
                Actions.setResultEditorHeight('550px');
            } else {
                Actions.setResultEditorHeight('275px');
            }
        },
        render() {
            return (
                <div>
                    {/*<Safet initialTop={100} initialRight={0}/>*/}
                    <Row>
                        <DataEditor
                            dataEditorText={this.props.dataEditorText}
                            onChangeDataText={text => Actions.setDataEditorText(text)}
                            dataEditorWidth={this.props.dataEditorWidth}
                            dataEditorHeight={this.props.dataEditorHeight}
                            dataEditorHiddenClass={this.props.dataEditorHiddenClass}
                            toggleDataEditorWidth={this.toggleDataEditorWidth}
                            toggleDataEditorHeight={this.toggleDataEditorHeight}
                        />
                        <TemplateEditor
                            templateEditorText={this.props.templateEditorText}
                            templateName={this.props.templateName}
                            onChangeTemplateText={text => Actions.setTemplateEditorText(text)}
                            engine={this.props.engine}
                            onChangeEngine={this.changeEngine}
                            templateEditorWidth={this.props.templateEditorWidth}
                            templateEditorHeight={this.props.templateEditorHeight}
                            templateEditorHiddenClass={this.props.templateEditorHiddenClass}
                            toggleTemplateEditorWidth={this.toggleTemplateEditorWidth}
                            toggleTemplateEditorHeight={this.toggleTemplateEditorHeight}
                        />
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
                                height={this.props.resultEditorHeight}
                                fontSize={14}
                                mode="plain_text"
                                theme="tomorrow_night"
                                readOnly={true}
                                name="result-editor"
                                style={{zIndex: '0'}}
                                editorProps={{$blockScrolling: true}}
                                setOptions={{showInvisibles: true}}
                            />
                            <Button id="horizontal-result-button" style={{width: '100%', height: '12px', padding: '0', verticalAlign: 'top'}}
                                    onClick={this.toggleResultEditorHeight}/>
                        </Col>
                    </Row>
                </div>
            )
        }
    }, state => state.editor
);

export {Editor, EditorState};
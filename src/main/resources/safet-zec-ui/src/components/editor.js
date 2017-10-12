import React from 'react'
import {Component, State, Actions} from 'jumpsuit'
import AceEditor from 'react-ace'
import {Row, Col} from "react-bootstrap"
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
            engine: "FREEMARKER"
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
        render() {
            return (
                <div>
                    {/*<Safet initialTop={100} initialRight={0}/>*/}
                    <h1 style={{textAlign: "center"}}>Safet Zec</h1>
                    <Row>
                        <Col xs={6}>
                            <h3>Enter your data</h3>
                            <AceEditor
                                value={this.props.dataEditorText}
                                width="100%"
                                height="275px"
                                fontSize={14}
                                mode="json"
                                theme="tomorrow_night"
                                onChange={this.changeData}
                                name="data-editor"
                                style={{zIndex: '0'}}
                                editorProps={{$blockScrolling: true}}
                                setOptions={{showInvisibles: true}}
                            />
                        </Col>
                        <Col xs={6}>
                            <h3 style={{display: "inline-block"}}>Define your template</h3>
                            <div style={{marginTop: "20px", display: 'inline-block', float: 'right', width: '180px'}}>
                                <Select
                                    style={{cursor: 'pointer'}}
                                    name="form-field-name"
                                    value={this.props.engine}
                                    options={
                                        [
                                            {value: 'FREEMARKER', label: <div><img style={{height: '16px'}} src='http://freemarker.org/favicon.png' alt='freemarker'/>  Freemarker</div>},
                                            {value: 'HANDLEBARS', label: <div><img style={{height: '16px'}} src='http://handlebarsjs.com/images/favicon.png' alt='handlebars'/>  Handlebars</div>},
                                            {value: 'DUST', label: <div><img style={{height: '16px'}} src='https://d30y9cdsu7xlg0.cloudfront.net/png/915985-200.png' alt='dust'/>  Dust</div>}
                                        ]}
                                    onChange={this.changeEngine}
                                />
                            </div>
                            <AceEditor
                                value={this.props.templateEditorText}
                                width="100%"
                                height="275px"
                                fontSize={14}
                                mode="ftl"
                                theme="tomorrow_night"
                                onChange={this.changeTemplate}
                                name="template-editor"
                                style={{zIndex: '0'}}
                                editorProps={{$blockScrolling: true}}
                                setOptions={{showInvisibles: true}}
                            />
                        </Col>
                    </Row>
                    <Row>
                        <Col xs={12}>
                            <h3>Result</h3>
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
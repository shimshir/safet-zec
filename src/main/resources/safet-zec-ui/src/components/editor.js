import React from 'react'
import {Component, State, Actions} from 'jumpsuit'
import AceEditor from 'react-ace'
import {Row, Col} from "react-bootstrap"
import axios from 'axios'
import Safet from './safet'

import 'brace/mode/json'
import 'brace/mode/ftl'
import 'brace/mode/plain_text'
import 'brace/theme/tomorrow_night'

const exampleData = {
    name : "Admir",
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

const exampleTemplate = "<p>Hello ${name}, you have ${count} new messages</p>\n<ul>\n<#list messages as msg>\n  <li>From: ${msg.from}, Message: ${msg.value}</li>\n</#list>\n</ul>";

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
        }
    }
);

const Editor = Component(
    {
        componentWillMount() {
            Actions.setDataEditorText(JSON.stringify(exampleData, null, 2));
            Actions.setTemplateEditorText(exampleTemplate);
            setTimeout(this.submitForRendering, 1000);
        },
        changeData(text) {
            Actions.setDataEditorText(text);
            this.submitForRendering();
        },
        changeTemplate(text) {
            Actions.setTemplateEditorText(text);
            this.submitForRendering();
        },
        submitForRendering() {
            const dataText = this.props.dataEditorText;
            const templateText = this.props.templateEditorText;
            const engine = this.props.engine;
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
            } catch(err) {
                console.log(err);
            }
        },
        render() {
            return (
                <div>
                    <Safet/>
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
                                editorProps={{$blockScrolling: true}}
                                setOptions={{showInvisibles: true}}
                            />
                        </Col>
                        <Col xs={6}>
                            <h3>Define your template</h3>
                            <AceEditor
                                value={this.props.templateEditorText}
                                width="100%"
                                height="275px"
                                fontSize={14}
                                mode="ftl"
                                theme="tomorrow_night"
                                onChange={this.changeTemplate}
                                name="template-editor"
                                editorProps={{$blockScrolling: true}}
                                setOptions={{showInvisibles: true}}
                            />
                        </Col>
                    </Row>
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
                        editorProps={{$blockScrolling: true}}
                        setOptions={{showInvisibles: true}}
                    />
                </div>
            )
        }
    }, state => state.editor
);

export {Editor, EditorState};
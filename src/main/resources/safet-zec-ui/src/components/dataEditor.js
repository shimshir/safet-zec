import React from 'react'
import {Button, Col, Navbar} from "react-bootstrap"
import AceEditor from 'react-ace'


const DataEditor = ({dataEditorText, onChangeDataText, dataEditorWidth, dataEditorHeight, dataEditorHiddenClass, toggleDataEditorWidth, toggleDataEditorHeight}) => {
    return (
        <Col xs={dataEditorWidth} className={dataEditorHiddenClass}>
            <Navbar className="editor-navbar">
                <Navbar.Header>
                    <Navbar.Brand>
                        Data
                    </Navbar.Brand>
                </Navbar.Header>
            </Navbar>
            <div style={{height: dataEditorHeight}}>
                <div style={{height: '100%', width: '100%', display: 'inline-block'}}>
                    <AceEditor
                        value={dataEditorText}
                        width="100%"
                        height="100%"
                        fontSize={14}
                        mode="json"
                        theme="tomorrow_night"
                        onChange={onChangeDataText}
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
                    height: dataEditorHeight,
                    position: 'absolute'
                }}>
                    <Button id="vertical-data-button" style={{width: '100%', height: '100%', padding: 'inherit'}}
                            onClick={toggleDataEditorWidth}/>
                </div>
            </div>
            <Button id="horizontal-data-button" style={{width: '100%', height: '12px', padding: '0', verticalAlign: 'top'}}
                    onClick={toggleDataEditorHeight}/>
        </Col>
    );
};

export default DataEditor;

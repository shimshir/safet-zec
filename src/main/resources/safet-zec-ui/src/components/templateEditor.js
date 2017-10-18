import React from 'react'
import {Button, Col} from "react-bootstrap"
import AceEditor from 'react-ace'
import TemplateEditorNavbar from './templateEditorNavbar'
import {SaveTemplateModal} from './saveTemplateModal'
import {LoadTemplateModal} from './loadTemplateModal'


const TemplateEditor = ({
                            templateEditorText,
                            templateName,
                            onChangeTemplateText,
                            engine,
                            onChangeEngine,
                            templateEditorWidth,
                            templateEditorHeight,
                            templateEditorHiddenClass,
                            toggleTemplateEditorWidth,
                            toggleTemplateEditorHeight
                        }) => {
    return (
        <Col xs={templateEditorWidth} className={templateEditorHiddenClass}>
            <SaveTemplateModal/>
            <LoadTemplateModal/>
            <TemplateEditorNavbar engine={engine} onChangeEngine={onChangeEngine} templateName={templateName}/>
            <div style={{height: templateEditorHeight}}>
                <div style={{
                    width: '12px',
                    display: 'inline-block',
                    verticalAlign: 'top',
                    height: '275px',
                    position: 'absolute',
                    marginLeft: '-12px'
                }}>
                    <Button id="vertical-template-button" style={{width: '100%', height: templateEditorHeight, padding: 'inherit'}}
                            onClick={toggleTemplateEditorWidth}/>
                </div>
                <div style={{height: '100%', width: '100%', display: 'inline-block'}}>
                    <AceEditor
                        value={templateEditorText}
                        width="100%"
                        height="100%"
                        fontSize={14}
                        mode="ftl"
                        theme="tomorrow_night"
                        onChange={onChangeTemplateText}
                        name="template-editor"
                        style={{zIndex: '0'}}
                        editorProps={{$blockScrolling: true}}
                        setOptions={{showInvisibles: true}}
                    />
                </div>
            </div>
            <Button id="horizontal-template-button" style={{width: '100%', height: '12px', padding: '0', verticalAlign: 'top'}}
                    onClick={toggleTemplateEditorHeight}/>
        </Col>
    );
};

export default TemplateEditor;

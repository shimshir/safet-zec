import React from 'react'
import {Component, State, Actions} from 'jumpsuit'
import Modal from 'react-modal'
import {Button, ControlLabel, Form, FormControl, FormGroup} from 'react-bootstrap'
import axios from 'axios'
import {getApiHost} from '../utils'

const modalStyles = {
    overlay: {
        backgroundColor: 'rgba(50, 50, 50, 0.75)'
    },
    content: {
        top: '50%',
        left: '50%',
        right: 'auto',
        bottom: 'auto',
        marginRight: '-50%',
        transform: 'translate(-50%, -50%)'
    }
};

const SaveTemplateModalState = State(
    {
        initial: {
            isSaveTemplateModalOpen: false
        },
        openSaveTemplateModal(state) {
            return {...state, isSaveTemplateModalOpen: true};
        },
        closeSaveTemplateModal(state) {
            return {...state, isSaveTemplateModalOpen: false};
        },
    }
);

const SaveTemplateModal = Component(
    {
        componentWillMount() {
            this.setState({saveButtonText: '...'});
        },
        onTemplateNameInputChange(e) {
            this.setState({templateNameInput: e.target.value});
            this.updateSaveButtonText(e.target.value);
        },
        saveTemplate() {
            const url = `${getApiHost()}/api/templates`;
            const templateModel = {
                name: this.state.templateNameInput,
                value: this.props.templateText,
                engine: this.props.engine
            };
            axios.post(url, templateModel).then(res => {
                Actions.setTemplateName(this.state.templateNameInput);
                Actions.closeSaveTemplateModal();
            }).catch(error => {
            })
        },
        updateSaveButtonText(templateNameInput) {
            const url = `${getApiHost()}/api/templates/${templateNameInput}`;
            axios.get(url).then(res => {
                this.setState({saveButtonText: 'Overwrite'})
            }).catch(error => {
                this.setState({saveButtonText: 'Save'})
            })
        },
        onAfterOpenModal() {
            this.setState({templateNameInput: this.props.loadedTemplateName});
            if (this.props.loadedTemplateName) {
                this.setState({saveButtonText: 'Overwrite'});
            }
        },
        render() {
            return (
                <div>
                    <Modal
                        isOpen={this.props.isSaveTemplateModalOpen}
                        onAfterOpen={() => this.onAfterOpenModal(this.props.loadedTemplateName)}
                        onRequestClose={() => Actions.closeSaveTemplateModal()}
                        style={modalStyles}>
                        <h1>Save Template</h1>
                        <hr/>
                        <form className="form-inline" onSubmit={e => {
                            e.preventDefault();
                            this.saveTemplate();
                        }}>
                            <FormGroup>
                                <ControlLabel>Name</ControlLabel>
                                {' '}
                                <FormControl type="text" value={this.state.templateNameInput} placeholder="Enter Template Name"
                                             onChange={this.onTemplateNameInputChange}/>
                            </FormGroup>
                            {' '}
                            <Button style={{minWidth: '86px'}} onClick={this.saveTemplate}>
                                {this.state.saveButtonText}
                            </Button>
                        </form>
                    </Modal>
                </div>
            );
        }
    }, state => {
        return {
            ...state.saveTemplateModal,
            loadedTemplateName: state.editor.templateName,
            templateText: state.editor.templateEditorText,
            engine: state.editor.engine
        };
    }
);

export {SaveTemplateModal, SaveTemplateModalState};

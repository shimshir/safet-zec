import React from 'react'
import {Component, State, Actions} from 'jumpsuit'
import Modal from 'react-modal'
import {FormControl, Table} from 'react-bootstrap'
import axios from 'axios'

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

const LoadTemplateModalState = State(
    {
        initial: {
            isLoadTemplateModalOpen: false,
            fetchedTemplateModels: [],
        },
        openLoadTemplateModal(state) {
            return {...state, isLoadTemplateModalOpen: true};
        },
        closeLoadTemplateModal(state) {
            return {...state, isLoadTemplateModalOpen: false};
        },
        setFetchedTemplateModels(state, fetchedTemplateModels) {
            return {...state, fetchedTemplateModels};
        }
    }
);

const LoadTemplateModal = Component(
    {
        componentWillMount() {
            this.setState({filterInput: ''})
        },
        fetchAllTemplateModels() {
            const url = `http://${window.location.hostname}:5151/api/templates`;
            axios.get(url).then(res => Actions.setFetchedTemplateModels(res.data));
        },
        loadTemplate(templateModel) {
            Actions.setTemplateEditorText(templateModel.value);
            Actions.setTemplateName(templateModel.name);
            Actions.setEngine(templateModel.engine);
            Actions.closeLoadTemplateModal();
        },
        render() {
            return (
                <div>
                    <Modal
                        isOpen={this.props.isLoadTemplateModalOpen}
                        onAfterOpen={this.fetchAllTemplateModels}
                        onRequestClose={() => Actions.closeLoadTemplateModal()}
                        style={modalStyles}>
                        <h1>Load Template</h1>
                        <hr/>
                        <FormControl style={{width: '200px'}}
                                     type="text"
                                     value={this.state.filterInput}
                                     placeholder="Filter by name"
                                     onChange={e => this.setState({filterInput: e.target.value})}/>
                        <br/>
                        <Table striped bordered hover style={{minWidth: '660px'}}>
                            <thead>
                            <tr>
                                <th>Name</th>
                                <th>Value</th>
                                <th>Engine</th>
                                <th/>
                            </tr>
                            </thead>
                            <tbody>
                            {
                                this.props.fetchedTemplateModels
                                    .filter(tm => tm.name.toLowerCase().includes(this.state.filterInput.toLowerCase()))
                                    .map(templateModel => {
                                        return (
                                            <tr key={`${templateModel.name}-row`}>
                                                <td>{templateModel.name}</td>
                                                <td className="ellipsis">{templateModel.value}</td>
                                                <td>{templateModel.engine}</td>
                                                <td><a style={{cursor: 'pointer'}} onClick={() => this.loadTemplate(templateModel)}>Load</a></td>
                                            </tr>
                                        );
                                    })
                            }
                            </tbody>
                        </Table>
                    </Modal>
                </div>
            );
        }
    }, state => {
        return {
            ...state.loadTemplateModal,
        };
    }
);

export {LoadTemplateModal, LoadTemplateModalState};

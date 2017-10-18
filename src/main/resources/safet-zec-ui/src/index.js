import React from 'react'
import {Render} from 'jumpsuit'
import App from './components/app'
import 'bootstrap/dist/css/bootstrap.css'
import './css/base.css'

import {EditorState} from './components/editor'
import {SaveTemplateModalState} from './components/saveTemplateModal'
import {LoadTemplateModalState} from './components/loadTemplateModal'

const globalState = {editor: EditorState, saveTemplateModal: SaveTemplateModalState, loadTemplateModal: LoadTemplateModalState};

Render(globalState, <App/>);

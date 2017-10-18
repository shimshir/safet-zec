import React from 'react'
import {Render} from 'jumpsuit'
import App from './components/app'
import 'bootstrap/dist/css/bootstrap.css'
import './css/base.css'

import {EditorState} from './components/editor'
import {SaveTemplateModalState} from './components/saveTemplateModal'

const globalState = {editor: EditorState, saveTemplateModal: SaveTemplateModalState};

Render(globalState, <App/>);

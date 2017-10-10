import React from 'react'
import {Render} from 'jumpsuit'
import App from './components/app'
import 'bootstrap/dist/css/bootstrap.css'

import {EditorState} from './components/editor'

const globalState = {editor: EditorState};

Render(globalState, <App/>);

import { html, PolymerElement } from '@polymer/polymer/polymer-element.js';
import '@vaadin/vaadin-ordered-layout/src/vaadin-vertical-layout.js';
import {ThemableMixin} from '@vaadin/vaadin-themable-mixin/vaadin-themable-mixin.js';

class HelloWorld extends ThemableMixin(PolymerElement) {

    static get template() {
        return html `
<style include="shared-styles">
                :host {
                    display: block;
                    height: 50px;
                }
            </style>
<div>
<h1>Bookstore</h1>
</div>
`;
    }

    static get is() {
        return 'hello-world';
    }

    static get properties() {
        return {
            // Declare your properties here.
        };
    }

    ready() {
        super.ready();
        console.log('ready');
    }

    connectedCallback() {
        super.connectedCallback();
        console.log('connectedCallback');
    }

    disconnectedCallback() {
        super.disconnectedCallback();
        console.log('disconnectedCallback');
    }

    constructor() {
        super();
        console.log('constructor');
    }
}

customElements.define(HelloWorld.is, HelloWorld);
import { css, html, LitElement, type PropertyValueMap } from "lit";
import { ThemableMixin } from "@vaadin/vaadin-themable-mixin/vaadin-themable-mixin.js";
import { customElement, property } from "lit/decorators.js";

@customElement("bookstore-title")
export class BookstoreTitle extends ThemableMixin(LitElement) {
  static get is() {
    return "bookstore-title";
  }

  static get styles() {
    return css`
      :host {
        display: block;
        height: 50px;
      }
    `;
  }

  render() {
    return html`
      <div>
        <h1 part="title">Bookstore</h1>
      </div>
    `;
  }

  firstUpdated(_changedProperties: PropertyValueMap<any> | Map<PropertyKey, unknown>) {
    super.firstUpdated(_changedProperties);
    console.log("firstUpdated");
  }

  connectedCallback() {
    super.connectedCallback();
    console.log("connectedCallback");
  }

  disconnectedCallback() {
    super.disconnectedCallback();
    console.log("disconnectedCallback");
  }
}

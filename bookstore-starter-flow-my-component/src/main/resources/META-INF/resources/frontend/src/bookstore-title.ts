import { css, html, LitElement, TemplateResult, nothing } from "lit";
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
        <h1>Bookstore</h1>
      </div>
    `;
  }

  firstUpdated() {
    super.firstUpdated();
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

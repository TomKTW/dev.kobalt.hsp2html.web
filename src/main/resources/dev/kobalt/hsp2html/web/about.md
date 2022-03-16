### Purpose

This tool converts your page from Hypnospace Outlaw to document in HTML format.

### Mechanism

The uploaded file is parsed as JSON data object. Page data from object is converted to HTML/CSS/JS equivalents. External
game resources are fetched and embedded into page. To ensure portability and simplicity, all external content is
embedded under Base64 data URLs.

### Limitations

- This converter will only use resources from given single path.
- Any custom resources may fail rendering the page.
- Embedding music from HSM format is not implemented.
- Some animations or visual effects might not be implemented properly.
- An external font is required to render all text content.
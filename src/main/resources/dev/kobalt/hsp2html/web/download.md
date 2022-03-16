### Executable

This is a simple JAR executable file that runs on Java Virtual Machine.

[Download (~3 MiB)](./hsp2html.jar)

### Requirements

- Java Runtime Environment, version 1.8
- Hypnospace Outlaw game resources
- somybmp01_7 font [(Available from dafont.com)](https://www.dafont.com/somybmp01-7.font)

### Usage

Launch executable with following command:

'java -jar hsp2html.jar --resourcePath "%RESOURCE%" --fontPath "%FONT%" --pagePath "%PAGE% > %OUTPUT%"'

Replace following values:

%RESOURCE% - Path to Hypnospace Outlaw data folder containing game resources

%FONT% - Path to the font file that will be used to render text.

%PAGE% - Path to the page file that will be converted to HTML document.

%OUTPUT% - Path to the generated HTML document.
![CI - UNIT TESTS](https://github.com/buckerslondon/sequencer-eventsourcing/workflows/CI%20-%20Run%20unit%20tests/badge.svg)

![CI - INTEGRATION TESTS](https://github.com/buckerslondon/sequencer-eventsourcing/workflows/CI%20-%20Run%20integration%20tests/badge.svg)

# sequencer

High performance java architecture that can be used to implement an event sourcing architecture with a pluggable processing interface that allows application logic to be embedded into the sequencer

To run the sequencer you need two command line properties -configtype <file|url> -config <.properties file path|url>

Only file is currently supported and a sample properties file is included within the config directory for running the necessary processes locally

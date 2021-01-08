# MSD Epicor9 to FFARS mediator
[![Java CI Badge](https://github.com/SoftmedTanzania/ffars-mediator-e9/workflows/Java%20CI%20with%20Maven/badge.svg)](https://github.com/SoftmedTanzania/ffars-mediator-e9/actions?query=workflow%3A%22Java+CI+with+Maven%22)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/8b48ed6d14ef4eb1896399fb7e29d85a)](https://www.codacy.com/gh/SoftmedTanzania/ffars-mediator-e9/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=SoftmedTanzania/ffars-mediator-e9&amp;utm_campaign=Badge_Grade)
[![Coverage Status](https://coveralls.io/repos/github/SoftmedTanzania/ffars-mediator-e9/badge.svg?branch=development)](https://coveralls.io/github/SoftmedTanzania/ffars-mediator-e9?branch=development)

An [OpenHIM](http://openhim.org/) mediator for handling  sharing of Facility Fund allocations and Expenditure from MSD Epicor9 to FFARS.

# Getting Started
Clone the repository and run `npm install`

Open up `src/main/resources/mediator.properties` and supply your OpenHIM config details and save:

```
  mediator.name=MSD-Epicor9-to-FFARS-Mediator
  # you may need to change this to 0.0.0.0 if your mediator is on another server than HIM Core
  mediator.host=localhost
  mediator.port=4000
  mediator.timeout=60000

  core.host=localhost
  core.api.port=8080
  # update your user information if required
  core.api.user=openhim-username
  core.api.password=openhim-password
```

To build and launch our mediator, run

```
  mvn install
  java -jar target/ffars-mediator-e9-0.1.0-jar-with-dependencies.jar
```


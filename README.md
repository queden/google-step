# Google Student Training in Engineering Program

This repo contains Caden's portfolio and STEP projects.

## To run

To run this project:
* First cd into the [Angular](./portfolio/src/main/angular) folder. Run `npm install` in the terminal. This will import the necessary node packages to use Angular.
* Run `npm install -g @angular/cli` to import the Angular CLI. 
* To test the Angular project on a local server, run `ng serve`. 
* To build the Angular sources to the Maven project, run `ng build`. This will create a webapp folder in [/portfolio/src/main](./portfolio/src/main) with the source files. 
* Then, cd up into the ~/portolio and run `mvn package appengine:run` to run the app on a local server, or `mvn package appengine:deploy` to deploy the app.

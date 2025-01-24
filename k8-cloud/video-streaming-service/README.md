# Video Streaming Service

## Managing Service :: PROD

In order to run this microservice in PROD environment, you should do the following (all commands should be run from the microservice catalog):

1. `npm install --omit=dev` - install production dependencies (all except dev - development)
2. `PORT=3000 node src/index.js` - run application server from console 'as is'
3. `export PORT=3000; node src/index.js` - same as above, different notation for the env variable
4. `PORT=3000 npm start` - run application by common Node.js convention, this script is set up in the file **package.json**
5. `export PORT=3000; npm start` - same as above, different env variable syntax

## Developing Microservice :: DEV

1. `npm install` - install all dependencies (production + development)
2. `PORT=3000 npm run start:dev` - run locally this microservice with node monitor that will reload the application on code changes
3. `export PORT=3000; npm run start:dev` - same as above, different env variable syntax
4. `PORT=3000 nodemon ./src/index.js` - run the microservice in DEV environment using plain instruments and module nodemon for live reloading

### Microservice with Docker

1. `docker build -t video-streaming-service --file Dockerfile .` - building docker image for this microservice
2. `docker run -d -p 3000:3000 –e PORT=3000 video-streaming-service` - run container with the microservice from previously built image

FROM node:20.17.0

WORKDIR /usr/src/app

# copy configuration
COPY package*.json ./

# install dependencies
RUN npm ci --omit=dev

# copy application itself
COPY ./src ./src
COPY ./videos ./videos

# run application
CMD npm start

//
// This code should use library 'express' version 5 (beta 3 on 09.09.2024)
//
const express = require("express");
const os = require("os");
const fs = require("fs");
const app = express();

//
// Throws an error if the PORT environment variable is missing.
//
if (!process.env.PORT) {
    throw new Error("Please specify the port number for the HTTP server with the environment variable PORT.");
}

//
// Useful constants for the application
//
const PORT = process.env.PORT;
const VERSION = 'ver. 1';
const MESSAGE = `<h1>Hello World! <br><i>(host: ${os.hostname()}, version: ${VERSION})</i></h1>`;
const HEALTH_MESSAGE = "OK"

//
// Registers a HTTP GET handler for / URI (root)
//
app.get("/", async (req, res) => {

    console.log("Requested path: [/]");

    console.log(`Returning ${MESSAGE} string.`)
    res.send(MESSAGE)
});

//
// Registers a HTTP GET handler for /health URI (health check)
//
app.get("/health", async (req, res) => {

    console.log("Requested path: [/health]");

    res.send(HEALTH_MESSAGE);
});

//
// Registers a HTTP GET route for video streaming URI
//
app.get("/video", async (req, res) => {

    console.log("Requested path: [/video]");

    const videoPath = "../videos/SampleVideo_1280x720_1mb.mp4";
    const stats = await fs.promises.stat(videoPath);

    res.writeHead(200, {
        "Content-Length": stats.size,
        "Content-Type": "video/mp4",
    });
    fs.createReadStream(videoPath).pipe(res);
});

//
// Starts the application (HTTP server) and listening to the specified port
//
app.listen(PORT, () => {
    console.log(`Microservice is listening on port ${PORT}, point your browser at http://localhost:${PORT}/video`);
})

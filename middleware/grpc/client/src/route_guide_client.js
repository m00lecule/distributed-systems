var PROTO_PATH = __dirname + "../../../protos/events.proto";

var async = require("async");
var fs = require("fs");
var parseArgs = require("minimist");
var path = require("path");
var _ = require("lodash");
var grpc = require("grpc");
var protoLoader = require("@grpc/proto-loader");
var packageDefinition = protoLoader.loadSync(PROTO_PATH, {});
var server = grpc.loadPackageDefinition(packageDefinition).server;
var client = new server.Server(
  "localhost:50051",
  grpc.credentials.createInsecure()
);

var COORD_FACTOR = 1e7;

const historyFile = "history.bin";

let subscribtionHistory = {};

const currentConnections = {};

const refreshSubscribctionHistory = (topic, timestamp) => {
  subscribtionHistory[topic] = timestamp;

  console.log(subscribtionHistory);

  fs.writeFile(
    historyFile,
    JSON.stringify(subscribtionHistory),
    "binary",
    (err) => {
      if (err) console.log(`${historyFile} doesnt exist`);
      else console.log("File saved");
    }
  );
};

const readHistory = () => {
  fs.readFile(historyFile, (err, data) => {
    if (err) {
      console.log(` ${historyFile} doesnt exist`);
      console.log(">>");
    } else {
      subscribtionHistory = JSON.parse(data);
      console.log(subscribtionHistory);
      console.log(">>");
    }
  });
};

const topicStreams = {};

const cancelStream = (topic) => {
  console.log(`Cancelled topic ${topic}`);
  console.log(">>");
  cancelStream[topic].cancel();
};

function subscribeTopic(timestamp, topic) {
  const topicObj = { topic, timestamp };

  var call = client.SubscribeTopic(topicObj);

  currentConnections[topic] = call;

  call.on("data", function (response) {
    console.log("\n\n[EVENT OCCURRED]");
    console.log(response);
    refreshSubscribctionHistory(topic, response.timestamp);
    console.log(">>");
  });

  call.on("error", function (e) {
    console.log(
      `An error has occurred and the topic stream ${topicObj.topic} has been closed`
    );
    console.log(">>");
    delete currentConnections[topic];
  });
}

function getTopics(callback) {
  function eventCallback(error, response) {
    if (error) {
      console.log("error");
      return;
    }

    console.log("\n\n[TOPICS]");
    console.log(response.topic);
  }

  client.getTopics({}, eventCallback);
}

function addEvent(name, topic) {
  function eventCallback(error, response) {
    if (error) {
      return;
    }
    console.log(response);
  }
  console.log(topic);

  const event = {
    name,
    desc: {
      organizers: [
        {
          firstname: "michal",
          lastname: "dygas",
          age: 16,
        },
        {
          firstname: "michal",
          lastname: "dygas",
          age: 16,
        },
      ],
      state: 2,
    },
    location: {
      street: "urzednicza",
      city: "torun",
      flat: 2,
      block: 1,
    },
    topic,
  };

  client.addEvent(event, eventCallback);
}
const readline = require("readline");

const rl = readline.createInterface({
  input: process.stdin,
  output: process.stdout,
});

var log = console.log;

const recursiveMain = () => {
  rl.question("Load already subscribed events? \n>> ", (answer) => {
    if (answer == "yes") {
      readHistory();
    }
    recursiveAsyncReadLine();
  });
};

var recursiveAsyncReadLine = function () {
  rl.question(">>", (answer) => {
    switch (answer) {
      case "sub":
        rl.question(">> insert name:\n", (name) => {
          rl.question(">> load timestamp?\n", (answer) => {
            let timestamp = 0;
            if (answer == "yes") {
              console.log(`${name} ${answer}`);
              timestamp = subscribtionHistory[name] || 0;
            }
            subscribeTopic(timestamp, name);
            recursiveAsyncReadLine();
          });
        });
        break;
      case "top":
        getTopics();
        break;
      case "del":
        rl.question(">> insert topic:\n", (topic) => {
          if (topic in currentConnections) {
            currentConnections[topic].cancel();
            delete currentConnections[topic];
          }
          recursiveAsyncReadLine();
        });
        break;
      case "list":
        console.log(
          "sub - subscribe event, add - add event, top - all current topics, list - all commands, del - delete subscripction"
        );
        break;
      case "add":
        rl.question(">> insert event name:\n", (name) => {
          rl.question(">> insert event topics \n", (answer) => {
            addEvent(name, answer.split(" "));
            recursiveAsyncReadLine();
          });
        });
        break;
      default:
        console.log("command not found");
    }
    recursiveAsyncReadLine();
  });
};

if (require.main === module) {
  recursiveMain();
}

exports.addEvent = addEvent;

exports.subscribeTopic = subscribeTopic;

exports.getTopics = getTopics;

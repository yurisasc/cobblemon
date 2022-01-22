const PokemonShowdown = require('pokemon-showdown');
var Net = require('net')

const port = 25567;
const battleMap = new Map();

// Use net.createServer() in your code. This is just for illustration purpose.
// Create a new TCP server.
const server = Net.createServer();
// The server listens to a socket for a client to make a connection request.
// Think of a socket as an end point.
server.listen(port, function () {
    console.log('Server listening for connection requests on socket localhost: ' + port);
});

// When a client requests a connection with the server, the server creates a new
// socket dedicated to that client.
server.on('connection', function (socket) {
    console.log('A new connection has been established.');

    // The server can also receive data from the client by reading from its socket.
    // The end of the message is the tag {EOT}
    let messageBuffer = new MessageBuffer("{EOT}")
    socket.on('data', function (chunk) {
        messageBuffer.push(chunk);

        // When is a message is fully formed from chunking, we handle the data
        while (!messageBuffer.isFinished()) {
            const message = messageBuffer.handleData()
            try {
                // Parses the request and goes over message types
                const request = JSON.parse(message);
                switch(request.RequestType) {
                    case 'StartBattle': {
                        // TODO: Check for if battle already exists with that id
                        // TODO: Check for if participants are already in another battle
                        const battleStream = new PokemonShowdown.BattleStream();
                        battleMap.set(request.RequestBattleId, battleStream);

                        // Join messages with new line
                        for(const element of request.RequestMessages) {
                            console.log(element);
                            battleStream.write(element);
                        }
    
                        // Any battle output then gets written to the socket
                        // TODO: Send the battle id with this
                        // TODO: Add a way to cancel battle earlier and close stream
                        (async () => {
                            for await (const output of battleStream) {
                                socket.write(request.RequestBattleId + "{SOT}" + output + "{EOT}")
                                console.log('Sending data out');
                                console.log('===================================');
                                console.log(output);
                                console.log('===================================');
                                console.log();
                            }
                        })();
                        break;
                    }
                    case 'SendMessage': {
                      const battleStream = battleMap.get(request.RequestBattleId)
                      for(const element of request.RequestMessages) {
                        console.log(element);
                        battleStream.write(element);
                      }
                      break;
                    }
                }
            } catch (error) {
                console.error(error);
            }
        }
    });


    // When the client requests to end the TCP connection with the server, the server
    // ends the connection.
    socket.on('end', function () {
        console.log('Closing connection with the client');
    });

    // Don't forget to catch error, for your own sake.
    socket.on('error', function (err) {
        console.log(`Error: ${err}`);
    });
});

class MessageBuffer {
    constructor(delimiter) {
      this.delimiter = delimiter
      this.buffer = ""
    }
  
    isFinished() {
      if (
        this.buffer.length === 0 ||
        this.buffer.indexOf(this.delimiter) === -1
      ) {
        return true
      }
      return false
    }
  
    push(data) {
      this.buffer += data
    }
  
    getMessage() {
      const delimiterIndex = this.buffer.indexOf(this.delimiter)
      if (delimiterIndex !== -1) {
        const message = this.buffer.slice(0, delimiterIndex)
        this.buffer = this.buffer.replace(message + this.delimiter, "")
        return message
      }
      return null
    }
  
    handleData() {
      /**
       * Try to accumulate the buffer with messages
       *
       * If the server isnt sending delimiters for some reason
       * then nothing will ever come back for these requests
       */
      const message = this.getMessage()
      return message
    }
  }
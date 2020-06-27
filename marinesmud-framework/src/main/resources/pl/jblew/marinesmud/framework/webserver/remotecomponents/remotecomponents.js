$(document).ready(function() {    
    window.channel = new Channel(function(data) {
        if(data.type === "alert") {
            alert(data.message);
        }
        else if(data.type === "ContentLoaderResponse") {
            window.contentloader.messageReceived(data.object);
        }
    });
    window.channel.connect(function() {
        window.channel.send("{{registerChannelEventClass}}");
    });        
});


/**
 * CHANNEL
 */
function Channel() {
    this.listeners = {};
}


Channel.prototype.connect = function (callback) {
    var channelThis = this;
    
    if (!window.WebSocket) {
        window.WebSocket = window.MozWebSocket;
    }
    if (window.WebSocket) {
        this.socket = new WebSocket("wss://"+window.location.host+"/websocket");
        
        this.socket.onmessage = function (event) {
            var data = JSON.parse(event.data);
            channelThis.onMessage(data);
        };
        this.socket.onopen = function (event) {
            callback();
        };
        this.socket.onclose = function (event) {
            setTimeout(function() {channelThis.connect(callback)}, 2000);
        };
        this.socket.onerror=function(event) {
            if(channelThis.isClosed()) {
                setTimeout(function() {channelThis.connect(callback)}, 2000);
            }
        }
    } else {
        alert("Your browser does not support Web Socket.");
    }
};

Channel.prototype.isConnected = function() {
    return this.socket.readyState == WebSocket.OPEN;
}

Channel.prototype.isClosed = function() {
    return this.socket.readyState == WebSocket.CLOSED || this.socket.readyState == WebSocket.CLOSING;
}

Channel.prototype.send = function(eventClass, data) {
    if(typeof data === 'undefined') data = {};
    var objectToSend = {type: eventClass, object: data};
    
    console.log(objectToSend);
    
    if(typeof this.socket !== 'undefined' && this.socket !== null) {
        return this.socket.send(JSON.stringify(objectToSend));
    }
    else console.log("Channel.socket is undefined");
}

Channel.prototype.onMessage = function(msg) {
    console.log(msg);
    if(typeof msg.type === 'undefined' || typeof msg.data === 'undefined') {
        console.log({error: "Malformed message", msg: msg});
    }
    if(typeof this.listeners[msg.type] !== 'undefined') {
        for(var i in this.listeners[msg.type]) {
            this.listeners[msg.type][i](msg.data);
        }
    }
}

Channel.prototype.on = function(type, callback) {
    if(typeof this.listeners[type] === 'undefined') {
        this.listeners[type] = [];
    }
    this.listeners[type].push(callback);
}

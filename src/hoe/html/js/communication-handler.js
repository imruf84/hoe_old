var chatMsgBox;
var chatInput;
var sendChatMsgButton;
var userNameDiv;
var gameStateDiv;
var downloadinMessages = true;
var messagesCount = 20;

function initCommunicationHandler() {
    chatMsgBox = document.getElementById('chatMsgBox');
    chatInput = document.getElementById('chatInput');
    sendChatMsgButton = document.getElementById('sendChatMsgButton');
    userNameDiv = document.getElementById('userNameDiv');
    userNameDiv.innerHTML = unescape('#!PLAYER_NAME!#');
    gameStateDiv = document.getElementById('gameStateDiv');
    chatInput.focused = false;
    chatInput.hasFocus = function () {
        return this.focused;
    };
    chatInput.onfocus = function () {
        this.focused = true;
    };
    chatInput.onblur = function () {
        this.focused = false;
    };
    sendChatMsgButton.onclick = function () {
        sendMsgFunc();
    };
    chatMsgBox.innerHTML = unescape('#!DOWNLOADING_CHAT_MESSAGES!#');

    // Press Enter to enter/send chat messages.
    document.body.addEventListener('keydown', function (e) {
        if (13 === e.keyCode) {
            if (!chatInput.hasFocus()) {
                chatInput.focus();
                return;
            }
            sendMsgFunc();
            chatInput.blur();
        }
    });

}

// Send chat message.
var sendMsgFunc = function () {
    if ('' !== chatInput.value) {
        sendToServer(JSON.stringify({a: 'cm', d: {msg: escape(chatInput.value)}}));
        chatInput.value = '';
    }
};

function isJSON(text) {
    try {
        JSON.parse(text);
        return true;
    } catch (error) {
        return false;
    }
}

// Handle received message.
var handleResponse = function (responseText) {
    if ('' !== responseText)
        toDebug(responseText);
    if ('' === responseText || !isJSON(responseText))
        return;
    // Transforming message.
    var response = JSON.parse(responseText);
    for (var i in response) {
        var o = response[i];
        switch (o['a']) {
            // GameStateChanged
            case 'gsc':
                var data = o['d'];
                var state = data['state'];
                setCurrentGameState(state);
                toDebug('Game state is changed to: ' + state);

                var scene = data['scene'];
                var currentTurn = scene['currentTurn'];

                var tileBounds = scene['tileBounds'];
                
                if (currentTurn >= 0) {
                if (!isTilesMapCreated()) {
                    var tilesX = Math.max(tileBounds[0], tileBounds[1]) - Math.min(tileBounds[0], tileBounds[1]) + 1;
                    var tilesY = Math.max(tileBounds[2], tileBounds[3]) - Math.min(tileBounds[2], tileBounds[3]) + 1;

                    initTiles(getTileSize(), tilesX, tilesY);
                }

                refreshTiles(currentTurn);
            }
                updateGameStateDiv();

                // GetSceneData
                sendToServer(JSON.stringify({a: 'gsd', d: {}}));
                break;
                // ReDirect
            case 'rd':
                toDebug('redirected to: ' + o['d']['url']);
                window.location.href = o['d']['url'];
                break;
                // ChatMessage
            case 'cm':
                var cmb = document.getElementById('chatMsgBox');
                var count = (cmb.innerHTML.match(/<br>/g) || []).length;
                if (count === messagesCount) {
                    cmb.innerHTML = cmb.innerHTML.substring(cmb.innerHTML.indexOf('<br>') + 4);
                }
                if (downloadinMessages) {
                    cmb.innerHTML = "";
                    downloadinMessages = false;
                }
                cmb.innerHTML = cmb.innerHTML + '<b><u>' + o['d']['user'] + '</u></b>: ' + unescape(o['d']['msg']) + '<br>';
                cmb.scrollTop = cmb.scrollHeight;
                break;
        }
    }
};

// Syncing scene data.
function syncData() {
    // GetChatMessages
    sendToServer(JSON.stringify({a: 'gcm', d: {mc: messagesCount}}));
    // GetGameState
    sendToServer(JSON.stringify({a: 'ggs', d: {}}));
}

// Send end turn signal.
function sendEndTurn() {
    sendToServer(JSON.stringify({a: 'et', d: {}}));
}
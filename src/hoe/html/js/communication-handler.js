var chatMsgBox;
var chatInput;
var sendChatMsgButton;
var userNameDiv;
var downloadinMessages = true;
// Egyszerre megjelenő üzenetek száma.
var messagesCount = 20;

function initCommunicationHandler() {
    chatMsgBox = document.getElementById('chatMsgBox');
    chatInput = document.getElementById('chatInput');
    sendChatMsgButton = document.getElementById('sendChatMsgButton');
    userNameDiv = document.getElementById('userNameDiv');
    userNameDiv.innerHTML = unescape('#!PLAYER_NAME!#');
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

    // Enter lenyomása az üzenet beírásához/elküldéséhez.
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

// Üzenet küldése.
var sendMsgFunc = function () {
    if ('' !== chatInput.value) {
        sendToServer(JSON.stringify({a: 'cm', d: {msg: escape(chatInput.value)}}));
        chatInput.value = '';
    }
};

// Üzenet érkezett.
function isJSON(text) {
    try {
        JSON.parse(text);
        return true;
    } catch (error) {
        return false;
    }
}

// Érkezett üzenet feldolgozása.
var handleResponse = function (responseText) {
    if ('' !== responseText)
        toDebug(responseText);
    if ('' === responseText || !isJSON(responseText))
        return;
    // Üzenet átalakítása.
    var response = JSON.parse(responseText);
    for (var i in response) {
        var o = response[i];
        switch (o['a']) {
            // Játékállapotváltozás érkezett (GameStateChanged).
            case 'gsc':
                toDebug('game state changed to: ' + o['d']['state']);
                var state = o['d']['state'];
                var gsd = document.getElementById('gameStateDiv');
                gsd.innerHTML = state;
                // Objektumok letöltése (GetSceneData).
                sendToServer(JSON.stringify({a: 'gsd', d: {}}));
                break;
                // Átirányítás érkezett (ReDirect).
            case 'rd':
                toDebug('redirected to: ' + o['d']['url']);
                window.location.href = o['d']['url'];
                break;
                // Chat üzenet érkezett (ChatMessage).
            case 'cm':
                var cmb = document.getElementById('chatMsgBox');
                var count = (cmb.innerHTML.match(/<br>/g) || []).length;
                if (count === messagesCount)
                    cmb.innerHTML = cmb.innerHTML.substring(cmb.innerHTML.indexOf('<br>') + 4);
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

// Játékadatok szinkronizálása.
function syncData() {
    // Chat üzenetek letöltése (GetChatMessages).
    sendToServer(JSON.stringify({a: 'gcm', d: {mc: messagesCount}}));
    // Játékállapot letöltése (GetGameState).
    sendToServer(JSON.stringify({a: 'ggs', d: {}}));
}
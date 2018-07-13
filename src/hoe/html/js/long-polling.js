var syncNeeded = true;
var lp = function () {
    toDebug('openResponseChannel()');
    var xhrlp = new XMLHttpRequest();
    xhrlp.open('POST', '/play', true);
    xhrlp.responseType = 'text';
    xhrlp.onreadystatechange = function (oEvent) {
        if (xhrlp.readyState === 4) {
            if (xhrlp.status === 200) {
                handleResponse(xhrlp.responseText);
            } else {
                toDebug('Error:', xhrlp.statusText);
                syncNeeded = true;
                xhrlp.abort();
            }
            lp();
        }
    };
    xhrlp.timeout = 0;

    // Ha megszakadt a kapcsolat, vagy elsőként küldünk üzenetet akkor szinkronizáljuk az adatokat.
    if (syncNeeded) {
        syncNeeded = false;
        syncData();
    }

    // Üres üzenet küldése a válaszcsatornához.
    xhrlp.send('');
};

// Üzenet küldése a szervernek.
var sendToServer = function (text) {
    toDebug('sendToServer(' + text + ');');
    var xhr = new XMLHttpRequest();
    xhr.open('POST', '/play', true);
    xhr.responseType = 'text';
    xhr.onload = function () {
        handleResponse(xhr.responseText);
    };
    xhr.send(text);
};
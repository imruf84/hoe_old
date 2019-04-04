/* global gameStateDiv */
/* global renderProgressDiv */

var currentGameState = NaN;
var turn = NaN;

function setCurrentGameState(state) {
    updateRenderProgressDiv('');
    currentGameState = state;
}

function getCurrentGameState() {
    return currentGameState;
}

function setCurrentTurn(t) {
    turn = t;
}

function getCurrentTurn() {
    return turn;
}

function updateGameStateDiv() {
    gameStateDiv.innerHTML = getCurrentGameState() + ' ' + getCurrentTurn();
}

function updateRenderProgressDiv(progress, time) {
    
    var lProgress = progress;
    
    if (lProgress !== '') {
        lProgress = (Math.round(parseFloat(progress)*100*100)/100)+'% ' + time;
        renderProgressDiv.style.visibility = 'visible';
    } else {
        renderProgressDiv.style.visibility = 'hidden';
    }
    
    renderProgressDiv.innerHTML = lProgress;
}
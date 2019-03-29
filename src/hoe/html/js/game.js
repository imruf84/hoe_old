/* global gameStateDiv */

var currentGameState = NaN;
var turn = NaN;

function setCurrentGameState(state) {
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
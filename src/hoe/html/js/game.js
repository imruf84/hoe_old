/* global gameStateDiv */

var currentGameState = NaN;

function setCurrentGameState(state) {
    currentGameState = state;
}

function getCurrentGameState() {
    return currentGameState;
}

function updateGameStateDiv() {
    gameStateDiv.innerHTML = getCurrentGameState() + ' ' + getCurrentTurn();
}
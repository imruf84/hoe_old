var debug = '#!DEBUG_ENABLED!#' === 'true' ? true : false;
function toDebug(msg) {
    if (debug) {
        console.log('DEBUG: ' + msg);
    }
}
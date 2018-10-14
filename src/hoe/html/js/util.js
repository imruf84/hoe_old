/* global chatInput */
/* global myScroll */

function isCursorInInputField() {
    return chatInput.focused;
}

function calculateScreenToWorld(screenX, screenY) {
        var x = screenX;
        var y = screenY;

        var globalX = x - myScroll.x;
        var globalY = y - myScroll.y;

        globalX /= myScroll.scale;
        globalY /= myScroll.scale;
        
        return [globalX, globalY];
    }

function isElementInViewport(el) {

    var rect = el.getBoundingClientRect();
    return (
            Math.max(rect.left, 0) < Math.min(rect.right, (getWindowSize()[0])) &&
            Math.max(rect.top, 0) < Math.min(rect.bottom, (getWindowSize()[1]))
            );

}

function isElementInViewportFully(el) {

    var rect = el.getBoundingClientRect();
    return (
            rect.top <= 0 &&
            rect.left <= 0 &&
            rect.bottom >= getWindowSize()[0] &&
            rect.right >= getWindowSize()[1]
            );

}

function getWindowSize() {
    return [window.innerWidth || document.documentElement.clientWidth, window.innerHeight || document.documentElement.clientheight];
}

function addClass(element, name) {
    var arr = element.className.split(' ');
    if (arr.indexOf(name) === -1) {
        element.className += ' ' + name;
    }
}

function removeClass(element, name) {
    element.className = element.className.replace(new RegExp('(?:^|\\s)'+ name + '(?:\\s|$)'), ' ');
}
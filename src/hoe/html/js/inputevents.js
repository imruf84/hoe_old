/* global myScroll */
/* global tileWidth */
/* global tileHeight */
/* global video */

function initKeyboardEvents() {
    window.onkeyup = function (e) {

        // Ha éppen chat üzenett írunk, akkor nem dolgozunk fel eseményeket.
        if (isCursorInInputField()) {
            return;
        }

        var key = e.keyCode ? e.keyCode : e.which;
        var isShift = e.shiftKey;
        var isCtrl = e.ctrlKey;

/*        var scrollBy = [myScroll.scale * tileWidth, myScroll.scale * tileHeight];
        if (isShift) {
            scrollBy = [getWindowSize()[0], getWindowSize()[1]];
        }
        var scrollByDuration = 500;

        var screenCenterX = getWindowSize()[0] / 2;
        var screenCenterY = getWindowSize()[1] / 2;
        var scaleBy = (myScroll.options.zoomMax - myScroll.options.zoomMin) / 2;
        if (isShift) {
            scaleBy = myScroll.options.zoomMax - myScroll.options.zoomMin;
        }
        var scaleByDuration = 1000;
*/
        switch (key) {
            // delete (logout)
            case 46:
                window.location.href = 'logout';
                break;
                // space
            case 32:
                if (video.paused)
                    video.play();
                else
                    video.pause();
                break;
                // + (zoom in)
/*            case 51:
            case 107:
                myScroll.zoom(myScroll.scale + scaleBy, screenCenterX, screenCenterY, scaleByDuration);
                break;
                // - (zoom out)
            case 109:
            case 189:
                myScroll.zoom(myScroll.scale - scaleBy, screenCenterX, screenCenterY, scaleByDuration);
                break;
            case 38:
                myScroll.scrollBy(0, scrollBy[1], scrollByDuration);
                break;
                // down
            case 40:
                myScroll.scrollBy(0, -scrollBy[1], scrollByDuration);
                break;
                // right
            case 39:
                myScroll.scrollBy(-scrollBy[0], 0, scrollByDuration);
                break;
                // left
            case 37:
                myScroll.scrollBy(scrollBy[0], 0, scrollByDuration);
                break;*/
        }
    };
    
}
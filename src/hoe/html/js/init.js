var myScroll;
var canvasVideo;
var scrollerDiv;
var video;
var tilesDiv;
var tileWidth = 500;
var tileHeight = 500;

function isCursorInInputField() {
    return chatInput.focused;
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

function initScroll() {

    scrollerDiv = document.getElementById('scroller');

    myScroll = new IScroll('#wrapper', {
        startX: 0,
        startY: 0,
        scrollY: true,
        scrollX: true,
        freeScroll: true,
        zoom: true,
        mouseWheel: true,
        wheelAction: 'zoom',
        startZoom: 1,
        zoomMin: .35,
        zoomMax: 1,
        tap: true,
        click: true,
        bounce: !true,
        useTransition: true,
        keyBindings: false
    });

    function onDblClick(evt) {
        var x = evt.pageX;
        var y = evt.pageY;

        var globalX = x - myScroll.x;
        var globalY = y - myScroll.y;

        var mouseTarget = document.getElementById('mousePointerToScroll');
        mouseTarget.style.left = globalX + 'px';
        mouseTarget.style.top = globalY + 'px';
        myScroll.scrollToElement(mouseTarget, 1000, true, true)

        globalX /= myScroll.scale;
        globalY /= myScroll.scale;

        console.log('click', 'globalX:', globalX, 'globalY:', globalY);
    }

    window.onkeyup = function (e) {

        // Ha éppen chat üzenett írunk, akkor nem dolgozunk fel eseményeket.
        if (isCursorInInputField()) {
            return;
        }

        var key = e.keyCode ? e.keyCode : e.which;
        var isShift = e.shiftKey;
        var isCtrl = e.ctrlKey;

        var scrollBy = [myScroll.scale * tileWidth, myScroll.scale * tileHeight];
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
            case 51:
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
                break;
        }
    }

    function onClick(evt) {
    }

    scrollerDiv.addEventListener('tap', onClick, false);
    scrollerDiv.addEventListener('dblclick', onDblClick, false);
}

function createTiles() {

    let tilesQueueWasEmpty = true;
    let tilesQueueToLoad = [];

    function loadTileFromQueue() {
        let img = tilesQueueToLoad.pop();
        if (img) {
            img.onload = function () {
                loadTileFromQueue();
            };
            setTimeout(function () {
                img.setAttribute('src', img.getAttribute('load-src'));
                img.removeAttribute('load-src');
            }, 2);
        }
    }
    ;

    setInterval(function () {
        tilesQueueWasEmpty = (tilesQueueToLoad.length === 0);

        [].forEach.call(document.querySelectorAll('img[data-src]'),
                function (img) {

                    if (isElementInViewport(img)) {
                        img.setAttribute('load-src', img.getAttribute('data-src'));
                        img.removeAttribute('data-src');
                        tilesQueueToLoad.push(img);
                    }
            });

        if (tilesQueueWasEmpty) {
            loadTileFromQueue();
        }

        if (tilesQueueWasEmpty) {
            loadTileFromQueue();
        }
    }, 100);

    tilesDiv = document.getElementById('tiles');
    var tilesCountX = 8000 / tileWidth;
    var tilesCountY = 13000 / tileHeight;

    scrollerDiv.style.width = tilesCountX * tileWidth + 'px';
    scrollerDiv.style.height = tilesCountY * tileHeight + 'px';
    myScroll.refresh();
    let tilesToLoad = [];
    for (let x = 0; x < tilesCountX; x++) {
        for (let y = 0; y < tilesCountY; y++) {
            let image = new Image();
            image.style.position = 'absolute';
            image.style.left = (x * tileWidth) + 'px';
            image.style.top = (y * tileHeight) + 'px';
            image.width = tileWidth;
            image.height = tileHeight;
            image.style.border = 'solid 2px red';
            image.tileX = x;
            image.tileY = y;
            image.className = 'lazy';
            image.id = 'tile_' + image.tileX + '_' + image.tileY;
            image.setAttribute('data-src', 'tile/' + image.tileX + '/' + image.tileY);
            tilesDiv.appendChild(image);
            image.addEventListener('tap',
                    function (e) {
                        console.log('tile click', this.tileX, this.tileY);
                    }, false);
            tilesToLoad.push(image);
        }
    }

}



function initVideo() {
    video = document.getElementById('video');
    //video.style.display = 'none';
    /*
     document.addEventListener('DOMContentLoaded', function () {
     
     //    var canvas = document.getElementById('c');
     //    var context = canvas.getContext('2d');
     
     video.addEventListener("loadedmetadata", function (e) {
     //canvas.width = this.videoWidth;
     //canvas.height = this.videoHeight;
     
     scrollerDiv.style.width = this.videoWidth + 'px';
     scrollerDiv.style.height = this.videoHeight + 'px';
     myScroll.refresh();
     
     }, false);
     
     }, false);
     */
}

function init() {
    initScroll();
    initCommunicationHandler();
    longPolling();
    //initVideo();
    createTiles();
}
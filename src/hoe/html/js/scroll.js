var myScroll;
var scrollerDiv;

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
        scrollToCoordScreen(evt.pageX, evt.pageY);
    }

    function onClick(evt) {
        var world = calculateScreenToWorld(evt.pageX, evt.pageY);
        toDebug('click ' + 'worldX:' + world[0] + ' worldY:' + world[1]);
    }

    scrollerDiv.addEventListener('tap', onClick, false);
    scrollerDiv.addEventListener('dblclick', onDblClick, false);
    myScroll.on('scrollEnd',markCenterTile);
    myScroll.on('zoomEnd',markCenterTile);
}

function scrollToCoordScreen(screenX, screenY) {
    var worldX = screenX - myScroll.x;
    var worldY = screenY - myScroll.y;

    scrollToCoordWorld(worldX, worldY);
}

function scrollToCoordWorld(worldX, worldY) {
    var mouseTarget = document.getElementById('mousePointerToScroll');
    mouseTarget.style.left = worldX + 'px';
    mouseTarget.style.top = worldY + 'px';
    myScroll.scrollToElement(mouseTarget, 1000, true, true);
}
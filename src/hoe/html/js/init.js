var myScroll;
var canvasVideo;

function initScroll() {
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
        zoomMin: .5,
        zoomMax: 4,
        tap: true,
        bounce: true
    });

    function onClick(evt) {
        var x = evt.pageX;
        var y = evt.pageY;
        console.log('click', 'x:', x, 'y:', y);
    }

    document.getElementById('scroller').addEventListener('tap', onClick, false);
}

function initVideo() {
    canvasVideo = new CanvasVideoPlayer({
        videoSelector: '.js-video',
        canvasSelector: '.js-canvas',
        audio: !true
    });

    canvasVideo.play();
}

function init() {
    initScroll();
    initVideo();
    initCommunicationHandler();
    longPolling();
}
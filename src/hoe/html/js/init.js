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
/*
function createTiles() {
    for (let x = 0; x < 2; x++) {
        for (let y = 0; y < 2; y++) {
            let tile = {};
            let div = tile.div = document.createElement('div');
            div.id = 'tile_' + x + '_' + y;
            div.style.position = 'absolute';
            div.style.width = "500px";
            div.style.height = "500px";
            div.style.top = y * 500 + "px";
            div.style.left = x * 500 + "px";
            div.style.border = 'solid 1px black';
            document.getElementById('scroller').appendChild(div);
            let vc = tile.videoControl = canvid({
                selector: '#' + div.id,
                videos: {
                    clip1: {src: 'http://localhost/tile/' + y + '/' + x, frames: 238, cols: 15, loops: Math.NaN, fps: 10, onEnd: function () {}}
                },
                width: 500,
                height: 500,
                loaded: function () {
                    vc.play('clip1');
                    //vc.pause();
                }
            });
        }
    }

    var scroller = document.getElementById('scroller');
    scroller.style.width = 500 * 7 + 'px';
    scroller.style.height = 500 * 4 + 'px';
    myScroll.refresh();
}
*/
function initVideo() {
    //https://gka.github.io/canvid/
    /*canvasVideo = canvid({
     selector: '#tile00',
     videos: {
     clip1: {src: 'http://localhost/tile/0/1', frames: 238, cols: 15, loops: NaN, fps: 10, onEnd: function () {}}
     },
     width: 500,
     height: 500,
     loaded: function () {
     var scroller = document.getElementById('scroller');
     scroller.style.width = 500 * 4 + 'px';
     scroller.style.height = 500 * 4 + 'px';
     myScroll.refresh();
     canvasVideo.play('clip1');
     //canvidControl.pause();
     }
     });*/

    //createTiles();

    var tileWidth = 500;
    var tileHeight = 500;
    var tilesCountX = 4;
    var tilesCountY = 2;
    var columnsCount = 15;
    var framesCount = (15 * 16) - 2;
    var frame = 0;
    var canvasWidth = tileWidth * tilesCountX;
    var canvasHeight = tileHeight * tilesCountY;

    var scroller = document.getElementById('scroller');
    scroller.style.width = canvasWidth + 'px';
    scroller.style.height = canvasHeight + 'px';
    myScroll.refresh();


    var canvas = document.getElementById('tiles');
    var c2d = canvas.getContext('2d');
    c2d.canvas.width = canvasWidth;
    c2d.canvas.height = canvasHeight;

    for (var x = 0; x < tilesCountX; x++) {
        for (var y = 0; y < tilesCountY; y++) {
            c2d.strokeRect(x * tileWidth, y * tileHeight, tileWidth, tileHeight);
        }
    }

    var img = new Image();
    img.onload = function () {
        var timer = setInterval(function () {

            /*anim.style.backgroundPositionX = -tileWidth * (frame % columnsCount);
             anim.style.backgroundPositionY = -tileHeight * Math.floor(frame / columnsCount);*/

            for (var xx = 0; xx < tilesCountX; xx++)
                for (var yy = 0; yy < tilesCountY; yy++) {
                    var x = tileWidth * (frame % columnsCount);
                    var y = tileHeight * Math.floor(frame / columnsCount);
                    c2d.drawImage(img, x, y, 500, 500, xx*tileWidth, yy*tileHeight, 500, 500);
                }

            // loop
            //frame = (frame+1)%framesCount;

            //play once
            //frame=Math.min(frame+1,framesCount-1);

            // stop after end
            frame++;
            if (frame === framesCount) {
                //clearInterval(timer);
                frame = 0;
            }

        }, 1 / 10 * 1000);

    };
    //img.src = 'http://84.21.7.31:8000/tiles/test.jpg';
    //img.src = 'http://84.21.7.31:8000/videos/anim_1_3.jpg';
    img.src = 'tile/1/3';

}

function init() {
    initScroll();
    initCommunicationHandler();
    longPolling();
    initVideo();
}
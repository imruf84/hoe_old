var myScroll;
var canvasVideo;
var scrollerDiv;
var video;

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
        bounce: !true
    });

    function onDblClick(evt) {
        var x = evt.pageX;
        var y = evt.pageY;

        //var x = evt.pageX - this.offsetLeft; 
        //var y = evt.pageY - this.offsetTop; 

        var globalX = x - myScroll.x;
        var globalY = y - myScroll.y;

        //myScroll.scale
        //
        //myScroll.scrollTo(x, y, 400, IScroll.utils.ease.quadratic);

        var mouseTarget = document.getElementById('mousePointerToScroll');
        mouseTarget.style.left = globalX + 'px';
        mouseTarget.style.top = globalY + 'px';
        myScroll.scrollToElement(mouseTarget, 1000, true, true)

        //myScroll.scrollToElement(document.getElementById('target1'),200,true,true)

        globalX /= myScroll.scale;
        globalY /= myScroll.scale;

        console.log('click', 'globalX:', globalX, 'globalY:', globalY);
    }

    window.onkeyup = function (e) {
        var key = e.keyCode ? e.keyCode : e.which;

        switch (key) {
            case 32:
                if (video.paused)
                    video.play();
                else
                    video.pause();
                break;
        }
    }

    function onClick(evt) {
    }

    scrollerDiv.addEventListener('tap', onClick, false);
    scrollerDiv.addEventListener('dblclick', onDblClick, false);
}

function createTiles() {

    var tilesDiv = document.getElementById('tiles');
    var tileWidth = 500;
    var tileHeight = 500;
    var tilesCountX = 8000 / tileWidth;
    var tilesCountY = 13000 / tileHeight;

    scrollerDiv.style.width = tilesCountX * tileWidth + 'px';
    scrollerDiv.style.height = tilesCountY * tileHeight + 'px';
    myScroll.refresh();
    let images = [];
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
            image.id = 'tile_' + image.tileX + '_' + image.tileY;
            tilesDiv.appendChild(image);
            image.addEventListener('tap',
                    function (e) {
                        console.log(this.tileX, this.tileY);
                    }, false);
            image.onload = function () {
                let i = images.shift();
                if (!i) {
                    return;
                }
                i.src = 'tile/' + i.tileX + '/' + i.tileY;
            };
            images.push(image);
        }
    }

    let i = images.shift();
    i.src = 'tile/' + i.tileX + '/' + i.tileY;



    /* for (let x = 0; x < 2; x++) {
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
     myScroll.refresh();*/
}



function initVideo() {
    video = document.getElementById('video');
    //video.style.display = 'none';

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
        /*
             v.addEventListener('play', function () {
                 draw(this, context, cw, ch);
             }, false);*/

    }, false);

    /*
     var tileWidth = 500;
     var tileHeight = 500;
     var tilesCountX = 7;
     var tilesCountY = 4;
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
     */
    /*
     var tilesToLoad = [];
     let tiles = [];
     
     //for (let xx = 0; xx < tilesCountX; xx++) {
     for (let xx = 1; xx < tilesCountX-2; xx++) {
     //for (let yy = 0; yy < tilesCountY; yy++) {
     for (let yy = 0; yy < tilesCountY-1; yy++) {
     let t = new Tile({
     x: xx, y: yy,
     width: 500,
     height: 500,
     columns: 15,
     //frames: (15 * 16) - 2,
     frames: [0, (15 * 16) - 2 - 1],
     //frames: [0, 40],
     context: c2d,
     onload: function(tile) {
     
     tiles.push(tile);
     
     tile.draw();
     toDebug(tile.id+' loaded:'+tile.images.length);
     let tt = tilesToLoad.shift();
     if (tt) {
     tt.load();
     return;
     }
     
     setInterval(function (){
     for (var t of tiles) {
     t.drawNext();
     }
     },1/10*1000);
     }
     });
     
     tilesToLoad.push(t);
     }
     }
     
     //toDebug(tilesToLoad.length);
     //console.log(tilesToLoad);
     
     tilesToLoad.shift().load();
     */
}

function init() {
    initScroll();
    initCommunicationHandler();
    longPolling();
    //initVideo();
    createTiles();
}
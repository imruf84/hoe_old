/* global scrollerDiv */
/* global myScroll */

var tilesDiv;
var tileWidth = 500;
var tileHeight = 500;

function initTiles() {

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
            image.style.left = (x * tileWidth) + 'px';
            image.style.top = (y * tileHeight) + 'px';
            image.width = tileWidth;
            image.height = tileHeight;
            image.tileX = x;
            image.tileY = y;
            image.className = 'tile';
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

function markCenterTile() {
    var ct = getClosestTileToCenterOfScreen();
    if (!ct) {
        return;
    }
    iterateTiles(function (t){removeClass(t, 'tile-video');});
    addClass(ct, 'tile-video');
}

function getClosestTileToCenterOfScreen() {
    let result = null;
    let dMin = 0;
    let sc = [getWindowSize()[0]/2,getWindowSize()[1]/2];
    iterateTiles(function (tile) {
        if (isElementInViewport(tile)) {
        let rect = tile.getBoundingClientRect();
        let tc = [(rect.left+rect.right)/2,(rect.top+rect.bottom)/2];
        let d = Math.sqrt((tc[0]-sc[0])*(tc[0]-sc[0])+(tc[1]-sc[1])*(tc[1]-sc[1]));
        
        if (result === null || d < dMin) {
            result = tile;
            dMin = d;
        }
    }
    });
    
    return result;
}

function iterateTiles(func) {
    [].forEach.call(document.querySelectorAll('.tile'),func);
}
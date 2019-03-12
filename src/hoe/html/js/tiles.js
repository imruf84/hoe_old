/* global scrollerDiv */
/* global myScroll */

var tilesDiv;
var tileWidth = 500;
var tileHeight = 500;

function initTiles() {

    let tilesQueueWasEmpty = true;
    let tilesQueueToLoad = [];
    function loadTileFromQueue() {
        sortTilesByDistanceOfScreenCenter(tilesQueueToLoad);
        let img = tilesQueueToLoad.shift();
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
    var tilesFromX = -5;
    var tilesToX = 5;
    var tilesFromY = -3;
    var tilesToY = 3;
    var tilesCountX = tilesToX - tilesFromX + 1;
    var tilesCountY = tilesToY - tilesFromY + 1;
    scrollerDiv.style.width = tilesCountX * tileWidth + 'px';
    scrollerDiv.style.height = tilesCountY * tileHeight + 'px';
    myScroll.refresh();
    scrollToCoordWorldQuickly(tilesCountX/2*tileWidth,tilesCountY/2*tileHeight);
    let tilesToLoad = [];
    for (let x = tilesFromX; x <= tilesToX; x++) {
        for (let y = tilesFromY; y <= tilesToY; y++) {
            let image = new Image();
            image.style.left = ((-tilesFromX+x) * tileWidth) + 'px';
            image.style.top = ((-tilesFromY+y) * tileHeight) + 'px';
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
    var ct = getClosestTileToScreenCenter();
    if (!ct) {
        return;
    }
    iterateTiles(function (t) {
        removeClass(t, 'tile-video');
    });
    addClass(ct, 'tile-video');
}

function getTileDistanceFromScreenCenter(tile) {
    let sc = [getWindowSize()[0] / 2, getWindowSize()[1] / 2];
    let rect = tile.getBoundingClientRect();
    let tc = [(rect.left + rect.right) / 2, (rect.top + rect.bottom) / 2];
    let d = Math.sqrt((tc[0] - sc[0]) * (tc[0] - sc[0]) + (tc[1] - sc[1]) * (tc[1] - sc[1]));
    return d;
}

function getClosestTileToScreenCenter() {
    let result = null;
    let dMin = 0;
    iterateTiles(function (tile) {
        if (isElementInViewport(tile)) {
            let d = getTileDistanceFromScreenCenter(tile);
            if (result === null || d < dMin) {
                result = tile;
                dMin = d;
            }
        }
    });
    return result;
}

function sortTilesByDistanceOfScreenCenter(tiles) {
    function compare(a,b) {
        var da = getTileDistanceFromScreenCenter(a);
        var db = getTileDistanceFromScreenCenter(b);
        if (da < db)
            return -1;
        if (da > db)
            return 1;
        return 0;
    }
    
    tiles.sort(compare);
    
}

function iterateTiles(func) {
    [].forEach.call(document.querySelectorAll('.tile'), func);
}
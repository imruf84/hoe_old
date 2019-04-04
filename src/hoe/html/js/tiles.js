/* global scrollerDiv */
/* global myScroll */

var tilesDiv;
var map = null;
//var tileWidth = 500;
//var tileHeight = 500;

function getTileSize() {
    return 500;
}

function isTilesMapCreated() {
    return map !== null;
}

function getTilesMap() {
    return map;
}

function getTilesUrl() {
    return 'tile/{x}/{y}/' + getCurrentTurn();
}

function refreshTiles(t) {

    if (t !== getCurrentTurn()) {
        toDebug('Refresh tiles for turn: ' + t);
        setCurrentTurn(t);
        map.getLayers().array_[0].getSource().setUrl(getTilesUrl());
    } else {
        //map.getLayers().array_[0].getSource().refresh();
    }
    //map.getLayers().array_[0].getSource().refresh();
    // map.getView().animate({center: [0,0],duration: duration});
    // map.updateSize();
}

function initTiles(tileSize, tilesX, tilesY) {

    /*var tileSize = 500;
     var tilesX = 11;
     var tilesY = 7;*/
    var mapSize = [tileSize * tilesX, tileSize * tilesY];

    var extent = [0, 0, mapSize[0], mapSize[1]];

    var proj = new ol.proj.Projection({
        code: 'pixel',
        units: 'pixels',
        extent: extent
    });

    var resolutions = [1];

    var source = new ol.source.TileImage({
        tileGrid: new ol.tilegrid.TileGrid({
            //origin: [Math.trunc(tilesX/2)*tileSize,Math.trunc(tilesY/2)*tileSize],
            origin: [Math.trunc(tilesX / 2) * tileSize, Math.round(tilesY / 2) * tileSize],
            resolutions: resolutions,
            tileSize: tileSize
        }),
        projection: proj,
        url: getTilesUrl()
                /*tileUrlFunction: function(tileCoord, pixelRatio, projection) {
                 if (tileCoord === null) return undefined;
                 
                 var z = tileCoord[0];
                 var x = tileCoord[1];
                 var y = tileCoord[2];
                 
                 //return 'https://a.tiles.mapbox.com/v3/mapbox.blue-marble-topo-jan/3/'+x+'/'+(tilesY-y)+'.png';
                 return 'tile/' + x + '/' + y;
                 }*/
    });
    
    /*source.on('tileloaderror', function(e) {
        alert(e.type);
      });*/


    var layer = new ol.layer.Tile({
        extent: proj.getExtent(),
        source: source
    });

    map = new ol.Map({
        loadTilesWhileAnimating: true,
        target: 'tiles',
        layers: [layer],
        controls: [new ol.control.FullScreen()],
        interactions: ol.interaction.defaults().extend([
            new ol.interaction.KeyboardPan({
                duration: 200,
                pixelDelta: tileSize
            }),
            new ol.interaction.KeyboardZoom({
                duration: 200,
                delta: 1
            })
        ]),
        keyboardEventTarget: document,
        view: new ol.View({
            extent: extent,
            enableRotation: false,
            projection: proj,
            center: ol.extent.getCenter(extent),
            resolution: 1,
            minResolution: 1,
            maxResolution: 4,
            zoom: 1
        })
    });

}
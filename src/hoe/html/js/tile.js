function Tile(p) {
    let THIS = this;
    this.frame = 0;
    this.id = p.id || '';
    this.x = p.x || 0;
    this.y = p.y || 0;
    this.tileWidth = p.width || 0;
    this.tileHeight = p.height || 0;
    this.columnsCount = p.columns || 0;
    this.framesCount = p.frames || 0;
    this.context = p.context || null;
    this.src = p.src || '';
    this.framesToLoad = [];
    if (p.frames instanceof Array) {
        switch(p.frames.length) {
            case 1:
                this.framesToLoad = p.frames;
                break;
            case 2:
                for (var i = p.frames[0]; i <= p.frames[1]; i++) {
                    this.framesToLoad.push(i);
                }
                break;
        }
    }
    this.onload = function () {
        THIS.isLoaded = true;
        p.onload(THIS);
    } || function () {};
    this.onerror = function () {
        p.onerror(THIS);
    } || function () {};
    this.images = [];
    this.isLoaded = false;

    if (this.id === '') {
        this.id = this.x + '_' + this.y;
    }

    if (this.src === '') {
        this.src = 'tile/' + this.x + '/' + this.y;
    }

    if (p.autoLoad) {
        this.load();
    }

}
;

Tile.prototype.unload = function () {
    this.src = '';
    this.framesToLoad = [];
    if (this.image.length === 0) {
        return;
    }
    delete this.images;
    this.image = [];
};

Tile.prototype.loadSrc = function (src) {
    if (src === '') {
        this.unload();
        return;
    }

    let THIS = this;
    this.isLoaded = false;


    //this.image.onload = this.onload;
    //this.image.onerror = this.onerror;


    /*let img = this.image;
     let s = this.src;
     setTimeout(function () {
     img.src = s;
     },0);*/

    function loadFunc() {
        if (THIS.framesToLoad.length === 0) {
            THIS.onload();
            return;
        }

        let image = new Image();
        THIS.images.push(image);
        image.onload = function () {
            loadFunc();
        };

        let lSrc = src;
        let f = THIS.framesToLoad.shift();
        lSrc += '/' + f;
        image.src = lSrc;
        //toDebug('.......loading image:' + lSrc);

    }

    loadFunc();

};

Tile.prototype.load = function () {
    this.loadSrc(this.src);
};

Tile.prototype.drawNext = function () {
    this.frame++;
    if (this.frame >= this.images.length) {
        this.frame = 0;
    }
    
    this.draw();
};

Tile.prototype.draw = function () {
    if (this.images.length === 0 || !this.isLoaded) {
        return;
    }

    var image = this.images[this.frame];
    if (!image) {
        return;
    }
    /*var ix = this.tileWidth * (this.frame % this.columnsCount);
     var iy = this.tileHeight * Math.floor(this.frame / this.columnsCount);*/

    //this.context.drawImage(image, 0, 0, this.tileWidth, this.tileHeight, this.x * this.tileWidth, this.y * this.tileHeight, this.tileWidth, this.tileHeight);
    this.context.drawImage(image, this.x * this.tileWidth, this.y * this.tileHeight);
};

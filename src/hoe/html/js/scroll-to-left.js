function scrollTopLeft() {
    var d = document.body;
    d.scrollLeft = 0;
    d.scrollTop = 0;
}
document.addEventListener('gesturestart', function (e) {
    scrollTopLeft();
    e.preventDefault();
});
document.addEventListener('touchmove', function (event) {
    scrollTopLeft();
    event.preventDefault();
}, false);
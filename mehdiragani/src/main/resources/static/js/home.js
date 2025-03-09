// Get the user's screen dimensions
var screenWidth = window.innerWidth;
var screenHeight = window.innerHeight;

var sliderImages = document.getElementsByClassName("slider-image");
var artworkNames = document.getElementsByClassName("artwork-name");
var validImages = [];


Array.from(sliderImages).forEach(function (image) {
    image.onload = function () {
        var aspectRatio = image.naturalWidth / image.naturalHeight;

        // Resize dimensions to cover the screen while keeping the aspect ratio
        var adjustedWidth = Math.max(screenWidth, screenHeight * aspectRatio);
        var adjustedHeight = adjustedWidth / aspectRatio;

        image.style.width = adjustedWidth + "px";
        image.style.height = adjustedHeight + "px";

        validImages.push(image);

        if (validImages.length === sliderImages.length) {
            initSlider(validImages, artworkNames);
        }
    };
});

function initSlider(images, names) {
    let currentIndex = 0;

    // Show the first valid image and name
    images[currentIndex].classList.add('active');
    names[currentIndex].classList.add('active');

    // Switch images and artwork names
    function changeSlide() {
        images[currentIndex].classList.remove('active');
        names[currentIndex].classList.remove('active');

        currentIndex = (currentIndex + 1) % images.length;

        images[currentIndex].classList.add('active');
        names[currentIndex].classList.add('active');
    }

    setInterval(changeSlide, 5000);
}
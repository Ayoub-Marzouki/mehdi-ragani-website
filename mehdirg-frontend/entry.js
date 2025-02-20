// Scroll to section
function scrollToSection(buttonId, sectionId) {
    const scrollButton = document.getElementById(buttonId);
    const section = document.getElementById(sectionId);
    if (scrollButton && section) {
        scrollButton.addEventListener('click', () => {
            const offset = section.offsetTop;

            window.scrollTo({
                top: offset,
                behavior: 'smooth'
            });
        });
}
}


scrollToSection('enter-now','gallery-navigation');


function createSlider(sliderSections) {
    // To be able to use loops and whatnot on this collection
    var sections = Array.from(document.getElementsByClassName(sliderSections));
    var walls = Array.from(document.getElementsByClassName("wall"));
    var fadeEffect = document.getElementById("fade-in-out");
    var leftArrow = document.getElementById("left-arrow");
    var rightArrow = document.getElementById("right-arrow");

    var galleryNavigation = document.getElementById("gallery-navigation");
    
    sections[0].style.display="block";
    var i = 0, j = 0;

    
    leftArrow.addEventListener("click", function () {
        fadeEffect.style.animation = "none"; 
        fadeEffect.style.animation = "fade-in-out 5s linear forwards";
        
        // sections[i].style.opaciy = "0";
        setTimeout(function () {
            sections[i].style.display = "none";
            
            if (i === 0) {
                i = sections.length - 1;
            } else {
                i--;
            }
            if (j === 0) {
                j = walls.length - 1;
            } else {
                j--;
            }
            galleryNavigation.style.backgroundImage = `url(${walls[j].getAttribute('src')})`;
            sections[i].style.display = "block";
        }, 2500);
    });    

    rightArrow.addEventListener("click", function() {
        fadeEffect.style.animation = "none"; 
        fadeEffect.offsetHeight; // Trigger reflow to reset the animation
        fadeEffect.style.animation = "fade-in-out 5s linear forwards";

        setTimeout(function() {
        sections[i].style.display = "none";
        if (i === sections.length - 1) {
            i = 0; 
        } else {
            i++; 
        }   
        if (j === walls.length - 1) {
            j = 0;
        } else {
            j++;
        } 
        galleryNavigation.style.backgroundImage = `url(${walls[j].getAttribute('src')})`;
        sections[i].style.display = "block"; 
    }, 2500);
    });  
}

function createLightbox(imageUrl) {
    const lightbox = document.createElement('div');
    lightbox.classList.add('lightbox');

    const image = document.createElement('img');
    image.src = imageUrl;
    lightbox.appendChild(image);
    document.body.appendChild(lightbox);

    lightbox.addEventListener('click', function () {
        document.body.removeChild(lightbox);
    });
}


// Adjusting the images so that they fit on the wall
function adjustSize(images) {
    images = Array.from(images);
    for (var i = 0; i < images.length; i++) {
        var width = images[i].naturalWidth;
        var height = images[i].naturalHeight;
        while (height > 450) {
            // Scale down by a fixed ratio 
            width *= 0.9;
            height *= 0.9;
        }
        images[i].style.width = `${width}px`; // if we go for this : images[i].style.width = width + "px"; we'll be adding a number to a string, and it will not work
        images[i].style.height = `${height}px`;
    }
}



createSlider("flex-item");

var sliderImages = Array.from(document.getElementsByClassName("art-type"));
sliderImages.forEach(element => {
    element.addEventListener('click', function () {
        createLightbox(element.src);
    });
});

// The reason we went for 'window.onload' is that the adjustment of the size sometimes occurs before even the image is loaded; the initial width/height of the images are 0, thus it's invisible until we soft refresh the website
window.onload = function () {
    adjustSize(sliderImages);
};

function createLightbox(imageUrl) {
    const lightbox = document.createElement('div');
    lightbox.classList.add('lightbox');
    
    const image = document.createElement('img');
    image.src = imageUrl;
    
    lightbox.appendChild(image);
    
    document.body.appendChild(lightbox);
    
    lightbox.addEventListener('click', function() {
        document.body.removeChild(lightbox);
    });
}

const artworkImage = document.getElementById('artwork-image');
artworkImage.addEventListener('click', function(event) {
    event.stopPropagation();

    const imageUrl = this.src;
    createLightbox(imageUrl);
});

const secondaryImages = document.getElementsByClassName('secondary-images');
for (const image of secondaryImages) {
    image.addEventListener('click', function(event) {
        event.stopPropagation();
        const imageUrl = this.src;
        updateMainImage(imageUrl);

    });
}

// Function to update the main image with the clicked secondary image
function updateMainImage(imageUrl) {
    if (artworkImage.src === imageUrl) {
        return;
    }
    artworkImage.style.transition = 'opacity 1s';
    artworkImage.style.opacity = '0';
    
    setTimeout(function() {
        artworkImage.src = imageUrl;
        
        artworkImage.style.opacity = '1';
    }, 1000);
}

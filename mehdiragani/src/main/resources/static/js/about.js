const moreOfArtistImages = document.getElementsByClassName("more-of-artist-image");

function createLightbox(imageUrl) {
  // Create a lightbox overlay
  const lightbox = document.createElement('div');
  lightbox.classList.add('lightbox');
  
  // Create an image element for the background image
  const image = document.createElement('img');
  image.src = imageUrl;
  
  // Append the image to the lightbox
  lightbox.appendChild(image);
  
  // Append the lightbox to the document body
  document.body.appendChild(lightbox);
  
  // Close the lightbox when clicking outside of it
  lightbox.addEventListener('click', function() {
      document.body.removeChild(lightbox);
  });
}

document.addEventListener("DOMContentLoaded", function () {
    const moreOfArtistImages = document.querySelectorAll(".more-of-artist-image");

    moreOfArtistImages.forEach(image => {
        image.addEventListener("click", function () {
            createLightbox(this.src);
        });
    });
});



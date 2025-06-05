document.addEventListener('DOMContentLoaded', () => {
	const screenWidth  = window.innerWidth;
	const screenHeight = window.innerHeight;

	// live HTMLCollections → arrays
	const sliderImages  = Array.from(document.getElementsByClassName("slider-image"));
	const artworkNames  = Array.from(document.getElementsByClassName("artwork-name"));
	const artworkYears  = Array.from(document.getElementsByClassName("artwork-year"));

	sliderImages.forEach(img => {
		img.addEventListener('load', () => {
			const aspectRatio   = img.naturalWidth / img.naturalHeight;
			const adjustedWidth = Math.max(screenWidth, screenHeight * aspectRatio);
			img.style.width     = adjustedWidth + "px";
			img.style.height    = (adjustedWidth / aspectRatio) + "px";
		});
		// trigger if already cached
		if (img.complete) img.dispatchEvent(new Event('load'));
	});

	// initialize slider immediately
	initSlider(sliderImages, artworkNames, artworkYears);
});

function initSlider(images, names, years) {
	let currentIndex = 0;

	// show first slide & name
	images[currentIndex].classList.add('active');
	names[currentIndex].classList.add('active');
	years[currentIndex].classList.add('active');

	function changeSlide() {
		images[currentIndex].classList.remove('active');
		names[currentIndex].classList.remove('active');
		years[currentIndex].classList.remove('active');
		
		currentIndex = (currentIndex + 1) % images.length;

		images[currentIndex].classList.add('active');
		names[currentIndex].classList.add('active');
		years[currentIndex].classList.add('active');
	}

	setInterval(changeSlide, 5000);
}
var slider_holder=document.getElementsByClassName("slider-image");
var counter=0;
for (let i=2;i<=slider_holder.length-1;i++) {
    slider_holder[i].style.display="none";
}
function auto_slider(array_holder, current_counter) {
    array_holder[current_counter].style.display="none";
    array_holder[current_counter+1].style.display="none";
    if (current_counter+2>=array_holder.length) {
        current_counter=0;
    }
    else {
        current_counter=current_counter+2;
    }
    array_holder[current_counter].style.display="block";
    array_holder[current_counter+1].style.display="block";
    return current_counter;
}
setInterval(function() {
    counter=auto_slider(slider_holder,counter);
}, 5000);



// Function to simulate typing effect for a specific element
function typeWriter(element, text) {
    let index = 0;
    const typingInterval = 50; // Adjust typing speed here
    
    function type() {
        if (index < text.length) {
            element.innerHTML += text.charAt(index);
            index++;
            setTimeout(type, typingInterval);
        }
    }
    
    // Start typing animation
    type();
}

// Define options for the intersection observer
const options = {
    root: null, // Use the viewport as the root
    rootMargin: '0px', // No margin
    threshold: 0.8 // Trigger when 50% of the element is visible
};

// Define the callback function for the intersection observer
function intersectionCallback(entries, observer) {
    entries.forEach(entry => {
        // If the paragraph is intersecting (i.e., at least 80% visible)
        if (entry.isIntersecting) {
            // Start the typing animation for the paragraph
            typeWriter(entry.target, entry.target.dataset.text);
            // Stop observing this paragraph
            observer.unobserve(entry.target);
        }
    });
}

// Create a new intersection observer
const observer = new IntersectionObserver(intersectionCallback, options);

// Get all paragraphs with the 'type-writer' class
const paragraphs = document.getElementsByClassName('type-writer');

// Start observing each paragraph
for (const paragraph of paragraphs) {
    // Store the text content of each paragraph in a data attribute
    paragraph.dataset.text = paragraph.textContent.trim();
    // Clear the paragraph's content
    paragraph.innerHTML = '';
    // Start observing the paragraph
    observer.observe(paragraph);
}

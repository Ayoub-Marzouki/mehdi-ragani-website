const printId = document.querySelector('input[name="printId"]').value;
const typeSelect = document.getElementById('printType');
const sizeSelect = document.getElementById('printSize');
const framingSelect = document.getElementById('framing');
const quantityInput = document.getElementById('quantity');
const priceText = document.getElementById('print-price-text');
const csrfToken = document.querySelector('input[name="_csrf"]').value;
const csrfParam = document.querySelector('input[name="_csrf"]').name;
const form = document.getElementById('print-cart-form');

function updatePrice() {
    const type = typeSelect.value;
    const size = sizeSelect.value;
    const framing = framingSelect.value;
    const quantity = quantityInput.value;
    
    // Only update price if quantity is a valid positive number
    if (!quantity || isNaN(quantity) || quantity <= 0) {
        return;
    }
    
    fetch('/store/print/price', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: `printId=${encodeURIComponent(printId)}&type=${encodeURIComponent(type)}&size=${encodeURIComponent(size)}&framing=${encodeURIComponent(framing)}&quantity=${encodeURIComponent(quantity)}&${csrfParam}=${encodeURIComponent(csrfToken)}`
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        return response.text();
    })
    .then(price => {
        priceText.textContent = price;
    })
    .catch(error => {
        console.error('Error updating price:', error);
        // Don't show error to user, just log it
    });
}

// Only update price when user finishes typing (debounced)
let priceUpdateTimeout;
quantityInput.addEventListener('input', function() {
    clearTimeout(priceUpdateTimeout);
    priceUpdateTimeout = setTimeout(updatePrice, 500); // Wait 500ms after user stops typing
});

typeSelect.addEventListener('change', updatePrice);
sizeSelect.addEventListener('change', updatePrice);
framingSelect.addEventListener('change', updatePrice);

// Form validation on submit
form.addEventListener('submit', function(e) {
    const quantity = quantityInput.value;
    
    // Validate quantity
    if (!quantity || isNaN(quantity) || quantity <= 0) {
        e.preventDefault();
        alert('Please enter a valid quantity (positive number)');
        quantityInput.focus();
        return false;
    }
    
    // Check if quantity exceeds stock (if stock is limited)
    const maxStock = quantityInput.getAttribute('max');
    if (maxStock && parseInt(quantity) > parseInt(maxStock)) {
        e.preventDefault();
        alert(`Quantity cannot exceed available stock (${maxStock})`);
        quantityInput.focus();
        return false;
    }
    
    // If validation passes, allow form submission
    return true;
});

// Initial price update
updatePrice(); 
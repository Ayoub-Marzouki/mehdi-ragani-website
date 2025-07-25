const printId = document.querySelector('input[name="printId"]').value;
const typeSelect = document.getElementById('printType');
const sizeSelect = document.getElementById('printSize');
const framingSelect = document.getElementById('framing');
const quantityInput = document.getElementById('quantity');
const priceText = document.getElementById('print-price-text');
const csrfToken = document.querySelector('input[name="_csrf"]').value;
const csrfParam = document.querySelector('input[name="_csrf"]').name;

function updatePrice() {
    const type = typeSelect.value;
    const size = sizeSelect.value;
    const framing = framingSelect.value;
    const quantity = quantityInput.value;
    fetch('/store/print/price', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: `printId=${encodeURIComponent(printId)}&type=${encodeURIComponent(type)}&size=${encodeURIComponent(size)}&framing=${encodeURIComponent(framing)}&quantity=${encodeURIComponent(quantity)}&${csrfParam}=${encodeURIComponent(csrfToken)}`
    })
    .then(response => response.text())
    .then(price => {
        priceText.textContent = price;
    });
}

typeSelect.addEventListener('change', updatePrice);
sizeSelect.addEventListener('change', updatePrice);
framingSelect.addEventListener('change', updatePrice);
quantityInput.addEventListener('input', updatePrice);

// Initial price update
updatePrice(); 
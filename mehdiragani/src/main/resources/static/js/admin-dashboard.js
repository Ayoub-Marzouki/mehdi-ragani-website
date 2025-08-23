// Capitalize the tables names and handle special cases
const tableNames = document.querySelectorAll('#tables .table-name');
tableNames.forEach(link => { // link is the anchor tag of tables 
    const text = link.textContent.trim();
    if (text) {
        // Handle special case where table name is already plural
        if (text.toLowerCase() === 'orders') {
            link.textContent = 'Orders'; // Just capitalize, don't add 's'
        } else {
            link.textContent = text.charAt(0).toUpperCase() + text.slice(1) + 's';
        }
    }
});
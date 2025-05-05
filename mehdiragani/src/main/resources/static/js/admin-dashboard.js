// Capitalize the tables names 
const tableNames = document.querySelectorAll('#tables .table-name');
tableNames.forEach(link => { // link is the anchor tag of tables 
    const text = link.textContent.trim();
    if (text) {
        link.textContent = text.charAt(0).toUpperCase() + text.slice(1) + 's';
    }
});
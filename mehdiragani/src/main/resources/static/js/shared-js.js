document.addEventListener('DOMContentLoaded', () => {
    const toggleButton = document.getElementById('phone-navbar-toggle');
    const phoneNavbarBody = document.getElementById('phone-navbar-body');

    toggleButton.addEventListener('click', () => {
        phoneNavbarBody.classList.toggle('toggled');
    });
});
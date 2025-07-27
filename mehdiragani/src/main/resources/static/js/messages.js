document.addEventListener('DOMContentLoaded', function() {
    const messages = document.querySelectorAll('#messages .message');
    messages.forEach(function(message) {
        setTimeout(function() {
            message.style.opacity = 1;
        }, 0);
        setTimeout(function() {
            message.style.opacity = 0;
        }, 4000);
    });
});
function toggleMode() {
    // Toggle the 'light-mode' class on the body
    var body = document.body;
    body.classList.toggle('light-mode');
    body.classList.toggle('dark-mode');


    // Update button text based on the current mode
    var toggleButton = document.getElementById('toggleModeButton');
    if (document.body.classList.contains('light-mode')) {
        toggleButton.textContent = 'Dark Mode';
    } else {
        toggleButton.textContent = 'Light Mode';
    }
}
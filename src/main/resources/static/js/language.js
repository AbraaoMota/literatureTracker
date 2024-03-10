// Updated JavaScript file
document.addEventListener('DOMContentLoaded', function () {

    // Retrieve the selected language code from local storage
    const savedLanguageCode = localStorage.getItem('selectedLanguageCode')

    // Fetch language data from the provided URL
    fetch('https://b.jw-cdn.org/apis/mediator/v1/languages/E/all')
        .then(response => response.json())
        .then(data => {
            // Extract languages from the response
            const languages = Object.values(data);

            // Get the dropdown element
            const dropdown = document.getElementById('languageSelector');

            // Clear previous options
            while (dropdown.firstChild) {
                dropdown.removeChild(dropdown.firstChild);
            }

            if (!savedLanguageCode) {
                // Add a default option (English)
                const defaultOption = document.createElement('option');
                defaultOption.value = 'E';
                defaultOption.textContent = 'English (E)';
                dropdown.appendChild(defaultOption);
            }


            // Populate the dropdown with language options
            languages[0].forEach(language => {
                const option = document.createElement('option');
                option.value = language.code;
                option.textContent = `${language.name} (${language.code})`;
                dropdown.appendChild(option);
            });


        })
        .catch(error => console.error('Error fetching language data:', error))
        .then(_ => {
            if (savedLanguageCode) {
                languageSelector.value = savedLanguageCode;
            }

            fetchAllItems();
        });

    // Set the selected language code in the languageSelector
    const languageSelector = document.getElementById('languageSelector');
    // if (savedLanguageCode) {
    //     languageSelector.value = savedLanguageCode;
    // }

    languageSelector.addEventListener('change', function () {
        // Save the selected language code to local storage
        localStorage.setItem('selectedLanguageCode', languageSelector.value);


        // Call fetchAllItems when the language code changes
        fetchAllItems();
    });
});

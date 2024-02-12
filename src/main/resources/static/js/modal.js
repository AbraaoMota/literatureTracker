function openModal() {
    document.getElementById("myModal").style.display = "flex";
}

function closeModal() {
    // Close the modal
    const modal = document.getElementById('myModal');
    modal.style.display = 'none';

    // Reset values in the form
    document.getElementById('code').value = '';  // Reset literature code to empty
    document.getElementById('literatureName').value = '';  // Reset literature name to empty
    document.getElementById('currentTotalQuantity').value = '';  // Reset total quantity to empty
    document.getElementById('frontQuantity').value = '';  // Reset front quantity to empty
    document.getElementById('backQuantity').value = '';  // Reset back quantity to empty
    document.getElementById('quantity').value = '';  // Reset submission quantity to empty
    document.getElementById('auxiliaryText').textContent = ''; // Reset auxiliary text to Empty
    document.getElementById('imageContainer').style = 'display: none';

    // Reset image source if the image element exists
    const imageElement = document.getElementById('literatureImage');
    if (imageElement) {
        imageElement.src = '';  // Reset image source to empty
    }
}

function submitForm() {
    // Get form data
    const code = document.getElementById('code').value;
    const quantity = parseInt(document.getElementById('quantity').value);
    const stockType = document.getElementById('selectedStockType').value;

    // Include the hardcoded parameter congId as a string
    const congId = '1'; // Converted to string

    // Get the selected language code from the dropdown
    const languageSelector = document.getElementById('languageSelector');
    const selectedLanguageCode = languageSelector.value;

    // Construct item to be inserted into DynamoDB
    const item = {
        congId: congId,
        languages: {
            [selectedLanguageCode]: {
                [code]: {
                    frontQuantity: 0,  // Initialize with default values
                    backQuantity: 0,   // Initialize with default values
                    totalQuantity: 0   // Initialize with default values
                }
            }
        }
    };

    // DynamoDB GetItem parameters to retrieve existing item
    const getItemParams = {
        TableName: stockTableName,
        Key: {
            'congId': congId
        },
        ProjectionExpression: `languages.${selectedLanguageCode}.#code.frontQuantity, languages.${selectedLanguageCode}.#code.backQuantity, languages.${selectedLanguageCode}.#code.totalQuantity`,
        ExpressionAttributeNames: {
            '#code': `${code}`
        }
    };

    // Use DynamoDB DocumentClient to get existing item
    dynamoDB.get(getItemParams, function (err, data) {
        if (err) {
            console.error('Error fetching existing item:', err);
            return;
        }

        // Check if the item exists in the database
        const existingItem = data.Item;
        if (existingItem && existingItem.languages[selectedLanguageCode] && existingItem.languages[selectedLanguageCode][code]) {
            // Reconcile quantities based on stockType
            if (stockType === 'front') {
                item.languages[selectedLanguageCode][code].frontQuantity = quantity;
                item.languages[selectedLanguageCode][code].backQuantity = existingItem.languages[selectedLanguageCode][code].backQuantity;
            } else if (stockType === 'back') {
                item.languages[selectedLanguageCode][code].frontQuantity = existingItem.languages[selectedLanguageCode][code].frontQuantity;
                item.languages[selectedLanguageCode][code].backQuantity = quantity;
            }
        } else {
            // If the item doesn't exist, set quantities based on stockType
            if (stockType === 'front') {
                item.languages[selectedLanguageCode][code].frontQuantity = quantity;
            } else if (stockType === 'back') {
                item.languages[selectedLanguageCode][code].backQuantity = quantity;
            }
        }

        // Calculate totalQuantity as the sum of frontQuantity and backQuantity
        item.languages[selectedLanguageCode][code].totalQuantity =
            item.languages[selectedLanguageCode][code].frontQuantity +
            item.languages[selectedLanguageCode][code].backQuantity;

        // DynamoDB PutItem parameters
        const putItemParams = {
            TableName: stockTableName,
            Item: item,
        };

        // Use DynamoDB DocumentClient to put item
        dynamoDB.put(putItemParams, function (putErr, putData) {
            if (putErr) {
                console.error('Unable to add/update item', putErr);
            } else {
                console.log('Item added/updated successfully', putData);
                closeModal();  // Close the modal after successful submission
            }
        });
    });
}

function fetchDetails() {
    // Get literature code from the input
    const code = document.getElementById('code').value;

    // Replace 'E' with the dynamically selected language code
    const selectedLanguageCode = document.getElementById('languageSelector').value;

    // DynamoDB query parameters for stock table
    const stockParams = {
        TableName: stockTableName,
        KeyConditionExpression: 'congId = :id',
        ExpressionAttributeValues: {
            ':id': '1', // Assuming a fixed congId for this example
        },
        ProjectionExpression: `languages.${selectedLanguageCode}.#code.frontQuantity, languages.${selectedLanguageCode}.#code.backQuantity, languages.${selectedLanguageCode}.#code.totalQuantity`,
        ExpressionAttributeNames: {
            '#code': `${code}`
        }
    };

    // DynamoDB query parameters for literatureRefData table
    const literatureParams = {
        TableName: literatureRefTableName,
        Key: {
            'litCode': code
        },
        ProjectionExpression: 'title, image'
    };

    // Use DynamoDB DocumentClient to query stock table
    dynamoDB.query(stockParams, function (stockErr, stockData) {
        if (stockErr) {
            console.error('Error fetching stock data:', stockErr);
            return;
        }

        // Use DynamoDB DocumentClient to query literatureRefData table
        dynamoDB.get(literatureParams, function (literatureErr, literatureData) {
            if (literatureErr) {
                console.error('Error fetching literature data:', literatureErr);
                return;
            }

            // Extract relevant data from the query results
            const stockItem = stockData.Items[0];
            const literatureItem = literatureData.Item;

            // Populate form fields with the retrieved data
            document.getElementById('literatureName').value = literatureItem.title || 'Not Available';
            document.getElementById('currentTotalQuantity').value = stockItem ? stockItem.languages[selectedLanguageCode][code].totalQuantity || 0 : 0;
            document.getElementById('frontQuantity').value = stockItem ? stockItem.languages[selectedLanguageCode][code].frontQuantity || 0 : 0;
            document.getElementById('backQuantity').value = stockItem ? stockItem.languages[selectedLanguageCode][code].backQuantity || 0 : 0;

            // Set the image source
            const literatureImage = document.getElementById('literatureImage');
            const imageContainer = document.getElementById('imageContainer');

            // Hide the image if no image is available
            if (literatureItem.image) {
                literatureImage.src = literatureItem.image; // Assuming 'image' is a URL
                imageContainer.style.display = 'block'; // Show the image container
            } else {
                literatureImage.src = ''; // Clear the image source
                imageContainer.style.display = 'none'; // Hide the image container
            }
        });
    });
}

// JavaScript function to handle stock type selection
function selectStockType(type) {
    // Remove 'selected' class from all buttons
    document.querySelectorAll('.stock-button').forEach(function(button) {
        button.classList.remove('selected');
    });

    // Add 'selected' class to the clicked button
    var selectedButton = document.querySelector('.stock-button[for="' + type + 'Stock"]');
    if (selectedButton) {
        selectedButton.classList.add('selected');
    }

    // Update the hidden input value
    document.getElementById('selectedStockType').value = type;
}

// Event listener to fetch details when ID is entered in the modal
document.getElementById("code").addEventListener("blur", fetchDetails);

// Function to initialize the page and set up event listeners
function initializePage() {
    // Add event listener to the quantity input
    var quantityInput = document.getElementById('quantity');
    if (quantityInput) {
        quantityInput.addEventListener('input', updateAuxiliaryText);
    }

    // Add event listener to the parent element of stock type radio inputs
    var stockButtonList = document.querySelectorAll('label[class="stock-button"]');
    stockButtonList.forEach(function (button) {
        button.addEventListener('click', updateAuxiliaryText);
    });

    // Initial call to set up auxiliary text based on the initial state
    updateAuxiliaryText();
}

// Function to update auxiliary text based on the current state
function updateAuxiliaryText() {

    var quantityInput = document.getElementById('quantity');
    var auxiliaryText = document.getElementById('auxiliaryText');
    var stockTypeRadio = document.querySelector('input[name="stockType"]');
    var currentTotalQuantityInput = document.getElementById('currentTotalQuantity');
    var frontQuantityInput = document.getElementById('frontQuantity');
    var backQuantityInput = document.getElementById('backQuantity');

    if (quantityInput && stockTypeRadio && currentTotalQuantityInput && frontQuantityInput && backQuantityInput) {
        var stockType = stockTypeRadio.value;

        var currentTotalQuantity = parseInt(currentTotalQuantityInput.value, 10) || 0;
        var currentFrontQuantity = parseInt(frontQuantityInput.value, 10) || 0;
        var currentBackQuantity = parseInt(backQuantityInput.value, 10) || 0;

        var currentQuantity;

        switch (stockType) {
            case 'back':
                currentQuantity = currentBackQuantity;
                break;
            case 'front':
                currentQuantity = currentFrontQuantity;
                break;
            default:
                break;
        }

        var enteredQuantity = parseInt(quantityInput.value, 10);
        var quantityDifference = enteredQuantity - currentQuantity;

        if (quantityDifference > 0) {
            auxiliaryText.textContent = `+ ${quantityDifference} (received)`;
        } else if (quantityDifference < 0) {
            auxiliaryText.textContent = `- ${Math.abs(quantityDifference)} (moved)`;
        } else {
            auxiliaryText.textContent = 'no change';
        }
    }
}

// Call the initializePage function when the page is fully loaded
window.onload = initializePage;
function openModal() {
    document.getElementById("myModal").style.display = "flex";
}

function closeModal() {
    document.getElementById("myModal").style.display = "none";
}

function submitForm() {
    var code = document.getElementById('code').value;
    var stockType = document.querySelector('input[name="stockType"]:checked').value;
    var quantity = document.getElementById("quantity").value;

    // Example: Display the submitted values in the console
    console.log("Code:", code);
    console.log("Stock Type:", stockType);
    console.log("Quantity:", quantity);

    // You can now make a POST request with the gathered data
    // For simplicity, this example just displays the values in the console
    // In a real application, you would send these values to a server
    // using AJAX or fetch to perform the desired operations.

    var data = {
        code: code,
        stockType: stockType,
        quantity: quantity
    };

    // This will be my Lambda function URL
    // Making a POST request using Fetch API
    fetch('https://your-api-endpoint.com/addItem', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
            // Add any other headers if needed
        },
        body: JSON.stringify(data)
    })
        .then(response => response.json())
        .then(data => {
            // Handle the response data as needed
            console.log('POST Request Response:', data);
        })
        .catch(error => {
            console.error('Error:', error);
        });




    // After handling the submission, you can close the modal
    closeModal();
}

// Function to simulate fetching details based on the entered Code
function fetchDetails() {
    // This is just a placeholder function; replace it with actual fetching logic
    var code = document.getElementById("code").value;

    // Example: Displaying dummy data in the form
    document.getElementById("literatureName").value = "Sample Literature";
    document.getElementById("currentTotalQuantity").value = 100;
    document.getElementById("frontQuantity").value = 40;
    document.getElementById("backQuantity").value = 60;
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
function addItem() {
    // Get the ID from the form
    var itemId = document.getElementById("itemId").value;


    // Making a GET request using Fetch API
    fetch(`https://your-api-endpoint.com/getDetails?id=${itemId}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
            // Add any other headers if needed
        }
    })
        .then(response => response.json())
        .then(data => {
            // Handle the response data as needed
            console.log('GET Request Response:', data);

            // Populate the form fields with fetched data
            document.getElementById("literatureName").value = data.literatureName;
            document.getElementById("currentTotalQuantity").value = data.currentTotalQuantity;
            document.getElementById("frontQuantity").value = data.frontQuantity;
            document.getElementById("backQuantity").value = data.backQuantity;
        })
        .catch(error => {
            console.error('Error:', error);
        });


    // Below is sample code that listen to event and populates
    // // Add the item to the table
    // var table = document.getElementById("stockList");
    // var newRow = table.insertRow(table.rows.length);
    // var cell1 = newRow.insertCell(0);
    // cell1.innerHTML = itemId;
    // // Add more cells for additional item details as needed

    // Close the modal
    closeModal();
}

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
            case 'total':
                currentQuantity = currentTotalQuantity;
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
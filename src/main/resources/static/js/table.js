// Add this function to fetch all items from DynamoDB and populate the main screen table
function fetchAllItems() {
    const stockListTable = document.getElementById('stockList');
    stockListTable.innerHTML = ''; // Clear existing table content

    const selectedLanguageCode = document.getElementById('languageSelector').value;

    // DynamoDB scan parameters for stock table
    const scanParams = {
        TableName: stockTableName,
    };

    // Use DynamoDB DocumentClient to perform a scan operation
    dynamoDB.scan(scanParams, function (scanErr, scanData) {
        if (scanErr) {
            console.error('Error fetching all items:', scanErr);
            // Handle error, display a message, or take appropriate action
            return;
        }

        // Extract relevant data from the scan results
        const congregationList = scanData.Items;

        // Populate the main screen table with the retrieved data
        congregationList.forEach(congregation => {

            if (!isObjectEmpty(congregation.languages) && congregation.languages[selectedLanguageCode] != null) {

                let allLangLiterature = congregation.languages[selectedLanguageCode]
                const literatureCodes = [];

                // Inside the congregationList.forEach loop
                Object.keys(allLangLiterature).forEach(litCode => {
                    literatureCodes.push(litCode);
                });

                Promise.all(getLitDetailsFromRefTable(literatureCodes))
                    .then(literatureDetails => {
                        // Now you can use this information to populate litNameCell and literatureImageCells
                        literatureDetails.forEach(({ litCode, details }) => {
                            const row = stockListTable.insertRow();

                            const congIdCell = row.insertCell(0);
                            const litImageCell = row.insertCell(1);
                            const litCodeCell = row.insertCell(2);
                            const litNameCell = row.insertCell(3);
                            const frontQuantityCell = row.insertCell(4);
                            const backQuantityCell = row.insertCell(5);
                            const totalQuantityCell = row.insertCell(6);

                            let litItem = allLangLiterature[litCode]

                            litCodeCell.innerHTML = litCode;
                            congIdCell.innerHTML = '1'; // todo: hardcoded
                            frontQuantityCell.innerHTML = litItem.frontQuantity || 0;
                            backQuantityCell.innerHTML = litItem.backQuantity || 0;
                            totalQuantityCell.innerHTML = litItem.totalQuantity || 0;
                            litNameCell.innerHTML = details.title || 'N/A';

                            // Create img element for the image cell
                            const imgElement = document.createElement('img');
                            imgElement.style.display = 'block'; // Show the image cell

                            // Check if the image is available before setting the src attribute
                            if (details.image) {
                                imgElement.src = details.image;
                            }

                            // Append the img element to the litImageCell
                            litImageCell.appendChild(imgElement);

                        });
                    })
                    .catch(error => {
                        // Handle error, display a message, or take appropriate action
                        console.error('Error fetching literature details:', error);
                    });
            }

        });
    });
}

function getLitDetailsFromRefTable(literatureCodes) {

    // After fetching literature codes, make a query to get details
    return literatureCodes.map(litCode => {
        const literatureParams = {
            TableName: literatureRefTableName,
            Key: {
                'litCode': litCode
            },
            ProjectionExpression: 'title, image'
        };

        return new Promise((resolve, reject) => {
            dynamoDB.get(literatureParams, (literatureErr, literatureData) => {
                if (literatureErr) {
                    console.error('Error fetching literature details:', literatureErr);
                    reject(literatureErr);
                } else {
                    resolve({litCode, details: literatureData.Item});
                }
            });
        });
    });
}

// Call fetchAllItems when the page loads
document.addEventListener('DOMContentLoaded', fetchAllItems);

AWS.config.update({
    region: 'us-east-1',
    credentials: new AWS.Credentials('PUBLIC KEY', 'SECRET KEY'),
});

const dynamoDB = new AWS.DynamoDB.DocumentClient();
const stockTableName = 'congStock';
const literatureRefTableName = 'literatureRefData';

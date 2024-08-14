const express = require('express');

const app = express ();
app.use(express.json());

const PORT = process.env.PORT || 3000;

app.listen(PORT, () => {
  console.log("Server Listening on PORT:", PORT);
});

function getRandomInt(max) {
    return Math.floor(Math.random() * max);
}

function getRandomStr(length) {
    let result = '';
    const characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    const charactersLength = characters.length;
    let counter = 0;
    while (counter < length) {
        result += characters.charAt(Math.floor(Math.random() * charactersLength));
        counter += 1;
    }
    return result;
}

function generateResponse() {
    let response = {
        "code": 0,
        "message": "success",
        "number": getRandomInt(100000),
        "text": getRandomStr(500),
        "records": [
            {
                "id": getRandomInt(100),
                "name": getRandomStr(12),
                "location": {
                    "country": "AT",
                    "ZIP": "4040",
                    "city": "Linz",
                    "office": "GWK"
                }
            },
            {
                "id": getRandomInt(100),
                "name": getRandomStr(12),
                "location": {
                    "country": "AT",
                    "ZIP": "4040",
                    "city": "Linz",
                    "office": "GWK"
                }
            },
            {
                "id": getRandomInt(100),
                "name": getRandomStr(12),
                "location": {
                    "country": "AT",
                    "ZIP": "4040",
                    "city": "Linz",
                    "office": "GWK"
                }
            },
            {
                "id": getRandomInt(100),
                "name": getRandomStr(12),
                "location": {
                    "country": "AT",
                    "ZIP": "4040",
                    "city": "Linz",
                    "office": "GWK"
                }
            },
            {
                "id": getRandomInt(100),
                "name": getRandomStr(12),
                "location": {
                    "country": "AT",
                    "ZIP": "4040",
                    "city": "Linz",
                    "office": "GWK"
                }
            },
            {
                "id": getRandomInt(100),
                "name": getRandomStr(12),
                "location": {
                    "country": "AT",
                    "ZIP": "4040",
                    "city": "Linz",
                    "office": "GWK"
                }
            },
            {
                "id": getRandomInt(100),
                "name": getRandomStr(12),
                "location": {
                    "country": "AT",
                    "ZIP": "4040",
                    "city": "Linz",
                    "office": "GWK"
                }
            },
            {
                "id": getRandomInt(100),
                "name": getRandomStr(12),
                "location": {
                    "country": "AT",
                    "ZIP": "4040",
                    "city": "Linz",
                    "office": "GWK"
                }
            },
            {
                "id": getRandomInt(100),
                "name": getRandomStr(12),
                "location": {
                    "country": "AT",
                    "ZIP": "4040",
                    "city": "Linz",
                    "office": "GWK"
                }
            },
            {
                "id": getRandomInt(100),
                "name": getRandomStr(12),
                "location": {
                    "country": "AT",
                    "ZIP": "4040",
                    "city": "Linz",
                    "office": "GWK"
                }
            },
            {
                "id": getRandomInt(100),
                "name": getRandomStr(12),
                "location": {
                    "country": "AT",
                    "ZIP": "4040",
                    "city": "Linz",
                    "office": "GWK"
                }
            },
            {
                "id": getRandomInt(100),
                "name": getRandomStr(12),
                "location": {
                    "country": "AT",
                    "ZIP": "4040",
                    "city": "Linz",
                    "office": "GWK"
                }
            },
            {
                "id": getRandomInt(100),
                "name": getRandomStr(12),
                "location": {
                    "country": "AT",
                    "ZIP": "4040",
                    "city": "Linz",
                    "office": "GWK"
                }
            },
            {
                "id": getRandomInt(100),
                "name": getRandomStr(12),
                "location": {
                    "country": "AT",
                    "ZIP": "4040",
                    "city": "Linz",
                    "office": "GWK"
                }
            }
        ]
    };

    return response;
}

app.get("/status", (request, response) => {
   const status = {
      "Status": "Running"
   };
   // console.log("Running");
   response.send(status);
});

app.get("/test", (request, response) => {
    // console.log("Testing");
    response.send(generateResponse());
});

const http = require('http');


// let result = http.get('http://localhost:3000/test', (res) => {
//         if (res.statusCode !== 200) {
//             console.error(`Did not get an OK from the server. Code: ${res.statusCode}`);
//             res.resume();
//             return;
//         }
//
//         console.log('Retrieved all data');
//         // console.log(res);
// });

for (let i = 0; i < 5; i++) {
    http.get('http://localhost:3000/test');
}
// console.log(result);
console.log('done');

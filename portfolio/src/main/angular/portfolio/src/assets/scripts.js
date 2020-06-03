/**
 * Fetches data from Java servlet
 */
function getData() {
    console.log('Fetching a random quote');

    const responsePromise = fetch('/data');


    responsePromise.then(handleResponse);
}

function niceFun() {
    document.getElementById('display').innerText = "Hello darkness my old friend";

}
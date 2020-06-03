/**
 * Fetches data from Java servlet and displays it in HTML div
 */
async function getDataAsync() {
    console.log('Fetching data from server');
    
    // fetching data from /data endpoint in Java servlet
    const response = await fetch('/data');

    const data = await response.text();

    document.getElementById('display').innerHTML = data;
}

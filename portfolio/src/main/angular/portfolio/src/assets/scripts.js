/**
 * Fetches data from Java servlet and displays it in HTML div
 */
function getData() {
    fetch('/data').then(response => response.json()).then((data) =>{

        const display = document.getElementById("display");

        display.innerHTML = '';

        for (var i = 0; i < data.length; i++) {
            display.appendChild(
                createListElement(data[i])
            );
        }
    });
}

function createListElement(text) {
    const liElem = document.createElement('li');
    liElem.innerText = text;
    return liElem;
}

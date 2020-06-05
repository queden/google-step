/**
 * Fetches data from Java servlet and displays it in HTML div
 */
export function getData() {
    console.log("Getting data from servlet");

    const maxComments = document.getElementById("max-comments").value;

    fetch(`/data?max-comments=${maxComments}`).then(response => response.json()).then((data) =>{

        const display = document.getElementById("display");

        display.innerHTML = '';

        for (var i = 0; i < data.length; i++) {
            display.appendChild(
                createListElement(data[i])
            );
        }
    });
}

export function deleteData() {
    console.log("Posting to /delete-data");

    const request = new Request('/delete-data', {method: 'POST'});

    // Posts to /delete-data to delete all comments then
    // gets contents of server to ensure all is deleted
    fetch(request).then(getData());
}

function createListElement(text) {
    const liElem = document.createElement('li');
    liElem.innerText = text;
    return liElem;
}

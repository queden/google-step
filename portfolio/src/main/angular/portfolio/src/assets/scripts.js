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
            display.appendChild(createComment(data[i]));
        }

        if (data === undefined || data.length == 0) {
            const pTag = document.createElement('p');
            pTag.innerText = "No comments to show : (";
            display.appendChild(pTag);
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

function createComment(data) {
    const liElem = document.createElement('li');
    liElem.className = 'comment';
    liElem.style.border = '1px solid black';
    liElem.style.margin = '0.5%';

    const userElem = document.createElement('h4');
    userElem.innerText = data.name;

    const dateElem = document.createElement('h5');
    const date = new Date(data.timestamp); // Multiply by 1000 as JS counts in ms, not seconds
    const dateStr = date.toString(); 
    dateElem.innerText = dateStr;

    const commElem = document.createElement('p');
    commElem.readOnly = true;
    commElem.innerText = data.comment;
    
    liElem.appendChild(userElem);
    // liElem.appendChild(document.createElement('br'));
    liElem.appendChild(dateElem); 
    // liElem.appendChild(document.createElement('br'));
    liElem.appendChild(commElem);

    return liElem;
}

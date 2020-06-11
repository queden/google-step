export function drawGauge() {
    var moodSum = 0;
    var commentCount = 0;

    fetch('/data?max-comments=all').then(response => response.json()).then((data) => {
        for (var i = 0; i < data.length; i++) {
            moodSum += data[i].mood;
            commentCount++;
        }

        var avgMood = 0;
        if (commentCount != 0) {
            avgMood = moodSum / commentCount;
        }

        avgMood = Math.round(avgMood);

        var data = google.visualization.arrayToDataTable([
            ['Label', 'Value'],
            ['Mood', avgMood]
        ]);

        var options = {
            width: '100%', 
            height: '100%',
            greenFrom: 87.5, greenTo: 100,
        };
    
        var chart = new google.visualization.Gauge(document.getElementById('chart-div'));

        chart.draw(data, options);

        const moodFeedback = document.getElementById('mood-feedback');
        var fontColor = "green";
        var message = "! Woohoo!"
        if (avgMood <= 65) {
            fontColor = "red";
            message = "? Oh no! I hope everyone's okay."
        }
        else if (avgMood < 87.5) {
            fontColor = "orange";
            message = ". Not great, always here for you guys."
        }
        var mood = String(avgMood).fontcolor(fontColor);
        moodFeedback.innerHTML = `Average mood is ${mood}${message}`;
    });
}
 

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
    if (!data.name || data.name.length === 0) {
        userElem.innerText = 'anon';
    }
    else {
        userElem.innerText = data.name;
    }

    const moodElem = document.createElement('h5');
    var fontColor = "green";
    if (data.mood <= 65) {
        fontColor = "red";
    }
    else if (data.mood < 87.5) {
        fontColor = "orange";
    }
    var mood = String(data.mood).fontcolor(fontColor);
    moodElem.innerHTML = `Mood: ${mood}/100`;


    const dateElem = document.createElement('h5');
    const date = new Date(data.timestamp);
    const dateStr = date.toString(); 
    dateElem.innerText = dateStr;

    const commElem = document.createElement('p');
    commElem.readOnly = true;
    commElem.innerText = data.comment;
    
    liElem.appendChild(userElem);
    liElem.appendChild(moodElem);
    liElem.appendChild(dateElem); 
    liElem.appendChild(commElem);

    return liElem;
}

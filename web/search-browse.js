function handleTitleGenreBrwose(resultData){
    console.log("handleTitleBrwose: populating title list");

    let titleList = jQuery("#title-list");
    let genreList = jQuery("#genre-list");
    let numberList = ["1", "2", "3", "4", "5", "6", "7", "8", "9", "0"];
    let alphabetList = ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"];

    for (let i = 0; i < 2; i++){ //populating numberList
        let rowNum = "";
        rowNum += "<li>";
        for (let y = 0; y < 5; y++){
            let curElement = numberList.shift();
            rowNum += "<a href='index.html?start=" +
                curElement + "&firstsort=rating&secondsort=title&firstmethod=desc&secondmethod=asc&resultperpage=20&page=0'>" + curElement + "</a> ";
        }
        rowNum += "</li>";
        titleList.append(rowNum);
    }

    for (let i = 0; i < 4; i++){ //populating alphabetList
        let rowAlpha = "";
        rowAlpha += "<li>";
        for (let y = 0; y < 6; y++){
            let curElement = alphabetList.shift();
            rowAlpha += "<a href='index.html?start=" +
                curElement + "&firstsort=rating&secondsort=title&firstmethod=desc&secondmethod=asc&resultperpage=20&page=0'>" + curElement + "</a> ";
        }
        rowAlpha += "</li>";
        titleList.append(rowAlpha);
    }

    titleList.append("<li><a href='index.html?start=*'>*</a>");

    let blockGenre = "<p>";
    for (let i = 0; i < resultData.length; i++){
        blockGenre += "<a href='index.html?genre=" +
            resultData[i] + "&firstsort=rating&secondsort=title&firstmethod=desc&secondmethod=asc&resultperpage=20&page=0'>" + resultData[i] + "</a> ";
    }
    blockGenre += "</p>";
    genreList.append(blockGenre);
}

jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/browse", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleTitleGenreBrwose(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});
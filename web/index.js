function handleMovieListResult(resultData) {
    console.log("handleMovieListResult: populating movieList table from resultData");

    let movieListTableBodyElement = jQuery("#movieList_table_body");

    for (let i = 0; i < Math.min(20, resultData.length); i++) {

        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>"+'<a href="single-movie.html?id=' +
            resultData[i]['movie_id']+'">'+
            resultData[i]["movie_title"] + '</a>'+"</th>";    // display movie_name for the link text

        rowHTML += "<th>" + resultData[i]["movie_year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_director"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_rating"] + "</th>";
        rowHTML += "<th>";
        for (let y = 0; y<resultData[i]["movie_genres"].length; y++){
            rowHTML += '<span class="badge badge-secondary font-weight-light">'+resultData[i]["movie_genres"][y] + "</span> ";
        }
        rowHTML += "</th>";


        rowHTML += "<th>";
        for (let y = 0; y<resultData[i]["movie_stars"].length; y++){
            // console.log(resultData[i]["movie_stars"][y]);
            rowHTML += '<a class="badge badge-info font-weight-light" href="single-star.html?id=' +
                resultData[i]['movie_stars_id'][y] + '">' +
                resultData[i]["movie_stars"][y] + "</a>" + "  ";
        }

        rowHTML += "</th>";


        // rowHTML += "<th>" + resultData[i]["movie_genres_one"] + ", " + resultData[i]["movie_genres_two"] + ", " + resultData[i]["movie_genres_three"] + ", " + "</th>";
        // rowHTML += "<th>" + resultData[i]["movie_stars_one"] + ", " + resultData[i]["movie_stars_two"] + ", " + resultData[i]["movie_stars_three"] + ", " + "</th>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieListTableBodyElement.append(rowHTML);
    }
}

function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Ues regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

let start = getParameterByName('start');
let genre = getParameterByName('genre');
let search = getParameterByName('search');
if( search==null ){
    console.log(1);
    jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "GET", // Setting request method
        url: "api/movielist?start=" + start + "&genre=" + genre + "&search="+search, // Setting request url, which is mapped by StarsServlet in Stars.java
        success: (resultData) => handleMovieListResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
    });
}else{
    console.log(2);
    let title = getParameterByName('title');
    let director = getParameterByName('director');
    let year = getParameterByName('year');
    let star_name = getParameterByName('star_name');
    jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "GET", // Setting request method
        url: "api/movielist?search=true&title=" + title + "&director=" + director + "&year=" + year + "&star_name=" + star_name, // Setting request url, which is mapped by StarsServlet in Stars.java
        success: (resultData) => handleMovieListResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
    });
}


// Makes the HTTP GET request and registers on success callback function handleStarResult
/*
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/movielist?start=" + start + "&genre=" + genre, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleMovieListResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});

 */
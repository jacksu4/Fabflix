function handleMovieListResult(resultData) {
    console.log("handleMovieListResult: populating movieList table from resultData");

    let movieListTableBodyElement = jQuery("#movieList_table_body");

    for (let i = 0; i < Math.min(20, resultData.length); i++) {

        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>"+'<a href="single-movie.html?id='+resultData[i]['movie_id']+'">'+ resultData[i]["movie_title"] + '</a>'+"</th>";    // display movie_name for the link text
        rowHTML += "<th>" + resultData[i]["movie_year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_director"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_rating"] + "</th>";
        rowHTML += "<th>";
        for (let y = 0; y<resultData[i]["movie_genres"].length; y++){
            rowHTML += resultData[i]["movie_genres"][y] + " ";
        }
        rowHTML += "</th>";


        rowHTML += "<th>";
        for (let y = 0; y<resultData[i]["movie_stars"].length; y++){
            // console.log(resultData[i]["movie_stars"][y]);
            rowHTML += '<a href="single-star.html?id=' +
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




// Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/movielist", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleMovieListResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});
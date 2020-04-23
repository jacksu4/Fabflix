function handleMovieListResult(resultData) {
    console.log("handleMovieListResult: populating movieList table from resultData");

    let movieListTableBodyElement = jQuery("#movieList_table_body");

    for (let i = 0; i < Math.min(100, resultData.length); i++) {

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

        if (resultData.length < resultperpage){
            $("#next").removeAttr('href');
        }
        else{
            if(search==null){
                let hyperlink = "index.html?start=" + start + "&genre=" + genre
                    + "&firstsort=" + firstsort + "&secondsort=" + secondsort + "&firstmethod=" + firstmethod
                    + "&secondmethod=" + secondmethod +"&resultperpage=" + resultperpage + "&page=" + (parseInt(page)+1).toString();
                $("#next").attr("href", hyperlink);
            }
            else{
                let hyperlink = "index.html?" + "search=" + search + "&title=" + title + "&director=" + director + "&year=" + year + "&star_name=" + star_name
                    + "&firstsort=" + firstsort + "&secondsort=" + secondsort + "&firstmethod=" + firstmethod
                    + "&secondmethod=" + secondmethod +"&resultperpage=" + resultperpage + "&page=" + (parseInt(page)+1).toString();
                $("#next").attr("href", hyperlink);
            }
        }

        if(page==0){
            $("#prev").removeAttr('href');
        }else{
            if(search==null){
                let hyperlink = "index.html?start=" + start + "&genre=" + genre
                    + "&firstsort=" + firstsort + "&secondsort=" + secondsort + "&firstmethod=" + firstmethod
                    + "&secondmethod=" + secondmethod +"&resultperpage=" + resultperpage + "&page=" + (parseInt(page)-1).toString();
                $("#prev").attr("href", hyperlink);
            }else{
                let hyperlink = "index.html?" + "search=" + search + "&title=" + title + "&director=" + director + "&year=" + year + "&star_name=" + star_name
                    + "&firstsort=" + firstsort + "&secondsort=" + secondsort + "&firstmethod=" + firstmethod
                    + "&secondmethod=" + secondmethod +"&resultperpage=" + resultperpage + "&page=" + (parseInt(page)-1).toString();
                $("#prev").attr("href", hyperlink);
            }

        }
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

var start = getParameterByName('start');
var genre = getParameterByName('genre');
var search = getParameterByName('search');

var title = getParameterByName('title');
var director = getParameterByName('director');
var year = getParameterByName('year');
var star_name = getParameterByName('star_name');

var $select = $('select[name="pageresult"]');//generate page option
var firstsort = getParameterByName('firstsort');
var secondsort = getParameterByName('secondsort');
var firstmethod = getParameterByName('firstmethod');
var secondmethod = getParameterByName('secondmethod');
var resultperpage = getParameterByName('resultperpage');
var page = getParameterByName('page');


if(search==null) {
    jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "GET", // Setting request method
        url: "api/movielist?start=" + start + "&genre=" + genre + "&search=" + search
            + "&firstsort=" + firstsort + "&secondsort=" + secondsort + "&firstmethod=" + firstmethod
            + "&secondmethod=" + secondmethod +"&resultperpage=" + resultperpage + "&page=" + page, // Setting request url, which is mapped by StarsServlet in Stars.java
        success: (resultData) => handleMovieListResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
    });
} else {
    jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "GET", // Setting request method
        url: "api/movielist?" + "search=" + search + "&title=" + title + "&director=" + director + "&year=" + year + "&star_name=" + star_name
            + "&firstsort=" + firstsort + "&secondsort=" + secondsort + "&firstmethod=" + firstmethod
            + "&secondmethod=" + secondmethod +"&resultperpage=" + resultperpage + "&page=" + page, // Setting request url, which is mapped by StarsServlet in Stars.java
        success: (resultData) => handleMovieListResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
    });
}

$select.append(
    $('<option />')
        .text('5')
        .val('5'),
    $('<option />')
        .text('20')
        .val('20'),
    $('<option />')
        .text('25')
        .val('25'),
    $('<option />')
        .text('100')
        .val('100'),
);

$select.on("change", function(){
    resultperpage = this.value;
});

var $firstsort = $('select[name="firstsort"]');

$firstsort.append(
    $('<option />')
        .text('rating')
        .val('rating'),
    $('<option />')
        .text('title')
        .val('title'),
)

$firstsort.on("change", function(){
    firstsort = this.value;
    if (this.value === "rating"){
        secondsort = "title";
    } else {
        secondsort = "rating";
    }
});

var $pagemethod = $('select[name="pagemethod"]');

$pagemethod.append(
    $('<option />')
        .text('asc desc')
        .val('asc desc'),
    $('<option />')
        .text('desc asc')
        .val('desc asc'),
    $('<option />')
        .text('asc asc')
        .val('asc asc'),
    $('<option />')
        .text('desc desc')
        .val('desc desc'),
)

$pagemethod.on("change", function(){
    let splitedArray = this.value.split(/(\s+)/);
    firstmethod = splitedArray[0];
    secondmethod = splitedArray[1];
});

$("#submit_sort").bind( "click", function(){
    if (search==null){
        console.log("enter nonsearch");
        window.location.href = "index.html?start=" + start + "&genre=" + genre
            + "&firstsort=" + firstsort + "&secondsort=" + secondsort + "&firstmethod=" + firstmethod
            + "&secondmethod=" + secondmethod +"&resultperpage=" + resultperpage + "&page=" + page;
    }else{
        window.location.href = "index.html?" + "search=" + search + "&title=" + title + "&director=" + director + "&year=" + year + "&star_name=" + star_name
            + "&firstsort=" + firstsort + "&secondsort=" + secondsort + "&firstmethod=" + firstmethod
            + "&secondmethod=" + secondmethod +"&resultperpage=" + resultperpage + "&page=" + page;
    }
});




// Makes the HTTP GET request and registers on success callback function handleStarResult
/*
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/movielist?start=" + start + "&genre=" + genre, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleMovieListResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});

 */

// $("#submit_sort").bind( "click", function(){
//     if( search==null ) {
//         jQuery.ajax({
//             dataType: "json", // Setting return data type
//             method: "GET", // Setting request method
//             url: "api/movielist?start=" + start + "&genre=" + genre + "&search=" + search
//                 + "&firstsort=" + firstsort + "&secondsort=" + secondsort + "&firstmethod=" + firstmethod
//                 + "&secondmethod=" + secondmethod +"&resultperpage=" + resultperpage + "&page=0", // Setting request url, which is mapped by StarsServlet in Stars.java
//             success: (resultData) => handleMovieListResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
//         });
//     } else {
//         jQuery.ajax({
//             dataType: "json", // Setting return data type
//             method: "GET", // Setting request method
//             url: "api/movielist?search=true&title=" + title + "&director=" + director + "&year=" + year + "&star_name=" + star_name
//                 + "&firstsort=" + firstsort + "&secondsort=" + secondsort + "&firstmethod=" + firstmethod
//                 + "&secondmethod=" + secondmethod +"&resultperpage=" + resultperpage + "&page=0", // Setting request url, which is mapped by StarsServlet in Stars.java
//             success: (resultData) => handleMovieListResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
//         });
//     }
// });










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

function handleResult(resultData){
    console.log("handleResult: populating single movie result from resultData");
    //populate the movie-info h3
    let movieTitleElement = jQuery("#movie-title");
    movieTitleElement.append("<p>"+resultData[0]["movie_title"]+"</p>");
    console.log(resultData[0]["movie_title"]);

    let movieInfoElement = jQuery("#movie-info");
    movieInfoElement.append("<p>Year: "+resultData[0]["movie_year"]+"</p>"+"<p>Director: "+resultData[0]["movie_director"]+"</p>"
        +"<p>Rating: "+resultData[0]["movie_rating"]+"</p>");

    let movieGenresElement = jQuery("#movie-genres");
    console.log(resultData[1]["genres"].length);
    for(let i=0;i<resultData[1]["genres"].length;i++){
        movieGenresElement.append("<li class=\"list-group\">"+resultData[1]["genres"][i]+"</li>");
    }

    let movieStarsElement = jQuery("#movie-stars");
    for(let j=0;j<resultData[1]["stars_name"].length;j++){
        movieStarsElement.append("<li class=\"list-group\">"+'<a href="single-star.html?id='+resultData[1]["stars_id"][j]+'">'+resultData[1]["stars_name"][j]+
            '</a>'+"</li>");
    }



}
let movieId = getParameterByName('id');
console.log(movieId);

jQuery.ajax({
    datatype: "json",
    method: "GET",
    url: "api/single-movie?id="+movieId,
    success: (resultData) => handleResult(resultData)
});
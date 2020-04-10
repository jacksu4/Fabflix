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
    let movieInfoElement = jQuery("#movie-info");
    movieInfoElement.append("<p>Year: "+resultData[0]["movie_year"]+"</p>"+"<p>Director: "+resultData[0]["movie_director"]+"</p>"
        +"<p>Rating: "+resultData[0]["movie_rating"]+"</p>");
}
let movieId = getParameterByName('id');
console.log(movieId);

jQuery.ajax({
    datatype: "json",
    method: "GET",
    url: "api/single-movie?id="+movieId,
    success: (resultData) => handleResult(resultData)
});
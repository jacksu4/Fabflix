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

function displayMessage(){
    console.log("successfully added to cart");
    let message = jQuery("#addToCartMessage");
    if(message.css("display")=="none"){
        console.log("hidden");
        message.fadeIn();
    }else{
        console.log("visible");
        message.fadeOut();
        message.fadeIn();
    }
}

function add(){
    console.log("function add() is called");
    let movieId = getParameterByName('id');
    $.ajax(
        "api/addToCart", {
            method: "get",
            // Serialize the login form to the data sent by POST request
            data: {"id":movieId},
            success: displayMessage
        }
    );
}


function handleResult(resultData){
    console.log("handleResult: populating single movie result from resultData");
    //console.log(resultData.length);
    //populate the movie-info h3
    let movieTitleElement = jQuery("#movie-title");
    movieTitleElement.append("<p>"+resultData[0]["movie_title"]+"</p>");
    //console.log(resultData[0]["movie_title"]);

    let movieInfoElement = jQuery("#movie-info");
    let infoHtml = "";
    infoHtml += '<p>Year: <span class="font-weight-light">'+resultData[0]["movie_year"]+"</span></p>"+'<p>Director: <span class=\"font-weight-light\">'+resultData[0]["movie_director"]+"</span></p>";
    if (resultData.length<3){
        infoHtml += '<p>Rating: <span class="font-weight-light">N/A </span></p>';
    }else{
        infoHtml += '<p>Rating: <span class=\"font-weight-light\">'+resultData[2]["movie_rating"]+"</span></p>";
    }
    movieInfoElement.append(infoHtml);

    let movieGenresElement = jQuery("#movie-genres");
    //console.log(resultData[1]["genres"].length);
    for(let i=0;i<resultData[1]["genres"].length;i++){
        movieGenresElement.append('<span class="badge badge-secondary font-weight-light">'+resultData[1]["genres"][i]+"</span> ");
    }

    let movieStarsElement = jQuery("#movie-stars");
    for(let j=0;j<resultData[1]["stars_name"].length;j++){
        movieStarsElement.append('<a class="badge badge-info font-weight-light" href="single-star.html?id='+resultData[1]["stars_id"][j]+'">'+resultData[1]["stars_name"][j]+
            '</a>');
    }
    let movielist_url = resultData[3]["movielist_url"];
    let backButton = jQuery("#backButton");
    backButton.click(function(){
        window.location.href = movielist_url;
    });

}
let movieId = getParameterByName('id');
console.log(movieId);

jQuery.ajax({
    datatype: "json",
    method: "GET",
    url: "api/single-movie?id="+movieId,
    success: (resultData) => handleResult(resultData)
});
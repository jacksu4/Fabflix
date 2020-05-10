let star_input = jQuery("#add_star");
let movie_input = jQuery("#add_movie");

function handleMetadata(resultData) {
    console.log("handleMetadata: populating Metadata");
    let datatype = {4:"Integer", 12:"Varchar", 91:"Date", 7:"float"};
    let metadata = jQuery("#metadata");

    for(let i=0; i<resultData.length; i++){
        let rowHTML = "";
        rowHTML += "<li>" + resultData[i][0][0];
        rowHTML += "<ul>";
        for(let y=0; y<resultData[i].length; y++){
            rowHTML += "<li>" + resultData[i][y][1] + " " + datatype[resultData[i][y][2]] + "</li>";
        }
        rowHTML += "</ul>";
        rowHTML += "</li>";
        metadata.append(rowHTML)
    }
}

function output_star(resultData){
    $("#star_message").text("star added " + resultData);
}




function submit_movie_info(formSubmitEvent){
    console.log("submit star info");
    formSubmitEvent.preventDefault();

    $.ajax(
        "api/employee_add_movie", {
            method: "POST",
            data: movie_input.serialize(),
            success: handleMovieResult
        });
}

function handleMovieResult(resultDataString) {
    $("#movie_message").text(resultDataString);
}

function submit_star_info(formSubmitEvent){
    console.log("submit star info");
    formSubmitEvent.preventDefault();

    $.ajax(
        "api/employee_add_star", {
            method: "POST",
            data: star_input.serialize(),
            success: (Data) => output_star(Data)
});
}

jQuery.ajax({
    datatype: "json",
    method: "GET",
    url: "api/metadata",
    success: (resultData) => handleMetadata(resultData)
});

star_input.submit(submit_star_info);
movie_input.submit(submit_movie_info);
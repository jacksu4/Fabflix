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
    console.log("handleResult: populating shopping cart from result data");
    //console.log(resultData.length);
    //populate the movie-info h3
    let item_list = jQuery("#item-table");
    let total = 0;
    for(var i=0;i<resultData.length;i++){
        let html = "<tr>";
        html += "<th>"+(i+1)+"</th>";
        html += "<th>"+resultData[i]["movie_title"]+"</th>";
        html += "<th>"+resultData[i]["movie_price"]+"</th>";

        html += "<th>"+'<a href="shopping-cart.html?movie_id='+resultData[i]["movie_id"]+'&change=decrease">'
            +'<</a>'
            +'&nbsp&nbsp&nbsp'+resultData[i]["quantity"]+'&nbsp&nbsp&nbsp'
            +'<a href="shopping-cart.html?movie_id='+resultData[i]["movie_id"]+'&change=increase">'
            +'></a>'+"</th>";

        html += "<th>"+'<a class="btn btn-outline-danger stretched-link" href="shopping-cart.html?movie_id='
            +resultData[i]["movie_id"]+'&change=delete">'+"Delete Item</a></th>"

        html += "</tr>";
        item_list.append(html);
        total += resultData[i]["movie_price"]*resultData[i]["quantity"];
    }
    let total_price = jQuery("#total-price");
    total_price.append("Total Price: "+total);
    $("#proceedToPayment").attr("href","payment.html?total="+total);
}
let movie_id = getParameterByName('movie_id');
let change = getParameterByName('change');


if(movie_id==null){
    jQuery.ajax({
        datatype: "json",
        method: "GET",
        url: "api/shopping-cart",
        success: (resultData) => handleResult(resultData)
    });
}else{
    jQuery.ajax({
        datatype: "json",
        method: "GET",
        url: "api/shopping-cart?movie_id="+movie_id+"&change="+change,
        success: (resultData) => handleResult(resultData)
    });
}

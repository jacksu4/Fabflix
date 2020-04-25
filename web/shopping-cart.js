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
        html += "<th>"+resultData[i]["quantity"]+"</th>";
        html += "</tr>";
        item_list.append(html);
        total += resultData[i]["movie_price"]*resultData[i]["quantity"];
    }
    let total_price = jQuery("#total-price");
    total_price.append("Total Price: "+total);
}


jQuery.ajax({
    datatype: "json",
    method: "GET",
    url: "api/shopping-cart",
    success: (resultData) => handleResult(resultData)
});
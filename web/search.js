let search_form = $("#search-form");
let advance_search_form = $("#advance-search-form");

/**
 * Handle the data returned by SearchServlet
 * @param resultDataString jsonObject
 */
function handleAdvanceSearchResult(resultDataString) {
    console.log(resultDataString[0]);
    console.log("handle advance search response");
    let new_advance_address = "index.html?advance=true";

    new_advance_address += "&title="+resultDataString[0]["title"];
    new_advance_address += "&firstsort=rating&secondsort=title&firstmethod=desc&secondmethod=asc&resultperpage=20&page=0";

    window.location.replace(new_advance_address);
}

function handleSearchResult(resultDataString) {
    console.log(resultDataString[0]);
    console.log("handle search response");
    let new_address = "index.html?search=true";
    if(resultDataString[0]["title"]!=""){
        new_address += "&title="+resultDataString[0]["title"];
    }
    if(resultDataString[0]["year"]!=""){
        new_address += "&year="+resultDataString[0]["year"];
    }
    if(resultDataString[0]["director"]!=""){
        new_address += "&director="+resultDataString[0]["director"];
    }
    if(resultDataString[0]["star_name"]!=""){
        new_address += "&star_name="+resultDataString[0]["star_name"];
    }

    new_address += "&firstsort=rating&secondsort=title&firstmethod=desc&secondmethod=asc&resultperpage=20&page=0";

    window.location.replace(new_address);
}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitSearchForm(formSubmitEvent) {
    console.log("submit search form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();

    $.ajax(
        "api/search", {
            method: "POST",
            // Serialize the login form to the data sent by POST request
            data: search_form.serialize(),
            success: handleSearchResult
        }
    );
}

function submitAdvanceSearchForm(formSubmitEvent) {
    console.log("submit advance search form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();

    $.ajax(
        "api/advancesearch", {
            method: "POST",
            // Serialize the login form to the data sent by POST request
            data: advance_search_form.serialize(),
            success: handleAdvanceSearchResult
        }
    );
}

// Bind the submit action of the form to a handler function
search_form.submit(submitSearchForm);
advance_search_form.submit(submitAdvanceSearchForm);


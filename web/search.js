let search_form = $("#search-form");
let advance_search_form = $("#advance-search-form");
let auto_complete = $("#autocomplete");


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

function handleSelectSuggestion(suggestion) {

    window.location.href = suggestion["data"]["url"];
}

function handleLookup(query, doneCallback) {
    console.log("autocomplete initiated");

    if (localStorage.getItem(query)){
        doneCallback( { suggestions: JSON.parse(localStorage.getItem(query)) } );
        console.log("using cache");
        console.log(JSON.parse(localStorage.getItem(query)));
    } else{
        console.log("sending AJAX request to backend Java Servlet");
        jQuery.ajax({
            "method": "GET",
            // generate the request url from the query.
            // escape the query string to avoid errors caused by special characters
            "url": "api/autocomplete?title=" + escape(query),
            "success": function(data) {
                // pass the data, query, and doneCallback function into the success handler
                handleLookupAjaxSuccess(data, query, doneCallback)
            },
            "error": function(errorData) {
                console.log("lookup ajax error");
                console.log(errorData)
            }
        })
    }

    // sending the HTTP GET request to the Java Servlet endpoint hero-suggestion
    // with the query data
}

function handleLookupAjaxSuccess(data, query, doneCallback) {

    // parse the string into JSON
    // var jsonData = JSON.parse(data);
    console.log(data);


    localStorage.setItem(query, JSON.stringify(data));
    // call the callback function provided by the autocomplete library
    // add "{suggestions: jsonData}" to satisfy the library response format according to
    //   the "Response Format" section in documentation
    doneCallback( { suggestions: data } );
}

function handleNormalSearch(query) {
    console.log("doing normal search with query: " + query);

    let new_advance_address = "index.html?advance=true";
    new_advance_address += "&title="+query;
    new_advance_address += "&firstsort=rating&secondsort=title&firstmethod=desc&secondmethod=asc&resultperpage=20&page=0";
    window.location.replace(new_advance_address);
}

// Bind the submit action of the form to a handler function
search_form.submit(submitSearchForm);
advance_search_form.submit(submitAdvanceSearchForm);



auto_complete.autocomplete({
    // documentation of the lookup function can be found under the "Custom lookup function" section
    lookup: function (query, doneCallback) {
        handleLookup(query, doneCallback)
    },
    onSelect: function(suggestion) {
        handleSelectSuggestion(suggestion)
    },
    // set delay time
    deferRequestBy: 300,
    // there are some other parameters that you might want to use to satisfy all the requirements
    minChars: 3
});

auto_complete.keypress(function(event) {
    // keyCode 13 is the enter key
    if (event.keyCode == 13) {
        // pass the value of the input box to the handler function
        handleNormalSearch($('#autocomplete').val())
    }
});

<%@page pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="favicon.ico" rel="icon" type="image/x-icon" />
        <link href="favicon.ico" rel="shortcut icon" type="image/x-icon" />
        <link href="css/semdic.css" rel="stylesheet" type="text/css" />


        <title>The BuckPark Corpus</title>
        <script src="js/jquery-1.10.2.min.js"></script>
        <script>
        $(document).ready(function(event) {
//                alert('Loaded!');
                $('table#parse_gen').hide();
                $('#showLexiconSpan').hide();
                $('#lexicon').hide();
                $.post(
                    "kalam", 
                    {loading: "loading"},
//                    { sentenceToParse: sentenceToParse, 
//                        catType: catType }, 
                    function(responseText) { 
//                            $('#wait').text(responseText);         
                        //alert(responseText.loading);
                        $("select[class='gram'] option").remove();
                        $parse_gram = $("select[class='gram']");
                        $(responseText.loading).appendTo($parse_gram);
                        //$('#outResults').html(responseText.loading); //test to delete
                    },
                'json');
                $('.gram').change(function(event) {
                    //alert($("select#parse_gram option:selected").text());
                    if(($("select#parse_gram").val() > 0) && 
                        ($("select#gen_gram").val() > 0)) {
                            $("table#parse_gen").show();
                        }
                    else {
                        $("table#parse_gen").hide();
                        
                    }
                    if(this.id == "parse_gram") {
                        $('#lexicon').html("");
                        $('#lexicon').hide();
                        $('#showLexicon').attr('checked', false);
                    };
                    if($("select#parse_gram").val() > 0) {
                        $('#showLexiconSpan').show()
                    }
                    else {
                        $("#showLexiconSpan").hide()
                        $('#lexicon').hide();
                    }
                    //alert($(event.target).find(":selected").text());
                    if ($(event.target).find(":selected").val() > 0) {
                        $('#waitGram').text("Loading grammar '" + 
                            $(event.target).find(":selected").text() + "'...");
                        $.post(
                            "kalam", 
                            {target: event.target.id,
                                grammar: $(event.target).find(":selected").text()
                            },
    //                    { sentenceToParse: sentenceToParse, 
    //                        catType: catType }, 
                            function(responseText) { 
    //                            $('#wait').text(responseText);         
                            //alert(responseText.loading);
                            clearInterval(loading);
                            $('#waitGram').text('');
                            },
                        'json') };
                    var loading = setInterval(function() { $('#waitGram')[0].innerHTML += '.' }, 200);
                    return false;
                });
                $('#showLexicon').change(function() {
                    if(this.checked) {
                        if($('#lexicon').html() == "") {
                            $('#lexicon').text("Loading lexicon...");
                            $.post(
                                "kalam", 
                                {lexicon: ""},
                                function(responseText) { 
                                    $('#lexicon').html(responseText.lexicon);
                                    $('#lexicon').show();
                                    clearInterval(loading);
                                    },
                            'json');
                            var loading = setInterval(function() { $('#lexicon')[0].innerHTML += '.' }, 200);
                        }
                        $('#lexicon').show();
                    }
                    else $('#lexicon').hide();
                });
                $('#showDerivs').change(function() {
                    if($(this).is(':checked')) {
                        $('div.derivs').show();
                    }
                    else {
                        $('div.derivs').hide();
                    }
                
                });
                var actions = ['idle', 'initParseGen', 'parseSentences',
                        'buildParseList', 'realizeParses', 'createOutput'];
                var actions_descriptions = ['',
                    'Initializing the parser...', 'Parsing the input sentence...',
                        'Building the list of parses...', 'Realizing parses...', 
                        'Producing the output...'];
                var submit_kalam = function() {  
//                    var sentenceToParse=$('#sentenceToParse').val();
//                    var catType = $("#catType").val();
                    function adding_dots() {
                        if ($('#wait')[0].innerHTML != '') {
                            $('#wait')[0].innerHTML += '.';}
                        else {
                            clearInterval(loading)}
                    }
                    clearInterval(loading);
                    function response_text(responseText) { 
//                            $('#wait').text(responseText);         
                            //alert('triggering...');
                            //clearInterval(loading);
                            $('#wait').text('');
                            var curAction = $('input#action').val();
                            //alert(curAction);
                            //alert(responseText.outResults);
                            var actionPosition = $.inArray(curAction, actions);
                            if (curAction == 'idle') {
                                $('#outResults').html(responseText.outResults);
                                $('#outParses').html(responseText.outParses);
                            }
                                else {
                                    $('#wait').html(actions_descriptions[actionPosition]);
                                }
                            if (actionPosition > 0) {
                                $.ajax({
                                    type: 'POST',
                                    url: "kalam",
                                    data: $("#call_kalam").serialize(),
                                    success: response_text,
                                    dataType: 'json',
                                    async:true
                                  }
                                );
                                $('input#action').val(actions[(actionPosition + 1) % 6]).trigger('change');
                            }
                            /*var loading = setInterval(function() { 
                                adding_dots();
                                }, 200);*/
                            return false;
                        }
                    $('input#action').val('initParseGen');
                    $('#wait').text('Analyzing...');
                    $('#outResults').text('');
                    $('#outParses').text('');
                    $.ajax({
                        type: 'POST',
                        url: "kalam",
                        data: $("#call_kalam").serialize(),
                        success: response_text,
                        dataType: 'json',
                        async:true
                      }
                        );
                    var loading = setInterval(function() { 
                        adding_dots();
                        }, 500);
                    return false;
                }
                $('#submit_kalam').click(submit_kalam);
                 $("input").bind("keydown", function(event) {
               // track enter key
               var keycode = (event.keyCode ? event.keyCode : (event.which ? event.which : event.charCode));
               if (keycode == 13) { // keycode for enter key
                  // force the 'Enter Key' to implicitly click the Update button
                  document.getElementById('submit_kalam').click(event);
                  return false;
               } else  {
                  return true;
               }
            }); // end of function        

            });
        </script>
</head>
<body>
    <h1>The SemDic Project Parsing and Generation App</h1>
    <%@page import="java.security.Principal, org.apache.commons.lang3.text.WordUtils"%>
    <p><i>Welcome, </i></p>
        <form id="call_kalam" class="larger_form">
            <div id="grammar_choosing" class="parse_gen">
            <div class="choose_grammar">
            Parsing grammar:
                <select class="gram" id="parse_gram">
                    <option value="0">&lt;Select a grammar&gt;</option>
                </select>
                <span id="showLexiconSpan">
                <input type="checkbox" id="showLexicon" />
                Show parsing lexicon
                </span>
            </div>
            <div class="choose_grammar">
                Generation grammar:
                <select class="gram" id="gen_gram">
                    <option value="0">&lt;Select a grammar&gt;</option>
                </select>
            </div>
          <div id="lexicon" class="boxed"></div>
            
          <div id="waitGram" class="waiting">
            </div>
            </div>
    <div id="parse_gen" class="parse_gen">
        <div class="choose_grammar">Sentence to parse:
               <input type="text" id="sentenceToParse" 
                      name="sentenceToParse" size="80"/>
        </div>
        <div class="choose_grammar">
                <input type="radio" name="catType" value="sentences" 
                       checked="checked">
                Saturated categories only
                <input type="radio" name="catType" value="chunks">
                Include category chunks
                <input type="checkbox" name="showDerivs" id="showDerivs" />
                Show derivations
                <input id="submit_kalam" type="button" value="Submit">
                <!--<input type="button" id="submit" value="Ajax Submit"/>-->
                <input type="hidden" name="action" id="action" value="idle">
        </div>
    </div>
        </form>

          <div id="wait">
            </div>
          <div id="outResults">
            </div>
          <div id="outParses">
            </div>
        <div class="choose_grammar"><a href="index.jsp">Back</a> to the main page.
        </div>
</body>
</html>
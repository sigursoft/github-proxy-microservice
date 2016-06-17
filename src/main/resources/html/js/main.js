 "use strict";
 $(function(){

      $("#search").click(function(e) {
        e.preventDefault();
        var owner = $("#icon_prefix").val();
        var repo  = $("#icon_code").val();

        $.get({
                url: "/repositories/" + owner + "/" + repo,
                success: function(response) {
                    // create a card with 5 fields
                    var repository = JSON.parse(response);
                    $('#full_name').text(repository.fullName);
                    $('#description').text(repository.description);
                    $('#stars').text(repository.stars);
                    $('#clone_url').attr("href", repository.cloneUri).text(repository.cloneUri);
                    $('#created').text(repository.createdAt.replace("T"," ").replace("Z",""));
                    $('#modal1').openModal();
                }
              });
      });
 });

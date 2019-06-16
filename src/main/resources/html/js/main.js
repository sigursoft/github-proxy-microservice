 "use strict";
 $(function(){

     $('#modal1').modal();
     $("#search").click(function(e) {
         e.preventDefault();
         const owner = $("#icon_prefix").val();
         const repo  = $("#icon_code").val();
         $.get({
                url: "/repositories/" + owner + "/" + repo,
                success: function(repository) {
                    $('#full_name').text(repository.fullName);
                    $('#description').text(repository.description);
                    $('#stars').text(repository.stars);
                    $('#clone_url').attr("href", repository.cloneUri).text(repository.cloneUri);
                    $('#created').text(repository.createdAt.replace("T"," ").replace("Z",""));
                    $('#modal1').modal('open');
                }
              });
         });
 });

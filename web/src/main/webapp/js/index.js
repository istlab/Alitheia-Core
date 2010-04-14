var baseurl = "http://localhost:8080/api";

$(document).ready(function() {
  
  //Fill in projects
  $.getJSON(baseurl + "/projects",
      function(data){
        //$.each(data.collection.project, function(i, project){
          //$("<option/>").attr("value", project.id).appendTo("#projectSelect");
        //});
      });
})
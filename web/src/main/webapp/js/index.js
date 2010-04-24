$(document).ready(function() {

  $.ajax({
    url : "/web/proxy/projects",
    dataType : 'json',
    type : 'GET',
    success : function(xml) {
      $.each(data.collection.project, function(i, project) {
        $("<option/>").attr("value", project.id).appendTo("#projectSelect");
      });
      
      //$(xml).find("project").each(function() {
      //  $("#projectSelect").append("<option>" + $(this).find("name").text() + "</option>");
      //});
    },
    error : function(xhr, status, error) {
        alert("No projects found");
    }
  });
})
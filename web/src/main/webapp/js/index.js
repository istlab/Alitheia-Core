var prefix = "/web";

$(document).ready(function() {
  
    getMetricTypes();
  
    $("#metricTypeSelect").change(function(){
      getMetrics($("#metricTypeSelect :selected").text());
    });
    
    $("#versionrange").slider({
      range: true,
      min: 0,
      max: 0,
      values: [0, 0],
      slide: function(event, ui) {
        $("#versionlbl").val(ui.values[0] + '-' + ui.values[1]);
      }
    });
    $("#versionlbl").val("Select a project please");
});


function getProjects() {
  $.ajax( {
    url : prefix + "/proxy/projects",
    dataType : 'json',
    type : 'GET',
    success : function(data) {
      $.each(data, function(i, obj) {
        $("<option/>").attr("value", obj.project.id).text(obj.project.name)
            .appendTo("#projectSelect");
      });

    },
    error : function(xhr, status, error) {
      alert("No projects found");
    }
  }); 
}


function getMetricTypes() {
  
  $.ajax({
    url : prefix + "/proxy/metrics/types",
    dataType : 'json',
    type : 'GET',
    success : function(data) {
      $.each(data, function(i, obj) {
        $("<option/>").attr("value", obj.metrictype.id)
            .text(obj.metrictype.type)
            .appendTo("#metricTypeSelect");
      });
    },
    error : function(xhr, status, error) {
      alert("No metric types found");
    }
  });
}

function getProjectVersion(version) {
  
}

function getMetrics(type) {
  $("#metricSelect").children().remove();
  if (type == null)
    $.ajax({
      url : prefix + "/proxy/metrics",
      dataType : 'json',
      type : 'GET',
      success : function(data) {
        $.each(data, function(i, obj) {
          $("<option/>").attr("value", obj.metric.id)
              .text(obj.metric.mnemonic)
              .appendTo("#metricSelect");
        });
      },
      error : function(xhr, status, error) {
        alert("No metrics found");
      }
    });
  else 
    $.ajax({
      url : prefix + "/proxy/metrics/by-type/" + type,
      dataType : 'json',
      type : 'GET',
      success : function(data) {
        $.each(data, function(i, obj) {
          $("<option/>").attr("value", obj.metric.id)
              .text(obj.metric.mnemonic)
              .appendTo("#metricSelect");
        });
      },
      error : function(xhr, status, error) {
        alert("No metrics of type " + type);
      }
    });
}
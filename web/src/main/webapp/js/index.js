var prefix = "/web";

var state = {
    prid : '',
    verLatest : '',
    verFirst : '',
    metrics: '',
    selVersion: '',
    verFiles: ''
}

$(document).ready(function() {

    getProjects();
    state.metrics = new Array();
    $("#projectSelect").change(projectSelected);
    getMetrics('SOURCE_FILE');
    getMetrics('SOURCE_DIRECTORY');
	getMetrics('PROJECT_VERSION');
    
    //$.jqplot('verplot1plot',  [[[1, 2],[3,5.12],[5,13.1],[7,33.6],[9,85.9],[11,219.9]]]);
    //$.jqplot('verplot2plot',  [[[1, 2],[3,5.12],[5,13.1],[7,33.6],[9,85.9],[11,219.9]]]);
});

function projectSelected() {
  state.prid = $("#projectSelect option:selected").val();
  
  if (state.prid == "-")
    return;

  $("#projectName").text($("#projectSelect").text());
  
  $.getJSON(prefix + '/proxy/projects/' + state.prid + "/versions/latest",
      gotLatestVersion);
  
  $.each(state.metrics['PROJECT_VERSION'], function(i, obj){
	  $("<option/>").attr("value", obj.metric.id).text(obj.metric.mnemonic 
			  + " | " + obj.metric.description).appendTo("#verplot2metr");
	  $("<option/>").attr("value", obj.metric.id).text(obj.metric.mnemonic 
			  + " | " + obj.metric.description).appendTo("#verplot1metr");
  });
    
  $.each(state.metrics['SOURCE_FILE'], function(i, obj) {
	  
  });
}

function gotLatestVersion (data) {
  state.verLatest = data;
  $.getJSON(prefix + '/proxy/projects/' + state.prid + "/versions/first", 
      gotFirstVersion);
  getFiles(state.verLatest);
}

function gotFirstVersion (data) {
  state.verFirst = data;
  
  $("#versionrange").slider({
    range: true,
    min: state.verFirst.version.revisionId,
    max: state.verLatest.version.revisionId,
    values: [0, 0],
    slide: function(event, ui) {
      $("#versionlbl").val(ui.values[0] + '-' + ui.values[1]);
    }
  });
  
  $("#verrngcont").removeClass("hidden");
}

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

function getFiles(version, callback) {
	if (state.selVersion == version) {
		callback();
		return;
	}
	
	$.ajax({
		url : prefix + "/proxy/projects/" 
			+ state.prid + "/versions/" 
			+ state.verLatest + "/files",
	    dataType : 'json',
	    type : 'GET',
	    success : function(data) {
			state.verFiles = data;
			state.selVersion = version;
			callback();
	    },
	    error : function(xhr, status, error) {
	    	alert("No files found for version: " + version);
	    }
	});
}

function getMetrics(type) {
  $.ajax({
	  url : prefix + "/proxy/metrics/by-type/" + type,
      dataType : 'json',
      type : 'GET',
      success : function(data) {
	  	state.metrics[type] = data;
      },
      error : function(xhr, status, error) {
        alert("No metrics of type " + type);
      }
  });
}
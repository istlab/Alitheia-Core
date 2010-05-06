var prefix = "/web";

var state = {
    prid : '',
    verLatest : '',
    verFirst : '',
    metrics: '',
    selVersion: '',
    verFiles: '',
    dirs: '',
    files: ''
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
}

function gotLatestVersion (data) {
  state.verLatest = data;
  $.getJSON(prefix + '/proxy/projects/' + state.prid + "/versions/first", 
      gotFirstVersion);
  getFiles(state.verLatest, displayFiles);
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
	if ( state.selVersion != '' &&
			state.selVersion.version.id == version.version.id) {
		if (callback != null) callback();
		return;
	}
	
	$.ajax({
		url : prefix + "/proxy/projects/" 
			+ state.prid + "/versions/" 
			+ state.verLatest.version.revisionId + "/files",
	    dataType : 'json',
	    type : 'GET',
	    success : function(data) {
			state.verFiles = data;
			state.dirs = new Array(); state.files = new Array();
			
			state.verFiles.forEach(function(f) {
				if (f.file.isdir == true)
					state.dirs[f.file.dir.id] = f.file;
				else {
					if (files[f.file.dir.id] == null)
						state.files[f.file.dir.id] = new Array();
					state.files[f.file.dir.id].push(f.file);
				}
			});
			state.selVersion = version;
			if (callback != null) callback();
	    },
	    error : function(xhr, status, error) {
	    	alert("No files found for version: " + version);
	    }
	});
}

function displayFiles() {
	$("#filelist thead tr").empty();
	$("#filelist tbody").empty();
	
	//Print the headers
	$("#filelist thead tr").append("<td>Files</td>");
	$.each(state.metrics['SOURCE_DIRECTORY'], function(i, obj) {
		$("#filelist thead tr").append("<td>" + obj.metric.mnemonic + "</td>");
	});
	
	//Print the files
	for (var i in state.dirs) {
		$("#filelist tbody").append($("<tr/>").attr("id", state.dirs[i].id));
		$("#filelist tr[id=" + state.dirs[i].id + "]").append("<td class=\"dir\">" + state.dirs[i].dir.path +"/" + state.dirs[i].name + "</td>");
		
		$.each(state.metrics['SOURCE_DIRECTORY'], function(j, obj) {
			$("#filelist tr[id=" + state.dirs[i].id + "]").append("<td></td>");
		});
	}
	
	//Add a mouse over effect
	$("#filelist tr").mouseover(function(){
		$(this).addClass("fileover");
	}).mouseout(function(){
		$(this).removeClass("fileover");
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
        //alert("No metrics of type " + type);
      }
  });
}
var prefix = "/web";

var state = {
    prid : '',
    verLatest : '',
    verFirst : '',
    metrics: '',
    selVersion: '',
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

  $("#projectName").text($("#projectSelect option:selected").text());
  
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
  getDirs(state.verLatest, displayDirs);
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

function getDirs(version, callback) {
	if (state.selVersion != '' &&
			state.selVersion.version.id == version.version.id) {
		if (callback != null) callback();
		return;
	}
	
	$.ajax({
		url : prefix + "/proxy/projects/" 
			+ state.prid + "/versions/" 
			+ version.version.revisionId + "/dirs/",
	    dataType : 'json',
	    type : 'GET',
	    success : function(data) {
			state.verFiles = data;
			state.dirs = new Array(); state.files = new Array();
			
			  //Index dirs by path
		    data.forEach(function(f) {
		      var fullpath = f.file.dir.path + "/" + f.file.name;
		      state.dirs[fullpath] = f.file;
		    });
		    
		    state.selVersion = version;
		    if (callback != null) callback();
	    },
	    error : function(xhr, status, error) {
	    	alert("No directories found for version: " + version);
	    }
	});
}

function displayDirs() {
  //Clean up old table contents
	$("#dirs thead tr").empty();
	$("#dirs tbody").empty();
	
	//Print the headers
	$("#dirs thead tr").append("<td>Directories</td>");
	$.each(state.metrics['SOURCE_DIRECTORY'], function(i, obj) {
		$("#dirs thead tr").append("<td>" + obj.metric.mnemonic + "</td>");
	});
	
	//Print the directories
	for (var i in state.dirs) {
		$("#dirs tbody").append($("<tr/>").attr("id", i));
		$("#dirs tr[id="+ i + "]").append("<td class=\"dir\">" + i + "</td>");
		
		//Placeholders for metrics
		$.each(state.metrics['SOURCE_DIRECTORY'], function(j, obj) {
			$("#dirs tr[id=" + i + "]").append("<td></td>");
		});
	}
	
	//Add a mouse over effect
	$("#dirs tbody tr td").mouseover(function(){
		$(this).addClass("fileover");
	}).mouseout(function(){
		$(this).removeClass("fileover");
	});
	
	//Add a click event, show the files
  $("#dirs .dir").click(function(){
    $(".dirclicked").removeClass("dirclicked");
    $(this).parent().addClass("dirclicked");
    getFiles($(this).parent().attr("id"), displayFiles);
  });
}

/* Retrieve files for a dir, cache them and call a function if successful*/
function getFiles(dir, callback) {
  
  if (state.files[dir] != null 
      && state.files[dir].length > 0) {
    if (callback != null) callback(dir);
    return;
  }
  
  $.ajax({
    url : prefix + "/proxy/projects/" 
      + state.prid + "/versions/" 
      + state.selVersion.version.revisionId + "/files" + dir,
      dataType : 'json',
      type : 'GET',
      success : function(data) {
        //Index files by including dir path
        data.forEach(function(f) {
          if (state.files[dir] == null)
            state.files[dir] = new Array();
          
          state.files[dir].push(f.file);
        });
        
        if (callback != null) callback(dir);
      },
      error : function(xhr, status, error) {
        alert("No files found for version: " + version);
      }
  });
}

function displayFiles(dir) {
//Clean up old table contents
  $("#files thead tr").empty();
  $("#files tbody").empty();
  
  //Print the headers
  $("#files thead tr").append("<td>Files</td>");
  $.each(state.metrics['SOURCE_FILE'], function(i, obj) {
    $("#files thead tr").append("<td>" + obj.metric.mnemonic + "</td>");
  });

  for (var i in state.files[dir]) {
    $("#files tbody").append($("<tr/>").attr("id", i));
    $("#files tr[id="+ i + "]").append("<td class=\"file\">" + state.files[dir][i].name + "</td>");
    
    //Placeholders for metrics
    $.each(state.metrics['SOURCE_DIRECTORY'], function(j, obj) {
      $("#dirs tr[id=" + i + "]").append("<td></td>");
    });
  }
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
var prefix = "/web";

/* Local object cache, gets built gradually as AJAX requests return*/
var state = {
    prid : '',
    verLatest : '',
    verFirst : '',
    metrics: '',
    selVersion: '',
    dirs: '',
    files: '',
    results: ''
}

//Javascript entry point
$(document).ready(function() {

    getProjects();
    state.metrics = new Array();
    $("#projectSelect").change(projectSelected);
    getMetrics('SOURCE_FILE');
    getMetrics('SOURCE_DIRECTORY');
    getMetrics('PROJECT_VERSION');
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

function projectSelected() {
  cleanup();
  state.prid = $("#projectSelect option:selected").val();
  
  if (state.prid == "-")
    return;

  $("#projectName").text($("#projectSelect option:selected").text());
  
  $.getJSON(prefix + '/proxy/projects/' + state.prid + "/versions/latest",
      gotLatestVersion);
  //getVersion("latest", gotLatestVersion);
}

function gotLatestVersion (data) {
  state.verLatest = data;
  getDirs(state.verLatest, displayDirs);
  $.getJSON(prefix + '/proxy/projects/' + state.prid + "/versions/first", 
      gotFirstVersion);
}

function gotFirstVersion (data) {
  state.verFirst = data;
  displayVersions();
}

//Cleanup and re-initialise controls
function cleanup() {
  $("#verplot2metr").empty();
  $("#verplot1metr").empty();
  $("<option/>").text("-- Select a metric --").appendTo("#verplot2metr");
  $("<option/>").text("-- Select a metric --").appendTo("#verplot1metr");
  $('#verplot1plot').empty();
  $('#verplot2plot').empty();
  $("#dirs thead tr").empty();
  $("#dirs tbody").empty();
  $("#files thead tr").empty();
  $("#files tbody").empty();
}

function displayVersions() {
  //Fill in metric selectors
  $.each(state.metrics['PROJECT_VERSION'], function(i, obj){
    $("<option/>").attr("value", obj.metric.id).text(obj.metric.mnemonic 
        + " | " + obj.metric.description).appendTo("#verplot2metr");
    $("<option/>").attr("value", obj.metric.id).text(obj.metric.mnemonic 
        + " | " + obj.metric.description).appendTo("#verplot1metr");
  });
  
  //Setup triggers for metric selectors
  $("#verplot1metr").change(loadVerPlot1);
  $("#verplot2metr").change(loadVerPlot2);
  
  //Trigger metric a metric selection, to start metric results download
  //verReplot();
  
  //Setup version slider
  $("#versionrange").slider({
    range: true,
    min: state.verFirst.version.revisionId,
    max: state.verLatest.version.revisionId,
    values: [0, state.verLatest.version.revisionId],
    stop: function(event, ui) {
      $("#versionlbl").val(ui.values[0] + '-' + ui.values[1]);
      verReplot();
      state.selVersion =  ui.values[1];
      getDirs(state.selVersion, displayDirs);
    }
  });
  $("#versionlbl").val($("#versionrange").slider("values", 0) + ' - ' 
      + $("#versionrange").slider("values", 1));
  
  //Setup the sample size label
  $("#samplesize").slider({
    min: 0,
    max: 50,
    values: [25],
    stop: function(event, ui) {
      $("#smpllbl").val(ui.values[0]);
      verReplot();
    }
  });
  $("#smpllbl").val($("#samplesize").slider("values", 0));
  
  //Show the version pane
  if ($("#versionpane").is(":visible") == false)
    $("#versionpane").show('clip', null, 500);
}

function verReplot() {
  if($('#verplot1plot').val() == null)
    $('#verplot1plot').val(2);
  
  if($('#verplot1plot').val() == null)
    $('#verplot1plot').val(2);
  
  loadVerPlot1();
  loadVerPlot2();
}

function loadVerPlot1(e) {
  $('#verplot1plot').empty();
  
  var revs = getVersionNumbers(
      $("#samplesize").slider("values", 0),
      state.verFirst.version.revisionId, 
      state.verLatest.version.revisionId);
  
  getResults(revs, $('#verplot1metr').val(), plotVerPlot, 
      {plot: 'verplot1plot', ylabel: $('#verplot1metr :selected').text()});
}

function loadVerPlot2(e) {
  $('#verplot2plot').empty();
  
  var revs = getVersionNumbers(
      $("#samplesize").slider("values", 0),
      state.verFirst.version.revisionId, 
      state.verLatest.version.revisionId);
  
  getResults(revs, $('#verplot2metr').val(), plotVerPlot, 
      {plot: 'verplot2plot', ylabel: $('#verplot2metr :selected').text()});
}

//Plot a project version metric
function plotVerPlot(metric, args) {
  var points = [];
  $.each(state.byMetricCache[metric], function(i, data) {
    points.push([data.r.artifactId, data.r.result]);
  });
  
  $.jqplot(args.plot, [points],  {
    axes:{
      xaxis:{
        label:'Version',
        min: 0
      },
      yaxis:{
        label: args.ylabel,
        autoscale: true,
        min: 0
      }
    }
  });
}

/*Metrics helpers*/
/*Get and cache a list of metrics by type*/
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

/*Get and cache metric results by resource and metric id*/
function getResults(resourceIds, metricId, callback, callbackargs) {
  var resources = new String();
 // var  
  
  if (resourceIds instanceof Array) {
    $.each(resourceIds, function(i, val) {
      resources = resources + val + ",";
    });
  } else { 
    resources = resourceIds;
  }
  
  $.ajax({
    url : prefix + "/proxy/metrics/by-id/" + metricId 
      + "/result/" + resources,
      dataType : 'json',
      type : 'GET',
      success : function(data) {
        if (state.byMetricCache == null)
          state.byMetricCache = new Array();
        if (state.byResourceCache == null)
          state.byResourceCache = new Array();
        
        $.each(data, function(i, val) {
          if (state.byMetricCache[metricId] == null)
            state.byMetricCache[metricId] = new Array();
          state.byMetricCache[metricId].push(val);
          
          if (state.byResourceCache[val.r.resourceId] == null)
            state.byResourceCache[val.r.resourceId] = new Array();
          
          state.byResourceCache[val.r.resourceId].push(val);
        });
        if (callback != null) callback(metricId, callbackargs);
      },
      error : function(xhr, status, error) {
        alert("No metrics found " + error);
      }
  });
}

/*Get an array of a maximum num evenly distributed numbers between min and max*/
function getVersionNumbers(num, min, max) {
  var result = new Array();
  
  if (max - min <= num) {
    var i;
    for (i = min; i <= max; i++)
      result.push(i);
    return result;
  }
  
  if (max - min <= 0)
    return result;
  
  var incr = (max - min)/num;
  
  for (i = min; i <= max; i += incr)
    result.push(Math.floor(i));
  
  return result;
}

function getVersion(version, callback) {
  
  var ver;
  //while (ver == null) {
    $.ajax({
      url: prefix + '/proxy/projects/' + state.prid + "/versions/" + version,
      dataType : 'json',
      type : 'GET',
      success: function (data) {
        ver = data;
      },
      error : function(xhr, status, error) {
          alert("No version found: " + version);
        }
    });
    
  //  version ++;
  //}
  
  if (callback != null) callback(ver);
}

/*File view*/
/*Get a list of all directories and cache them locally*/
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
	      state.dirs = new Array(); state.files = new Array();
			
			  //Index dirs by path
		    data.forEach(function(f) {
		      state.dirs.push(f.file);
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
	$.each(state.dirs, function(i, file) {
	  var name = getFilePath(file);
		$("#dirs tbody").append($("<tr/>").attr("id", name));
		$("#dirs tr[id="+ name + "]").append("<td class=\"dir\">" + name + "</td>");
		
		//Placeholders for metrics
		$.each(state.metrics['SOURCE_DIRECTORY'], function(j, obj) {
			$("#dirs tr[id=" + name + "]").append("<td></td>");
		});
	});
	
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
  
  if ($("#filepane").is(":visible") == false)
    $("#filepane").show('clip', null, 500);
}

function getFilePath(file) {
  if (file.dir.path == "/")
    return "/" + file.name;
  return file.dir.path + "/" + file.name;
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
        alert("No files found for dir: " + dir);
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
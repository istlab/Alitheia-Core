window.setInterval(function() {
  $.ajax({
    url: "/api/job_stats/",
    dataType: "json"
  }).done(function(response) {
    $("#jobs #jobs-executing").html(response.schedulerStats.runningJobs)
    $("#jobs #jobs-waiting").html(response.schedulerStats.waitingJobs)
    $("#jobs #jobs-failed").html(response.schedulerStats.failedJobs)
    $("#jobs #jobs-total").html(response.schedulerStats.totalJobs)
    $("#jobs #jobs-workers").html(response.schedulerStats.workerThreads)
  });
}, 2000)

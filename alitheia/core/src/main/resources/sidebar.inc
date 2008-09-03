      <div id="sidebar">
        <fieldset id="status">
          <legend>$tr.label("status")</legend>
          <ul>
            <li class="uptime">$tr.label("uptime") : $UPTIME</li>
            <li class="queue">$tr.label("queue_length") : $scheduler.WaitingJobs</li>
          </ul>
        </fieldset>
        <fieldset id="jobs">
          <legend>$tr.label("job_info")</legend>
          <table width='100%' cellspacing=0 cellpadding=3>
            <tr>
              <td>$tr.label("executing") :</td>
              <td class="number">$scheduler.RunningJobs</td>
            </tr>
            <tr>
              <td>$tr.label("waiting") :</td>
              <td class="number">$scheduler.WaitingJobs</td>
            </tr>
            <tr>
              <td>$tr.label("failed") :</td>
              <td class="number">$scheduler.FailedJobs</td>
            </tr>
            <tr>
              <td>$tr.label("total") :</td>
              <td class="number">$scheduler.TotalJobs</td>
            </tr>
            <tr class="newgroup">
              <td>$tr.label("workers") :</td>
              <td class="number">$scheduler.WorkerThreads</td>
            </tr>
          </table>
        </fieldset>
        $OPTIONS
      </div>
      <div id="footer">
        <p>
        $COPYRIGHT
        </p>
      </div>

#set ($schedulerStats = $jobs.getSchedulerStats())   
            <table style="width: 100%;" cellspacing="0" cellpadding="3">
              <tr>
                <td>$tr.label("executing") :</td>
                <td class="number">$schedulerStats.getRunningJobs()</td>
              </tr>
              <tr>
                <td>$tr.label("waiting") :</td>
                <td class="number">$schedulerStats.getWaitingJobs()</td>
              </tr>
              <tr>
                <td>$tr.label("failed") :</td>
                <td class="number">$schedulerStats.getFailedJobs()</td>
              </tr>
              <tr>
                <td>$tr.label("total") :</td>
                <td class="number">$schedulerStats.getTotalJobs()</td>
              </tr>
              <tr class="newgroup">
                <td>$tr.label("workers") :</td>
                <td class="number">$schedulerStats.getWorkerThreads()</td>
              </tr>
            </table>